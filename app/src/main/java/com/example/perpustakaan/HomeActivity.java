package com.example.perpustakaan;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.adapter.AdapterLocation;
import com.example.perpustakaan.model.LocationDataModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 123;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private AdapterLocation adapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor(); // Untuk memproses deteksi QR code

    LinearLayout dialogLayout;
    TextView txtkonf;
    Button btnPinjam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        previewView = findViewById(R.id.previewView);
        txtkonf = findViewById(R.id.textkonfirm);
        btnPinjam = findViewById(R.id.btnpinjam);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            initializeCamera();
        }

        LinearLayout profile = findViewById(R.id.toolbar);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.coordinator_layout, new ProfileFragment()); // Ganti R.id.container dengan ID yang sesuai dari container fragment Anda
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        dialogLayout = findViewById(R.id.dialog);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(dialogLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;

        int peekHeightPercentage;
        // Menggunakan kondisional untuk menetapkan tiga tinggi peek yang berbeda
        if (screenHeight < 1000) {
            peekHeightPercentage = (int) (20);
        } else if (screenHeight < 1500) {
            peekHeightPercentage = (int) (50);
        } else {
            peekHeightPercentage = (int) (100);
        }

        bottomSheetBehavior.setPeekHeight(peekHeightPercentage);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LocationDataModel> dataList = new ArrayList<>();

// Menambahkan objek LocationDataModel ke dalam dataList
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 1", "Kota A", "Jam 9:00 - 12:30"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 2", "Kota B", "Jam 8:00 - 11:30"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 3", "Kota C", "Jam 10:00 - 13:30"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 4", "Kota D", "Jam 7:30 - 10:00"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 5", "Kota E", "Jam 12:00 - 15:30"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 6", "Kota F", "Jam 11:30 - 14:00"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 7", "Kota F", "Jam 11:30 - 14:00"));
        dataList.add(new LocationDataModel(R.drawable.image, "Perpus Kampus 8", "Kota F", "Jam 11:30 - 14:00"));


// Gunakan dataList dalam adapter
        AdapterLocation adapter = new AdapterLocation(dataList);
        recyclerView.setAdapter(adapter);



    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Izin kamera dibutuhkan untuk menggunakan fitur kamera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initializeCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Method untuk mengikat preview dan deteksi QR code ke kamera
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

        // Menambahkan analisis gambar untuk mendeteksi QR code
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = previewView.getBitmap();
                        String qrText = decodeQRCode(bitmap);
                        if (qrText != null) {
                            // Lakukan sesuatu dengan teks QR code yang didapatkan
                            Toast.makeText(HomeActivity.this, "QR Code: " + qrText, Toast.LENGTH_SHORT).show();
                            BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(dialogLayout);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            txtkonf.setText("Selamat Datang di " + qrText);
                            btnPinjam.setBackgroundResource(R.drawable.shapemasuk);
                            btnPinjam.setEnabled(Boolean.parseBoolean("true"));
                        }
                    }
                });
                image.close();
            }
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
    }

    // Method untuk mendekode QR code dari gambar Bitmap
    private String decodeQRCode(Bitmap bitmap) {
        String decodedText = null;
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {
            Result result = multiFormatReader.decode(binaryBitmap);
            decodedText = result.getText();
        } catch (Exception e) {
            Log.e(TAG, "QR code tidak ditemukan atau tidak valid: " + e.getMessage());
        }

        return decodedText;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
