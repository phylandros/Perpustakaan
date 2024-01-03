package com.example.perpustakaan.admin;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PerpustakaanActivity extends AppCompatActivity {

    private String api = BuildConfig.API;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private EditText etNamaPerpus, etAlamat, etKota, etKodePos, etNegara, etNoTelPerpus,etJamOperasional,etEmail;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perpustakaan);

        Button uploadImageButton = findViewById(R.id.uploadimage);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        etNamaPerpus = findViewById(R.id.etnamaperpus);
        etAlamat = findViewById(R.id.etalamatperpus);
        etKota = findViewById(R.id.etkotaperpus);
        etKodePos = findViewById(R.id.etkodepos);
        etNegara = findViewById(R.id.etnegara);
        etNoTelPerpus = findViewById(R.id.etnotelperpus);
        etJamOperasional = findViewById(R.id.etjamoperasional);
        etEmail = findViewById(R.id.etemailperpus);

        Button tambahperpus = findViewById(R.id.btntambahperpus);
        tambahperpus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nama = etNamaPerpus.getText().toString();
                String alamat = etAlamat.getText().toString();
                String kota = etKota.getText().toString();
                String kodePos = etKodePos.getText().toString();
                String negara = etNegara.getText().toString();
                String telepon = etNoTelPerpus.getText().toString();
                String jamOperasional = etJamOperasional.getText().toString();
                String email = etEmail.getText().toString();

                new KirimDataPerpustakaanTask().execute(nama, alamat, kota, imagePath, kodePos, negara, telepon, jamOperasional, email);
            }
        });

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
            imagePath = getPathFromUri(selectedImageUri);

            Toast.makeText(this, "Image Path: " + imagePath, Toast.LENGTH_SHORT).show();
        }
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

    private class KirimDataPerpustakaanTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params.length < 9) {
                return null;
            }

            String nama = params[0];
            String alamat = params[1];
            String kota = params[2];
            String imagePath = params[3];
            String kodePos = params[4];
            String negara = params[5];
            String telepon = params[6];
            String jamOperasional = params[7];
            String email = params[8];

            try {
                URL url = new URL(api + "/perpus");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=*****");

                File file = new File(imagePath);
                FileInputStream fileInputStream = new FileInputStream(file);

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"nama\"\r\n\r\n");
                outputStream.writeBytes(nama + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"alamat\"\r\n\r\n");
                outputStream.writeBytes(alamat + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"kota\"\r\n\r\n");
                outputStream.writeBytes(kota + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"kode_pos\"\r\n\r\n");
                outputStream.writeBytes(kodePos + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"negara\"\r\n\r\n");
                outputStream.writeBytes(negara + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"telepon\"\r\n\r\n");
                outputStream.writeBytes(telepon + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"jam_operasional\"\r\n\r\n");
                outputStream.writeBytes(jamOperasional + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"email\"\r\n\r\n");
                outputStream.writeBytes(email + "\r\n");

                outputStream.writeBytes("------*****\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"gambar\";filename=\"" + file.getName() + "\"\r\n");
                outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.writeBytes("\r\n--*****--\r\n");
                outputStream.flush();
                outputStream.close();

                int statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    // Proses respons dari server jika diperlukan
                }

                fileInputStream.close();
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


}