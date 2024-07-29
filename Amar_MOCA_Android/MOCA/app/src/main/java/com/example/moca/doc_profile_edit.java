package com.example.moca;

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

public class doc_profile_edit extends AppCompatActivity {
    String image, id, name, email, password, designation;
    EditText nameedit, emailEdit, passwordEdit, designationEdit;
    CircleImageView imgprofile;
    Bitmap img;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile_edit);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        save = findViewById(R.id.saveButton);
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        designation = intent.getStringExtra("designation");
        image = intent.getStringExtra("profile");
        nameedit = findViewById(R.id.doc_name_edit);
        emailEdit = findViewById(R.id.doc_email_edit);
        imgprofile = findViewById(R.id.doc_profile_edit);
        passwordEdit = findViewById(R.id.doc_password_edit);
        designationEdit = findViewById(R.id.doc_designation_edit);
        nameedit.setText(name);
        emailEdit.setText(email);
        passwordEdit.setText(password);
        designationEdit.setText(designation);
        Picasso.get().load(Api.api + image).into(imgprofile);
        imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new sendDoctorDetails().execute(nameedit.getText().toString(), emailEdit.getText().toString(), passwordEdit.getText().toString(), designationEdit.getText().toString());

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
                    Bitmap img = (Bitmap) extras.get("data");
                    imgprofile.setImageBitmap(img);
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
                                doc_profile_edit.this.getContentResolver(),
                                selectedImageUri
                        );
                        img = bitmap;
                        imgprofile.setImageBitmap(img);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    private class sendDoctorDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            BufferedReader reader = null;
            try {
                // URL of your PHP script
                URL url = new URL(Api.api+"doctor_profile_update.php");

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
                String[] keys = {"name", "email", "password", "designation"};
                for (int i = 0; i < keys.length; i++) {
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + keys[i] + "\"\r\n\r\n");
                    outputStream.writeBytes(params[i] + "\r\n");
                }

                // Write profile pic data
                if (img != null) {
                    String randomId = generateRandomId();
                    outputStream.writeBytes("--" + "*****" + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"doctor_img\";filename=\"" + randomId + ".jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    outputStream.write(bitmapToByteArray(img));
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
                        Toast.makeText(doc_profile_edit.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(doc_profile_edit.this, doctor_profile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error occurred
                        Toast.makeText(doc_profile_edit.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(doc_profile_edit.this, doctor_profile.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // JSON parsing error
                    Toast.makeText(doc_profile_edit.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Null response received
                Toast.makeText(doc_profile_edit.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }



        private byte[] bitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
    }
}