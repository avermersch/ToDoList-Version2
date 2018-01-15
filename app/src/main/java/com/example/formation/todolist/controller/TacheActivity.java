package com.example.formation.todolist.controller;

import android.app.ActionBar;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.formation.todolist.R;
import com.example.formation.todolist.model.DatabaseHandler;
import com.example.formation.todolist.model.Tache;
import com.example.formation.todolist.model.TacheDAO;


public class TacheActivity extends AppCompatActivity {

    private EditText editTextTacheName;

    /**
     * Création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tache);

        editTextTacheName = findViewById(R.id.editTextTache);

        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     *Validation du formulaire
     */
    public void onValidTache(View view){
        String tacheName = this.editTextTacheName.getText().toString();

        if (tacheName.trim().equals("")){
            String message = "La tâche ne peut être vide";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Tache tache = new Tache(tacheName);
            this.processForm(tache);
        }
    }

    /**
     * Traitement des données du formulaire
     */
    private void processForm(Tache tache){
        DatabaseHandler db = new DatabaseHandler( this);
        TacheDAO dao = new TacheDAO(db);
        String message;

        try {
            dao.persist(tache);
            setResult(RESULT_OK);
            message = "Tâche enregistrée";
        } catch (SQLiteException ex) {
            setResult(RESULT_CANCELED);
            Log.d("DEBUG", ex.getMessage());
            message = "Impossible d'enregistrer la tâche";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
