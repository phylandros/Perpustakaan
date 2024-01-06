package com.example.perpustakaan.adapter;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.BuildConfig;
import com.example.perpustakaan.R;
import com.example.perpustakaan.model.Peminjaman;
import com.example.perpustakaan.model.Verifikasi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PeminjamanAdapter extends RecyclerView.Adapter<PeminjamanAdapter.PeminjamanViewHolder> {
    private List<Peminjaman> peminjamanList;

    public PeminjamanAdapter(List<Peminjaman> peminjamanList) {
        this.peminjamanList = peminjamanList;
    }

    @NonNull
    @Override
    public PeminjamanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peminjaman, parent, false);
        return new PeminjamanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeminjamanViewHolder holder, int position) {
        Peminjaman peminjaman = peminjamanList.get(position);
        holder.bind(peminjaman);
    }

    @Override
    public int getItemCount() {
        return peminjamanList.size();
    }

    public class PeminjamanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView namaTextView;
        private TextView bukuTextView;
        private TextView tanggalTextView;
        private String api = BuildConfig.API;

        public PeminjamanViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTextView = itemView.findViewById(R.id.namaTextView);
            bukuTextView = itemView.findViewById(R.id.bukuTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
        }

        public void bind(Peminjaman peminjaman) {
            namaTextView.setText(peminjaman.getNamaUser());
            bukuTextView.setText(peminjaman.getNamaBuku());
            tanggalTextView.setText(peminjaman.getTanggalKembali());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION) {
//                if (v.getId() == R.id.accpinjaman) {
//                    Peminjaman peminjaman = peminjamanList.get(position);
//                    int pinjamId = getPinjamIdFromPeminjaman(peminjaman); // Fungsi untuk mendapatkan pinjam_id
//                    if (pinjamId != 0) {
//                        sendPatchRequest(pinjamId);
//                    }
//                    Toast.makeText(itemView.getContext(), "Peminjaman Disetujui", Toast.LENGTH_SHORT).show();
//                } else if (v.getId() == R.id.cancelpeminjaman) {
//                    Peminjaman peminjaman = peminjamanList.get(position);
//                    int pinjamId = getPinjamIdFromPeminjaman(peminjaman); // Fungsi untuk mendapatkan pinjam_id
//                    if (pinjamId != 0) {
//                        sendDeleteRequest(pinjamId);
//                    }
//                    Toast.makeText(itemView.getContext(), "Peminjaman Dibatalkan", Toast.LENGTH_SHORT).show();
//                }
//            }
        }
        private int getPinjamIdFromPeminjaman(Peminjaman peminjaman) {
            return peminjaman.getPinjamId();
        }

        private void sendPatchRequest(int pinjamId) {
            new AsyncTask<Integer, Void, Integer>() {
                @Override
                protected Integer doInBackground(Integer... params) {
                    int pinjamId = params[0];
                    try {
                        URL url = new URL(api+"/pinjam/" + pinjamId);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("PATCH");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");

                        // Tambahkan payload PATCH di sini jika diperlukan
                        String payload = "{\"isVerif\": 1}";
                        OutputStream os = connection.getOutputStream();
                        os.write(payload.getBytes());
                        os.flush();
                        os.close();

                        int responseCode = connection.getResponseCode();
                        connection.disconnect();

                        return responseCode;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Integer responseCode) {
//                    if (responseCode != null) {
//                        if (responseCode == HttpURLConnection.HTTP_OK) {
//                            Toast.makeText(itemView.getContext(), "Respon Peminjaman Berhasil", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(itemView.getContext(), "Respon Peminjaman Dibatalkan", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(itemView.getContext(), "Ada kesalahan dalam proses", Toast.LENGTH_SHORT).show();
//                    }
                }
            }.execute(pinjamId);
        }

        private void sendDeleteRequest(int pinjamId) {
            new AsyncTask<Integer, Void, Integer>() {
                @Override
                protected Integer doInBackground(Integer... params) {
                    int pinjamId = params[0];
                    try {
                        URL url = new URL(api + "/pinjam/" + pinjamId);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("DELETE");

                        int responseCode = connection.getResponseCode();
                        connection.disconnect();

                        return responseCode;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Integer responseCode) {
                    if (responseCode != null) {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Toast.makeText(itemView.getContext(), "Peminjaman Berhasil Dibatalkan", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(itemView.getContext(), "Gagal Membatalkan Peminjaman", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "Terjadi kesalahan dalam proses", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(pinjamId);
        }


    }
}