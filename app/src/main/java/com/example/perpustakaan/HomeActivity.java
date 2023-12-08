package com.example.perpustakaan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.adapter.AdapterLocation;
import com.example.perpustakaan.model.LocationDataModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 123;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private AdapterLocation adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        previewView = findViewById(R.id.previewView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            initializeCamera();
        }
        LinearLayout dialogLayout = findViewById(R.id.dialog);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(dialogLayout);
        bottomSheetBehavior.setPeekHeight(100);
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

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }


}