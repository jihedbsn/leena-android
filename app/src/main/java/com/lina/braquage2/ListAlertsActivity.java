package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ListAlertsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ListView listeViewAlerts;
    DatabaseReference databaseReference;
    ArrayList<Alert> liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leena");
        setContentView(R.layout.activity_list_alerts);

        mAuth = FirebaseAuth.getInstance();
        listeViewAlerts=findViewById(R.id.listeViewAlerts);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView bottomNavigationItemView = findViewById(R.id.bottomNavigationViewID);
        bottomNavigationItemView.setSelectedItemId(R.id.listeAlertID);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeID:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.profileItemID:
                        startActivity(new Intent(getApplicationContext(),ProfilActivity.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });

        listeViewAlerts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Alert currentAlertSelected = liste.get(i);
                Intent intent=new Intent(ListAlertsActivity.this,MapsActivity.class);
                intent.putExtra("alert_selected",currentAlertSelected);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.child("Database").child("Alert").orderByChild("statut").equalTo("active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                liste=new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Alert alert=dataSnapshot.getValue(Alert.class);
                    liste.add(alert);
                }
                Collections.reverse(liste);
                AlertAdapter alertAdapter=new AlertAdapter(ListAlertsActivity.this,R.layout.item_user,liste);
                listeViewAlerts.setAdapter(alertAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}