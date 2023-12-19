package com.example.perpustakaan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
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
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView txtUserName;
    TextView txtNipPerpus;
    TextView txtKtp;
    TextView txtAlamat;
    TextView txtUserEmail;
    TextView txtUserPassword;

    String emailused;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String userId = bundle.getString("userid", "");

            txtUserName = view.findViewById(R.id.nampeng);
            txtUserName.setText(userId);

            // Gunakan userId untuk membuat URL
            String apiUrl = "http://8.219.70.58:5988/users/" + userId;

            // Sekarang, lakukan permintaan HTTP ke API dengan URL yang sudah dibuat
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

        Button btnLogout = view.findViewById(R.id.btnlogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Tambahkan class AsyncTask di dalam ProfileFragment

    private class FetchUserDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null) {
                return null;
            }

            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strings[0]); // Mengambil URL dari parameter AsyncTask
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Parsing JSON dan menampilkan data di sini
            try {
                JSONObject jsonObject = new JSONObject(s);

                // Ambil nilai yang dibutuhkan dari objek JSON dengan pengecekan agar tidak null
                String userName = jsonObject.has("name") ? jsonObject.optString("name") : "";
                JSONObject biodata = jsonObject.optJSONObject("biodata");
                String nipPerpus = (biodata != null && biodata.has("nip_perpus")) ? biodata.optString("nip_perpus") : "";
                String ktp = (biodata != null && biodata.has("ktp")) ? biodata.optString("ktp") : "";
                String alamat = (biodata != null && biodata.has("alamat")) ? biodata.optString("alamat") : "";
                String userEmail = jsonObject.has("email") ? jsonObject.optString("email") : "";
                String userPassword = jsonObject.has("password") ? jsonObject.optString("password") : "";

                // Tampilkan nilai-nilai ini dalam TextView yang sesuai
                txtUserName = getView().findViewById(R.id.nampeng);
                txtNipPerpus = getView().findViewById(R.id.noang);
                txtKtp = getView().findViewById(R.id.noktp);
                txtAlamat = getView().findViewById(R.id.alamat);
                txtUserEmail = getView().findViewById(R.id.email);
                txtUserPassword = getView().findViewById(R.id.password);

                // Set nilai TextView sesuai data yang didapat
                txtUserName.setText(userName);
                txtNipPerpus.setText(nipPerpus);
                txtKtp.setText(ktp);
                txtAlamat.setText(alamat);
                txtUserEmail.setText(userEmail);
                txtUserPassword.setText(userPassword);
            } catch (JSONException e) {
                e.printStackTrace();
                // Tambahkan penanganan kesalahan jika terjadi kesalahan dalam pengolahan JSON
            }
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Panggil AsyncTask untuk mengambil data saat fragment dibuat
        new FetchUserDataTask().execute();
    }

}