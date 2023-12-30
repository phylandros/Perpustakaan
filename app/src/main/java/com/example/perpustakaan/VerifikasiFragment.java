package com.example.perpustakaan;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.perpustakaan.adapter.VerifikasiAdapter;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifikasiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifikasiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private RecyclerView rvVerifikasi;
    private List<String> dataNama;
    private List<String> dataBuku;
    private List<String> dataTanggal;


    public VerifikasiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifikasiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifikasiFragment newInstance(String param1, String param2) {
        VerifikasiFragment fragment = new VerifikasiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verifikasi, container, false);
        rvVerifikasi = view.findViewById(R.id.rvverifikasi);
        rvVerifikasi.setLayoutManager(new LinearLayoutManager(getContext()));

        dataNama = new ArrayList<>();
        dataBuku = new ArrayList<>();
        dataTanggal = new ArrayList<>();

        new FetchDataTask().execute("http://8.219.70.58:5988/pinjam");

        return view;
    }

    private class FetchDataTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... urls) {
            JSONArray jsonArray = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                jsonArray = new JSONArray(builder.toString());
                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null) {
                // Ambil informasi yang dibutuhkan dari JSON
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // Mengambil nilai dari tanggal kembali
                        String tanggalKembali = jsonObject.getString("tanggal_kembali");

                        // Mengambil nilai dari user
                        JSONObject userObject = jsonObject.getJSONObject("user");
                        String namaUser = userObject.getString("name");

                        // Mengambil nilai dari buku
                        JSONObject bukuObject = jsonObject.getJSONObject("buku");
                        String judulBuku = bukuObject.getString("judul");
                        // Tambahkan ke list dataNama, dataBuku, dan dataTanggal
                        dataNama.add(namaUser);
                        dataBuku.add(judulBuku);
                        dataTanggal.add(tanggalKembali);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Set adapter ke RecyclerView dengan ID rvverifikasi
                VerifikasiAdapter adapter = new VerifikasiAdapter(dataNama, dataBuku, dataTanggal);
                rvVerifikasi.setAdapter(adapter);
            }
        }
    }
}