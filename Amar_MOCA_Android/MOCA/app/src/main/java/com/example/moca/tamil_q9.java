package com.example.moca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class tamil_q9 extends AppCompatActivity {
    private Button[] optionButtons = new Button[4];
    private int selectedOptionIndex = -1;
    private int previousSelectedIndex = -1;
    int count , visual,naming,memory,attention,language,abstraction=0;
    byte[] q2, q3;
    String correctAnswer,id;
    TextView topic, question;
    ImageView img;
    String url = Api.api+"Questions_tamil.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamil_q9);
        Intent intent = getIntent();
        id=intent.getStringExtra("id");
        q2 = intent.getByteArrayExtra("q2");
        q3 = intent.getByteArrayExtra("q3");
        visual = intent.getIntExtra("visual score",0);
        count = intent.getIntExtra("total score",0);
        naming = intent.getIntExtra("naming",0);
        memory = intent.getIntExtra("memory",0);
        attention = intent.getIntExtra("attention",0);
        language=intent.getIntExtra("language",0);
        topic = findViewById(R.id.ques_type2);
        question = findViewById(R.id.question_6);
        img = findViewById(R.id.question_content);
        optionButtons[0] = findViewById(R.id.option1Button);
        optionButtons[1] = findViewById(R.id.option2Button);
        optionButtons[2] = findViewById(R.id.option3Button);
        optionButtons[3] = findViewById(R.id.option4Button);
        for (int i = 0; i < optionButtons.length; i++) {
            final int index = i;
            optionButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Reset background color for all buttons
                    resetButtonColors();

                    // Highlight the selected button
                    highlightButton(optionButtons[index]);

                    // Save the selected option index and reset the color of the previous button
                    previousSelectedIndex = selectedOptionIndex;
                    selectedOptionIndex = index;

                    // Reset the color of the previous button
                    if (previousSelectedIndex != -1) {
                        optionButtons[previousSelectedIndex].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90ee90")));
                    }
                }
            });
        }

        // Set click listener for the "Next" button
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any option is selected
                if (selectedOptionIndex == -1) {
                    Toast.makeText(tamil_q9.this, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the correct answer (you need to replace "CorrectAnswer" with the actual correct answer)


                // Check if the selected option is correct
                String selectedOptionText = optionButtons[selectedOptionIndex].getText().toString();
                if (selectedOptionText.equals(correctAnswer)) {
                    count++;
                    abstraction++;
                    // Display correct answer message
                }
                // Reset selected option index and button colors
                selectedOptionIndex = -1;
                resetButtonColors();
                Intent intent = new Intent(tamil_q9.this, tamil_q10.class);
                intent.putExtra("q2", q2);
                intent.putExtra("q3", q3);
                intent.putExtra("id",id);
                intent.putExtra("total score", count);
                intent.putExtra("visual score", visual);
                intent.putExtra("naming",naming);
                intent.putExtra("memory",memory);
                intent.putExtra("attention",attention);
                intent.putExtra("language",language);
                intent.putExtra("abstraction",abstraction);
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
                Toast.makeText(tamil_q9.this, "Error fetching questions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseJsonResponse(String jsonResponse) {

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");
            displayQuestion(questionsArray.getJSONObject(8));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(tamil_q9.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayQuestion(JSONObject questionObject) {

        try {
            String questionId = questionObject.getString("id");

            String questionText = questionObject.getString("question");
            String questionType = questionObject.getString("type");
            String questionContent = questionObject.getString("ques_content");
            correctAnswer = questionObject.getString("answer");
            JSONArray optionsArray = questionObject.getJSONArray("options");

            topic.setText(questionType);
            question.setText(questionText);
            String image = Api.api + questionContent;
            Picasso.get().load(image).into(img);

            for (int i = 0; i < optionsArray.length(); i++) {


                optionButtons[i].setText(optionsArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(tamil_q9.this, "Error displaying question", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetButtonColors() {
        for (int i = 0; i < optionButtons.length; i++) {

            optionButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90ee90")));
        }
    }

    private void highlightButton(Button button) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00ff00")));
    }
}