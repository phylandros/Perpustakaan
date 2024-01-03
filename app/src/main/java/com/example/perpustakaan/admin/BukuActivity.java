package com.example.perpustakaan.admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BukuActivity extends AppCompatActivity {
    private Spinner perpusSpinner;
    private List<String> perpusNames = new ArrayList<>();
    private List<Integer> perpusIds = new ArrayList<>();
    private String api = BuildConfig.API;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private EditText etJudul, etPengarang, etPenerbit, etTahunTerbit, etDeskripsi, etKategori;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buku);

        perpusSpinner = findViewById(R.id.spperpus); // Ganti dengan ID spinner Anda
        new FetchPerpusDataTask().execute(api+"/perpus");

        Button uploadImageButton = findViewById(R.id.uploadimage);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        etJudul = findViewById(R.id.etnamabuku);
        etPengarang = findViewById(R.id.etpengarang);
        etPenerbit = findViewById(R.id.etpenerbit);
        etTahunTerbit = findViewById(R.id.ettahunterbit);
        etDeskripsi = findViewById(R.id.etdeskripsi);
        etKategori = findViewById(R.id.etkategori);

        Button tambahbuku = findViewById(R.id.btntambahbuku);

        tambahbuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String judul = etJudul.getText().toString();
                String pengarang = etPengarang.getText().toString();
                String penerbit = etPenerbit.getText().toString();
                String tahunTerbit = etTahunTerbit.getText().toString();
                String deskripsi = etDeskripsi.getText().toString();
                String kategori = etKategori.getText().toString();
                String perpusId = String.valueOf(perpusIds.get(perpusSpinner.getSelectedItemPosition()));

                new UploadBukuAsyncTask().execute(judul, pengarang, penerbit, tahunTerbit, deskripsi, kategori, perpusId, imagePath);
            }
        });


    }

    private class FetchPerpusDataTask extends AsyncTask<String, Void, String> {

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
            parsePerpusData(s);
            setupSpinner();
        }
    }

    private void parsePerpusData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int perpusId = jsonObject.getInt("perpus_id");
                perpusIds.add(perpusId); // Menambahkan perpusId sebagai integer ke dalam list
                String perpusName = jsonObject.getString("nama");
                perpusNames.add(perpusName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                perpusNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        perpusSpinner.setAdapter(spinnerAdapter);
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



    private class UploadBukuAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params.length < 8) {
                return null;
            }

            String judul = params[0];
            String pengarang = params[1];
            String penerbit = params[2];
            String tahunTerbit = params[3];
            String deskripsi = params[4];
            String kategori = params[5];
            String perpusId = params[6];
            String imagePath = params[7];

            try {
                URL url = new URL(api+"/buku");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----Boundary");

                String boundary = "----Boundary";
                String lineEnd = "\r\n";

                File imageFile = new File(imagePath);
                FileInputStream fileInputStream = new FileInputStream(imageFile);

                String data = "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"judul\"" + lineEnd +
                        lineEnd +
                        judul + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"pengarang\"" + lineEnd +
                        lineEnd +
                        pengarang + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"penerbit\"" + lineEnd +
                        lineEnd +
                        penerbit + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"tahun_terbit\"" + lineEnd +
                        lineEnd +
                        tahunTerbit + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"deskripsi\"" + lineEnd +
                        lineEnd +
                        deskripsi + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"kategori\"" + lineEnd +
                        lineEnd +
                        kategori + lineEnd +
                        "------Boundary" + lineEnd +
                        "Content-Disposition: form-data; name=\"perpus_id\"" + lineEnd +
                        lineEnd +
                        perpusId + lineEnd +
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
}
