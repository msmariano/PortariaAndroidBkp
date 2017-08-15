package com.projetos.marcelo.portaria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.projetos.marcelo.portaria.model.Trace;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class TraceActivity extends AppCompatActivity {

    ListView  lvTrace;
    private ArrayAdapter<String> adaptador;
    List<String> listaTrace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);
        this.setTitle("Trace");
        this.lvTrace = (ListView) findViewById(R.id.lvTrace);

        List<Trace> listaTraceObj = SugarRecord.findWithQuery(Trace.class,"select * from trace");
        listaTrace = new ArrayList<>();

        for(Trace trace : listaTraceObj){
            SimpleDateFormat sd = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            listaTrace.add(String.valueOf(trace.getVelocidade())+" "+ sd.format(trace.getData()));
        }

        this.adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaTrace);

        //atribuindo o adaptador ao componente 1
        this.lvTrace.setAdapter(this.adaptador);



    }
}
