package com.example.perpustakaan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.R;

import java.util.List;

public class VerifikasiAdapter extends RecyclerView.Adapter<VerifikasiAdapter.VerifikasiViewHolder> {

    private List<String> dataNama;
    private List<String> dataBuku;
    private List<String> dataTanggal;

    public VerifikasiAdapter(List<String> dataNama, List<String> dataBuku, List<String> dataTanggal) {
        this.dataNama = dataNama;
        this.dataBuku = dataBuku;
        this.dataTanggal = dataTanggal;
    }

    @NonNull
    @Override
    public VerifikasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verifikasi_peminjaman, parent, false);
        return new VerifikasiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifikasiViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return dataNama.size();
    }

    public class VerifikasiViewHolder extends RecyclerView.ViewHolder {

        private TextView namaTextView;
        private TextView bukuTextView;
        private TextView tanggalTextView;

        public VerifikasiViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTextView = itemView.findViewById(R.id.namaTextView);
            bukuTextView = itemView.findViewById(R.id.bukuTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
        }

        public void bindData(int position) {
            namaTextView.setText(dataNama.get(position));
            bukuTextView.setText(dataBuku.get(position));
            tanggalTextView.setText(dataTanggal.get(position));
        }
    }
}
