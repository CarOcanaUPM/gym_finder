package com.example.gymfindermadrid;

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
import java.util.List;

public class GimnasiosAdapter extends RecyclerView.Adapter<GimnasiosAdapter.GimnasioViewHolder> {

    private List<Gimnasio> gimnasios;
    private Context context;

    public GimnasiosAdapter(Context context, List<Gimnasio> gimnasios) {
        this.context = context;
        this.gimnasios = gimnasios;
    }

    @NonNull
    @Override
    public GimnasioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gimnasio, parent, false);
        return new GimnasioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GimnasioViewHolder holder, int position) {
        Gimnasio gimnasio = gimnasios.get(position);
        holder.nombre.setText(gimnasio.getNombre());
        holder.direccion.setText(gimnasio.getDireccion());
        holder.rating.setText("Puntuación: " + gimnasio.getPuntuacion());

        String imageUrl = gimnasio.getPhotoUrl(context.getString(R.string.google_maps_key));
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.gym_background);
        }


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("NOMBRE_GIMNASIO", gimnasio.getNombre());
            intent.putExtra("DIRECCION_GIMNASIO", gimnasio.getDireccion());
            intent.putExtra("RATING_GIMNASIO", String.valueOf(gimnasio.getPuntuacion()));
            intent.putExtra("IMAGE_URL", gimnasio.getPhotoUrl(context.getString(R.string.google_maps_key)));
            intent.putExtra("PLACE_ID", gimnasio.getPlaceId());
            context.startActivity(intent);

            ((MainActivity) context).obtenerDetallesGimnasio(gimnasio.getPlaceId());
        });
    }



    @Override
    public int getItemCount() {
        return gimnasios.size();
    }

    public static class GimnasioViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, direccion, rating;
        ImageView imagen;

        public GimnasioViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.gymName);
            direccion = itemView.findViewById(R.id.gymAddress);
            rating = itemView.findViewById(R.id.gymRating);
            imagen = itemView.findViewById(R.id.gymImage);
        }
    }
}
