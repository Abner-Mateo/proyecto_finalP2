package com.example.prox0;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prox0.adapter.ChatAdapter;
import com.example.prox0.api.OpenAIService;
import com.example.prox0.model.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Button btnBack, btnClearChat, btnProductos, btnServicios, btnLugares, btnSend;
    private EditText etMessage;
    private RecyclerView rvChat;
    private LinearLayout layoutLoading;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private OpenAIService openAIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupRecyclerView();
        setupListeners();
        showWelcomeMessage();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnClearChat = findViewById(R.id.btn_clear_chat);
        btnProductos = findViewById(R.id.btn_productos);
        btnServicios = findViewById(R.id.btn_servicios);
        btnLugares = findViewById(R.id.btn_lugares);
        btnSend = findViewById(R.id.btn_send);
        etMessage = findViewById(R.id.et_message);
        rvChat = findViewById(R.id.rv_chat);
        layoutLoading = findViewById(R.id.layout_loading);

        messages = new ArrayList<>();
        openAIService = new OpenAIService();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(this, messages);
        rvChat.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnClearChat.setOnClickListener(v -> clearChat());

        btnProductos.setOnClickListener(v -> sendQuickQuery("Recomiéndame productos populares y de calidad"));

        btnServicios.setOnClickListener(v -> sendQuickQuery("Qué servicios me recomiendas para mejorar mi vida"));

        btnLugares.setOnClickListener(v -> sendQuickQuery("Sugiere lugares interesantes para visitar"));

        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void showWelcomeMessage() {
        String welcomeText = "¡Hola! 👋 Soy tu asistente de IA para ProX Experience.\n\n" +
                "Puedo ayudarte con:\n" +
                "🛍️ Recomendaciones de productos\n" +
                "🔧 Sugerencias de servicios\n" +
                "📍 Lugares favoritos para visitar\n\n" +
                "¿En qué puedo ayudarte hoy?";

        ChatMessage welcomeMessage = new ChatMessage(welcomeText, false);
        chatAdapter.addMessage(welcomeMessage);
        scrollToBottom();
    }

    private void sendQuickQuery(String query) {
        etMessage.setText(query);
        sendMessage();
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            return;
        }

        // Añadir mensaje del usuario
        ChatMessage userMessage = new ChatMessage(message, true);
        chatAdapter.addMessage(userMessage);
        scrollToBottom();

        // Limpiar input y mostrar loading
        etMessage.setText("");
        showLoading(true);

        // Determinar tipo de consulta y enviar a OpenAI
        if (isLocationQuery(message)) {
            handleLocationQuery(message);
        } else {
            handleGeneralQuery(message);
        }
    }

    private boolean isLocationQuery(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("lugar") || lowerMessage.contains("sitio") ||
               lowerMessage.contains("donde") || lowerMessage.contains("visitar") ||
               lowerMessage.contains("restaurante") || lowerMessage.contains("tienda");
    }

    private void handleLocationQuery(String message) {
        // Extraer ubicación o usar una por defecto
        String ubicacion = "la ciudad"; // Podrías hacer esto más inteligente
        String tipoLugar = "lugares interesantes";

        openAIService.sugerirLugaresFavoritos(ubicacion, tipoLugar, new OpenAIService.OpenAICallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    ChatMessage botMessage = new ChatMessage(response, false);
                    chatAdapter.addMessage(botMessage);
                    scrollToBottom();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showErrorMessage(error);
                });
            }
        });
    }

    private void handleGeneralQuery(String message) {
        openAIService.consultarProductoServicio(message, new OpenAIService.OpenAICallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    ChatMessage botMessage = new ChatMessage(response, false);
                    chatAdapter.addMessage(botMessage);
                    scrollToBottom();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showErrorMessage(error);
                });
            }
        });
    }

    private void showErrorMessage(String error) {
        String errorMsg = "Lo siento, ocurrió un error al procesar tu consulta: " + error +
                         "\n\nPor favor, inténtalo de nuevo o verifica tu conexión a internet.";
        ChatMessage errorMessage = new ChatMessage(errorMsg, false);
        chatAdapter.addMessage(errorMessage);
        scrollToBottom();
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSend.setEnabled(!show);
    }

    private void clearChat() {
        chatAdapter.clearMessages();
        showWelcomeMessage();
        Toast.makeText(this, "Chat limpiado", Toast.LENGTH_SHORT).show();
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            rvChat.smoothScrollToPosition(messages.size() - 1);
        }
    }
}
