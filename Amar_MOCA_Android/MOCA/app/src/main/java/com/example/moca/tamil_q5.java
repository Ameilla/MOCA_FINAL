package com.example.moca;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class tamil_q5 extends AppCompatActivity {
    int correctAnswer;
    ImageButton[] optionButtons = new ImageButton[4];
    String id;
    byte[] q2, q3;
    TextView qno, topic, question;
    int visual, count, naming ,memory=0;
    private int selectedOptionIndex = -1;
    private int previousSelectedIndex = -1;
    Button nextButton_new;
    String url = Api.api+"Questions_tamil.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamil_q5);
        topic = findViewById(R.id.ques_type2);
        question = findViewById(R.id.question_5);
        nextButton_new = findViewById(R.id.nextButton);

        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        q2 = intent.getByteArrayExtra("q2");
        q3 = intent.getByteArrayExtra("q3");
        visual = intent.getIntExtra("visual score",0);
        count = intent.getIntExtra("total score",0);
        naming = intent.getIntExtra("naming",0);
        optionButtons[0] = findViewById(R.id.imageButton1);
        optionButtons[1] = findViewById(R.id.imageButton2);
        optionButtons[2] = findViewById(R.id.imageButton3);
        optionButtons[3] = findViewById(R.id.imageButton4);
        for (int i = 0; i < optionButtons.length; i++) {
            final int index = i;
            optionButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetButtonColors();
                    highlightButton(optionButtons[index]);
                    previousSelectedIndex = selectedOptionIndex;
                    selectedOptionIndex = index;
                    if (previousSelectedIndex != -1) {
                        optionButtons[previousSelectedIndex].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    }
                }
            });
        }
        nextButton_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any option is selected
                if (selectedOptionIndex == -1) {
                    Toast.makeText(tamil_q5.this, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedOptionIndex == (correctAnswer - 1)) {

                    count++;
                    memory++;
                }
                selectedOptionIndex = -1;
                resetButtonColors();
                Intent intent = new Intent(tamil_q5.this, tamil_q6.class);
                intent.putExtra("q2", q2);
                intent.putExtra("q3", q3);
                intent.putExtra("id",id);
                intent.putExtra("total score", count);
                intent.putExtra("visual score", visual);
                intent.putExtra("naming",naming);
                intent.putExtra("memory",memory);
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
                Toast.makeText(tamil_q5.this, "Error fetching questions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseJsonResponse(String jsonResponse) {

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");
            displayQuestion(questionsArray.getJSONObject(4));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(tamil_q5.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayQuestion(JSONObject questionObject) {

        try {

            String questionId = questionObject.getString("id");
            String questionText = questionObject.getString("question");
            String questionType = questionObject.getString("type");
            String questionContent = Api.api + questionObject.getString("ques_content");
            String answer = questionObject.getString("answer");
            correctAnswer = Integer.parseInt(answer);
            JSONArray optionsArray = questionObject.getJSONArray("options");
            Log.e("ji","options" + optionsArray);
            topic.setText(questionType);
            question.setText(questionText);


            for (int i = 0; i < optionsArray.length(); i++) {
                String image = Api.api + optionsArray.getString(i);
                Picasso.get().load(image).into(optionButtons[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(tamil_q5.this, "Error displaying question", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetButtonColors() {
        for (int i = 0; i < optionButtons.length; i++) {
            if (i != selectedOptionIndex) {
                optionButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            }
        }
    }


    private void highlightButton(ImageButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00ff00")));
    }
}