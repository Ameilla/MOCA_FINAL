package com.example.moca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class results_page extends AppCompatActivity {
    TextView score1, score2, score3, score4, score5, score6, score7, totalScore, interpret;
    private ImageView testingImage, img2;
    EditText comment;
    byte[] q2, q3;
    String id;
    Button add,profile;
    int count, visual, naming, memory, attention, language, abstraction, orientation;
    private String name, age, gender, phoneNo, alterPhoneNo, diagnosis, drug, hippoCampal, cc, p, m, a;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_page);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        q2 = intent.getByteArrayExtra("q2");
        q3 = intent.getByteArrayExtra("q3");
        visual = intent.getIntExtra("visual score", 0)*5;

        naming = intent.getIntExtra("naming", 0)*3;
        memory = intent.getIntExtra("memory", 0)*5;
        attention = intent.getIntExtra("attention", 0)*3;
        language = intent.getIntExtra("language", 0)*3;
        abstraction = intent.getIntExtra("abstraction", 0)*2;
        orientation = intent.getIntExtra("orientation", 0)*6;
        count = intent.getIntExtra("total score", 0);
        testingImage = findViewById(R.id.image1);
        comment = findViewById(R.id.comment);
        add = findViewById(R.id.addComment);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddCommentTask().execute();
            }
        });
        profile = findViewById(R.id.profilebutton);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(results_page.this,Patient_info.class);
                intent.putExtra("id", id);
                intent.putExtra("name",name);
                intent.putExtra("age", age);
                intent.putExtra("sex", gender);
                intent.putExtra("Diagnosis", diagnosis);
                intent.putExtra("patient",p);// Pass sex to next page
                intent.putExtra("drug", drug);
                startActivity(intent);

            }
        });
        img2 = findViewById(R.id.image2);
        score1 = findViewById(R.id.visual_score);
        score2 = findViewById(R.id.naming_score);
        score3 = findViewById(R.id.memory_score);
        score4 = findViewById(R.id.attention_score);
        score5 = findViewById(R.id.language_score);
        score6 = findViewById(R.id.abstraction_Score);
        score7 = findViewById(R.id.orientation_score);
        totalScore = findViewById(R.id.total_Score);
        interpret = findViewById(R.id.interpretation);
        score1.setText(visual + "/5");
        score2.setText(naming+ "/3");
        score3.setText(memory + "/5");
        score4.setText(attention + "/6");
        score5.setText(language + "/3");
        score6.setText(abstraction + "/2");
        score7.setText(orientation + "/6");
        totalScore.setText((visual+naming+memory+attention+language+abstraction+orientation) + "/30");
        if (q2 != null) {
            // Convert byte array to bitmap
            Bitmap drawingBitmap = BitmapFactory.decodeByteArray(q2, 0, q2.length);
            // Set the bitmap to the ImageView
            testingImage.setImageBitmap(drawingBitmap);
        } else {
            Log.e("TestingActivity", "Failed to receive image for q2");
        }
        if (q3 != null) {
            // Convert byte array to bitmap
            Bitmap drawingBitmap = BitmapFactory.decodeByteArray(q3, 0, q3.length);
            // Set the bitmap to the ImageView
            img2.setImageBitmap(drawingBitmap);
        } else {
            Log.e("TestingActivity", "Failed to receive image for q3");
        }
        if (count >= 7 && count <= 8) {
            interpret.setText("Normal Cognitive");
        } else if (count >= 5 && count <= 6) {
            interpret.setText("Mild cognitive impairment");
        } else if (count >= 3 && count <= 4) {
            interpret.setText("Moderate Cognitive impairment");
        } else {
            interpret.setText("Severe Cognitive impairment");
        }
        new FetchPatientDetailsTask().execute(id);
    }
    private class FetchPatientDetailsTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String num = params[0]; // Assuming the 'num' parameter is passed as the first parameter
            JSONObject jsonResponse = null;

            try {
                // URL of your PHP script
                URL url = new URL(Api.api+"view_patient_details_final.php");
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
    public  void onBackPressed(){
        Intent intent = new Intent(results_page.this,dashboard.class);
        startActivity(intent);
    }

    // AsyncTask to perform the network operation in the background
    private class AddCommentTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // Execute the network operation (HTTP POST request) in the background
            return sendResultsToServer();
        }

        @Override
        protected void onPostExecute(String result) {
            handleServerResponse(result);
        }
    }

    private String sendResultsToServer() {
        try {
            // Get the values
            String commentText = comment.getText().toString();

            // Construct the URL of your PHP file
            URL url = new URL(Api.api+"result.php");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");

            // Get the output stream of the connection to write data
            OutputStream os = connection.getOutputStream();

            // Start writing the multipart/form-data content
            writeFormField(os, "result", String.valueOf(count));
            writeFormField(os, "task1", String.valueOf(visual));
            writeFormField(os, "task2", String.valueOf(naming));
            writeFormField(os, "task3", String.valueOf(memory));
            writeFormField(os, "task4", String.valueOf(attention));
            writeFormField(os, "task5", String.valueOf(language));
            writeFormField(os, "task6", String.valueOf(abstraction));
            writeFormField(os, "task7", String.valueOf(orientation));
            writeFormField(os, "iD", id);
            writeFormField(os, "interpretation", interpret.getText().toString());
            writeFormField(os, "comment", commentText);

            String randomFileName1 = generateRandomFileName();
            String randomFileName2 = generateRandomFileName();

            writeImageAsAttachment(os, "image1", q2, randomFileName1 + ".jpg");
            writeImageAsAttachment(os, "image2", q3, randomFileName2 + ".jpg");

            // Finish the multipart/form-data content
            os.write(("--*****--\r\n").getBytes());

            // Close the streams
            os.flush();
            os.close();

            // Get the response from the server
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Return the response
                return response.toString();
            } else {
                // Handle the error response
                Log.e("sendResultsToServer", "HTTP error code: " + responseCode);
            }

            // Disconnect the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleServerResponse(String result) {
        if (result != null && !result.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if ("success".equals(status)) {
                    // Update UI or perform actions for success
                    Toast.makeText(results_page.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Update UI or perform actions for error
                    Toast.makeText(results_page.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                Toast.makeText(results_page.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where result is empty or null
            Toast.makeText(results_page.this, "Error: Empty or null response", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateRandomFileName() {
        // Generate a random value for the file name
        return "file_" + System.currentTimeMillis() + "_" + Math.abs(new Random().nextInt());
    }

    private void writeFormField(OutputStream os, String fieldName, String value) throws Exception {
        os.write(("--*****\r\n" + "Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n" + value + "\r\n").getBytes());
    }

    private void writeImageAsAttachment(OutputStream os, String fieldName, byte[] imageData, String fileName) throws Exception {
        os.write(("--*****\r\n" + "Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fileName + "\"\r\n\r\n").getBytes());
        os.write(imageData, 0, imageData.length);
        os.write("\r\n".getBytes());
    }
}
