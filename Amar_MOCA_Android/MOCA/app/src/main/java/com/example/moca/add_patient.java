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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class add_patient extends AppCompatActivity {
    EditText name, age, gender, phoneNo, alterPhoneNo, diagnosis, drug, hippoCampal;
    Button addPatient, mri, profile_pic;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Bitmap profilePicBitmap;
    private Bitmap mriBitmap;
    String currentImageType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        mri = findViewById(R.id.selectMri);
        profile_pic = findViewById(R.id.selectProfilePic);

        mri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImageType = "mri";
                showImageDialog();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImageType = "profile";
                showImageDialog();
            }
        });

        name = findViewById(R.id.nameEditText);
        age = findViewById(R.id.ageEditText);
        gender = findViewById(R.id.genderEditText);
        phoneNo = findViewById(R.id.phoneNoEditText);
        alterPhoneNo = findViewById(R.id.alterPhoneNoEditText);
        diagnosis = findViewById(R.id.diagnosisEditText);
        drug = findViewById(R.id.drugEditText);
        hippoCampal = findViewById(R.id.hippoCampalEditText);
        addPatient = findViewById(R.id.addPatientButton);

        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameValue = name.getText().toString();
                String ageValue = age.getText().toString();
                String genderValue = gender.getText().toString();
                String phoneNoValue = phoneNo.getText().toString();
                String alterPhoneNoValue = alterPhoneNo.getText().toString();
                String diagnosisValue = diagnosis.getText().toString();
                String drugValue = drug.getText().toString();
                String hippoCampalValue = hippoCampal.getText().toString();

                // Execute AsyncTask to send data to server
                new SendPatientDetailsTask().execute(nameValue, ageValue, genderValue, phoneNoValue, alterPhoneNoValue, diagnosisValue, drugValue, hippoCampalValue);
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

                    if ("mri".equals(currentImageType)) {
                        mriBitmap = bitmap;
                        mri.setText("Mri  Selected");
                    } else if ("profile".equals(currentImageType)) {
                        profilePicBitmap = bitmap;
                        profile_pic.setText("Profile Pic Selected");
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
                                add_patient.this.getContentResolver(),
                                selectedImageUri
                        );
                        if ("mri".equals(currentImageType)) {
                            mriBitmap = bitmap;
                            mri.setText("Mri  Selected");
                        } else if ("profile".equals(currentImageType)) {
                            profilePicBitmap = bitmap;
                            profile_pic.setText("Profile Pic Selected");
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
                URL url = new URL(Api.api+"add_patient_final.php");

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
                String[] keys = {"name", "age", "gender", "phoneNo", "alterPhoneNo", "diagnosis", "drug", "hippoCampal"};
                for (int i = 0; i < keys.length; i++) {
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + keys[i] + "\"\r\n\r\n");
                    outputStream.writeBytes(params[i] + "\r\n");
                }

                // Write profile pic data
                if (profilePicBitmap != null) {
                    String randomId = generateRandomId();
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"patient_img\";filename=\""+randomId+".jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    outputStream.write(bitmapToByteArray(profilePicBitmap));
                    outputStream.writeBytes("\r\n");
                }if (mriBitmap != null) {
                    String randomI = generateRandomId();
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"mri_before\";filename=\""+randomI+".jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    outputStream.write(bitmapToByteArray(mriBitmap));
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
            Log.e("da","sd"+response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    // Handle the response status
                    if ("success".equals(status)) {
                        // Records inserted successfully
                        Toast.makeText(add_patient.this,  message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(add_patient.this,dashboard.class);
                        startActivity(intent);
                    } else {
                        // Error occurred
                        Toast.makeText(add_patient.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(add_patient.this,dashboard.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // JSON parsing error
                    Toast.makeText(add_patient.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Null response received
                Toast.makeText(add_patient.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }

        private byte[] bitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
    }
}
