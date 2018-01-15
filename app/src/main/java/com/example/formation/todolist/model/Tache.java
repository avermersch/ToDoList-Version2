package com.example.formation.todolist.model;

/**
 * Entité modélisant une tâche
 */
public class Tache {

    private String tacheName;
    private Boolean done = false;
    private Long id;
    private String user_name;

    public Tache() {
    }

    public Tache(String tacheName) { this.tacheName = tacheName; }

    public Tache(String tacheName, Boolean done){
        this.tacheName = tacheName;
        this.done = done;
    }

    public String getTacheName() {
        return tacheName;
    }

    public Tache setTacheName(String tacheName) {
        this.tacheName = tacheName;
        return this;
    }

    public Boolean isDone() { return done; }

    public Tache setDone(Boolean done) {
        this.done = done;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Tache setId(Long id) {
        this.id = id;
        return this;
    }


    public int getDoneAsInteger() {
        if(this.done){
            return 1;
        } else {
            return 0;
        }
    }

    public String getUser_name() {
        return user_name;
    }

    public Tache setUser_name(String user_name) {
        this.user_name = user_name;
        return this;
    }
}
