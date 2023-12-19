package com.example.perpustakaan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btnWelkom;
    BottomSheetDialog dialog;

    private EditText inpEmail, inpPassword;
    private Button btnMasuk;

    EditText namaReg, emailReg, passReg, conpassReg, noktpReg, alamatReg, notelReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnWelkom = findViewById(R.id.btnWelkom);
        dialog = new BottomSheetDialog(this);

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
                URL url = new URL("http://8.219.70.58:5988/login");
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
                        String accessToken = jsonResponse.getString("accessToken");
                        if (accessToken != null && !accessToken.isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", accessToken);
                            editor.apply();
                            JSONObject data = jsonResponse.getJSONObject("data");
                            if (data.has("name") && data.has("email") && data.has("userId")) {
                                String name = data.getString("name");
                                String email = data.getString("email");
                                String userId = data.getString("userId");
                                editor.putString("name", name);
                                editor.putString("email", email);
                                editor.putString("userid", userId);
                                editor.apply();
                            }
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                    else {
                        Toast.makeText(MainActivity.this, "Gagal mendapatkan accessToken", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Parse Error", "Error parsing JSON response");
                }
            } else {
                Toast.makeText(MainActivity.this,"Username dan password salah",Toast.LENGTH_SHORT).show();
                Log.e("Connection Error", "No data received from server");
            }
        }
    }

    private class PostRegisterAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {
                URL url = new URL("http://8.219.70.58:5988/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject postData = new JSONObject();
                postData.put("nama", params[0]);
                postData.put("email", params[1]);
                postData.put("password", params[2]);
                postData.put("confPassword", params[3]);
                postData.put("gambar", "gambar"); // Menggunakan placeholder "gambar"
                postData.put("nip_perpus", String.valueOf(System.currentTimeMillis()));
                postData.put("ktp", params[4]);
                postData.put("alamat", params[5]);
                postData.put("phone", params[6]);

                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    br.close();
                } else {
                    response = "";
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle response dari server
            try {
                JSONObject jsonResponse = new JSONObject(result);
                // Lakukan sesuatu dengan response JSON dari server
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON Parse Error", "Error parsing JSON response");
            }
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
                    dialog.dismiss();
                    showRegister2Dialog(nama, email, password, conpassword);
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

        noktpReg = dialog.findViewById(R.id.etNoKtp);
        alamatReg = dialog.findViewById(R.id.etAlamat);
        notelReg = dialog.findViewById(R.id.etNotel);
        btnLanjut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ktp = noktpReg.getText().toString().trim();
                String alamat = alamatReg.getText().toString().trim();
                String notel = notelReg.getText().toString().trim();

                if (!ktp.isEmpty() && !alamat.isEmpty() && !notel.isEmpty()) {
                    new PostRegisterAsyncTask().execute(nama, email, password, conpassword, ktp, alamat, notel);
                    showRegisterDoneDialog();
                } else {
                    Toast.makeText(MainActivity.this, "Isi semua field terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
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

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }




}