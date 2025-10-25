package com.example.prox0;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prox0.adapter.ExperienceAdapter;
import com.example.prox0.database.DatabaseHelper;
import com.example.prox0.model.Experience;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements ExperienceAdapter.OnExperienceClickListener {

    private Button btnBack, btnShareAll;
    private TextView tvFavoritesCount, tvFavoritesCost;
    private RecyclerView rvFavorites;
    private LinearLayout layoutEmptyFavorites;

    private ExperienceAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<Experience> favoriteExperiences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadFavorites();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnShareAll = findViewById(R.id.btn_share_all);
        tvFavoritesCount = findViewById(R.id.tv_favorites_count);
        tvFavoritesCost = findViewById(R.id.tv_favorites_cost);
        rvFavorites = findViewById(R.id.rv_favorites);
        layoutEmptyFavorites = findViewById(R.id.layout_empty_favorites);

        databaseHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnShareAll.setOnClickListener(v -> shareAllFavorites());
    }

    private void loadFavorites() {
        favoriteExperiences = databaseHelper.getFavoriteExperiences();

        if (favoriteExperiences.isEmpty()) {
            showEmptyState();
        } else {
            showFavorites();
        }

        updateStatistics();
    }

    private void showEmptyState() {
        rvFavorites.setVisibility(View.GONE);
        layoutEmptyFavorites.setVisibility(View.VISIBLE);
        btnShareAll.setEnabled(false);
    }

    private void showFavorites() {
        rvFavorites.setVisibility(View.VISIBLE);
        layoutEmptyFavorites.setVisibility(View.GONE);
        btnShareAll.setEnabled(true);

        if (adapter == null) {
            adapter = new ExperienceAdapter(this, favoriteExperiences);
            adapter.setOnExperienceClickListener(this);
            rvFavorites.setAdapter(adapter);
        } else {
            adapter.updateData(favoriteExperiences);
        }
    }

    private void updateStatistics() {
        int favoritesCount = favoriteExperiences.size();
        double totalCost = 0;

        for (Experience exp : favoriteExperiences) {
            totalCost += exp.getCosto();
        }

        tvFavoritesCount.setText(String.valueOf(favoritesCount));
        tvFavoritesCost.setText(String.format("$%.2f", totalCost));
    }

    private void shareAllFavorites() {
        if (favoriteExperiences.isEmpty()) {
            Toast.makeText(this, "No tienes favoritos para compartir", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("⭐ *Mis experiencias favoritas en ProX*\n\n");

        for (int i = 0; i < favoriteExperiences.size(); i++) {
            Experience exp = favoriteExperiences.get(i);
            message.append(String.format("%d. ", i + 1));
            message.append("📦 ").append(exp.getProducto());

            if (exp.getServicio() != null && !exp.getServicio().isEmpty()) {
                message.append(" | 🔧 ").append(exp.getServicio());
            }

            message.append("\n💰 $").append(String.format("%.2f", exp.getCosto()));

            if (exp.getDescripcion() != null && !exp.getDescripcion().isEmpty()) {
                message.append("\n📝 ").append(exp.getDescripcion());
            }

            message.append("\n\n");
        }

        // Calcular total sin streams
        double totalValue = 0;
        for (Experience exp : favoriteExperiences) {
            totalValue += exp.getCosto();
        }

        message.append("💎 *Total invertido en favoritos: $").append(String.format("%.2f", totalValue)).append("*\n");
        message.append("\n¡Te recomiendo todas estas experiencias! 👍");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(message.toString())));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(Experience experience, int position) {
        // Remover de favoritos
        databaseHelper.updateFavoriteStatus(experience.getId(), false);
        favoriteExperiences.remove(position);
        adapter.removeItem(position);
        updateStatistics();

        if (favoriteExperiences.isEmpty()) {
            showEmptyState();
        }

        Toast.makeText(this, "Removido de favoritos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Experience experience, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta experiencia favorita?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    databaseHelper.deleteExperience(experience.getId());
                    favoriteExperiences.remove(position);
                    adapter.removeItem(position);
                    updateStatistics();

                    if (favoriteExperiences.isEmpty()) {
                        showEmptyState();
                    }

                    Toast.makeText(this, "Experiencia eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onShareClick(Experience experience) {
        StringBuilder message = new StringBuilder();
        message.append("⭐ *Mi experiencia favorita en ProX*\n\n");
        message.append("📦 *Producto:* ").append(experience.getProducto()).append("\n");
        message.append("🔧 *Servicio:* ").append(experience.getServicio()).append("\n");
        message.append("💰 *Costo:* $").append(String.format("%.2f", experience.getCosto())).append("\n");
        message.append("📅 *Fecha:* ").append(experience.getFecha()).append("\n");

        if (experience.getDescripcion() != null && !experience.getDescripcion().isEmpty()) {
            message.append("📝 *Descripción:* ").append(experience.getDescripcion()).append("\n");
        }

        message.append("\n⭐ *¡Es uno de mis favoritos absolutos!*\n");
        message.append("\n¡Te lo recomiendo mucho! 👍");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(message.toString())));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // Recargar cuando se regrese a esta actividad
    }
}
