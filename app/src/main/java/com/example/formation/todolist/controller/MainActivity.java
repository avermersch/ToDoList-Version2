package com.example.formation.todolist.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.formation.todolist.R;
import com.example.formation.todolist.model.DatabaseHandler;
import com.example.formation.todolist.model.Tache;
import com.example.formation.todolist.model.TacheDAO;

import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int TASK_FORM = 1;

    private ListView tacheListView;
    private List<Tache> tacheList;
    private Spinner spinnerStatus;
    private DatabaseHandler db;
    private TacheDAO dao;

    /**
     * Création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////Instanciation de la connexion de base de données
        this.db = new DatabaseHandler(this);
        //Instantciation du DAO pour les taches
        this.dao = new TacheDAO(this.db);

        //Mise à jour de la table
        if(this.db.isUpdated()){
            this.dao.upgrade();
        }

        //Insertion des données
        this.dao.insertTodo(this.db.getWritableDatabase());

        //Référence au widget  ListView sur le layout
        tacheListView = findViewById(R.id.todoListView);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        spinnerStatus.setOnItemSelectedListener(this);

        //this.tacheList = this.dao.findAll();
        tacheListInit();
    }

    /**
     * Initialisation de la liste des tâches
     * dans un composant ListView
     * Les données présentées dépendent de la sélection du statut
     */
    private void tacheListInit() {
        //Récupération du statut sélectionné
        String status = this.spinnerStatus.getSelectedItem().toString();

        //Récupération d'une liste de tâche en fonction du statut
        if(status.equals("Toutes")){
            this.tacheList = this.dao.findAll();
        } else if(status.equals("En cours")){
            this.tacheList = this.dao.findAllPendingTasks();
        } else {
            this.tacheList = this.dao.findAllDoneTasks();
        }

        //Instanciation de l'adapter
        TacheArrayAdapter adapter = new TacheArrayAdapter(this, R.layout.tache_check_view, this.tacheList);
        //Liaison entre l'adapter et la ListView
        this.tacheListView.setAdapter(adapter);
    }

    /**
     * Ouverture du formulaire de Création d'une tâche
     */
    public void onAddTache(View view){
        Intent intentNewTache = new Intent( this, TacheActivity.class);
        startActivityForResult(intentNewTache, TASK_FORM);
    }

    /**
     * Coche d'une case dans la ListView
     * Met à jour l'entité et persiste celle-ci en base de données
     */
    public void onCheck(View v){
        //Récupération de la position taguée
        int position = (int)v.getTag();

        //Récupération de l'état de la case à cocher
        CheckBox check = (CheckBox) v;
        boolean done = check.isChecked();

        //Récupération et modification de l'entité
        Tache tache = this.tacheList.get(position);
        tache.setDone(done);

        //Persistance des données
        this.dao.persist(tache);
    }

    /**
     * Retour du formulaire de création de tâche
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TASK_FORM && resultCode == RESULT_OK){
            //this.tacheList = this.dao.findAll();
            tacheListInit();
        }
    }

    /**
     * Changement de statut
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        tacheListInit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Confirmation de la suppression
     */
    private AlertDialog getConfirmDeleteDialog(final Long id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Voulez-vous vraiment supprimer cette tâche ?");
        //Gestion de la confirmation OK
        dialogBuilder.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dao.deleteOneById(id);
                tacheListInit();
                dialog.dismiss();
            }
        });

        //Gestion de la confirmation KO
        dialogBuilder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        return  dialogBuilder.create();
    }

    /**
     * Clic sur le bouton supprimer
     */
    public void onDelete(View v){
        //Récupération de la position taguée
        int position = (int) v.getTag();
        Tache tache = this.tacheList.get(position);
        //Affichage de la confirmation
        AlertDialog dialog = getConfirmDeleteDialog(tache.getId());
        dialog.show();
    }

    /**
     * ArrayAdapter pour la liste des tâches
     */
    private class TacheArrayAdapter extends ArrayAdapter<Tache> {
        List<Tache> data;
        int layout;
        Activity context;

        /**
         * Constructeur de l'adapter
         */
        public TacheArrayAdapter(@NonNull Activity context, int resource, @NonNull List<Tache> data){
            super(context, resource, data);
            this.context = context;
            this.layout = resource;
            this.data = data;
        }

        /**
         * Affichage d'une ligne
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            //Obtention de la vue
            LayoutInflater inflater = this.context.getLayoutInflater();
            View v = inflater.inflate(this.layout, parent, false);

            //Récupération de l'entité
            Tache currentTache = this.data.get(position);

            //Affichage du texte de la tâche
            TextView textView = v.findViewById(R.id.textViewTacheName);
            textView.setText(currentTache.getTacheName());

            //Affichage de la case à cocher
            CheckBox checkDone = v.findViewById(R.id.checkboxTacheDone);
            checkDone.setChecked(currentTache.isDone());

            //Référence au bouton supprimer
            ImageView deleteButton = v.findViewById(R.id.deleteButton);

            //Le tag permet de transmettre une information au gestinonaire d'évènement
            deleteButton.setTag(position);
            checkDone.setTag(position);

            return v;
        }
    }
}
