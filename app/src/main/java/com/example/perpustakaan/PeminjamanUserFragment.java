package com.example.perpustakaan;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.perpustakaan.adapter.BukuAdapter;
import com.example.perpustakaan.adapter.BukuDipinjamAdapter;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeminjamanUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeminjamanUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int perpusId;
    private ArrayList<Integer> selectedBookIds;
    private String api = BuildConfig.API;
    private View view;

    public PeminjamanUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeminjamanUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeminjamanUserFragment newInstance(String param1, String param2) {
        PeminjamanUserFragment fragment = new PeminjamanUserFragment();
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
            selectedBookIds = getArguments().getIntegerArrayList("selectedBookIds");
            perpusId = getArguments().getInt("perpusId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_peminjaman_user, container, false);

        Log.d("PeminjamanUserFragment", "Selected Book IDs: " + selectedBookIds);
        Log.d("PeminjamanUserFragment", "Perpus ID: " + perpusId);

        new FetchBookData().execute(api + "/perpus/" + perpusId);

        return view;
    }

    private class FetchBookData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length == 0) return null;
            String apiUrl = urls[0];

            try {
                // URL dari API
                URL url = new URL(apiUrl);

                // Buat koneksi HTTP
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Baca data dari input stream
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                // Tutup stream dan koneksi
                bufferedReader.close();
                inputStream.close();
                urlConnection.disconnect();

                // Kembalikan data yang telah diambil dari API
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

                    // Pemilihan buku dengan ID yang sesuai dengan selectedBookIds
                    for (int i = 0; i < bukusArray.length(); i++) {
                        JSONObject bukuObject = bukusArray.getJSONObject(i);
                        int bukuId = bukuObject.getInt("buku_id");

                        if (selectedBookIds.contains(bukuId)) {
                            selectedBooks.add(bukuObject);
                            Log.d("PeminjamanUserFragment", "Buku ID: " + bukuId + ", Judul: " + bukuObject.getString("judul"));
                        }
                    }

// Inisialisasi RecyclerView
                    RecyclerView recyclerView = view.findViewById(R.id.rvbukudipinjam);

// Atur GridLayoutManager dengan 2 kolom
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);

// Buat adapter dan atur ke RecyclerView
                    BukuDipinjamAdapter bukuAdapter = new BukuDipinjamAdapter(selectedBooks);
                    recyclerView.setAdapter(bukuAdapter);

// Notify adapter about data changes
                    bukuAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Gagal mengambil data dari API", Toast.LENGTH_SHORT).show();
            }
        }

    }

}