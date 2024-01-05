package com.example.perpustakaan.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;
import com.example.perpustakaan.adapter.AdapterLocation;
import com.example.perpustakaan.adapter.PustakawanAdapter;
import com.example.perpustakaan.admin.VerifikasiActivity;
import com.example.perpustakaan.model.LocationDataModel;
import com.example.perpustakaan.model.PustakawanModel;
import com.squareup.picasso.Picasso;

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
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    String userid, accesstoken, role;
    private Integer perpusId;
    private String api = BuildConfig.API;
    private RecyclerView recyclerView;
    private PustakawanAdapter pustakawanAdapter;
    private List<PustakawanModel> pustakawanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        Intent intent = getIntent();
        if (intent != null) {
            userid = intent.getStringExtra("userid");
            accesstoken = intent.getStringExtra("accessToken");
            new FetchUserDataTask().execute(api+"/users/" + userid);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchData().execute(api+"/perpus");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Toolbar
        LinearLayout toolbar = findViewById(R.id.toolbarhome);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke ProfileFragment
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                // Sisipkan data yang diperlukan ke fragment jika diperlukan
                bundle.putString("userid", userid);
                bundle.putString("accessToken", accesstoken);
                profileFragment.setArguments(bundle);

                // Ganti fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.coordinator_home, profileFragment);
                fragmentTransaction.addToBackStack(null); // Jika ingin kembali ke fragment sebelumnya
                fragmentTransaction.commit();
            }
        });

        // Setup RecyclerView untuk Pustakawan
        recyclerView = findViewById(R.id.pustakawan);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        pustakawanAdapter = new PustakawanAdapter(this, pustakawanList);
        pustakawanList.add(new PustakawanModel("Buku", R.drawable.buku));
        pustakawanList.add(new PustakawanModel("Verifikasi", R.drawable.verified));
        pustakawanList.add(new PustakawanModel("Perpustakaan", R.drawable.library));
        pustakawanAdapter.notifyDataSetChanged(); // Populasi data Pustakawan (sudah ada di dalam adapter)
        recyclerView.setAdapter(pustakawanAdapter);


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
                role = jsonObject.getString("role");
                TextView txNamapengguna = findViewById(R.id.namauser);
                txNamapengguna.setText(name);
                String imagePath = jsonObject.getJSONObject("biodata").getString("gambar");
                String[] pathParts = imagePath.split("\\\\"); // Melakukan split path berdasarkan backslash
                String imageName = pathParts[pathParts.length - 1];

                ImageView imageUser = findViewById(R.id.imageuser);
//                imageUser.setImageResource(api+"/profile/"+imageName);
                Picasso.get().load(api+"/profile/"+imageName).into(imageUser);

                LinearLayout dashAnggota = findViewById(R.id.dashanggota);
                LinearLayout dashPustakawan = findViewById(R.id.dashpustakawan);

                if (role.equals("anggota")) {
                    dashAnggota.setVisibility(View.VISIBLE);
                    dashPustakawan.setVisibility(View.GONE);
                    new FetchPerpusDataTask().execute(api+"/perpus/"+ perpusId);
                    new FetchData().execute(api+"/perpus");
                } else if (role.equals("admin")) {
                    dashAnggota.setVisibility(View.GONE);
                    dashPustakawan.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class FetchPerpusDataTask extends AsyncTask<String, Void, String> {

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
                TextView txNamapengguna = findViewById(R.id.namauser);
                txNamapengguna.setText(name);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class FetchData extends AsyncTask<String, Void, String> {

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
            JSONArray jsonArray = new JSONArray(jsonData);

            List<LocationDataModel> dataList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Integer perpusid = jsonObject.getInt("perpus_id");
                String name = jsonObject.getString("nama");
                String address = jsonObject.getString("alamat");
                String operatingHours = jsonObject.getString("jam_operasional");

                String imagePath = jsonObject.getString("gambar");
                String[] pathParts = imagePath.split("\\\\"); // Melakukan split path berdasarkan backslash
                String imageName = pathParts[pathParts.length - 1];

                dataList.add(new LocationDataModel(api+"/perpus/"+imageName, perpusid, name, address, operatingHours));
            }

            runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.perpustakaan);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                AdapterLocation adapter = new AdapterLocation(dataList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new AdapterLocation.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        LocationDataModel clickedItem = dataList.get(position);
                        int perpusId = clickedItem.getPerpusid();
                        String perpusnama = clickedItem.getTitle();

                        // Pindah ke MapActivity dan kirim perpusId sebagai argumen
                        Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                        intent.putExtra("perpusId", perpusId);
                        intent.putExtra("nama", perpusnama);
                        intent.putExtra("userid", userid);
                        startActivity(intent);
                    }
                });

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
