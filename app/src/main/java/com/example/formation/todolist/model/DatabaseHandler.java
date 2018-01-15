package com.example.formation.todolist.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Création de la structure de la base de données
 * et gestion de la connexion
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TACHE_TABLE_SQL = "CREATE TABLE taches("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "tache_name TEXT NOT NULL,"+
            "done INTEGER NOT NULL," +
            "user TEXT NOT NULL)";

    private Boolean isNew = false;
    private Boolean isUpdated = false;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *Création de la base de données si inexistante
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Création de la table
        sqLiteDatabase.execSQL(TACHE_TABLE_SQL);
        this.isNew = true;
    }

    /**
     *Mise à jour de la base de données
     * si la version sur le téléphone est inférieure à la version en cours
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersionNumber, int newVersionNumber) {
        this.isUpdated = true;

        /**supprime la table si existe
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS taches");
        //table se crée et se met à jour
        this.onCreate(sqLiteDatabase);
        this.isUpdated = true;*/
    }

    public Boolean isNew() {
        return isNew;
    }

    public Boolean isUpdated() {
        return isUpdated;
    }
}
