package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView alert1;
    String idUser,zone_place,num1="",num2="",num3="",text="";
    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leena");
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();
        alert1=findViewById(R.id.IDalert1);
        alert1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert();
            }
        });

        BottomNavigationView bottomNavigationItemView = findViewById(R.id.bottomNavigationViewID);
        bottomNavigationItemView.setSelectedItemId(R.id.homeID);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.listeAlertID:
                        startActivity(new Intent(getApplicationContext(), ListAlertsActivity.class));
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
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void fetchLocation(){
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.SEND_SMS},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation=location;
                    //Toast.makeText(HomeActivity.this, currentLocation.getLatitude()+","+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    try {
                        Geocoder geo = new Geocoder(HomeActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geo.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.isEmpty()) {
                            zone_place="En attend de localisation";
                            //Toast.makeText(HomeActivity.this, "En attend de localisation", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //recuperer l'adresse
                            zone_place=addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                            //Toast.makeText(HomeActivity.this, addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fetchLocation();
            }
        }
    }

    public void alert(){
        fetchLocation();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("Database").child("User");
        Query query=databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        user=dataSnapshot.getValue(User.class);
                        idUser=user.getId();
                    }
                    databaseReference=firebaseDatabase.getReference().child("Database").child("Alert");
                    String idAlert=databaseReference.push().getKey();
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String dateActu=formatter.format(date);
                    Alert alert=new Alert(idAlert,dateActu,zone_place,""+currentLocation.getLongitude(),""+currentLocation.getLatitude(),idUser,"Soupcon","active");
                    databaseReference.child(idAlert).setValue(alert);
                    HashMap hashMap=new HashMap();
                    hashMap.put("date_etape1",dateActu);
                    //****send message*************************************
                    FirebaseDatabase.getInstance().getReference().child("Database").child("User").child(idUser).child("numeros").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                SmsManager smsManager=SmsManager.getDefault();
                                text=user.getNom()+" est en etat de soupcon,\nMap : http://maps.google.com/maps?saddr=null,null&daddr="+currentLocation.getLatitude()+","+currentLocation.getLongitude();
                                Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
                                if(map.get("numero1")!=null){
                                    num1=(String) map.get("numero1");
                                    smsManager.sendTextMessage(num1,null,text,null,null);
                                }
                                if(map.get("numero2")!=null){
                                    num2=(String) map.get("numero2");
                                    smsManager.sendTextMessage(num2,null,text,null,null);
                                }
                                if(map.get("numero3")!=null){
                                    num3=(String) map.get("numero3");
                                    smsManager.sendTextMessage(num3,null,text,null,null);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //************************************************
                    FirebaseDatabase firebaseDatabase2=FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference=firebaseDatabase2.getReference().child("Database").child("Alert").child(idAlert).child("etapes");
                    databaseReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(HomeActivity.this, "alerte lancé", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(HomeActivity.this,FirstAlertActivity.class);
                            intent.putExtra("num1",num1);
                            intent.putExtra("num2",num2);
                            intent.putExtra("num3",num3);
                            intent.putExtra("text",text);
                            intent.putExtra("idUser",idUser);
                            intent.putExtra("idAlert",idAlert);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Toast.makeText(this, "alert lancé", Toast.LENGTH_SHORT).show();
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
        else if(item.getItemId()==R.id.privacy_policy){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://leena.tn/privacy-policy/"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}