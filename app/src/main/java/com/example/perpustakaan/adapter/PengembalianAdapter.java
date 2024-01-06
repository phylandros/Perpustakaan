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
import com.example.perpustakaan.model.Pengembalian;
import com.example.perpustakaan.model.Verifikasi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PengembalianAdapter extends RecyclerView.Adapter<PengembalianAdapter.PengembalianViewHolder> {
    private List<Pengembalian> pengembalianList;

    public PengembalianAdapter(List<Pengembalian> pengembalianList) {
        this.pengembalianList = pengembalianList;
    }

    @NonNull
    @Override
    public PengembalianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengembalian, parent, false);
        return new PengembalianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PengembalianViewHolder holder, int position) {
        Pengembalian pengembalian = pengembalianList.get(position);
        holder.bind(pengembalian);
    }

    @Override
    public int getItemCount() {
        return pengembalianList.size();
    }

    public class PengembalianViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView namaTextView;
        private TextView bukuTextView;
        private TextView tanggalTextView;
        private LinearLayout accPinjaman;
        private String api = BuildConfig.API;

        public PengembalianViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTextView = itemView.findViewById(R.id.namaTextView);
            bukuTextView = itemView.findViewById(R.id.bukuTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            accPinjaman = itemView.findViewById(R.id.accpinjaman);

            accPinjaman.setOnClickListener(this);
        }

        public void bind(Pengembalian pengembalian) {
            namaTextView.setText(pengembalian.getNamaUser());
            bukuTextView.setText(pengembalian.getNamaBuku());
            tanggalTextView.setText(pengembalian.getTanggalKembali());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.accpinjaman) {
                    Pengembalian pengembalian = pengembalianList.get(position);
                    int pinjamId = getPinjamIdFromPengembalian(pengembalian); // Fungsi untuk mendapatkan pinjam_id
                    if (pinjamId != 0) {
                        sendDeleteRequest(pinjamId);
                    }
                    Toast.makeText(itemView.getContext(), "Peminjaman Disetujui", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private int getPinjamIdFromPengembalian(Pengembalian pengembalian) {
            return pengembalian.getPinjamId();
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
                            Toast.makeText(itemView.getContext(), "Pengembalian Berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(itemView.getContext(), "Gagal mengembalikan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "Terjadi kesalahan dalam proses", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(pinjamId);
        }


    }
}