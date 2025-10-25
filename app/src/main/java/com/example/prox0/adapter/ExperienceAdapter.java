package com.example.prox0.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prox0.R;
import com.example.prox0.model.Experience;
import java.util.List;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder> {

    private List<Experience> experiences;
    private Context context;
    private OnExperienceClickListener listener;

    public interface OnExperienceClickListener {
        void onFavoriteClick(Experience experience, int position);
        void onDeleteClick(Experience experience, int position);
        void onShareClick(Experience experience);
    }

    public ExperienceAdapter(Context context, List<Experience> experiences) {
        this.context = context;
        this.experiences = experiences;
    }

    public void setOnExperienceClickListener(OnExperienceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_experience, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {
        Experience experience = experiences.get(position);

        // Mostrar categoría - ERROR CORREGIDO
        String categoria = experience.getCategoria(); // Ya no hay .toString()
        if (categoria == null || categoria.trim().isEmpty()) { // .trim().isEmpty() en lugar de .isEmpty()
            holder.tvCategoria.setText("📝 General");
            holder.tvCategoria.setVisibility(View.VISIBLE);
        } else {
            // Obtener el emoji y nombre de la categoría
            String categoriaTexto = getCategoriaConEmoji(categoria);
            holder.tvCategoria.setText(categoriaTexto);
            holder.tvCategoria.setVisibility(View.VISIBLE);
        }

        // Protección contra valores null
        holder.tvProducto.setText(experience.getProducto() != null ? experience.getProducto() : "Sin producto");
        holder.tvServicio.setText(experience.getServicio() != null ? experience.getServicio() : "Sin servicio");
        holder.tvCosto.setText("$" + String.format("%.2f", experience.getCosto()));
        holder.tvFecha.setText(experience.getFecha() != null ? experience.getFecha() : "Sin fecha");
        holder.tvDescripcion.setText(experience.getDescripcion() != null ? experience.getDescripcion() : "Sin descripción");

        // Configurar botón favorito
        holder.btnFavorite.setText(experience.isFavorite() ? "⭐" : "☆");
        holder.btnFavorite.setBackgroundResource(
            experience.isFavorite() ? R.drawable.button_primary : R.drawable.button_secondary
        );

        // Listeners
        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(experience, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(experience, position);
            }
        });

        holder.btnShare.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareClick(experience);
            }
        });
    }

    // Método que faltaba - aquí está la solución del error
    private String getCategoriaConEmoji(String categoria) {
        if (categoria == null) {
            return "📝 General";
        }

        // Verificar si ya tiene emoji
        if (categoria.startsWith("🍽️") || categoria.startsWith("🛍️") ||
            categoria.startsWith("✈️") || categoria.startsWith("🎬")) {
            return categoria;
        }

        // Agregar emoji según el texto de la categoría
        if (categoria.contains("Comidas") || categoria.contains("Restaurantes")) {
            return "🍽️ " + categoria;
        } else if (categoria.contains("Compras") || categoria.contains("Productos")) {
            return "🛍️ " + categoria;
        } else if (categoria.contains("Viajes") || categoria.contains("Lugares")) {
            return "✈️ " + categoria;
        } else if (categoria.contains("Entretenimiento")) {
            return "🎬 " + categoria;
        } else {
            return "📝 " + categoria;
        }
    }

    @Override
    public int getItemCount() {
        return experiences.size();
    }

    public void updateData(List<Experience> newExperiences) {
        this.experiences = newExperiences;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        experiences.remove(position);
        notifyItemRemoved(position);
    }

    public void updateItem(int position, Experience experience) {
        experiences.set(position, experience);
        notifyItemChanged(position);
    }

    static class ExperienceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria, tvProducto, tvServicio, tvCosto, tvFecha, tvDescripcion;
        Button btnFavorite, btnDelete, btnShare;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(R.id.tv_categoria);
            tvProducto = itemView.findViewById(R.id.tv_producto);
            tvServicio = itemView.findViewById(R.id.tv_servicio);
            tvCosto = itemView.findViewById(R.id.tv_costo);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnShare = itemView.findViewById(R.id.btn_share);
        }
    }
}
