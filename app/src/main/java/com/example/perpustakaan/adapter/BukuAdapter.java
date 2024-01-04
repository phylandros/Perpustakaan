package com.example.perpustakaan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.perpustakaan.R;
import com.example.perpustakaan.model.BukuModel;

import java.util.ArrayList;
import java.util.List;

public class BukuAdapter extends RecyclerView.Adapter<BukuAdapter.BukuViewHolder> {

    private List<BukuModel> dataList;
    private Context context;
    private List<Integer> selectedBukuIds = new ArrayList<>();
    private OnItemSelectedListener listener;

    public BukuAdapter(Context context, List<BukuModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    public List<Integer> getSelectedBukuIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (BukuModel buku : dataList) {
            if (buku.isSelected()) {
                selectedIds.add(buku.getBukuid());
            }
        }
        return selectedIds;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(List<Integer> selectedIds);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }




    @NonNull
    @Override
    public BukuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itembuku, parent, false);
        return new BukuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BukuViewHolder holder, int position) {
        BukuModel buku = dataList.get(position);
        holder.bind(buku);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class BukuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView nameTextView;
        TextView deskripsiTextView;
        ImageView checkImageView;

        public BukuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageshow);
            nameTextView = itemView.findViewById(R.id.titlename);
            deskripsiTextView = itemView.findViewById(R.id.descriptiontext);
            checkImageView = itemView.findViewById(R.id.checkselected); // ImageView untuk tanda centang
            itemView.setOnClickListener(this);

        }

        public void bind(BukuModel buku) {
//            imageView.setBackground(ContextCompat.getDrawable(context, buku.getGambar()));

            Glide.with(context)
                    .load(buku.getImageUrl()) // Gunakan URL dari BukuModel Anda
                    .placeholder(R.drawable.image) // Placeholder saat gambar sedang dimuat
                    .error(R.drawable.image) // Gambar jika terjadi kesalahan
                    .into(imageView);

            nameTextView.setText(buku.getJudul());
            deskripsiTextView.setText(buku.getDeskripsi());

            if (buku.isSelected()) {
                checkImageView.setImageResource(R.drawable.checklist);
            } else {
                checkImageView.setImageResource(R.drawable.uncheck);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                BukuModel buku = dataList.get(adapterPosition);
                buku.setSelected(!buku.isSelected());
                notifyItemChanged(adapterPosition);

                if (listener != null) {
                    List<Integer> selectedIds = getSelectedIds();
                    listener.onItemSelected(selectedIds);
                }
            }

        }

        public List<Integer> getSelectedIds() {
            List<Integer> selectedIds = new ArrayList<>();
            for (BukuModel buku : dataList) {
                if (buku.isSelected()) {
                    selectedIds.add(buku.getBukuid());
                }
            }
            return selectedIds;
        }
    }
}
