package com.example.prox0;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prox0.database.DatabaseHelper;
import com.example.prox0.model.Experience;

public class AddExperienceActivity extends AppCompatActivity {

    private Spinner spinnerCategoria; // Nuevo selector de categoría
    private EditText etProducto, etServicio, etCosto, etDescripcion;
    private Button btnBack, btnSave, btnSaveFavorite, btnShareWhatsapp;
    private DatabaseHelper databaseHelper;
    private boolean isFavorite = false;

    // Categorías disponibles
    private String[] categorias = {
        "🍽️ Comidas & Restaurantes",
        "🛍️ Compras & Productos",
        "✈️ Viajes & Lugares",
        "🎬 Entretenimiento"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_experience);

        initViews();
        setupCategorySpinner();
        setupListeners();
    }

    private void initViews() {
        spinnerCategoria = findViewById(R.id.spinner_categoria); // Nuevo spinner
        etProducto = findViewById(R.id.et_producto);
        etServicio = findViewById(R.id.et_servicio);
        etCosto = findViewById(R.id.et_costo);
        etDescripcion = findViewById(R.id.et_descripcion);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        btnSaveFavorite = findViewById(R.id.btn_save_favorite);
        btnShareWhatsapp = findViewById(R.id.btn_share_whatsapp);

        databaseHelper = new DatabaseHelper(this);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExperience(false);
            }
        });

        btnSaveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                btnSaveFavorite.setText(isFavorite ? "⭐ Es Favorito" : "⭐ Favorito");
                btnSaveFavorite.setBackgroundResource(isFavorite ? R.drawable.button_primary : R.drawable.button_secondary);
            }
        });

        btnShareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnWhatsApp();
            }
        });
    }

    private void saveExperience(boolean asFavorite) {
        String categoria = spinnerCategoria.getSelectedItem().toString(); // Obtener categoría seleccionada
        String producto = etProducto.getText().toString().trim();
        String servicio = etServicio.getText().toString().trim();
        String costoStr = etCosto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(producto) && TextUtils.isEmpty(servicio)) {
            Toast.makeText(this, "Ingrese al menos un producto o servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(costoStr)) {
            Toast.makeText(this, "Ingrese el costo", Toast.LENGTH_SHORT).show();
            return;
        }

        double costo;
        try {
            costo = Double.parseDouble(costoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un costo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear y guardar experiencia con categoría
        Experience experience = new Experience(categoria, producto, servicio, costo, descripcion);
        experience.setFavorite(isFavorite || asFavorite);

        long result = databaseHelper.insertExperience(experience);

        if (result != -1) {
            Toast.makeText(this, "Experiencia guardada exitosamente", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Error al guardar la experiencia", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareOnWhatsApp() {
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String producto = etProducto.getText().toString().trim();
        String servicio = etServicio.getText().toString().trim();
        String costoStr = etCosto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(producto) && TextUtils.isEmpty(servicio)) {
            Toast.makeText(this, "Ingrese información para compartir", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("🎉 *Mi nueva experiencia en ProX*\n\n");
        message.append("📂 *Categoría:* ").append(categoria).append("\n"); // Incluir categoría

        if (!TextUtils.isEmpty(producto)) {
            message.append("📦 *Producto:* ").append(producto).append("\n");
        }

        if (!TextUtils.isEmpty(servicio)) {
            message.append("🔧 *Servicio:* ").append(servicio).append("\n");
        }

        if (!TextUtils.isEmpty(costoStr)) {
            message.append("💰 *Costo:* $").append(costoStr).append("\n");
        }

        if (!TextUtils.isEmpty(descripcion)) {
            message.append("📝 *Descripción:* ").append(descripcion).append("\n");
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

    private void clearFields() {
        spinnerCategoria.setSelection(0); // Resetear spinner a primera opción
        etProducto.setText("");
        etServicio.setText("");
        etCosto.setText("");
        etDescripcion.setText("");
        isFavorite = false;
        btnSaveFavorite.setText("⭐ Favorito");
        btnSaveFavorite.setBackgroundResource(R.drawable.button_secondary);
    }
}
