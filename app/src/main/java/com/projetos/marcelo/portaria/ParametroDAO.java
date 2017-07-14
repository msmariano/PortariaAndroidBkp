package com.projetos.marcelo.portaria;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_APPEND;
import static android.database.sqlite.SQLiteDatabase.openDatabase;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by msmariano on 14/07/17.
 */

public class ParametroDAO {
    SQLiteDatabase mydatabase;

    public ParametroDAO (){
        mydatabase = openDatabase("/storage/emulated/0/Android/data/com.projetos.marcelo.portaria/portaria.db",null, MODE_APPEND);
    }

    Parametro buscarParamento (String argParametro){
        Parametro retorno = null;
        Cursor c = mydatabase.rawQuery("SELECT campo1,campo2 FROM Parametros WHERE parametro = '"+argParametro+"'", null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            retorno = new Parametro();
            retorno.setParametro(argParametro);
            retorno.setCampo1(c.getString(0));
            retorno.setCampo2(c.getString(1));
        }

        return retorno;
    }
    List<Parametro> listar(){
        List<Parametro> listaParametros = null;
        Cursor c = mydatabase.rawQuery("SELECT parametro,campo1,campo2 FROM Parametros ", null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            listaParametros = new ArrayList<>();
            do {
                Parametro parametro = new Parametro();
                parametro.setParametro(c.getString(0));
                parametro.setCampo1(c.getString(1));
                parametro.setCampo2(c.getString(2));
                listaParametros.add(parametro);
            }while(c.moveToNext());
        }
        return listaParametros;

    }
    public void salvar(Parametro parametro){
    	if( buscarParamento(parametro.getParametro()) == null)
    		mydatabase.execSQL("INSERT INTO Parametros VALUES('"+parametro.getParametro()+"','"+parametro.getCampo1()+"','"+parametro.getCampo2()+"');");
    	else
    		mydatabase.execSQL("UPDATE Parametros SET campo1='"+parametro.getCampo1()+"',campo2='"+parametro.getCampo2()+"' WHERE parametro = '"+parametro.getParametro()+"';");
    		
    }
    public void fechar(){
        mydatabase.close();
    }

}
