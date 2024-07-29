package com.example.moca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class tamil_q2 extends AppCompatActivity {
    private DrawingView drawingView;
    private Button clearButton, nextButton;
    TextView question;
    String id;
    int visual, count;
    String url = Api.api+"Questions_tamil.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamil_q2);
        question = findViewById(R.id.ques_image1);

        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        visual = intent.getIntExtra("visual score",0);
        count = intent.getIntExtra("total score",0);
        drawingView = findViewById(R.id.drawingView);
        clearButton = findViewById(R.id.clearDrawing);
        nextButton = findViewById(R.id.save_Drawing);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.clearDrawing();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Capture the drawing as a bitmap
                Bitmap drawingBitmap = drawingView.getDrawingBitmap();

                // Convert bitmap to byte array
                byte[] q2 = convertBitmapToByteArray(drawingBitmap);

                // Pass the byte array to the next activity
                Intent intent = new Intent(tamil_q2.this, tamil_q3.class);
                intent.putExtra("q2", q2);
                intent.putExtra("id",id);
                intent.putExtra("total score", count);
                intent.putExtra("visual score", visual);
                startActivity(intent);
            }
        });
        new PostRequestTask().execute(url);
    }
    public void onBackPressed() {
        // Do nothing (disable back operation)
    }

    private class PostRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;

            try {
                // Create URL
                URL url = new URL(params[0]);

                // Open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");


                // Get the input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                // Read the InputStream
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                jsonResponse = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                // Close the reader
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            if (jsonResponse != null) {
                // Parse the JSON response and update UI
                parseJsonResponse(jsonResponse);
            } else {
                Toast.makeText(tamil_q2.this, "Error fetching questions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseJsonResponse(String jsonResponse) {

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");

            // Assuming the questionsArray has questions in the order you want to display
            // You can customize this based on your data structure
            displayQuestion(questionsArray.getJSONObject(1)); // Display the first question
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(tamil_q2.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayQuestion(JSONObject questionObject) {
        Log.e("ss", "sss" + questionObject);
        try {
            String id = questionObject.getString("id");
            String questionText = questionObject.getString("question");
            question.setText(id+". "+questionText);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(tamil_q2.this, "Error displaying question", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}