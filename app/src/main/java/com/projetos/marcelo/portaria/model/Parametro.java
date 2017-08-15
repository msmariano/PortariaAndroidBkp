package com.projetos.marcelo.portaria.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * Created by msmariano on 14/07/17.
 */

@Table
public class Parametro  {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;
    private String parametro;
    private String campo1;
    private String campo2;

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public String getCampo1() {
        return campo1;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public String getCampo2() {
        return campo2;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }

    public Parametro(){

    }



}
