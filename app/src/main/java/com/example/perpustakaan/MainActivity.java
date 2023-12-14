package com.example.perpustakaan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.example.perpustakaan.Util.HttpHandler;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btnWelkom;
    BottomSheetDialog dialog;

    private EditText inpEmail, inpPassword;
    private Button btnMasuk;

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

    private void processJSONData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            // Lakukan sesuatu dengan data JSON, seperti login
            // ...
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                String email = inpEmail.getText().toString().trim();
                String password = inpPassword.getText().toString().trim();
                
                if (!email.isEmpty() && !password.isEmpty()) {
                    dialog.dismiss();
                    new LoginAsyncTask().execute(email, password);
                } else {
                    Toast.makeText(MainActivity.this, "Isi email dan password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String response = "";

            try {
                URL url = new URL("http://8.219.70.58:5988/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Membuat objek JSON untuk dikirim ke server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", email);
                jsonObject.put("password", password);

                // Mengirim data JSON ke server
                conn.getOutputStream().write(jsonObject.toString().getBytes());

                // Menerima respons dari server
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            loginProcess(response);
        }
    }

    private void loginProcess(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean isLoginSuccessful = jsonResponse.getBoolean("success");

            if (isLoginSuccessful) {
                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Pastikan finish() dipanggil setelah startActivity()
            } else {
                Toast.makeText(this, "Login gagal, silakan coba lagi", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public void showRegisterDialog() {
        // Tampilkan dialog register (bsdregister)
        dialog.setContentView(R.layout.bsdregister);
        Button btnLanjut = dialog.findViewById(R.id.btnlanjut);
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRegister2Dialog();
            }
        });
        dialog.show();
    }

    public void showRegister2Dialog() {
        // Tampilkan dialog register2 (bsdregister2)
        dialog.setContentView(R.layout.bsdregister2);
        Button btnLanjut2 = dialog.findViewById(R.id.btnlanjut2);
        btnLanjut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRegisterDoneDialog();
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

}