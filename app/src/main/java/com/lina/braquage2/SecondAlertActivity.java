package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondAlertActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String idAlert,num1="",num2="",num3="",idUser,text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leena");
        setContentView(R.layout.activity_second_alert);

        mAuth = FirebaseAuth.getInstance();
        idAlert=getIntent().getStringExtra("idAlert");
        num1=getIntent().getStringExtra("num1");
        num2=getIntent().getStringExtra("num2");
        num3=getIntent().getStringExtra("num3");
        idUser=getIntent().getStringExtra("idUser");
        text=getIntent().getStringExtra("text");
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
        else if(item.getItemId()==R.id.parametres){
            Intent intent=new Intent(this,ParametresActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.home){
            Intent intent=new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.privacy_policy){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://leena.tn/privacy-policy/"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void stopAllert(View view) {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void etape3starts(View view) {
        //send message
        FirebaseDatabase.getInstance().getReference().child("Database").child("User").child(idUser).child("numeros").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    SmsManager smsManager=SmsManager.getDefault();
                    if(!text.equals("")){
                        //text=text.replaceAll("d'urgence","d'urgence extreme");
                        text=text.replaceAll("de soupcon","d'urgence extreme");
                    }
                    if(!num1.equals("")){
                        smsManager.sendTextMessage(num1,null,text,null,null);
                    }
                    if(!num2.equals("")){
                        smsManager.sendTextMessage(num2,null,text,null,null);
                    }
                    if(!num3.equals("")){
                        smsManager.sendTextMessage(num3,null,text,null,null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------------
        firebaseDatabase=FirebaseDatabase.getInstance();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateActu=formatter.format(date);
        databaseReference = firebaseDatabase.getReference("Database").child("Alert").child(idAlert).child("etapes").child("date_etape3");
        databaseReference.setValue(dateActu);
        firebaseDatabase.getReference("Database").child("Alert").child(idAlert).child("etat").setValue("Urgence extréme");
        Intent intent=new Intent(this,ThirdAlertActivity.class);
        startActivity(intent);
        finish();
    }
}