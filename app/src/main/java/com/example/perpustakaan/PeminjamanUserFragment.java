package com.example.perpustakaan;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String userid;
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
            userid = getArguments().getString("userid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_peminjaman_user, container, false);
        new FetchBookData().execute(api + "/perpus/" + perpusId);

        Button tambahPinjamButton = view.findViewById(R.id.tambahpinjam);
        tambahPinjamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil daftar buku_id dari adapter RecyclerView atau dari sumber lain
                postDataToAPI(selectedBookIds, userid, perpusId);
                Toast.makeText(requireContext(), "Berhasil di tambahkan ke list peminjaman", Toast.LENGTH_SHORT).show();
                tambahPinjamButton.setEnabled(false);
                tambahPinjamButton.setBackgroundResource(R.drawable.shapedisable);
            }
        });

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

                    RecyclerView recyclerView = view.findViewById(R.id.rvbukudipinjam);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);

                    BukuDipinjamAdapter bukuAdapter = new BukuDipinjamAdapter(selectedBooks);
                    recyclerView.setAdapter(bukuAdapter);

                    bukuAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Gagal mengambil data dari API", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void postDataToAPI(ArrayList<Integer> selectedBookIds, String user, int perpus) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        EditText editTextTanggalKembali = view.findViewById(R.id.tglkembalipinjam);
        String tanggalKembali = editTextTanggalKembali.getText().toString();

        for (Integer bukuId : selectedBookIds) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("tanggal_pinjam", currentDate);
                postData.put("tanggal_kembali", tanggalKembali);
                postData.put("keterangan", "Belum di verifikasi");
                postData.put("user_id", user);
                postData.put("perpus_id", perpus);
                postData.put("buku_id", bukuId.toString()); // Ubah bukuId ke string
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new PostDataToAPI().execute(api + "/pinjam", postData.toString());
        }
    }


    private class PostDataToAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            if (params.length < 2) return null;

            String apiUrl = params[0];
            String postData = params[1];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Kirim data JSON
                urlConnection.getOutputStream().write(postData.getBytes());

                // Mendapatkan respons dari server (jika diperlukan)
                int responseCode = urlConnection.getResponseCode();

                // Tambahkan log atau respons handling di sini jika diperlukan

                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}