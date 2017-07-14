package com.projetos.marcelo.portaria;

import android.database.sqlite.SQLiteDatabase;

import static android.content.Context.MODE_APPEND;
import static android.database.sqlite.SQLiteDatabase.openDatabase;

/**
 * Created by msmariano on 14/07/17.
 */

public class TraceDAO {

    SQLiteDatabase mydatabase;
    TraceDAO(){
        mydatabase = openDatabase("/storage/emulated/0/Android/data/com.projetos.marcelo.portaria/portaria.db",null, MODE_APPEND);
    }

    public void salvar(Trace trace){

    }

}
