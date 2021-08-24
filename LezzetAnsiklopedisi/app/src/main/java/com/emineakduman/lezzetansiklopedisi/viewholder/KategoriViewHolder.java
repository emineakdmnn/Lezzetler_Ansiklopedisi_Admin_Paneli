package com.emineakduman.lezzetansiklopedisi.viewholder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emineakduman.lezzetansiklopedisi.R;
import com.emineakduman.lezzetansiklopedisi.arayuz.ItemClickListener;

public class KategoriViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView txtKategoriAdi;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public KategoriViewHolder(@NonNull View itemView) {
        super(itemView);

        txtKategoriAdi =itemView.findViewById(R.id.kategori_adi);
        imageView =itemView.findViewById(R.id.kategori_resmi);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){

        this.itemClickListener= itemClickListener;

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("EYLEM SEÇİN");
        menu.add(0,0,getAdapterPosition(),"Güncelle");
        menu.add(0,1,getAdapterPosition(),"Sil");
    }
}
