package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NumeroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String id,num1,num2,num3;
    EditText numero1,numero2,numero3;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leena");
        setContentView(R.layout.activity_numero);

        mAuth = FirebaseAuth.getInstance();
        numero1=findViewById(R.id.numero1);
        numero2=findViewById(R.id.numero2);
        numero3=findViewById(R.id.numero3);

        //numero1.setCursorVisible(false);

        initialiserNumero();
    }

    private void initialiserNumero() {
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("En cours");
        progressDialog.show();
        progressDialog.setCancelable(false);
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference();
        Query query=databaseReference.child("Database").child("User").orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        user=dataSnapshot.getValue(User.class);
                    }
                    databaseReference.child("Database").child("User").child(user.getId()).child("numeros").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
                                if(map.get("numero1")!=null){
                                    num1=(String) map.get("numero1");
                                    numero1.setText(num1);
                                }
                                if(map.get("numero2")!=null){
                                    num2=(String) map.get("numero2");
                                    numero2.setText(num2);
                                }
                                if(map.get("numero3")!=null){
                                    num3=(String) map.get("numero3");
                                    numero3.setText(num3);
                                }
                            }
                            else{
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent=new Intent(this,ConnexionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.deconnexion){
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(this,ConnexionActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.home){
            Intent intent=new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.parametres){
            Intent intent=new Intent(this,ParametresActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.privacy_policy){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://leena.tn/privacy-policy/"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveNumeros(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email=currentUser.getEmail();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Database").child("User");
        Query query=databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user=dataSnapshot.getValue(User.class);
                        id=user.getId();
                    }
                    String num1String=numero1.getText().toString().trim();
                    String num2String=numero2.getText().toString().trim();
                    String num3String=numero3.getText().toString().trim();
                    if(num1String.length()!=0 && num1String.length()!=8){
                        numero1.setError("numero de 8 chiffres");
                        return;
                    }
                    if(num2String.length()!=0 && num2String.length()!=8){
                        numero2.setError("numero de 8 chiffres");
                        return;
                    }
                    if(num3String.length()!=0 && num3String.length()!=8){
                        numero3.setError("numero de 8 chiffres");
                        return;
                    }
                    if(num1String.equals("") && num2String.equals("") && num3String.equals("")){
                        Toast.makeText(NumeroActivity.this, "Il faut choisir au moins un numéro", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //FirebaseDatabase.getInstance().getReference().child("Database").child("User").child(id).child("numeros").child("numero1").setValue(num1String);

                    HashMap hashMap=new HashMap();
                    hashMap.put("numero1",num1String);
                    hashMap.put("numero2",num2String);
                    hashMap.put("numero3",num3String);
                    FirebaseDatabase.getInstance().getReference().child("Database").child("User").child(id).child("numeros").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NumeroActivity.this, "Opération réussite", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NumeroActivity.this,HomeActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    //Toast.makeText(NumeroActivity.this, "id : "+id, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void passer(View view) {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}