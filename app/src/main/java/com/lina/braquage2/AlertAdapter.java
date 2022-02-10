package com.lina.braquage2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlertAdapter extends ArrayAdapter<Alert> {

    Context context;
    int resource;

    public AlertAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Alert> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        Alert currentAlert = getItem(position);
        TextView MessageAlertView = convertView.findViewById(R.id.MessageAlertView);
        TextView heureAletView = convertView.findViewById(R.id.heureAletView);
        TextView aideTextView = convertView.findViewById(R.id.aideTextView);
        TextView locationView = convertView.findViewById(R.id.locationView);
        ImageView desableView = convertView.findViewById(R.id.desableView);
        ImageView carteMapView = convertView.findViewById(R.id.carteMapView);
        ImageView imageUserView = convertView.findViewById(R.id.imageUserView);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String email = firebaseUser.getEmail();
        heureAletView.setText("Heure d'alerte : " + currentAlert.getDate());
        locationView.setText("à " + currentAlert.getGeoposition());
        String idUser = currentAlert.getIdUser();
        FirebaseDatabase.getInstance().getReference().child("Database").child("User").child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (!user.getEmail().equals(email)) {
                    desableView.setVisibility(View.GONE);
                }
                if (user.getSexe().equals("homme")) {
                    aideTextView.setText("Aidez-le !");
                } else {
                    aideTextView.setText("Aidez-la !");
                }
                String etat = currentAlert.getEtat();
                if (etat != null) {
                    if (etat.equals("Soupcon")) {
                        MessageAlertView.setText(user.getNom() + " est en état de " + etat);
                    } else {
                        MessageAlertView.setText(user.getNom() + " est en état d'" + etat);
                    }
                }
                if(!user.getPhoto().equals("no-photo") && user.getPhoto()!=null){
                    Glide.with(context).load(user.getPhoto()).into(imageUserView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        desableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Voulez vous arréter l'alerte !.");
                builder1.setTitle("Stoper l'alerte");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference();
                                databaseReference.child("Database").child("Alert").child(currentAlert.getId()).child("statut").setValue("desactive");
                                Toast.makeText(context, "Alert arrété !", Toast.LENGTH_SHORT).show();
                            }
                        });

                builder1.setNegativeButton(
                        "Non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();



                /*FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child("Database").child("Alert").child(currentAlert.getId()).child("statut").setValue("desactive");
                Toast.makeText(context, "Alert arrété !", Toast.LENGTH_SHORT).show();*/
            }
        });

        carteMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "http://maps.google.com/maps?saddr=" + null + "," + null + "&daddr=" + Double.parseDouble(currentAlert.getLatitude()) + "," + Double.parseDouble(currentAlert.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
