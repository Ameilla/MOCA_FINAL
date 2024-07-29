package com.example.moca;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class update_patient extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText nameEditText, ageEditText, genderEditText, phoneNoEditText, alterPhoneNoEditText, diagnosisEditText, drugEditText, hippocampalEditText;
    private ImageButton mriBeforeImageButton, mriAfterImageButton;
    Button update;
    String currentType;
    String id;
    Bitmap profileImage, mrib, mria;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_patient);
        update = findViewById(R.id.updatePatientDetails);


        // Initialize views
        profileImageView = findViewById(R.id.fetch_patient_profile);
        nameEditText = findViewById(R.id.fetch_name);
        ageEditText = findViewById(R.id.age);
        genderEditText = findViewById(R.id.fetch_gender);
        phoneNoEditText = findViewById(R.id.phone_no);
        alterPhoneNoEditText = findViewById(R.id.fetch_alter_ph_no);
        diagnosisEditText = findViewById(R.id.diagnosis);
        drugEditText = findViewById(R.id.drug);
        hippocampalEditText = findViewById(R.id.hippocampal);
        mriBeforeImageButton = findViewById(R.id.mri_before_edit);
        mriAfterImageButton = findViewById(R.id.mri_after_edit);


        // Retrieve data from previous activity
        Intent intent = getIntent();
         id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String phoneNo = intent.getStringExtra("phoneNo");
        String alterPhoneNo = intent.getStringExtra("alterPhoneNo");
        String diagnosis = intent.getStringExtra("diagnosis");
        String drug = intent.getStringExtra("drug");
        String hippocampal = intent.getStringExtra("hippocampal");
        String mriBeforeImageUrl = intent.getStringExtra("mriBefore");
        String mriAfterImageUrl = intent.getStringExtra("mriAfter");
        String profile = intent.getStringExtra("profile");


        // Set retrieved data to views
        nameEditText.setText(name);
        ageEditText.setText(age);
        genderEditText.setText(gender);
        phoneNoEditText.setText(phoneNo);
        alterPhoneNoEditText.setText(alterPhoneNo);
        diagnosisEditText.setText(diagnosis);
        drugEditText.setText(drug);
        hippocampalEditText.setText(hippocampal);

        // Load images using Picasso
        Picasso.get().load(Api.api+ profile).into(profileImageView);
        Picasso.get().load(Api.api + mriBeforeImageUrl).into(mriBeforeImageButton);
        Picasso.get().load(Api.api + mriAfterImageUrl).into(mriAfterImageButton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameValue = nameEditText.getText().toString();
                String ageValue = ageEditText.getText().toString();
                String genderValue = genderEditText.getText().toString();
                String phoneNoValue = phoneNoEditText.getText().toString();
                String alterPhoneNoValue = alterPhoneNoEditText.getText().toString();
                String diagnosisValue = diagnosisEditText.getText().toString();
                String drugValue = drugEditText.getText().toString();
                String hippoCampalValue = hippocampalEditText.getText().toString();
                new SendPatientDetailsTask().execute(nameValue, ageValue, genderValue, phoneNoValue, alterPhoneNoValue, diagnosisValue, drugValue, hippoCampalValue, id);
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = "profile";
                showImageDialog();
            }
        });
        mriBeforeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = "mrib";
                showImageDialog();
            }
        });
        mriAfterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = "mria";
                showImageDialog();
            }
        });
    }

    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else if (which == 1) {
                pickImageFromGallery();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageCaptureLauncher.launch(takePictureIntent);
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageGalleryLauncher.launch(pickIntent);
    }

    private final ActivityResultLauncher<Intent> imageCaptureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");

                    if ("profile".equals(currentType)) {
                        profileImage = bitmap;
                        profileImageView.setImageBitmap(bitmap);

                    } else if ("mrib".equals(currentType)) {
                        mrib = bitmap;
                        mriBeforeImageButton.setImageBitmap(bitmap);
                    } else if ("mria".equals(currentType)) {
                        mria = bitmap;
                        mriAfterImageButton.setImageBitmap(bitmap);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> imageGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                update_patient.this.getContentResolver(),
                                selectedImageUri
                        );
                        if ("profile".equals(currentType)) {
                            profileImage = bitmap;
                            profileImageView.setImageBitmap(bitmap);

                        } else if ("mrib".equals(currentType)) {
                            mrib = bitmap;
                            mriBeforeImageButton.setImageBitmap(bitmap);
                        } else if ("mria".equals(currentType)) {
                            mria = bitmap;
                            mriAfterImageButton.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    private class SendPatientDetailsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            BufferedReader reader = null;
            try {
                // URL of your PHP script
                URL url = new URL(Api.api+"update_patient.php");

                // Open connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");

                // Start content wrapper
                outputStream = new DataOutputStream(connection.getOutputStream());

                // Write text data
                String[] keys = {"name", "age", "gender", "ph_num", "alt_ph_num", "Diagnosis", "Drug", "hippocampal","id"};
                for (int i = 0; i < keys.length; i++) {
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + keys[i] + "\"\r\n\r\n");
                    outputStream.writeBytes(params[i] + "\r\n");
                }

                // Write profile pic data
                if (profileImage != null) {
                    String ra=generateRandomId();
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"patient_img\";filename=\""+ra+".jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    outputStream.write(bitmapToByteArray(profileImage));
                    outputStream.writeBytes("\r\n");
                }
                if (mrib != null) {
                    String dd=generateRandomId();
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"mri_before\";filename=\""+dd+".jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    outputStream.write(bitmapToByteArray(mrib));
                    outputStream.writeBytes("\r\n");
                }
                if (mria != null) {
                    String dc=generateRandomId();
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"mri_after\";filename=\""+dc+".jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    outputStream.write(bitmapToByteArray(mria));
                    outputStream.writeBytes("\r\n");
                }


                // End content wrapper
                outputStream.writeBytes("--" + "*****" + "--" + "\r\n");

                // Get response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                // Close streams and connection
                try {
                    if (outputStream != null) outputStream.close();
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private String generateRandomId() {
            // Generate a random string
            String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; ++i) {
                sb.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
            }

            // Append current timestamp
            sb.append(System.currentTimeMillis());

            return sb.toString();
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.e("da", "sd" + response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    // Handle the response status
                    if ("success".equals(status)) {
                        // Records inserted successfully
                        Toast.makeText(update_patient.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(update_patient.this,patientProfile.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    } else {
                        // Error occurred
                        Toast.makeText(update_patient.this, message, Toast.LENGTH_SHORT).show();
                       finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // JSON parsing error
                    Toast.makeText(update_patient.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Null response received
                Toast.makeText(update_patient.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }

        private byte[] bitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
    }

}
