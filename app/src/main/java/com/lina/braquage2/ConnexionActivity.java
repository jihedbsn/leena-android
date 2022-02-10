package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class ConnexionActivity extends AppCompatActivity {

    EditText editEmail,EditPassword;
    Button connexionBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        editEmail=findViewById(R.id.editEmail);
        EditPassword=findViewById(R.id.editMp);
        connexionBtn=findViewById(R.id.connexionBtn);

        connexionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidEmailId(editEmail.getText().toString().trim())==false){
                    editEmail.setError("Email invalide");
                }
                else if(EditPassword.getText().toString().trim().length()<8){
                    EditPassword.setError("Mot de passe invalide");
                }
                else{
                    String email=editEmail.getText().toString().trim();
                    String mp=EditPassword.getText().toString().trim();
                    login(email,mp);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(ConnexionActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void login(String email, String mp) {
        mAuth.signInWithEmailAndPassword(email, mp)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent=new Intent(ConnexionActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(ConnexionActivity.this, "verifier votre email et mot de passe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public void inscription(View view) {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}