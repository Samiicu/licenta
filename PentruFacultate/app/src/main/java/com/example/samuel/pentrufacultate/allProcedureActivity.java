//package com.example.samuel.pentrufacultate;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//
//
//import com.example.samuel.pentrufacultate.adapters.ProcedureAdapters;
//import com.example.samuel.pentrufacultate.models.ProcedureModel;
//import com.example.samuel.pentrufacultate.network.LoginHelper;
//
//import java.text.DateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//public class allProcedureActivity extends AppCompatActivity {
//    private ArrayList<ProcedureModel> proceduresList = new ArrayList<>();
//    ArrayAdapter<ProcedureModel> adapterProcedure;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.allprocedure_activity);
//
//
//        ArrayList<String> steps = new ArrayList<>();
//        steps.add("step1");
//        steps.add("step2");
//        steps.add("steps3");
//        proceduresList.add(new ProcedureModel("Pro1",DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()), "ProcedureModel 1 descrption", steps));
//        proceduresList.add(new ProcedureModel("Pro2", DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()), "ProcedureModel 2 descrption", steps));
//        proceduresList.add(new ProcedureModel("Pro3", DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()), "ProcedureModel 3 descrption", steps));
//
//        adapterProcedure = new ProcedureAdapters(this, R.layout.item_procedure_list, proceduresList);
//        ListView appListView = findViewById(R.id.procedureList);
//        appListView.setAdapter(adapterProcedure);
//    }
//
//}
