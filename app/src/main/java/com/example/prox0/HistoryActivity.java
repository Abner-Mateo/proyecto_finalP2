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
import com.example.prox0.api.OpenAIService;
import com.example.prox0.database.DatabaseHelper;
import com.example.prox0.model.Experience;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements ExperienceAdapter.OnExperienceClickListener {

    private Button btnBack, btnRecommendations;
    private TextView tvTotalExperiences, tvTotalCost, tvFavoritesCount;
    private RecyclerView rvExperiences;
    private LinearLayout layoutEmpty;

    private ExperienceAdapter adapter;
    private DatabaseHelper databaseHelper;
    private OpenAIService openAIService;
    private List<Experience> experiences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadExperiences();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnRecommendations = findViewById(R.id.btn_recommendations);
        tvTotalExperiences = findViewById(R.id.tv_total_experiences);
        tvTotalCost = findViewById(R.id.tv_total_cost);
        tvFavoritesCount = findViewById(R.id.tv_favorites_count);
        rvExperiences = findViewById(R.id.rv_experiences);
        layoutEmpty = findViewById(R.id.layout_empty);

        databaseHelper = new DatabaseHelper(this);
        openAIService = new OpenAIService();
    }

    private void setupRecyclerView() {
        rvExperiences.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRecommendations.setOnClickListener(v -> getAIRecommendations());
    }

    private void loadExperiences() {
        experiences = databaseHelper.getAllExperiences();

        if (experiences.isEmpty()) {
            showEmptyState();
        } else {
            showExperiences();
        }

        updateStatistics();
    }

    private void showEmptyState() {
        rvExperiences.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showExperiences() {
        rvExperiences.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        if (adapter == null) {
            adapter = new ExperienceAdapter(this, experiences);
            adapter.setOnExperienceClickListener(this);
            rvExperiences.setAdapter(adapter);
        } else {
            adapter.updateData(experiences);
        }
    }

    private void updateStatistics() {
        int totalExperiences = experiences.size();
        double totalCost = 0;
        int favoritesCount = 0;

        for (Experience exp : experiences) {
            totalCost += exp.getCosto();
            if (exp.isFavorite()) {
                favoritesCount++;
            }
        }

        tvTotalExperiences.setText(String.valueOf(totalExperiences));
        tvTotalCost.setText(String.format("$%.2f", totalCost));
        tvFavoritesCount.setText(String.valueOf(favoritesCount));
    }

    private void getAIRecommendations() {
        if (experiences.isEmpty()) {
            Toast.makeText(this, "Necesitas tener experiencias registradas para obtener recomendaciones", Toast.LENGTH_LONG).show();
            return;
        }

        // Crear resumen del historial
        StringBuilder historial = new StringBuilder();
        for (Experience exp : experiences) {
            historial.append("- Producto: ").append(exp.getProducto())
                     .append(", Servicio: ").append(exp.getServicio())
                     .append(", Costo: $").append(exp.getCosto());
            if (exp.isFavorite()) {
                historial.append(" (Favorito)");
            }
            historial.append("\n");
        }

        Toast.makeText(this, "Obteniendo recomendaciones de IA...", Toast.LENGTH_SHORT).show();

        openAIService.obtenerRecomendaciones(historial.toString(), new OpenAIService.OpenAICallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> showRecommendationsDialog(response));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "Error: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showRecommendationsDialog(String recommendations) {
        new AlertDialog.Builder(this)
                .setTitle("🤖 Recomendaciones de IA")
                .setMessage(recommendations)
                .setPositiveButton("Entendido", null)
                .setNeutralButton("Compartir", (dialog, which) -> shareRecommendations(recommendations))
                .show();
    }

    private void shareRecommendations(String recommendations) {
        String message = "🤖 *Recomendaciones de IA - ProX Experience*\n\n" + recommendations;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(message)));

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(Experience experience, int position) {
        boolean newFavoriteStatus = !experience.isFavorite();
        databaseHelper.updateFavoriteStatus(experience.getId(), newFavoriteStatus);
        experience.setFavorite(newFavoriteStatus);
        adapter.updateItem(position, experience);
        updateStatistics();

        String message = newFavoriteStatus ? "Añadido a favoritos" : "Removido de favoritos";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Experience experience, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta experiencia?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    databaseHelper.deleteExperience(experience.getId());
                    adapter.removeItem(position);
                    experiences.remove(position);
                    updateStatistics();

                    if (experiences.isEmpty()) {
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
        message.append("🎉 *Mi experiencia en ProX*\n\n");
        message.append("📦 *Producto:* ").append(experience.getProducto()).append("\n");
        message.append("🔧 *Servicio:* ").append(experience.getServicio()).append("\n");
        message.append("💰 *Costo:* $").append(String.format("%.2f", experience.getCosto())).append("\n");
        message.append("📅 *Fecha:* ").append(experience.getFecha()).append("\n");

        if (experience.getDescripcion() != null && !experience.getDescripcion().isEmpty()) {
            message.append("📝 *Descripción:* ").append(experience.getDescripcion()).append("\n");
        }

        if (experience.isFavorite()) {
            message.append("⭐ *¡Es uno de mis favoritos!*\n");
        }

        message.append("\n¡Te recomiendo probarlo! 👍");

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
        loadExperiences(); // Recargar cuando se regrese a esta actividad
    }
}
