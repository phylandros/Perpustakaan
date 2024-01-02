package com.example.perpustakaan.adapter;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.R;
import com.example.perpustakaan.model.Verifikasi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class VerifikasiAdapter extends RecyclerView.Adapter<VerifikasiAdapter.VerifikasiViewHolder> {
    private List<Verifikasi> verifikasiList;

    public VerifikasiAdapter(List<Verifikasi> verifikasiList) {
        this.verifikasiList = verifikasiList;
    }

    @NonNull
    @Override
    public VerifikasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verifikasi_peminjaman, parent, false);
        return new VerifikasiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifikasiViewHolder holder, int position) {
        Verifikasi verifikasi = verifikasiList.get(position);
        holder.bind(verifikasi);
    }

    @Override
    public int getItemCount() {
        return verifikasiList.size();
    }

    public class VerifikasiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView namaTextView;
        private TextView bukuTextView;
        private TextView tanggalTextView;
        private LinearLayout accPinjaman;
        private LinearLayout cancelPeminjaman;

        public VerifikasiViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTextView = itemView.findViewById(R.id.namaTextView);
            bukuTextView = itemView.findViewById(R.id.bukuTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            accPinjaman = itemView.findViewById(R.id.accpinjaman);
            cancelPeminjaman = itemView.findViewById(R.id.cancelpeminjaman);

            accPinjaman.setOnClickListener(this);
            cancelPeminjaman.setOnClickListener(this);
        }

        public void bind(Verifikasi verifikasi) {
            namaTextView.setText(verifikasi.getNamaUser());
            bukuTextView.setText(verifikasi.getNamaBuku());
            tanggalTextView.setText(verifikasi.getTanggalKembali());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.accpinjaman) {
                    Verifikasi verifikasi = verifikasiList.get(position);
                    int pinjamId = getPinjamIdFromVerifikasi(verifikasi); // Fungsi untuk mendapatkan pinjam_id

                    if (pinjamId != -1) {
                        sendPatchRequest(pinjamId);
                    }
                    Toast.makeText(itemView.getContext(), "Peminjaman Disetujui", Toast.LENGTH_SHORT).show();
                } else if (v.getId() == R.id.cancelpeminjaman) {
                    // Lakukan aksi untuk membatalkan peminjaman di sini
                    Toast.makeText(itemView.getContext(), "Peminjaman Dibatalkan", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private int getPinjamIdFromVerifikasi(Verifikasi verifikasi) {
            // Ambil pinjam_id dari objek Verifikasi
            // Ganti dengan cara Anda untuk mendapatkan pinjam_id dari objek Verifikasi
            return verifikasi.getPinjamId();
        }

        private void sendPatchRequest(int pinjamId) {
            new AsyncTask<Integer, Void, Integer>() {
                @Override
                protected Integer doInBackground(Integer... params) {
                    int pinjamId = params[0];
                    try {
                        URL url = new URL("http://8.219.70.58:5988/pinjam/" + pinjamId);
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
                    if (responseCode != null) {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Toast.makeText(itemView.getContext(), "Respon Peminjaman Berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(itemView.getContext(), "Respon Peminjaman Dibatalkan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "Ada kesalahan dalam proses", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(pinjamId);
        }


    }
}
