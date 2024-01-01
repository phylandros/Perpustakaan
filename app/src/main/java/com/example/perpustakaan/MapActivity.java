package com.example.perpustakaan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class MapActivity extends AppCompatActivity {

    private int perpusId;
    private String namaPerpus, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (getIntent() != null) {
            perpusId = getIntent().getIntExtra("perpusId", 0);
            namaPerpus = getIntent().getStringExtra("nama");
            userid = getIntent().getStringExtra("userid");
        }

        WebView webView = findViewById(R.id.webView);
        String searchQuery = namaPerpus ; // Menambahkan "lokasi" agar hasil pencarian lebih spesifik
        String url = "https://www.google.com/maps/search/" + Uri.encode(searchQuery);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        Button btnLanjut = findViewById(R.id.btnlnjt);
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Di sini Anda membuat instance dari ListBukuActivity dan menavigasi ke sana
                Intent intent = new Intent(MapActivity.this, ListBukuActivity.class);
                intent.putExtra("perpusId", perpusId); // Menambahkan perpusId ke Intent
                intent.putExtra("nama", namaPerpus); // Menambahkan namaPerpus ke Intent
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        LinearLayout toolbarMap = findViewById(R.id.toolbar_map);
        toolbarMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke HomeActivity
                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
                finish(); // Tutup MapActivity agar ketika di-back tidak kembali lagi ke sini
            }
        });
    }
}
