package com.lina.braquage2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {

    Button suivantBtn;
    FragmentManager fragmentManager;
    PaperOnboardingFragment paperOnboardingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        setTitle("Leena");
        suivantBtn=findViewById(R.id.suivantBtn);
        suivantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fragmentManager=getSupportFragmentManager();
        paperOnboardingFragment=PaperOnboardingFragment.newInstance(getDataForOnBoarding());
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentOne,paperOnboardingFragment);
        fragmentTransaction.commit();

    }

    private ArrayList<PaperOnboardingPage> getDataForOnBoarding() {
        PaperOnboardingPage src1=new PaperOnboardingPage("Bienvenu","Merci à vous de choisir Lina\n" +
                "comme application\n" +
                "Nous sommes la pour vous protéger.\n", Color.parseColor("#ffffff"),R.drawable.p1,R.drawable.p1);
        PaperOnboardingPage src2=new PaperOnboardingPage("Soyez en sécurité !","Grâce à cette application, vous pouvez lancer des alertes en cas d'agression instantané qui peuvent vous sauver la vie.", Color.parseColor("#ffffff"),R.drawable.p2,R.drawable.p2);
        PaperOnboardingPage src3=new PaperOnboardingPage("Protéger les autres !","Vous pouvez même prendre l'initiative\n" +
                "et protéger des personnes qui sont\n" +
                "en danger et à proximité de vous.", Color.parseColor("#ffffff"),R.drawable.p3,R.drawable.p3);

        PaperOnboardingPage src4=new PaperOnboardingPage(null,null, Color.parseColor("#ffffff"),R.drawable.logoleena,R.drawable.logoleena);
        ArrayList<PaperOnboardingPage> arrayList=new ArrayList<>();
        arrayList.add(src4);
        arrayList.add(src1);
        arrayList.add(src2);
        arrayList.add(src3);
        return arrayList;
    }
}