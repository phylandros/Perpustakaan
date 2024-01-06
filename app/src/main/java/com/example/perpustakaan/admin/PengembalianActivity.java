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
import com.example.perpustakaan.adapter.PengembalianAdapter;
import com.example.perpustakaan.model.Pengembalian;
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

public class PengembalianActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PengembalianAdapter adapter;
    private String api = BuildConfig.API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengembalian);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.rvpengembalian);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchDataTask().execute(api+"/pinjam"); // Ganti dengan URL API Anda

        LinearLayout btnback = findViewById(R.id.backpengembalian);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PengembalianActivity.this, HomeActivity.class);
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

    private class FetchDataTask extends AsyncTask<String, Void, List<Pengembalian>> {

        @Override
        protected List<Pengembalian> doInBackground(String... urls) {
            List<Pengembalian> pengembalianList = new ArrayList<>();
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
                        Pengembalian pengembalian = new Pengembalian();
                        pengembalian.setPinjamId(Integer.valueOf(pinjamId));
                        pengembalian.setNamaUser(namaUser);
                        pengembalian.setNamaBuku(namaBuku);
                        pengembalian.setTanggalKembali(tanggalKembali);
                        pengembalianList.add(pengembalian);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return pengembalianList;
        }

        @Override
        protected void onPostExecute(List<Pengembalian> pengembalianList) {
            adapter = new PengembalianAdapter(pengembalianList);
            recyclerView.setAdapter(adapter);
        }
    }
}