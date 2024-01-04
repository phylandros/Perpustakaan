package com.example.perpustakaan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.perpustakaan.R;
import com.example.perpustakaan.admin.BukuActivity;
import com.example.perpustakaan.admin.PerpustakaanActivity;
import com.example.perpustakaan.admin.VerifikasiActivity;
import com.example.perpustakaan.model.PustakawanModel;

import java.util.List;

public class PustakawanAdapter extends RecyclerView.Adapter<PustakawanAdapter.PustakawanViewHolder> {

    private static Context context;
    private static List<PustakawanModel> pustakawanList;

    public PustakawanAdapter(Context context, List<PustakawanModel> pustakawanList) {
        this.context = context;
        this.pustakawanList = pustakawanList;
    }

    @NonNull
    @Override
    public PustakawanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pustakawan, parent, false);
        return new PustakawanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PustakawanViewHolder holder, int position) {
        PustakawanModel pustakawan = pustakawanList.get(position);

        holder.textView.setText(pustakawan.getName());

        Glide.with(context)
                .load(pustakawan.getImageResource())
                .placeholder(R.drawable.image)
                .error(R.drawable.image)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return pustakawanList.size();
    }

    public static class PustakawanViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public PustakawanViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            textView = itemView.findViewById(R.id.textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        PustakawanModel clickedItem = pustakawanList.get(position);
                        if (clickedItem.getName().equals("Buku")) {
                            Intent bukuIntent = new Intent(context, BukuActivity.class);
                            context.startActivity(bukuIntent);
                        } else if (clickedItem.getName().equals("Verifikasi")) {
                            Intent verifikasiIntent = new Intent(context, VerifikasiActivity.class);
                            context.startActivity(verifikasiIntent);
                        } else if (clickedItem.getName().equals("Perpustakaan")) {
                            Intent verifikasiIntent = new Intent(context, PerpustakaanActivity.class);
                            context.startActivity(verifikasiIntent);
                        }
                    }
                }
            });
        }
    }
}