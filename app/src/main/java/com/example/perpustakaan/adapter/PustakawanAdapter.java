package com.example.perpustakaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.perpustakaan.BukuFragment;
import com.example.perpustakaan.PeminjamanFragment;
import com.example.perpustakaan.PengembalianFragment;
import com.example.perpustakaan.R;
import com.example.perpustakaan.VerifikasiFragment;
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

        // Set text to TextView
        holder.textView.setText(pustakawan.getName());

        // Load image using Glide from resource drawable
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
                            Fragment bukuFragment = new BukuFragment();
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_home, bukuFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } if (clickedItem.getName().equals("Verifikasi")) {
                            Fragment verifikasiFragment = new VerifikasiFragment();
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_home, verifikasiFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } if (clickedItem.getName().equals("Peminjaman")) {
                            Fragment peminjamanFragment = new PeminjamanFragment();
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_home, peminjamanFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } if (clickedItem.getName().equals("Pengembalian")) {
                            Fragment pengembalianFragment = new PengembalianFragment();
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_home, pengembalianFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            });
        }
    }
}

