package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    ImageView mbtnButtonBack;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;

    Button mButtonRegister;
    //inicializando var para la creacion del usuario
        AuthProvider mAuthprovider;
    //inicializando para el registro de datos en la bd
        UsersProvider mUsersProvider;
    // spot aler dialog
    //le muestra al usuario que debe esperar mientras termina un proceso
    // AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mbtnButtonBack = findViewById(R.id.imageRegisterGoBack);
        mTextInputName = findViewById(R.id.textInputUserName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        mButtonRegister = findViewById(R.id.btnRegister);

        mAuthprovider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        // cuadro de carga
        /** mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false).build(); **/

        // Accion de la flecha sup izq
        //El metodo finish devuelve a la pagina anterior
        mbtnButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // el btn register inicia metodo register();
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRegister();
            }
        });
    }

    //------------ METODOS -------------//

    //se adquiere los valores del form
    //.getText() = para adquirir el texto del campo
    //.toString = transformar el texto en una cadena
    private void validateRegister() {
        String userName = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();

        //se realiza una validacion
        //si falta algun campo...
        if (!userName.isEmpty() && !email.isEmpty() &&
                !password.isEmpty() && !confirmPassword.isEmpty()){

            if (isEmailValid(email)){
                //validando si las pass son iguales
                if (password.equals(confirmPassword)){
                    //validando el num de caracteres del pass
                    if (password.length()>= 6){
                        createUser(email, password, userName);
                    }else{
                        Toast.makeText(this, "The password is too short", Toast.LENGTH_LONG).show();
                    }
                }else{
                        Toast.makeText(this, "Passwords must be the same", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Oops it seems that something is missing", Toast.LENGTH_LONG).show();
        }
    }
    // Metodo de creacion de user en la bd
    private void createUser(String email, String password, String userName) {
        // mDialog.show();
        mAuthprovider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //Estamos adquiriendo el id para utilizarlo en el .document()
                    String id = mAuthprovider.getUid();
                    // pasando los datos desde el modelo de user al mUserProvider
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUserName(userName);
                    // new Date().getTime() = determina la fecha exacta de creacion de usuario
                    user.setTimestamp(new Date().getTime());
                    //collection es la coleccion de la bd
                    //.document() = agrega por el id en la coleccion seleccionada
                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // mDialog.dismiss();
                            if (task.isSuccessful()){

                                Toast.makeText(RegisterActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                //limpiando las activities para que el user no pueda volver a la de registro
                                // cierra la app si el user intenta volver a la actividad anterior
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            }else{
                                Toast.makeText(RegisterActivity.this, "Error creating user", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{
                    // mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error creating user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // METODO QUE VERIFICA SI EL EMAIL ESTA BIEN ESCRITO
    // metodo boolean = retorna solo dos resultados v o f

    public boolean isEmailValid(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}