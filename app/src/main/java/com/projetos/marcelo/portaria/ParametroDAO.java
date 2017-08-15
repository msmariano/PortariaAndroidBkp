package com.projetos.marcelo.portaria;

import com.orm.SugarRecord;
import com.projetos.marcelo.portaria.model.Parametro;

import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openDatabase;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by msmariano on 14/07/17.
 */

public class ParametroDAO {



    public ParametroDAO (){

    }

    public void setChave(String parametro,String campo1, String campo2){

        Parametro chave = null;

        chave = buscarParamento(parametro);

        if(chave == null) {
            chave = new Parametro();
            chave.setParametro(parametro);
            chave.setCampo1(campo1);
            chave.setCampo2(campo2);
            SugarRecord.save(chave);
        }
        else
        {
            SugarRecord.delete(chave);
            chave.setParametro(parametro);
            chave.setCampo1(campo1);
            chave.setCampo2(campo2);
            SugarRecord.save(chave);

        }


    }



    Parametro buscarParamento (String argParametro){

        List<Parametro> listaParametros =  SugarRecord.find(Parametro.class, "parametro = ? ", argParametro);
        if(listaParametros.size() > 0)
            return listaParametros.get(0);
        else
            return null;
    }
    List<Parametro> listar(){

        List<Parametro> listaParametros = SugarRecord.findWithQuery(Parametro.class, "SELECT * FROM Parametro");
        return listaParametros;

    }
    public void salvar(Parametro parametro){
        SugarRecord.save(parametro);
    		
    }

}
