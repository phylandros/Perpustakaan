package com.example.perpustakaan;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private Camera camera;
    private ImageAnalysis imageAnalysis;
    LinearLayout dialogLayout;
    TextView txtkonf;
    Button btnPinjam;

    private boolean isCameraRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        fetchDataFromAPI();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email","");
        String userid = sharedPreferences.getString("userid","");
        TextView namaUser = findViewById(R.id.namauser);
        namaUser.setText(name);
        Log.d("SharedPreferences", "Nilai name: " + name);

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
                stopCamera(); // Memanggil method untuk menghentikan kamera
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Mengirim data email ke ProfileFragment
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("userid", userid);
                profileFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.coordinator_layout, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCamera(); // Memanggil method untuk menghentikan kamera
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.coordinator_layout, new PeminjamanBukuFragment());
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
    private void fetchDataFromAPI() {
        new FetchData().execute("http://8.219.70.58:5988/perpus");
    }

    public class FetchData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processJSONData(s);
        }
    }
    private void processJSONData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            List<LocationDataModel> dataList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("nama");
                String address = jsonObject.getString("alamat");
                String operatingHours = jsonObject.getString("jam_operasional");
                Integer image = R.drawable.image;
                dataList.add(new LocationDataModel(image,name, address, operatingHours));
            }

            runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                AdapterLocation adapter = new AdapterLocation(dataList);
                recyclerView.setAdapter(adapter);
            });
        } catch (JSONException e) {
            e.printStackTrace();
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

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

        // Menambahkan analisis gambar untuk mendeteksi QR code
        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                Bitmap bitmap = previewView.getBitmap();
                if (bitmap != null) {
                    String qrText = decodeQRCode(bitmap);
                    if (qrText != null) {
                        // Show the dialog here
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set text to textkonfirm TextView with the scanned QR content

                                txtkonf.setText("Selamat datang di " + qrText);

                                // Change the background of btnPinjam to shapemasuk drawable
                                Button btnPinjam = findViewById(R.id.btnpinjam);
                                btnPinjam.setBackgroundResource(R.drawable.shapemasuk);
                                btnPinjam.setEnabled(true);
                                // Change the BottomSheetBehavior state to expanded when a QR code is detected
                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(dialogLayout);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                stopCamera();
                                // Here, you might also update any other UI elements inside the bottom sheet with the scanned QR content
                            }
                        });
                    }
                }
                image.close();
            }
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);

        preview.setSurfaceProvider(previewOutput -> {
            ViewGroup parent = (ViewGroup) previewView.getParent();
            parent.removeView(previewView);
            parent.addView(previewView, 0);

            preview.setSurfaceProvider(previewView.getSurfaceProvider());
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        stopCamera(); // Menghentikan kamera saat aplikasi di-minimize
        stopImageAnalysis();
        saveCameraStatus(isCameraRunning); // Menyimpan status kamera ke SharedPreferences
    }

    private void saveCameraStatus(boolean isRunning) {
        SharedPreferences sharedPreferences = getSharedPreferences("CameraPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isCameraRunning", isRunning);
        editor.apply();
    }

    private boolean getSavedCameraStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("CameraPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isCameraRunning", false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        boolean savedCameraStatus = getSavedCameraStatus(); // Mendapatkan status kamera dari SharedPreferences
        if (savedCameraStatus) {
            initializeCamera(); // Jika sebelumnya kamera sedang berjalan, inisialisasi kamera kembali
        }
    }

    private void stopCamera() {
        if (cameraProviderFuture != null) {
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll(); // Menghentikan semua koneksi kamera
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Error stopping camera: " + e.getMessage());
                }
            }, ContextCompat.getMainExecutor(this));
        }
    }

    private void stopImageAnalysis() {
        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
        }
    }
}
