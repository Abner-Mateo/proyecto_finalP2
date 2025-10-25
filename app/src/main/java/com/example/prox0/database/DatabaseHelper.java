package com.example.prox0.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.prox0.model.Experience;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ProXApp.db";
    private static final int DATABASE_VERSION = 2; // Incrementado para agregar categoria

    // Tabla de experiencias
    private static final String TABLE_EXPERIENCES = "experiences";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORIA = "categoria"; // Nuevo campo
    private static final String COLUMN_PRODUCTO = "producto";
    private static final String COLUMN_SERVICIO = "servicio";
    private static final String COLUMN_COSTO = "costo";
    private static final String COLUMN_IS_FAVORITE = "is_favorite";
    private static final String COLUMN_FECHA = "fecha";
    private static final String COLUMN_DESCRIPCION = "descripcion";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPERIENCES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORIA + " TEXT DEFAULT 'General', " + // Nuevo campo con valor por defecto
                COLUMN_PRODUCTO + " TEXT, " +
                COLUMN_SERVICIO + " TEXT, " +
                COLUMN_COSTO + " REAL, " +
                COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, " +
                COLUMN_FECHA + " TEXT, " +
                COLUMN_DESCRIPCION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Agregar columna categoria a registros existentes
            try {
                db.execSQL("ALTER TABLE " + TABLE_EXPERIENCES + " ADD COLUMN " + COLUMN_CATEGORIA + " TEXT DEFAULT 'General'");
            } catch (Exception e) {
                // Si falla, recrear la tabla
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPERIENCES);
                onCreate(db);
            }
        }
    }

    // Insertar experiencia
    public long insertExperience(Experience experience) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORIA, experience.getCategoria() != null ? experience.getCategoria() : "General");
        values.put(COLUMN_PRODUCTO, experience.getProducto());
        values.put(COLUMN_SERVICIO, experience.getServicio());
        values.put(COLUMN_COSTO, experience.getCosto());
        values.put(COLUMN_IS_FAVORITE, experience.isFavorite() ? 1 : 0);
        values.put(COLUMN_FECHA, experience.getFecha());
        values.put(COLUMN_DESCRIPCION, experience.getDescripcion());

        long result = db.insert(TABLE_EXPERIENCES, null, values);
        db.close();
        return result;
    }

    // Obtener todas las experiencias
    public List<Experience> getAllExperiences() {
        List<Experience> experiences = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPERIENCES + " ORDER BY " + COLUMN_ID + " DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    Experience experience = new Experience();
                    experience.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));

                    // Manejo seguro de categoria
                    int categoriaIndex = cursor.getColumnIndex(COLUMN_CATEGORIA);
                    if (categoriaIndex != -1) {
                        experience.setCategoria(cursor.getString(categoriaIndex));
                    } else {
                        experience.setCategoria("General"); // Valor por defecto para registros antiguos
                    }

                    experience.setProducto(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO)));
                    experience.setServicio(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICIO)));
                    experience.setCosto(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COSTO)));
                    experience.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1);
                    experience.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)));
                    experience.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)));
                    experiences.add(experience);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return experiences;
    }

    // Obtener favoritos
    public List<Experience> getFavoriteExperiences() {
        List<Experience> experiences = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPERIENCES + " WHERE " + COLUMN_IS_FAVORITE + " = 1 ORDER BY " + COLUMN_ID + " DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    Experience experience = new Experience();
                    experience.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));

                    // Manejo seguro de categoria
                    int categoriaIndex = cursor.getColumnIndex(COLUMN_CATEGORIA);
                    if (categoriaIndex != -1) {
                        experience.setCategoria(cursor.getString(categoriaIndex));
                    } else {
                        experience.setCategoria("General");
                    }

                    experience.setProducto(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO)));
                    experience.setServicio(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICIO)));
                    experience.setCosto(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COSTO)));
                    experience.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1);
                    experience.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)));
                    experience.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)));
                    experiences.add(experience);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return experiences;
    }

    // Actualizar favorito
    public void updateFavoriteStatus(int experienceId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_EXPERIENCES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(experienceId)});
        db.close();
    }

    // Eliminar experiencia
    public void deleteExperience(int experienceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPERIENCES, COLUMN_ID + " = ?", new String[]{String.valueOf(experienceId)});
        db.close();
    }
}
