package com.example.jaroga.applogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelosdatos.Model;

public class RealTime extends AppCompatActivity implements View.OnClickListener{

    Spinner spGroup, spLecture;
    EditText txtActivity;
    Button btnSave, btnDelete, btnUpdate;
    RecyclerView reciclerView;

    //Definir las variables de conexion a la base no-sql
    FirebaseDatabase firebaseDatabase;
    DatabaseReference modelClass;
    String idSeleccionado;

    String[] grupos= {"TI-701","AG-701","GE-701","IN-701","ME701"};
    String[] materias= {"Calculo I","Calculo II","Ecuaciones Dif","Colorimetria","Comunicacion Asertiva"};

    public ArrayList<Model> list = new ArrayList<>();
    public MyRecyclerViewHolder myRecyclerViewHolder;

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);

        firebaseDatabase = FirebaseDatabase.getInstance();
        modelClass = firebaseDatabase.getReference("Model");

        spGroup = findViewById(R.id.spGrupo);
        spLecture = findViewById(R.id.spMateria);
        txtActivity = findViewById(R.id.txtActividad);
        btnSave = findViewById(R.id.btnGuardar);
        btnDelete = findViewById(R.id.btnEliminar);
        btnUpdate = findViewById(R.id.btnActualizar);
        reciclerView = findViewById(R.id.recycler_view);

        String IdSeleccionado;
        //Asignar eventos
        reciclerView.setLayoutManager(new LinearLayoutManager(this));

        //Llenar Spinners
        spGroup.setAdapter(new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,grupos));

        spLecture.setAdapter(new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,materias));

        btnSave.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);


        getDataFromFirebase();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGuardar:
                //Tap en el boton guardar
                addNode();
                break;
            case R.id.btnEliminar:
                //Tap en el boton eliminar
                deleteNode(idSeleccionado);
                break;
            case R.id.btnActualizar:
                //Tap en el boton actualizar
                updateNode(idSeleccionado);

        }
    }

    public void addNode(){
        //Recolectar los datos del formulario
        //Grupo
        String datosGrupo = spGroup.getSelectedItem().toString();
        String datosMateria = spLecture.getSelectedItem().toString();
        String datosActividad = txtActivity.getText().toString().trim();
        if (datosActividad.isEmpty()){
            txtActivity.setError("Llenar campo");
            txtActivity.setFocusable(true);
        }else{
            //Agregamos el dato a firebase
            //Consultamos la base donde se agregan los elementos
            String idDatabase = modelClass.push().getKey();
            //Instancia de nuestro modelo de datos, para guardar informacion
            Model myActivity = new Model(idDatabase, datosGrupo, datosMateria, datosActividad);
            //Guardar en la base de datos de firebase
            modelClass.child("Lectures").child(idDatabase).setValue(myActivity);
            Toast.makeText(getApplicationContext(), "Agregado correctamete", Toast.LENGTH_SHORT).show();
        }
    }

    //Consultar datos de la base de datos de realtime almacenada en firebase
    public void getDataFromFirebase(){
        modelClass.child("Lectures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    list.clear();
                    //Procesar la información que recolectamos de firebase
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String id = ds.child("id").getValue().toString();
                        String group = ds.child("group").getValue().toString();
                        String lecture = ds.child("lecture").getValue().toString();
                        String activity = ds.child("activity").getValue().toString();

                        list.add(new Model(id,group,lecture,activity));
                    }

                    //Llenar el recycler view
                    myRecyclerViewHolder= new MyRecyclerViewHolder(list);
                    reciclerView.setAdapter(myRecyclerViewHolder);

                    myRecyclerViewHolder.setOnCLickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), ":" + list.get(reciclerView.getChildAdapterPosition(v)).getId(),Toast.LENGTH_SHORT).show();

                            txtActivity.setText(list.get(reciclerView.getChildAdapterPosition(v)).getActivity());
                            txtActivity.setFocusable(true);
                            idSeleccionado = list.get(reciclerView.getChildAdapterPosition(v)).getId();

                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void deleteNode(String id){
        modelClass.child("Lectures").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Elemento eliminado",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No se pudo eliminar, intenta nuevamente",
                        Toast.LENGTH_SHORT);
            }
        });
    }

    public void updateNode(String id){
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("activity", "actualizado");
        dataMap.put("group","actualizado");
        dataMap.put("lecture","actualizado");

        modelClass.child("Lectures").child(id).updateChildren(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Actualizado correctamente",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No se pudo actualizar, intente de nuevo",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
