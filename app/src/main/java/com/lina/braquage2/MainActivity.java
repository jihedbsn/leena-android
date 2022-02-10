package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    User user;
    EditText editNom,editEmail,editPassword,editConfirmPassword;
    RadioButton RadioButtonHomme,RadioButtonFemme;
    Button btnInscription,btnDateNaiss;
    int annee,mois,jour;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MOHAMED BEN RABAH , mohamed.benrabah98@gmail.com
        //---------Lanche first time--------------
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, FirstActivity.class));
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        //-----------------------

        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        recupreration();

        Calendar calendrier=Calendar.getInstance();
        annee=calendrier.get(Calendar.YEAR)-18;
        mois=calendrier.get(Calendar.MONTH)+1;
        jour=calendrier.get(Calendar.DAY_OF_MONTH);
        btnDateNaiss.setText(jour+"-"+mois+"-"+annee);

        btnDateNaiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        btnInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inscriptionMethod();
            }
        });

        //FirebaseDatabase database=FirebaseDatabase.getInstance();
        //DatabaseReference myRef=database.getReference("Database");
        //user=new User("fadoua","Femme","20-08-1994","fadwa@gmail.com","bvcxwxc","no-photo");
        //myRef.child("User").push().setValue(user);
        //myRef.child("User").push().setValue(user);
        //myRef.child("text").child("names").child("numbers").setValue("10");
        //myRef.child("text").child("names").push().child("nom").setValue("med");
    }

    private void inscriptionMethod() {
        String nom=editNom.getText().toString().trim();
        String email=editEmail.getText().toString().trim();
        String password=editPassword.getText().toString().trim();
        String passwordConfirm=editConfirmPassword.getText().toString().trim();
        if(nom.length()<3){
            editNom.setError("Nom invalide");
        }
        else if(!isValidEmailId(email)){
            editEmail.setError("Email invalide");
        }
        else if(!RadioButtonHomme.isChecked() && !RadioButtonFemme.isChecked()){
            Toast.makeText(MainActivity.this, "Choisir votre genre !", Toast.LENGTH_SHORT).show();
        }
        else if(password.length()<8){
            editPassword.setError("Mot de passe invalide");
        }
        else if(!passwordConfirm.equals(password)){
            editConfirmPassword.setError("Mot de passe invalide");
        }
        else{
            progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("En cours");
            progressDialog.show();
            progressDialog.setCancelable(false);

            database=FirebaseDatabase.getInstance();
            myRef=database.getReference("Database");
            String sexe="femme";
            if(RadioButtonHomme.isChecked()){
                sexe="homme";
            }
            //myRef.child("User").push().setValue(user);
            String keyID=myRef.child("User").push().getKey();
            //user=new User(keyID,nom,sexe,btnDateNaiss.getText().toString(),email,password,"no-photo");
            user=new User(keyID,nom,sexe,btnDateNaiss.getText().toString(),email,"no-photo");
            myRef.child("User").child(keyID).setValue(user);

            register(email,password);
        }
    }

    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent intent=new Intent(MainActivity.this,NumeroActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Inscription réussite", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Erreur d'inscription",Toast.LENGTH_SHORT).show();
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

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                btnDateNaiss.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        };
        DatePickerDialog datePickerDialog=new DatePickerDialog(MainActivity.this,listener,annee,mois-1,jour);
        datePickerDialog.show();
    }

    private void recupreration() {
        editNom=findViewById(R.id.editNom);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        editConfirmPassword=findViewById(R.id.editConfirmPassword);
        RadioButtonHomme=findViewById(R.id.hommeRadio);
        RadioButtonFemme=findViewById(R.id.femmeRadio);
        btnInscription=findViewById(R.id.BtnInscription);
        btnDateNaiss=findViewById(R.id.btnDateNaiss);
    }

    public void connexion(View view) {
        startActivity(new Intent(this,ConnexionActivity.class));
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /*
    public void getSingleData(View view){
        FirebaseDatabase.getInstance().getReference().child("Database").child("text").child("names").child("numbers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(MainActivity.this, ""+snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    */

    /*String names="";
    public void getMultipleData(View view) {
        database=FirebaseDatabase.getInstance();
        database.getReference().child("Database").child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    names=names+user.getNom()+"-";
                }
                Toast.makeText(MainActivity.this, ""+names, Toast.LENGTH_SHORT).show();
                names="";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    /*String emails="";
    public void queryGetName(View view){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Database").child("User");
        Query query=databaseReference.orderByChild("nom").equalTo("mohamed");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //supprimer tous la racine User
                //snapshot.getRef().removeValue();
                if(snapshot.exists()){
                    emails="";
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user=dataSnapshot.getValue(User.class);
                        emails+=user.getEmail()+"\n";
                    }
                    Toast.makeText(MainActivity.this, ""+emails, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    /*public void deleteUser(View view){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Database").child("User");
        Query query=databaseReference.orderByChild("nom").equalTo("mohamed");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //supprimer tous la racine User
                //snapshot.getRef().removeValue();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        dataSnapshot.getRef().removeValue();
                    }
                    Toast.makeText(MainActivity.this, "Suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}