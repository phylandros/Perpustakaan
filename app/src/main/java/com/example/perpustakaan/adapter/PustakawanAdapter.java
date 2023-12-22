package com.example.perpustakaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.perpustakaan.R;
import com.example.perpustakaan.model.PustakawanModel;

import java.util.List;

public class PustakawanAdapter extends RecyclerView.Adapter<PustakawanAdapter.PustakawanViewHolder> {

    private Context mContext;
    private List<PustakawanModel> pustakawanList; // Ganti dengan model yang sesuai

    public PustakawanAdapter(Context context, List<PustakawanModel> pustakawanList) {
        this.mContext = context;
        this.pustakawanList = pustakawanList;
    }

    @NonNull
    @Override
    public PustakawanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_pustakawan, parent, false);
        return new PustakawanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PustakawanViewHolder holder, int position) {
        PustakawanModel currentPustakawan = pustakawanList.get(position);

        // Set data pustakawan ke tampilan CardView di sini
        holder.namaPustakawan.setText(currentPustakawan.getNama());

        // Menggunakan Glide untuk menampilkan gambar (ganti dengan URL gambar yang sesuai)
        Glide.with(mContext)
                .load(currentPustakawan.getGambarURL())
                .into(holder.gambarPustakawan);
    }

    @Override
    public int getItemCount() {
        return pustakawanList.size();
    }

    public static class PustakawanViewHolder extends RecyclerView.ViewHolder {
        ImageView gambarPustakawan;
        TextView namaPustakawan;

        public PustakawanViewHolder(@NonNull View itemView) {
            super(itemView);
            gambarPustakawan = itemView.findViewById(R.id.imageview);
            namaPustakawan = itemView.findViewById(R.id.textview);
        }
    }
}

