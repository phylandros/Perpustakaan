package com.example.perpustakaan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perpustakaan.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class BukuDipinjamAdapter extends RecyclerView.Adapter<BukuDipinjamAdapter.BukuViewHolder> {
    private List<JSONObject> selectedBooks;

    public BukuDipinjamAdapter(List<JSONObject> selectedBooks) {
        this.selectedBooks = selectedBooks;
    }

    // Inner class untuk ViewHolder
    public static class BukuViewHolder extends RecyclerView.ViewHolder {
        public TextView judulTextView;

        public BukuViewHolder(View itemView) {
            super(itemView);
            judulTextView = itemView.findViewById(R.id.text_view_judul);
        }
    }

    @NonNull
    @Override
    public BukuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemuserpinjam, parent, false);
        return new BukuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BukuViewHolder holder, int position) {
            JSONObject buku = selectedBooks.get(position);
        try {
            holder.judulTextView.setText(buku.getString("judul"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return selectedBooks.size();
    }
}