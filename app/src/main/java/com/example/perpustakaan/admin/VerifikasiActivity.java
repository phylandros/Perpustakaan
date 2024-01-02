package com.example.perpustakaan.admin;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        recyclerView = findViewById(R.id.rvverifikasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FetchDataTask().execute("http://8.219.70.58:5988/pinjam"); // Ganti dengan URL API Anda

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
