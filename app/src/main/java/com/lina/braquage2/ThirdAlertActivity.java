package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ThirdAlertActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leena");
        setContentView(R.layout.activity_third_alert);

        mAuth = FirebaseAuth.getInstance();
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
}