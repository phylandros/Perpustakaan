package com.example.perpustakaan.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;
import com.example.perpustakaan.adapter.PeminjamanAdapter;
import com.example.perpustakaan.adapter.VerifikasiAdapter;
import com.example.perpustakaan.model.Peminjaman;
import com.example.perpustakaan.model.Verifikasi;
import com.example.perpustakaan.user.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PeminjamanAdapter adapter;
    private String api = BuildConfig.API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjaman);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.rvpeminjaman);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchDataTask().execute(api+"/pinjam"); // Ganti dengan URL API Anda

        LinearLayout btnback = findViewById(R.id.backpeminjaman);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PeminjamanActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchDataTask().execute(api+"/pinjam");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class FetchDataTask extends AsyncTask<String, Void, List<Peminjaman>> {

        @Override
        protected List<Peminjaman> doInBackground(String... urls) {
            List<Peminjaman> peminjamanList = new ArrayList<>();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int isVerif = jsonObject.getInt("isVerif");
                    if (isVerif == 1) {
                        JSONObject userObject = jsonObject.getJSONObject("user");
                        String namaUser = userObject.getString("name");

                        JSONObject bukuObject = jsonObject.getJSONObject("buku");
                        String namaBuku = bukuObject.getString("judul");

                        String tanggalKembali = jsonObject.getString("tanggal_kembali");
                        String pinjamId = jsonObject.getString("pinjam_id");
                        Peminjaman peminjaman = new Peminjaman();
                        peminjaman.setPinjamId(Integer.valueOf(pinjamId));
                        peminjaman.setNamaUser(namaUser);
                        peminjaman.setNamaBuku(namaBuku);
                        peminjaman.setTanggalKembali(tanggalKembali);
                        peminjamanList.add(peminjaman);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return peminjamanList;
        }

        @Override
        protected void onPostExecute(List<Peminjaman> peminjamanList) {
            adapter = new PeminjamanAdapter(peminjamanList);
            recyclerView.setAdapter(adapter);
        }
    }


}