package com.example.moca;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class doctor_profile extends AppCompatActivity {

    TextView doc_name, doc_email, doc_password, doc_designation;
    CircleImageView profileImg;
    String image, id, name, email, password, designation;
    Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        edit = findViewById(R.id.editProfile);
        new FetchDataTask().execute(Api.api+"doctor_profile.php");
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_profile.this, doc_profile_edit.class);
                intent.putExtra("profile", image);
                intent.putExtra("name", name);
                intent.putExtra("id", id);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("designation", designation);
                startActivity(intent);
            }
        });
        doc_name = findViewById(R.id.doc_name);
        doc_email = findViewById(R.id.doc_email);
        doc_password = findViewById(R.id.doc_password);
        doc_designation = findViewById(R.id.doc_designation);
        profileImg = findViewById(R.id.doc_profile);


    }

    public void onBackPressed() {
        Intent intent = new Intent(doctor_profile.this, dashboard.class);
        startActivity(intent);
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("Error", "Error fetching data", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String id = responseJson.getString("id");
                    name = responseJson.getString("name");
                    email = responseJson.getString("email");
                    password = responseJson.getString("password");
                    designation = responseJson.getString("designation");
                    image = responseJson.getString("image");

                    // Update UI with fetched data
                    doc_name.setText(name);
                    doc_email.setText(email);
                    doc_password.setText(password);
                    doc_designation.setText(designation);

                    Picasso.get().load(Api.api + image).into(profileImg);
                } catch (JSONException e) {
                    Log.e("Error", "Error parsing JSON", e);
                }
            } else {
                Log.e("Error", "No response from server");
            }
        }
    }
}
