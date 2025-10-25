package com.example.prox0.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.prox0.MainActivity;
import com.example.prox0.R;
import com.example.prox0.database.DatabaseHelper;
import com.example.prox0.model.Experience;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Worker {
    private static final String CHANNEL_ID = "prox_notifications";
    private static final String CHANNEL_NAME = "ProX Notificaciones";
    private static final int NOTIFICATION_ID = 1001;

    public NotificationService(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        createNotificationChannel();

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Experience> experiences = dbHelper.getAllExperiences();

        if (!experiences.isEmpty()) {
            sendSmartNotification(experiences);
        }

        // Programar próxima notificación (cada 24 horas)
        scheduleNextNotification(getApplicationContext());

        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones inteligentes de ProX");

            NotificationManager notificationManager =
                getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendSmartNotification(List<Experience> experiences) {
        SharedPreferences prefs = getApplicationContext()
            .getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);

        String[] messages = generateSmartMessages(experiences, prefs);
        String selectedMessage = messages[new Random().nextInt(messages.length)];

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            getApplicationContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
            getApplicationContext(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("ProX Experience 🎉")
            .setContentText(selectedMessage)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(selectedMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        NotificationManager notificationManager =
            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Guardar estadísticas
        updateNotificationStats(prefs);
    }

    private String[] generateSmartMessages(List<Experience> experiences, SharedPreferences prefs) {
        Experience lastExperience = experiences.get(0); // Más reciente

        // Obtener favoritos sin usar streams para compatibilidad
        List<Experience> favorites = new ArrayList<>();
        for (Experience exp : experiences) {
            if (exp.isFavorite()) {
                favorites.add(exp);
            }
        }

        // Calcular promedio sin usar streams
        double totalCost = 0;
        for (Experience exp : experiences) {
            totalCost += exp.getCosto();
        }
        double avgCost = experiences.size() > 0 ? totalCost / experiences.size() : 0.0;

        return new String[]{
            "¿Has probado algo similar a " + lastExperience.getProducto() + " últimamente? 🤔",

            "Tienes " + favorites.size() + " favoritos increíbles. ¿Tiempo de repetir alguno? ⭐",

            "Recuerda: Tu gasto promedio es $" + String.format("%.2f", avgCost) +
            ". ¿Qué tal algo nuevo en ese rango? 💡",

            "¡Hace días que no registras una experiencia! ¿Has probado algo genial? 📝",

            "Consejo IA: Basado en tus gustos, podrías explorar productos similares 🤖",

            "¿Sabías que puedes compartir tus favoritos en WhatsApp? ¡Inspira a otros! 📱"
        };
    }

    private void updateNotificationStats(SharedPreferences prefs) {
        int notificationCount = prefs.getInt("notification_count", 0);
        prefs.edit()
            .putInt("notification_count", notificationCount + 1)
            .putLong("last_notification", System.currentTimeMillis())
            .apply();
    }

    public static void scheduleNextNotification(Context context) {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationService.class)
            .setInitialDelay(24, TimeUnit.HOURS) // Próxima notificación en 24 horas
            .build();

        WorkManager.getInstance(context).enqueue(notificationWork);
    }

    public static void scheduleImmediateNotification(Context context) {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationService.class)
            .setInitialDelay(5, TimeUnit.SECONDS) // Para testing
            .build();

        WorkManager.getInstance(context).enqueue(notificationWork);
    }
}
