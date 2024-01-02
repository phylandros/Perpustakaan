package com.example.perpustakaan.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;
import com.example.perpustakaan.adapter.BukuAdapter;
import com.example.perpustakaan.model.BukuModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListBukuActivity extends AppCompatActivity {
    private Integer perpusId;
    private String userid;
    private String api = BuildConfig.API;
    private RecyclerView recyclerView;
    private List<BukuModel> dataList = new ArrayList<>();
    private BukuAdapter adapter;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_buku);

        Button btnPinjam = findViewById(R.id.btnpinjambukuuser);
        bundle = getIntent().getExtras();
        if (bundle != null){
            perpusId = bundle.getInt("perpusId",0);
            userid = bundle.getString("userid","");
            new FetchListBukuTask().execute(api+"/perpus/"+ perpusId);
        }

        btnPinjam.setOnClickListener(v -> {
            Integer[] selectedBookIds = adapter.getSelectedBukuIds().toArray(new Integer[0]);

            if (selectedBookIds.length == 0) {
                Toast.makeText(ListBukuActivity.this, "Tidak ada buku yang dipilih.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ListBukuActivity.this, PeminjamanUserActivity.class);
                intent.putIntegerArrayListExtra("selectedBookIds", new ArrayList<>(Arrays.asList(selectedBookIds)));
                intent.putExtra("perpusId", perpusId);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.listbuku);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new BukuAdapter(getApplicationContext(), dataList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemSelectedListener(selectedIds -> {
            String message;
            if (selectedIds.isEmpty()) {
                message = "Tidak ada perpus yang terpilih.";
            } else {
                StringBuilder selectedPerpusMessage = new StringBuilder("Buku yang terpilih:\n");
                for (Integer bukusId : selectedIds) {
                    selectedPerpusMessage.append("- Buku ID ").append(bukusId).append("\n");
                }
                message = selectedPerpusMessage.toString().trim();
            }
            // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private class FetchListBukuTask extends AsyncTask<String, Void, String> {
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
            processJSONData(s);
        }
    }

    private void processJSONData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray bukusArray = jsonObject.getJSONArray("bukus");

            for (int i = 0; i < bukusArray.length(); i++) {
                JSONObject bukuObject = bukusArray.getJSONObject(i);
                int bukus = bukuObject.getInt("buku_id");
                int image = R.drawable.content; // Ganti dengan sumber gambar yang sesuai
                String name = bukuObject.getString("judul");
                String deskripsi = bukuObject.getString("deskripsi");
                boolean selected = false; // Default value untuk selected

                BukuModel buku = new BukuModel(bukus, image, name, deskripsi, selected);
                dataList.add(buku);
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
