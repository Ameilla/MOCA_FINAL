package com.example.moca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class patientProfile extends AppCompatActivity {

    private TextView nameTextView, ageTextView, genderTextView, phoneNoTextView, alterPhoneNoTextView, diagnosisTextView, drugTextView, hippoCampalTextView, label;
    ImageView mri_before, profile, mri_after;
    Button update, delete;
    String id;
    private String name, age, gender, phoneNo, alterPhoneNo, diagnosis, drug, hippoCampal, cc, p, m, a;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        nameTextView = findViewById(R.id.name);
        ageTextView = findViewById(R.id.age);
        genderTextView = findViewById(R.id.gender);
        phoneNoTextView = findViewById(R.id.phone_no);
        alterPhoneNoTextView = findViewById(R.id.alter_ph_no);
        diagnosisTextView = findViewById(R.id.diagnosis);
        drugTextView = findViewById(R.id.drug);
        hippoCampalTextView = findViewById(R.id.hippocampal);
        label = findViewById(R.id.label);
        profile = findViewById(R.id.patient_profile);
        mri_before = findViewById(R.id.mri_before);
        mri_after = findViewById(R.id.mri_after);
        delete = findViewById(R.id.deletePatient);
        // Replace "123" with the actual patient ID
        new FetchPatientDetailsTask().execute(id);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePatient(id);
            }


        });
        update = findViewById(R.id.updatePatient);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patientProfile.this, update_patient.class);

                // Add data to the Intent
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("age", age);
                intent.putExtra("gender", gender);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("alterPhoneNo", alterPhoneNo);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("drug", drug);
                intent.putExtra("hippocampal", hippoCampal);
                intent.putExtra("profile", p);
                intent.putExtra("mriBefore", m);
                intent.putExtra("mriAfter", a);
                intent.putExtra("review", cc);
                startActivity(intent);

                // Start the PatientProfileActivity
                startActivity(intent);
            }
        });


    }

    public void onBackPressed() {
        // Do nothing (disable back operation)
       Intent intent = new Intent(patientProfile.this,Patient_info.class);
        intent.putExtra("id", id);
        intent.putExtra("name",name);
        intent.putExtra("age", age);
        intent.putExtra("sex", gender);
        intent.putExtra("Diagnosis", diagnosis);
        intent.putExtra("patient",p);// Pass sex to next page
        intent.putExtra("drug", drug);
       startActivity(intent);
    }

    private void deletePatient(String id) {
        new DeletePatientTask().execute(id);
    }

    private class DeletePatientTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String id = params[0]; // Assuming the 'id' parameter is passed as the first parameter
            JSONObject jsonResponse = null;

            try {
                // URL of your PHP script
                URL url = new URL(Api.api+"delete_patient_final.php?id=" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Get response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse JSON response
                jsonResponse = new JSONObject(response.toString());

                reader.close();
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);

            if (response != null) {
                try {
                    String status = response.getString("status");
                    String message = response.getString("message");
                    if ("success".equals(status)) {
                        Toast.makeText(patientProfile.this, "" + message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(patientProfile.this,dashboard.class);
                        startActivity(intent);
                    }

                    // Handle response status and message as needed
                    Log.d("DeletePatient", "Status: " + status + ", Message: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Null response received
                Log.d("DeletePatient", "Null response received");
            }
        }
    }

    private class FetchPatientDetailsTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String num = params[0]; // Assuming the 'num' parameter is passed as the first parameter
            JSONObject jsonResponse = null;

            try {
                // URL of your PHP script
                URL url = new URL("http://10.0.2.2/MOCA_AME/view_patient_details_final.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Send POST data
                connection.getOutputStream().write(("num=" + num).getBytes());

                // Get response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse JSON response
                jsonResponse = new JSONObject(response.toString());

                reader.close();
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);

            if (response != null) {
                try {
                    String status = response.getString("status");
                    String message = response.getString("message");

                    if ("success".equals(status)) {
                        // Data found
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);

                            // Extract patient details from JSON data
                            name = data.getString("Name");
                            age = data.getString("Age");
                            gender = data.getString("Gender");
                            phoneNo = data.getString("Phone number");
                            alterPhoneNo = data.getString("Alternate mobile num");
                            diagnosis = data.getString("Diagnosis");
                            drug = data.getString("Drug");
                            hippoCampal = data.getString("Hippocampal");
                            cc = data.optString("Discription");
                            p = data.getString("patient_img");
                            m = data.getString("mri_before");
                            a = data.getString("mri_after");
                            Picasso.get().load("http://10.0.2.2/MOCA_AME/" + p).into(profile);
                            Picasso.get().load("http://10.0.2.2/MOCA_AME/" + m).into(mri_before);
                            Picasso.get().load("http://10.0.2.2/MOCA_AME/" + a).into(mri_after);
                            // Update TextViews with patient details
                            nameTextView.setText(name);
                            ageTextView.setText(age);
                            genderTextView.setText(gender);
                            phoneNoTextView.setText(phoneNo);
                            alterPhoneNoTextView.setText(alterPhoneNo);
                            diagnosisTextView.setText(diagnosis);
                            drugTextView.setText(drug);
                            hippoCampalTextView.setText(hippoCampal);
                            label.setText(cc);
                        }
                    } else {
                        // No results found
                        Log.d("PatientDetails", "No results found: " + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Null response received
                Log.d("PatientDetails", "Null response received");
            }
        }
    }
}
