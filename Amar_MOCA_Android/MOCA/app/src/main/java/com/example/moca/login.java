package com.example.moca;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class login extends AppCompatActivity {
    EditText email;
    private EditText password;
    Button login;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.password_login);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(login.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Execute AsyncTask to perform login operation
                    new LoginTask().execute(userEmail, userPassword);
                }
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        private final String SERVER_URL = Api.api+"login.php/";

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(SERVER_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Construct data to send to server
                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(postData.getBytes());
                outputStream.flush();
                outputStream.close();

                // Receive response from server
                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                reader = new BufferedReader(inputStreamReader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } catch (IOException e) {
                Log.e("Error", "Error communicating with server", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("Error", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    if ("success".equals(status)) {
                        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(login.this, dashboard.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Login Status", status);
                    Log.d("Login Message", message);
                } catch (JSONException e) {
                    Log.e("Error", "Error parsing JSON", e);
                }
            } else {
                Toast.makeText(login.this, "No response from server", Toast.LENGTH_SHORT).show();
                Log.e("Error", "No response from server");
            }
        }
    }
}
