package com.lina.braquage2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button updatePorfilID,editDateNaisProfil;
    String email;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;
    ImageView profilIMG;
    TextView textToChangePhoto;
    EditText editNomProfil,editEmailProfil;
    RadioButton editFemmeSexProfil,editHommeSexProfil;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    int Xyear=-1,Xmonth=-1,Xday=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leena");
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        BottomNavigationView bottomNavigationItemView = findViewById(R.id.bottomNavigationViewID);
        bottomNavigationItemView.setSelectedItemId(R.id.profileItemID);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeID:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.listeAlertID:
                        startActivity(new Intent(getApplicationContext(), ListAlertsActivity.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });

        email=mAuth.getCurrentUser().getEmail();
        recuperChamps();
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("En cours");
        progressDialog.show();
        progressDialog.setCancelable(false);
        databaseReference.child("Database").child("User").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        user = dataSnapshot.getValue(User.class);
                    }
                    initialiserProfile(user);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //btn date nais
        editDateNaisProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editDateNaisProfil.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                        Xyear=year;
                        Xmonth=month+1;
                        Xday=dayOfMonth;
                    }
                };
                if(Xyear==-1 && Xmonth==-1 && Xday==-1) {
                    String dateDeNaissance = user.getDateNaissance();
                    int pos1 = dateDeNaissance.indexOf("-");
                    int pos2 = dateDeNaissance.lastIndexOf("-");
                    String jour = dateDeNaissance.substring(0, pos1);
                    System.out.println(jour);
                    String mois = dateDeNaissance.substring((pos1 + 1), pos2);
                    System.out.println(mois);
                    String annee = dateDeNaissance.substring((pos2 + 1), user.getDateNaissance().length());
                    System.out.println(annee);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ProfilActivity.this, listener, Integer.parseInt(annee), Integer.parseInt(mois) - 1, Integer.parseInt(jour));
                    datePickerDialog.show();
                }
                else{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ProfilActivity.this, listener, Xyear, Xmonth-1, Xday);
                    datePickerDialog.show();
                }
            }
        });

        //change photo
        textToChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        //btn update
        updatePorfilID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editNomProfil.getText().toString().trim().length()<3){
                    editNomProfil.setError("Nom invalide");
                    return;
                }
                updateProfile();
            }
        });
    }

    public void recuperChamps(){
        profilIMG=findViewById(R.id.profilIMG);
        textToChangePhoto=findViewById(R.id.textToChangePhoto);
        editNomProfil=findViewById(R.id.editNomProfil);
        editEmailProfil=findViewById(R.id.editEmailProfil);
        editHommeSexProfil=findViewById(R.id.editHommeSexProfil);
        editFemmeSexProfil=findViewById(R.id.editFemmeSexProfil);
        editDateNaisProfil=findViewById(R.id.editDateNaisProfil);
        updatePorfilID=findViewById(R.id.updatePorfilID);
    }

    public void initialiserProfile(User user){
        editNomProfil.setText(user.getNom());
        editEmailProfil.setText(user.getEmail());
        if(user.getSexe().equals("homme")){
            editHommeSexProfil.setChecked(true);
        }
        else{
            editFemmeSexProfil.setChecked(true);
        }
        editDateNaisProfil.setText(user.getDateNaissance());
        if(!user.getPhoto().equals("no-photo") && user.getPhoto()!=null){
            Glide.with(this).load(user.getPhoto()).into(profilIMG);
        }
    }

    private void choosePicture() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            profilIMG.setImageURI(imageUri);
        }
    }

    private void updateProfile() {
        if(imageUri!=null){
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Chargement d'image");
            progressDialog.show();
            progressDialog.setCancelable(false);
            ContentResolver cr=getContentResolver();
            MimeTypeMap mime=MimeTypeMap.getSingleton();
            String fileExtension=mime.getExtensionFromMimeType(cr.getType(imageUri));

            StorageReference fileRef = storageReference.child("imageUser").child(System.currentTimeMillis()+"."+fileExtension);
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String nom=editNomProfil.getText().toString().trim();
                            user.setNom(nom);
                            if(editHommeSexProfil.isChecked()){
                                user.setSexe("homme");
                            }
                            else{
                                user.setSexe("femme");
                            }
                            user.setDateNaissance(editDateNaisProfil.getText().toString());
                            user.setPhoto(uri.toString());
                            databaseReference.child("Database").child("User").child(user.getId()).setValue(user);
                            progressDialog.dismiss();
                            Toast.makeText(ProfilActivity.this, "Modification réussite", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    //double progressPercent=(100.00*snapshot.getBytesTransferred());
                    //progressDialog.setMessage("Chargement : "+progressPercent+"%");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfilActivity.this, "Erreur de chargement d'image", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            user.setNom(editNomProfil.getText().toString());
            if(editHommeSexProfil.isChecked()){
                user.setSexe("homme");
            }
            else{
                user.setSexe("femme");
            }
            user.setDateNaissance(editDateNaisProfil.getText().toString());
            databaseReference.child("Database").child("User").child(user.getId()).setValue(user);
            Toast.makeText(this, "Profil a été modifié", Toast.LENGTH_SHORT).show();
        }
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
}