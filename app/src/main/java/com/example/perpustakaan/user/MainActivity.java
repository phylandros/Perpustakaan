package com.example.perpustakaan.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpustakaan.BuildConfig;
import android.Manifest;
import com.example.perpustakaan.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private Button btnWelkom;
    private BottomSheetDialog dialog;
    private String api = BuildConfig.API;
    private EditText inpEmail, inpPassword;
    private Button btnMasuk;
    private String userId, accessToken;
    private EditText namaReg, emailReg, passReg, conpassReg, noktpReg, alamatReg, notelReg;
    private ImageView imageUser;
    TextView test;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnWelkom = findViewById(R.id.btnWelkom);
        dialog = new BottomSheetDialog(this);

        test = findViewById(R.id.textTengah);
        btnWelkom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.0f);



    }

    public void showLoginDialog() {
        dialog.setContentView(R.layout.bsdlogin);

        inpEmail = dialog.findViewById(R.id.inpEmail);
        inpPassword = dialog.findViewById(R.id.inpPassword);
        btnMasuk = dialog.findViewById(R.id.btnmasuk);
        Button btnDaftar = dialog.findViewById(R.id.btndaftar);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRegisterDialog();
            }
        });
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailValue = inpEmail.getText().toString().trim();
                String passwordValue = inpPassword.getText().toString().trim();

                if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
                    try {
                        JSONObject postData = new JSONObject();
                        postData.put("email", emailValue);
                        postData.put("password", passwordValue);

                        // Melakukan permintaan POST ke server
                        new PostLoginAsyncTask().execute(postData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Isi email dan password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private class PostLoginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String jsonData = "";

            try {
                URL url = new URL(api + "/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Mengirim data JSON ke server
                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(params[0]);
                writer.flush();
                writer.close();
                outputStream.close();

                // Menerima respons dari server
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonData += line;
                }

                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonData;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if (!jsonData.isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(jsonData);
                    if (jsonResponse.has("accessToken")) {
                        accessToken = jsonResponse.getString("accessToken");
                        if (accessToken != null && !accessToken.isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", accessToken);
                            editor.apply();

                            JSONObject data = jsonResponse.getJSONObject("data");
                            if (data.has("userId") && data.has("name") && data.has("email")) {
                                userId = data.getString("userId");
                                String name = data.getString("name");
                                String email = data.getString("email");
                                editor.putString("name", name);
                                editor.putString("email", email);
                                editor.putString("userid", userId);
                                editor.apply();

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("userid", userId);
                                intent.putExtra("accessToken", accessToken);
                                startActivity(intent);
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Gagal mendapatkan accessToken", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Parse Error", "Error parsing JSON response");
                }
            } else {
                Toast.makeText(MainActivity.this, "Username dan password salah", Toast.LENGTH_SHORT).show();
                Log.e("Connection Error", "No data received from server");
            }
        }
    }
    private class PostRegisterAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params.length < 9) {
                return null;
            }

            String nama = params[0];
            String email = params[1];
            String password = params[2];
            String confPassword = params[3];
            String nip_perpus = params[4];
            String ktp = params[5];
            String alamat = params[6];
            String phone = params[7];
            String imagePath = params[8];


            try {
                URL url = new URL(api+"/users");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----Boundary");

                String boundary = "----Boundary";
                String lineEnd = "\r\n";

                File imageFile = new File(imagePath);
                FileInputStream fileInputStream = new FileInputStream(imageFile);

                String data = "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"nama\"" + lineEnd +
                        lineEnd +
                        nama + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"email\"" + lineEnd +
                        lineEnd +
                        email + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"password\"" + lineEnd +
                        lineEnd +
                        password + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"confPassword\"" + lineEnd +
                        lineEnd +
                        confPassword + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"nip_perpus\"" + lineEnd +
                        lineEnd +
                        nip_perpus + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"ktp\"" + lineEnd +
                        lineEnd +
                        ktp + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"alamat\"" + lineEnd +
                        lineEnd +
                        alamat + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"phone\"" + lineEnd +
                        lineEnd +
                        phone + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"gambar\"; filename=\"" + imageFile.getName() + "\"" + lineEnd +
                        "Content-Type: image/jpeg" + lineEnd +
                        lineEnd;

                byte[] dataBytes = data.getBytes();

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(dataBytes);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes("------Boundary--" + lineEnd);

                outputStream.flush();
                fileInputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String serverResponse = response.toString();
                    Log.d("ServerResponse", serverResponse);
                } else {
                }

                outputStream.close();
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void showRegisterDialog() {
        // Tampilkan dialog register (bsdregister)
        dialog.setContentView(R.layout.bsdregister);
        Button btnLanjut = dialog.findViewById(R.id.btnlanjut);

        namaReg = dialog.findViewById(R.id.etNama);
        emailReg = dialog.findViewById(R.id.etEmail);
        passReg = dialog.findViewById(R.id.etPassword);
        conpassReg = dialog.findViewById(R.id.etconPassword);

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = namaReg.getText().toString().trim();
                String email = emailReg.getText().toString().trim();
                String password = passReg.getText().toString().trim();
                String conpassword = conpassReg.getText().toString().trim();

                if (!nama.isEmpty() && !email.isEmpty() && !password.isEmpty() && !conpassword.isEmpty()) {
                    if (password.equals(conpassword)) {
                        dialog.dismiss();
                        showRegister2Dialog(nama, email, password, conpassword);
                    } else {
                        Toast.makeText(MainActivity.this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Isi semua field terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void showRegister2Dialog(String nama, String email, String password, String conpassword) {
        dialog.setContentView(R.layout.bsdregister2);
        Button btnLanjut2 = dialog.findViewById(R.id.btnlanjut2);
        ImageView btnUpload = dialog.findViewById(R.id.uploadimage);
        imageUser = dialog.findViewById(R.id.imageuser);

        noktpReg = dialog.findViewById(R.id.etNoKtp);
        alamatReg = dialog.findViewById(R.id.etAlamat);
        notelReg = dialog.findViewById(R.id.etNotel);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReadPermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });

        btnLanjut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ktp = noktpReg.getText().toString().trim();
                String alamat = alamatReg.getText().toString().trim();
                String notel = notelReg.getText().toString().trim();
                String nip = String.valueOf(System.currentTimeMillis());
                if (!ktp.isEmpty() && !alamat.isEmpty() && !notel.isEmpty()) {
//                    imageUser.setDrawingCacheEnabled(true);

                    new PostRegisterAsyncTask().execute(nama, email, password, conpassword,nip, ktp, alamat, notel, imagePath);
                    showRegisterDoneDialog();
                } else {
                    Toast.makeText(MainActivity.this, "Isi semua field terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private boolean checkReadPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_PERMISSION_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                bitmap = rotateImageIfRequired(bitmap, selectedImageUri);
                imageUser.setImageBitmap(bitmap);
                imageUser.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imagePath = getPathFromUri(selectedImageUri);
            Toast.makeText(this, "Image Path: " + imagePath, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = this.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    private String getPathFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Izin akses galeri ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showRegisterDoneDialog() {
        // Tampilkan dialog register done (bsdregisdone)
        dialog.setContentView(R.layout.bsdregisdone);
        Button btnBacktoLogin = dialog.findViewById(R.id.btnbacklogin);


        btnBacktoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showLoginDialog();
            }
        });
        dialog.show();
    }

}