package com.projetos.marcelo.portaria;

import java.util.Date;

/**
 * Created by msmariano on 14/07/17.
 */

public class Trace {
	private String endereco;
	private Double latitude;
	private Double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trace trace = (Trace) o;

        if (!endereco.equals(trace.endereco)) return false;
        if (!latitude.equals(trace.latitude)) return false;
        if (!longitude.equals(trace.longitude)) return false;
        return data.equals(trace.data);

    }

    @Override
    public int hashCode() {
        int result = endereco.hashCode();
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    public String getEndereco() {

        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    private Date data;
    
}
