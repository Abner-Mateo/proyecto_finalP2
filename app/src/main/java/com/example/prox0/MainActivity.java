package com.example.prox0;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.prox0.notifications.NotificationService;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout;
    private CardView cardAddExperience, cardHistory, cardChat, cardFavorites, cardShareWhatsapp;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupWelcomeMessage();
        setupListeners();

        // Inicializar notificaciones inteligentes
        initNotifications();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnLogout = findViewById(R.id.btn_logout);
        cardAddExperience = findViewById(R.id.card_add_experience);
        cardHistory = findViewById(R.id.card_history);
        cardChat = findViewById(R.id.card_chat);
        cardFavorites = findViewById(R.id.card_favorites);
        cardShareWhatsapp = findViewById(R.id.card_share_whatsapp);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
    }

    private void setupWelcomeMessage() {
        String currentUser = sharedPreferences.getString("currentUser", "Usuario");
        tvWelcome.setText("¡Bienvenido " + currentUser + "!");
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        cardAddExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExperienceActivity.class);
                startActivity(intent);
            }
        });

        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        cardChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        cardFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });

        // Nuevo listener para compartir por WhatsApp
        cardShareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToWhatsApp();
            }
        });
    }

    private void initNotifications() {
        // Programar notificaciones inteligentes
        NotificationService.scheduleNextNotification(this);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("currentUser");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void shareToWhatsApp() {
        String currentUser = sharedPreferences.getString("currentUser", "Usuario");
        String shareText = "📲 ¡Hola! Te comparto mi app ProX Experience 🌟\n\n" +
                "Una app increíble para registrar todas mis experiencias:\n" +
                "✈️ Viajes\n" +
                "🍽️ Comidas\n" +
                "🛍️ Compras\n" +
                "🎬 Entretenimiento\n\n" +
                "¡Descárgala y registra tus momentos especiales! 😊\n\n" +
                "Enviado por: " + currentUser;

        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            // Si WhatsApp no está instalado, usar compartir genérico
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ProX Experience App");

            Intent chooser = Intent.createChooser(shareIntent, "Compartir ProX Experience");
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                Toast.makeText(this, "No se encontró ninguna app para compartir", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
