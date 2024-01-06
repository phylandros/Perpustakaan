package com.example.perpustakaan.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private String userId, refreshToken;
    private String api = BuildConfig.API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userid");
            new FetchUserDataTask().execute(api+"/users/" + userId);
        }

        LinearLayout toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                homeIntent.putExtra("userid", userId);
                startActivity(homeIntent);
                finish(); // Finish this activity if desired
            }
        });

        Button btnLogout = findViewById(R.id.btnlogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LogoutTask().execute(api+"/logout");
            }
        });
    }

    private class LogoutTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String apiUrl = urls[0];
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(apiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");

                // Set headers
                urlConnection.setRequestProperty("Cookie", "refreshToken="+refreshToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    // Logout berhasil
                    Log.d("LogoutTask", "Logout berhasil");
                } else {
                    // Logout gagal
                    Log.e("LogoutTask", "Logout gagal, response code: " + responseCode);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(ProfileActivity.this,"Anda Telah Logout",Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchUserDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String apiUrl = urls[0];
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(apiUrl);
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
        protected void onPostExecute(String userData) {
            super.onPostExecute(userData);
            try {
                JSONObject jsonObject = new JSONObject(userData);
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                refreshToken = jsonObject.getString("refreshToken");
                JSONObject biodataObject = jsonObject.getJSONObject("biodata");
                String ktp = biodataObject.getString("ktp");
                String nipPerpus = biodataObject.getString("nip_perpus");
                String alamat = biodataObject.getString("alamat");
                String phone = biodataObject.getString("phone");
                String imagePath = jsonObject.getJSONObject("biodata").getString("gambar");
                String[] pathParts = imagePath.split("/");
                String imageNameuser = pathParts[pathParts.length - 1];

                ImageView imageUser = findViewById(R.id.imageuser);
                Glide.with(ProfileActivity.this)
                        .load(api+"/profile/"+imageNameuser)
                        .centerCrop()
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .into(imageUser);


                TextView txNamapengguna = findViewById(R.id.nampeng);
                TextView txNoanggota = findViewById(R.id.noang);
                TextView txNoktp = findViewById(R.id.noktp);
                TextView txAlamat = findViewById(R.id.alamat);
                TextView txEmail = findViewById(R.id.email);

                txNamapengguna.setText(name);
                txNoanggota.setText(nipPerpus);
                txNoktp.setText(ktp);
                txAlamat.setText(alamat);
                txEmail.setText(email);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
