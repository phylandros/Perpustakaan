package com.example.perpustakaan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpustakaan.adapter.AdapterLocation;
import com.example.perpustakaan.adapter.BukuAdapter;
import com.example.perpustakaan.model.BukuModel;
import com.example.perpustakaan.model.LocationDataModel;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListBukuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListBukuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private Integer perpusId;
    private String userid;
    private String api = BuildConfig.API;
    private RecyclerView recyclerView;

    private List<BukuModel> dataList = new ArrayList<>();
    private BukuAdapter adapter;
    private Bundle bundle;
    public ListBukuFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListBukuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListBukuFragment newInstance(String param1, String param2) {
        ListBukuFragment fragment = new ListBukuFragment();
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

        view = inflater.inflate(R.layout.fragment_list_buku, container, false);
        // Inflate the layout for this fragment

        Button btnPinjam = view.findViewById(R.id.btnpinjambukuuser);


        bundle = getArguments();
        if (bundle != null){
            perpusId = bundle.getInt("perpusId",0);
            userid = bundle.getString("userid","");
            new FetchListBukuTask().execute(api+"/perpus/"+ perpusId);

        }

        btnPinjam.setOnClickListener(v -> {
            Integer[] selectedBookIds = adapter.getSelectedBukuIds().toArray(new Integer[0]);

            if (selectedBookIds.length == 0) {
                Toast.makeText(requireContext(), "Tidak ada buku yang dipilih.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(requireActivity(), PeminjamanUserActivity.class);
                intent.putIntegerArrayListExtra("selectedBookIds", new ArrayList<>(Arrays.asList(selectedBookIds)));
                intent.putExtra("perpusId", perpusId);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });


        return view;

    }

    private BukuModel findBookById(int bookId) {
        for (BukuModel book : dataList) {
            if (book.getBukuid() == bookId) {
                return book;
            }
        }
        return null;
    }


    public class FetchListBukuTask extends AsyncTask<String, Void, String> {

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

                BukuModel buku = new BukuModel(bukus,image, name, deskripsi, selected);
                dataList.add(buku);
            }

            // Tampilkan data yang dihasilkan dalam RecyclerView
            getActivity().runOnUiThread(() -> {
                RecyclerView recyclerView = view.findViewById(R.id.listbuku);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                adapter = new BukuAdapter(requireContext(),dataList);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemSelectedListener((selectedIds) -> {
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
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                });
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}