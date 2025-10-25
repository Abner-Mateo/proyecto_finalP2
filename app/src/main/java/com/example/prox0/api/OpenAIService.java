package com.example.prox0.api;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenAIService {
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private OkHttpClient client;
    private Gson gson;

    public interface OpenAICallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public OpenAIService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    public void consultarProductoServicio(String consulta, OpenAICallback callback) {
        String prompt = "Eres un asistente especializado en productos y servicios. " +
                "Responde a la siguiente consulta de manera útil y concisa, " +
                "sugiriendo productos, servicios o lugares que puedan ser relevantes: " + consulta;

        enviarConsulta(prompt, callback);
    }

    public void sugerirLugaresFavoritos(String ubicacion, String tipoLugar, OpenAICallback callback) {
        String prompt = "Sugiere 5 lugares favoritos y populares en " + ubicacion +
                " para " + tipoLugar + ". Incluye una breve descripción de cada lugar " +
                "y por qué es recomendable visitarlo.";

        enviarConsulta(prompt, callback);
    }

    public void obtenerRecomendaciones(String historialExperiencias, OpenAICallback callback) {
        String prompt = "Basándote en este historial de experiencias del usuario: " +
                historialExperiencias +
                ". Sugiere 3 nuevos productos o servicios que podrían interesarle, " +
                "explicando por qué podrían ser relevantes.";

        enviarConsulta(prompt, callback);
    }

    private void enviarConsulta(String mensaje, OpenAICallback callback) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", mensaje);
        messages.add(message);

        requestBody.add("messages", messages);
        requestBody.addProperty("max_tokens", 500);
        requestBody.addProperty("temperature", 0.7);

        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"),
            gson.toJson(requestBody)
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                        if (jsonResponse.has("choices")) {
                            JsonArray choices = jsonResponse.getAsJsonArray("choices");
                            if (choices.size() > 0) {
                                JsonObject choice = choices.get(0).getAsJsonObject();
                                JsonObject messageObj = choice.getAsJsonObject("message");
                                String content = messageObj.get("content").getAsString();
                                callback.onSuccess(content.trim());
                            } else {
                                callback.onError("No se recibió respuesta válida");
                            }
                        } else {
                            callback.onError("Formato de respuesta inválido");
                        }
                    } catch (Exception e) {
                        callback.onError("Error procesando respuesta: " + e.getMessage());
                    }
                } else {
                    callback.onError("Error del servidor: " + response.code() + " - " + response.message());
                }
            }
        });
    }
}
