//package com.example.perpustakaan;
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageProxy;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.perpustakaan.adapter.AdapterLocation;
//import com.example.perpustakaan.model.LocationDataModel;
//import com.google.android.material.bottomsheet.BottomSheetBehavior;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.zxing.BinaryBitmap;
//import com.google.zxing.LuminanceSource;
//import com.google.zxing.MultiFormatReader;
//import com.google.zxing.RGBLuminanceSource;
//import com.google.zxing.Result;
//import com.google.zxing.common.HybridBinarizer;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class HomeActivity extends AppCompatActivity {
//    private static final String TAG = "HomeActivity";
//    private static final int REQUEST_CAMERA_PERMISSION = 123;
//    private String api = BuildConfig.API;
//
//    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
//    private PreviewView previewView;
//    private AdapterLocation adapter;
//    private ExecutorService executor = Executors.newSingleThreadExecutor(); // Untuk memproses deteksi QR code
//    private Camera camera;
//    private ImageAnalysis imageAnalysis;
//    private LinearLayout dialogLayout;
//    private TextView txtkonf;
//    private Button btnPinjam;
//    private int idperpus;
//    String role;
//    private boolean isCameraRunning = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        fetchUserFromAPI();
//        if (role =="anggota"){
//            setContentView(R.layout.activity_home);
//        } else {
//
//        }
//
//        fetchDataFromAPI();
//
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String name = sharedPreferences.getString("name", "");
//        String email = sharedPreferences.getString("email","");
//        String userid = sharedPreferences.getString("userid","");
//        String accesstoken = sharedPreferences.getString("accessToken","");
//        TextView namaUser = findViewById(R.id.namauser);
//        namaUser.setText(name);
//        Log.d("SharedPreferences", "Nilai name: " + name);
//
////        previewView = findViewById(R.id.previewView);
////        txtkonf = findViewById(R.id.textkonfirm);
//        btnPinjam = findViewById(R.id.btnpinjam);
//
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission();
//        } else {
//            initializeCamera();
//        }
//
//        LinearLayout profile = findViewById(R.id.toolbar);
//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopCamera(); // Memanggil method untuk menghentikan kamera
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                ProfileFragment profileFragment = new ProfileFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("userid", userid); // Mengirim data userid ke ProfileFragment
//                bundle.putString("accessToken", accesstoken);
//                profileFragment.setArguments(bundle);
//
//                fragmentTransaction.replace(R.id.coordinator_layout, profileFragment); // Menggunakan profileFragment yang sudah di-set dengan bundle
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//
//        btnPinjam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopCamera(); // Memanggil method untuk menghentikan kamera
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                PeminjamanBukuFragment peminjamanBukuFragment = new PeminjamanBukuFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("userid", userid); // Mengirim data userid ke ProfileFragment
//                bundle.putString("accessToken", accesstoken);
//                bundle.putInt("perpusid", idperpus);
//                peminjamanBukuFragment.setArguments(bundle);
//
//                fragmentTransaction.replace(R.id.coordinator_layout, peminjamanBukuFragment); // Menggunakan profileFragment yang sudah di-set dengan bundle
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//
//
////        dialogLayout = findViewById(R.id.dialog);
//        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(dialogLayout);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//        int screenHeight = displayMetrics.heightPixels;
//
//        int peekHeightPercentage;
//        // Menggunakan kondisional untuk menetapkan tiga tinggi peek yang berbeda
//        if (screenHeight < 1000) {
//            peekHeightPercentage = (int) (20);
//        } else if (screenHeight < 1500) {
//            peekHeightPercentage = (int) (50);
//        } else {
//            peekHeightPercentage = (int) (100);
//        }
//
//        bottomSheetBehavior.setPeekHeight(peekHeightPercentage);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//    }
//
//    private void requestCameraPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                initializeCamera();
//            } else {
//                Toast.makeText(this, "Izin kamera dibutuhkan untuk menggunakan fitur kamera.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//
//    private void fetchDataFromAPI() {
//        new FetchData().execute(api+"/perpus");
//    }
//    private void fetchUserFromAPI() {
//        new FetchUserDataTask().execute(api+"/users");
//    }
//
//    private class FetchUserDataTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... urls) {
//            String apiUrl = urls[0];
//            StringBuilder result = new StringBuilder();
//            HttpURLConnection urlConnection = null;
//
//            try {
//                URL url = new URL(apiUrl);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream in = urlConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(in);
//                BufferedReader bufferedReader = new BufferedReader(reader);
//                String line;
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    result.append(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//            }
//
//            return result.toString();
//        }
//
//        @Override
//        protected void onPostExecute(String userData) {
//            super.onPostExecute(userData);
//            try {
//                JSONObject jsonObject = new JSONObject(userData);
//                role = jsonObject.getString("role");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//
//    public class FetchData extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... urls) {
//            StringBuilder result = new StringBuilder();
//            HttpURLConnection urlConnection = null;
//            try {
//                URL url = new URL(urls[0]);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream in = urlConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(in);
//                BufferedReader bufferedReader = new BufferedReader(reader);
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    result.append(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//            }
//            return result.toString();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            processJSONData(s);
//        }
//    }
//    private void processJSONData(String jsonData) {
//        try {
//            JSONArray jsonArray = new JSONArray(jsonData);
//
//            List<LocationDataModel> dataList = new ArrayList<>();
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Integer perpusid = jsonObject.getInt("perpus_id");
//                String name = jsonObject.getString("nama");
//                String address = jsonObject.getString("alamat");
//                String operatingHours = jsonObject.getString("jam_operasional");
//                Integer image = R.drawable.image;
//                dataList.add(new LocationDataModel(image,perpusid,name, address, operatingHours));
//            }
//
//            runOnUiThread(() -> {
////                RecyclerView recyclerView = findViewById(R.id.recyclerView);
////                recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//                AdapterLocation adapter = new AdapterLocation(dataList);
////                recyclerView.setAdapter(adapter);
//
//                adapter.setOnItemClickListener(new AdapterLocation.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(int position) {
//                        LocationDataModel clickedItem = dataList.get(position);
//                        String nama = clickedItem.getTitle();
//                        idperpus = clickedItem.getPerpusid();
//                        txtkonf.setText("Selamat datang di " + nama );
//                        btnPinjam.setBackgroundResource(R.drawable.shapemasuk);
//                        btnPinjam.setEnabled(true);
//                    }
//                });
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    private void initializeCamera() {
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                bindPreview(cameraProvider);
//            } catch (ExecutionException | InterruptedException e) {
//                Log.e(TAG, "Error starting camera: " + e.getMessage());
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    // Method untuk mengikat preview dan deteksi QR code ke kamera
//    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        Preview preview = new Preview.Builder().build();
//        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
//
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
//
//        // Menambahkan analisis gambar untuk mendeteksi QR code
//        imageAnalysis = new ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
//            @Override
//            public void analyze(@NonNull ImageProxy image) {
//                Bitmap bitmap = previewView.getBitmap();
//                if (bitmap != null) {
//                    String qrText = decodeQRCode(bitmap);
//                    if (qrText != null) {
//                        // Show the dialog here
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // Set text to textkonfirm TextView with the scanned QR content
//
//                                txtkonf.setText("Selamat datang di " + qrText);
//
//                                // Change the background of btnPinjam to shapemasuk drawable
//                                Button btnPinjam = findViewById(R.id.btnpinjam);
//                                btnPinjam.setBackgroundResource(R.drawable.shapemasuk);
//                                btnPinjam.setEnabled(true);
//                                // Change the BottomSheetBehavior state to expanded when a QR code is detected
//                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(dialogLayout);
//                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                                stopCamera();
//                                // Here, you might also update any other UI elements inside the bottom sheet with the scanned QR content
//                            }
//                        });
//                    }
//                }
//                image.close();
//            }
//        });
//
//        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
//
//        preview.setSurfaceProvider(previewOutput -> {
//            ViewGroup parent = (ViewGroup) previewView.getParent();
//            parent.removeView(previewView);
//            parent.addView(previewView, 0);
//
//            preview.setSurfaceProvider(previewView.getSurfaceProvider());
//        });
//    }
//
//
//    // Method untuk mendekode QR code dari gambar Bitmap
//    private String decodeQRCode(Bitmap bitmap) {
//        String decodedText = null;
//        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
//        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        MultiFormatReader multiFormatReader = new MultiFormatReader();
//        try {
//            Result result = multiFormatReader.decode(binaryBitmap);
//            decodedText = result.getText();
//        } catch (Exception e) {
//            Log.e(TAG, "QR code tidak ditemukan atau tidak valid: " + e.getMessage());
//        }
//
//        return decodedText;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        executor.shutdown();
//    }
//
//
//
//    private void saveCameraStatus(boolean isRunning) {
//        SharedPreferences sharedPreferences = getSharedPreferences("CameraPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("isCameraRunning", isRunning);
//        editor.apply();
//    }
//
//    private boolean getSavedCameraStatus() {
//        SharedPreferences sharedPreferences = getSharedPreferences("CameraPrefs", Context.MODE_PRIVATE);
//        return sharedPreferences.getBoolean("isCameraRunning", false);
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        boolean savedCameraStatus = getSavedCameraStatus();
//        if (savedCameraStatus) {
//            initializeCamera();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopCamera(); // Menghentikan kamera saat aplikasi di-minimize
//        stopImageAnalysis();
//        saveCameraStatus(isCameraRunning); // Menyimpan status kamera ke SharedPreferences
//    }
//
//
//
//    private void stopCamera() {
//        if (cameraProviderFuture != null) {
//            cameraProviderFuture.addListener(() -> {
//                try {
//                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                    cameraProvider.unbindAll(); // Menghentikan semua koneksi kamera
//                } catch (ExecutionException | InterruptedException e) {
//                    Log.e(TAG, "Error stopping camera: " + e.getMessage());
//                }
//            }, ContextCompat.getMainExecutor(this));
//        }
//    }
//
//    private void stopImageAnalysis() {
//        if (imageAnalysis != null) {
//            imageAnalysis.clearAnalyzer();
//        }
//    }
//}
