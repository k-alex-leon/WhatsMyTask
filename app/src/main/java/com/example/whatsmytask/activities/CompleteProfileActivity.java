package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputName;
    Button mButtonConfirm;
    //inicializando var para la creacion del usuario
    AuthProvider mAuthProvider;
    //inicializando para el registro de datos en la bd
    UsersProvider mUserProvider;
    // spot aler dialog
    //le muestra al usuerio que debe esperar mientras termina un proceso
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);


        mTextInputName = findViewById(R.id.textInputUserName);
        mButtonConfirm = findViewById(R.id.btnConfirm);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();

        // cuadro de carga
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false).build();


        // el btn register inicia metodo register();
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    //se adquiere los valores del form
    //.getText() = para adquirir el texto del campo
    //.toString = transformar el texto en una cadena
    private void register() {
        String userName = mTextInputName.getText().toString();

        //se realiza una validacion
        //si falta algun campo...
        if (!userName.isEmpty()){
            updateUser(userName);
         }else{
            Toast.makeText(this, "Oops it seems that something is missing", Toast.LENGTH_LONG).show();
        }
    }

    // Metodo de creacion de user en la bd
    private void updateUser(String userName) {
        //Estamos adquiriendo el id para utilizarlo en el .document()
        String id = mAuthProvider.getUid();
        //creando map para almacenar los valores del user

        User user = new User();
        user.setUserName(userName);
        user.setId(id);
        user.setTimestamp(new Date().getTime());
        mDialog.show();
        //collection es la coleccion de la bd
        //.document() = agrega por el id en la coleccion seleccionada
        //.update = para agregar el username sin sobrescribir los datos registrados
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(CompleteProfileActivity.this, "Error creating user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}