package com.example.perpustakaan;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.perpustakaan.adapter.PustakawanAdapter;
import com.example.perpustakaan.model.PustakawanModel;

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
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    String userid, accesstoken, role;
    Integer perpusId;
    private String api = BuildConfig.API;
    private RecyclerView recyclerView;
    private PustakawanAdapter pustakawanAdapter;
    private List<PustakawanModel> pustakawanList = new ArrayList<>(); // Data pustakawan yang akan ditampilkan


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static HomeFragment newInstance(String userId, String accessToken) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("userid", userId);
        args.putString("accessToken", accessToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userid = bundle.getString("userid", "");
            new FetchUserDataTask().execute(api+"/users/" + userid);
            new FetchPerpusDataTask().execute(api+"/perpus/"+ perpusId);

        }

        LinearLayout toolbar = view.findViewById(R.id.toolbarContainer);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userid", userid); // Mengirim data userid ke ProfileFragment
                bundle.putString("accessToken", accesstoken);
                profileFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_home, profileFragment); // Menggunakan profileFragment yang sudah di-set dengan bundle
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        recyclerView = view.findViewById(R.id.pustakawan);
        int numberOfColumns = 2; // Menentukan jumlah kolom
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        pustakawanAdapter = new PustakawanAdapter(getContext(), pustakawanList);
        recyclerView.setAdapter(pustakawanAdapter);
        pustakawanList.add(new PustakawanModel("Buku", R.drawable.image));
        pustakawanList.add(new PustakawanModel("Verifikasi", R.drawable.image));
        pustakawanList.add(new PustakawanModel("Peminjaman", R.drawable.image));
        pustakawanList.add(new PustakawanModel("Pengembalian", R.drawable.image));
        pustakawanAdapter.notifyDataSetChanged();


        return view;
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
                TextView txNamapengguna = view.findViewById(R.id.namauser);
                txNamapengguna.setText(name);

                LinearLayout dashAnggota = view.findViewById(R.id.dashanggota);
                LinearLayout dashPustakawan = view.findViewById(R.id.dashpustakawan);

                    if (role.equals("anggota")) {
                        dashAnggota.setVisibility(View.VISIBLE);
                        dashPustakawan.setVisibility(View.GONE);
                    } else if (role.equals("pustakawan")) {
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
                TextView txNamapengguna = view.findViewById(R.id.namauser);
                txNamapengguna.setText(name);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}