package com.example.formation.todolist.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Composant d'accès à la table des tâches
 */
public class TacheDAO implements DAOInterface<Tache> {

    //Gestionnaire de connexion
    private DatabaseHandler db;

    public TacheDAO(DatabaseHandler db){
        this.db = db;
    }

    /**
     * Récupération d'une entité Tache en fonction de sa clef primaire(id)
     */
    @Override
    public Tache findOneById(int id) throws SQLiteException{
        //Exécution de la requête
        String[] params = {String.valueOf(id)};
        String sql = "SELECT id, tache_name, done, user FROM taches WHERE id=?";
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, params);
        //Instanciation de la tache
        Tache Tache = new Tache();

        //Hydratation de la tache
        if (cursor.moveToNext()){
            Tache = hydrateTache(cursor);
        }

        //Fermeture du curseur
        cursor.close();

        return Tache;
    }

    /**
     * Hydratation d'une entité Tache en fonction des données du curseur
     */
    private Tache hydrateTache(Cursor cursor) {
        Tache Tache = new Tache();

        Tache.setId(cursor.getLong(0));
        Tache.setTacheName(cursor.getString(1));
        Tache.setDone(! cursor.getString(2).equals("0"));
        Tache.setUser(cursor.getString(3));

        return Tache;
    }

    /**
     * Requête sur l'ensemble des tâches en base de données
     * @return List<Tache une liste de tache
     */
    @Override
    public List<Tache> findAll() throws SQLiteException {
        //Instanciation de la liste de tache
        List<Tache> TacheList = new ArrayList<>();

        //Exécution de la requête sql
        String sql = "SELECT id, tache_name, done, user FROM taches";
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, null);
        //Boucle sur le curseur
        while(cursor.moveToNext()){
            //Remplissage de la Liste
            TacheList.add(this.hydrateTache(cursor));
        }

        //Fermeture du curseur
        cursor.close();

        return TacheList;
    }

    /**
     * Requête sur les tâches en fonction du statut
     * @return List<Tache une liste de tache
     */
    private List<Tache> findAllByDoneStatus(Boolean done) throws SQLiteException{
        //Instanciation de la liste de tache
        List<Tache> TacheList = new ArrayList<>();

        //Exécution de la requête sql
        String sql = "SELECT id, tache_name, done, user FROM taches WHERE done=?";
        String[] params = {done?"1":"0"};
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, params);
        //Boucle sur le curseur
        while(cursor.moveToNext()){
            TacheList.add(this.hydrateTache(cursor));
        }

        //Fermeture du curseur
        cursor.close();

        return TacheList;

    }

    public List<Tache> findAllPendingTaches() {
        return this.findAllByDoneStatus(false);
    }

    public List<Tache> findAllDoneTaches() {
        return this.findAllByDoneStatus(true);
    }

    /**
     * Suppression d'une tâche en fonction de sa clef primaire
     */
    @Override
    public void deleteOneById(Long id) throws SQLiteException{
        String[] params = {id.toString()};
        String sql = "DELETE FROM taches WHERE id=?";
        this.db.getWritableDatabase().execSQL(sql, params);
    }

    /**
     * Persistence d'une entité
     */
    @Override
    public void persist(Tache entity){
        if(entity.getId() == null){
            this.insert(entity);
        }else {
            this.update(entity);
        }
    }

    /**
     * Constitution d'un tableau de colonnes / valeurs
     * pour l'insertion ou la mise à jour de la table tache
     */
    private ContentValues getContentValuesFromEntity(Tache entity){
        ContentValues values = new ContentValues();
        values.put("tache_name", entity.getTacheName());
        values.put("done", entity.getDoneAsInteger());
        values.put("user", entity.getUser());
        return values;
    }

    /**
     * Insertion dans la base données
     */
    private void insert(Tache entity) {
        Long id = this.db.getWritableDatabase().insert(
                "taches", null,
                this.getContentValuesFromEntity(entity));entity.setId(id);
    }

    /**
     * Mise à jour d'une ligne de la table tache
     */
    private void update(Tache entity) {
        String[] params = {entity.getId().toString()};
        this.db.getWritableDatabase().update("taches",
                this.getContentValuesFromEntity(entity),
                "id=?", params);
    }

    public void insertTodo(SQLiteDatabase db){
        if(this.db.isNew()){
            String sql = "INSERT INTO taches (tache_name, done)  VALUES (?,?)";
            //Compilation de la requête
            SQLiteStatement statement = db.compileStatement(sql);

            //Définition des données et exécution multiples de la requête
            statement.bindString(1, "Sortir le chat");
            statement.bindLong(2, 0);
            statement.executeInsert();

            //Deuxième requête
            statement.bindString(1, "Sortir la poubelle");
            statement.bindLong(2, 0);
            Log.i("toto", "COUCOU");
            statement.executeInsert();
        }
    }

    public void upgrade(){
        SQLiteDatabase db = this.db.getWritableDatabase();
        //Début de la transaction
        db.beginTransaction();
        try{
            //La ou les commandes SQL a exécuter dans une transaction
            db.execSQL("ALTER TABLE taches ADD user TEXT");
            db.execSQL("UPDATE taches SET user='anonymous'");
            //Définir que la transaction est un succès
            db.setTransactionSuccessful();
        } catch (Exception ex){
            Log.d("DatabaseHandler", ex.getMessage());
        }finally{
            /**Finalisation de la transaction
             * commit si la transaction est un succes
             * sinon rollback (annulation des opérations
             */
            db.endTransaction();
        }
    }
}
