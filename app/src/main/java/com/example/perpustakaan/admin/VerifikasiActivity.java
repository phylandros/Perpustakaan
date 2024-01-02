package com.example.perpustakaan.admin;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;
import com.example.perpustakaan.adapter.VerifikasiAdapter;
import com.example.perpustakaan.model.Verifikasi;

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

public class VerifikasiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VerifikasiAdapter adapter;
    private String api = BuildConfig.API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.rvverifikasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchDataTask().execute(api+"/pinjam"); // Ganti dengan URL API Anda

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchDataTask().execute(api+"/pinjam");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class FetchDataTask extends AsyncTask<String, Void, List<Verifikasi>> {

        @Override
        protected List<Verifikasi> doInBackground(String... urls) {
            List<Verifikasi> verifikasiList = new ArrayList<>();
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
                    if (isVerif == 0) {
                        JSONObject userObject = jsonObject.getJSONObject("user");
                        String namaUser = userObject.getString("name");

                        JSONObject bukuObject = jsonObject.getJSONObject("buku");
                        String namaBuku = bukuObject.getString("judul");

                        String tanggalKembali = jsonObject.getString("tanggal_kembali");
                        String pinjamId = jsonObject.getString("pinjam_id");
                        Verifikasi verifikasi = new Verifikasi();
                        verifikasi.setPinjamId(Integer.valueOf(pinjamId));
                        verifikasi.setNamaUser(namaUser);
                        verifikasi.setNamaBuku(namaBuku);
                        verifikasi.setTanggalKembali(tanggalKembali);

                        verifikasiList.add(verifikasi);
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return verifikasiList;
        }

        @Override
        protected void onPostExecute(List<Verifikasi> verifikasiList) {
            adapter = new VerifikasiAdapter(verifikasiList);
            recyclerView.setAdapter(adapter);
        }
    }

}
