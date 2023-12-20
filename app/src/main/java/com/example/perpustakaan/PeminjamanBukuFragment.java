package com.example.perpustakaan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeminjamanBukuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeminjamanBukuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String userId;
    View view;
    public PeminjamanBukuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeminjamanBukuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeminjamanBukuFragment newInstance(String param1, String param2) {
        PeminjamanBukuFragment fragment = new PeminjamanBukuFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_peminjaman_buku, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("userid", "");
            new FetchUserDataTask().execute("http://8.219.70.58:5988/users/" + userId);

        }

        LinearLayout toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke HomeActivity
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });

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
                JSONObject biodataObject = jsonObject.getJSONObject("biodata");
                String nipPerpus = biodataObject.getString("nip_perpus");

                TextView txNamapengguna = view.findViewById(R.id.nampengpem);
                TextView txNoanggota = view.findViewById(R.id.noangpem);

                txNamapengguna.setText(name);
                txNoanggota.setText(nipPerpus);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}