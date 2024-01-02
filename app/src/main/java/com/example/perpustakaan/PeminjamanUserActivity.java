package com.example.perpustakaan;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.perpustakaan.adapter.BukuDipinjamAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PeminjamanUserActivity extends AppCompatActivity {

    private int perpusId;
    private ArrayList<Integer> selectedBookIds;
    private String api = BuildConfig.API;
    private String userid;
    private List<Integer> pinjamIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjaman_user);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedBookIds = bundle.getIntegerArrayList("selectedBookIds");
            perpusId = bundle.getInt("perpusId");
            userid = bundle.getString("userid");
        }

        Button tambahPinjamButton = findViewById(R.id.tambahpinjam);
        tambahPinjamButton.setOnClickListener(v -> {
            EditText editTextTanggalKembali = findViewById(R.id.tglkembalipinjam);
            String tanggalKembali = editTextTanggalKembali.getText().toString();
            postDataToAPI(selectedBookIds, userid, perpusId, tanggalKembali);
            Toast.makeText(getApplicationContext(), "Berhasil ditambahkan ke list peminjaman", Toast.LENGTH_SHORT).show();
            tambahPinjamButton.setEnabled(false);
            tambahPinjamButton.setBackgroundResource(R.drawable.shapedisable);
        });

        new FetchBookData().execute(api + "/perpus/" + perpusId);

        ImageView btnRefresh = findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinjamIds.size() > 0) {
                    for (Integer pinjamId : pinjamIds) {
                        new FetchPinjamData().execute(api + "/pinjam/" + pinjamId);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada peminjaman untuk diperiksa", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class FetchBookData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            if (urls.length == 0) return null;
            String apiUrl = urls[0];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                urlConnection.disconnect();

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray bukusArray = jsonObject.getJSONArray("bukus");

                    List<JSONObject> selectedBooks = new ArrayList<>();

                    for (int i = 0; i < bukusArray.length(); i++) {
                        JSONObject bukuObject = bukusArray.getJSONObject(i);
                        int bukuId = bukuObject.getInt("buku_id");

                        if (selectedBookIds.contains(bukuId)) {
                            selectedBooks.add(bukuObject);
                        }
                    }

                    RecyclerView recyclerView = findViewById(R.id.rvbukudipinjam);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);

                    BukuDipinjamAdapter bukuAdapter = new BukuDipinjamAdapter(selectedBooks);
                    recyclerView.setAdapter(bukuAdapter);
                    bukuAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Gagal mengambil data dari API", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postDataToAPI(ArrayList<Integer> selectedBookIds, String user, int perpus, String tanggalKembali) {
        for (Integer bukuId : selectedBookIds) {
            JSONObject postData = createPostData(user, perpus, tanggalKembali, bukuId);

            new PostDataToAPI().execute(api + "/pinjam", postData.toString());
        }
    }

    private class PostDataToAPI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (params.length < 2) return null;

            String apiUrl = params[0];
            String postData = params[1];
            String responseResult = null;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                urlConnection.getOutputStream().write(postData.getBytes());

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                inputStream.close();

                responseResult = stringBuilder.toString();

                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String message = jsonResponse.getString("message");
                    JSONObject data = jsonResponse.getJSONObject("data");

                    int pinjamId = data.getInt("pinjam_id");
                    pinjamIds.add(pinjamId);

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    // Lakukan apapun yang diperlukan dengan pinjamId
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Gagal membuat peminjaman", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private JSONObject createPostData(String user, int perpus, String tanggalKembali, int bukuId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());

        JSONObject postData = new JSONObject();
        try {
            postData.put("tanggal_pinjam", currentDate);
            postData.put("tanggal_kembali", tanggalKembali);
            postData.put("keterangan", "Belum di verifikasi");
            postData.put("user_id", user);
            postData.put("perpus_id", perpus);
            postData.put("buku_id", bukuId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postData;
    }

    private class FetchPinjamData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length == 0) return null;
            String apiUrl = urls[0];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                urlConnection.disconnect();

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    int isVerif = jsonResponse.getInt("isVerif");

                    TextView textVerif = findViewById(R.id.textverif);
                    if (isVerif == 1) {
                        textVerif.setText("Berhasil di verifikasi");
                        textVerif.setTextColor(Integer.parseInt("#65B741"));
                    } else {
                        textVerif.setText("Belum di verifikasi");
                        textVerif.setTextColor(Integer.parseInt("#B80000"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Gagal mengambil data pinjaman", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
