package com.example.formation.todolist.model;

import android.database.sqlite.SQLiteException;

import java.util.List;

/**
 * Created by Formation on 11/01/2018.
 */

interface DAOInterface<DTO> {
    DTO findOneById(int id) throws SQLiteException;

    List<DTO> findAll() throws SQLiteException;

    void deleteOneById(Long id) throws SQLiteException;

    void persist(DTO entity);

}
