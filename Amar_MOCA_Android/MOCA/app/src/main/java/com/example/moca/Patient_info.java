package com.example.moca;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class Patient_info extends AppCompatActivity {
    TextView name_P, age_P, sex_P, diagnosis_P, drug_P;
    ImageView profile_P;
    private PopupWindow popupWindow;
    Button takeTest, testResults, Details;
    String id, name, age, sex, diagnosis, profile, drug;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        takeTest = findViewById(R.id.takeTest);
        testResults = findViewById(R.id.testResults);
        Details = findViewById(R.id.patientDetails);
        Intent intent = getIntent();
        name_P = findViewById(R.id.nameInfo);
        age_P = findViewById(R.id.ageI);
        sex_P = findViewById(R.id.gend);
        diagnosis_P = findViewById(R.id.diagnoInf);
        drug_P = findViewById(R.id.drug);
        profile_P = findViewById(R.id.patient_profile);
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        age = intent.getStringExtra("age"); // 0 is the default value if "age" is not found
        sex = intent.getStringExtra("sex");
        diagnosis = intent.getStringExtra("Diagnosis");
        profile = intent.getStringExtra("patient"); // Assuming profile photo is a byte array
        drug = intent.getStringExtra("drug");
        name_P.setText(name);
        age_P.setText(age);
        sex_P.setText(sex);
        diagnosis_P.setText(diagnosis);
        drug_P.setText(drug);
        Picasso.get().load(Api.api + profile).into(profile_P);
        takeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showCustomPopup();
            }
        });
        testResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_info.this, testResults.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        Details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_info.this, patientProfile.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }
    public  void onBackPressed(){
        Intent intent = new Intent(Patient_info.this,dashboard.class);
        startActivity(intent);
    }
    private void showCustomPopup() {
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Set up the PopupWindow
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Set your custom message in the TextView inside the popup
        // Set up the close button in the popup
        Button english = popupView.findViewById(R.id.english);
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the popup when the close button is clicked
                popupWindow.dismiss();
                Intent intent = new Intent(Patient_info.this, q1.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });
        Button tamil = popupView.findViewById(R.id.tamil);
        tamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the popup when the close button is clicked
                popupWindow.dismiss();
                Intent intent = new Intent(Patient_info.this, tamil_q1.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
}