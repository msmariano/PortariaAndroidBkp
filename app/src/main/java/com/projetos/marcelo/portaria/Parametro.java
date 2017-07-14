package com.projetos.marcelo.portaria;

/**
 * Created by msmariano on 14/07/17.
 */

public class Parametro {
    private String parametro;
    private String campo1;
    private String campo2;

    public String getCampo1() {
        return campo1;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public String getCampo2() {
        return campo2;
    }
    public void setParametros(String parametro,String campo1,String campo2){
    	setParametro(parametro);
    	setCampo1(campo1);
    	setCampo2(campo2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parametro parametro1 = (Parametro) o;

        if (!parametro.equals(parametro1.parametro)) return false;
        if (!campo1.equals(parametro1.campo1)) return false;
        return campo2.equals(parametro1.campo2);

    }

    @Override
    public int hashCode() {
        int result = parametro.hashCode();
        result = 31 * result + campo1.hashCode();
        result = 31 * result + campo2.hashCode();
        return result;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }
}
