package com.example.moca;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class DrawingActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button clearButton, nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingView = findViewById(R.id.drawingView);
        clearButton = findViewById(R.id.clearButton);
        nextButton = findViewById(R.id.nextButton);

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
                byte[] byteArray = convertBitmapToByteArray(drawingBitmap);

                // Pass the byte array to the next activity
                Intent intent = new Intent(DrawingActivity.this, testing.class);
                intent.putExtra("drawingByteArray", byteArray);
                startActivity(intent);
            }
        });
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
