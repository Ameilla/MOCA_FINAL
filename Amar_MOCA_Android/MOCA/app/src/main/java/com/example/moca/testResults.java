package com.example.moca;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class testResults extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TestResultAdapter adapter;
    String id;
    Button graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        recyclerView = findViewById(R.id.totalResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TestResultAdapter();
        recyclerView.setAdapter(adapter);
        graph = findViewById(R.id.viewGraph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(testResults.this, testing.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        new FetchTestResultsTask().execute();
    }

    private class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.TestResultViewHolder> {

        private List<TestResult> testResults = new ArrayList<>();

        public void setTestResults(List<TestResult> testResults) {
            this.testResults = testResults;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TestResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.testresults_layout, parent, false);
            return new TestResultViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TestResultViewHolder holder, int position) {
            TestResult currentTestResult = testResults.get(position);
            holder.date.setText(currentTestResult.getId());
            holder.visual.setText(currentTestResult.getTask1());
            holder.naming.setText(currentTestResult.getTask2());
            holder.memory.setText(currentTestResult.getTask3());
            holder.attention.setText(currentTestResult.getTask4());
            holder.language.setText(currentTestResult.getTask5());
            holder.abstraction.setText(currentTestResult.getTask6());
            holder.orientation.setText(currentTestResult.getTask7());
            holder.total.setText(currentTestResult.getTotal());
            holder.interpretation.setText(currentTestResult.getInterpretation());
            holder.comment.setText(currentTestResult.getComment());

            String completeImageUrl = Api.api + currentTestResult.getImage1();
            Picasso.get().load(completeImageUrl).into(holder.img1);
            String url2 = Api.api+ currentTestResult.getImage2();
            Picasso.get().load(url2).into(holder.img2);
            // Set images using Picasso or Glide library
            // For example: Picasso.get().load(currentTestResult.getImage1()).into(holder.img1);
            // For example: Picasso.get().load(currentTestResult.getImage2()).into(holder.img2);
        }



        @Override
        public int getItemCount() {
            return testResults.size();
        }

        public class TestResultViewHolder extends RecyclerView.ViewHolder {
            public TextView date, visual, naming, memory, attention, language, abstraction, orientation, total, interpretation, comment;
            ImageView img1, img2;

            public TestResultViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                visual = itemView.findViewById(R.id.fetch_visual_score);
                naming = itemView.findViewById(R.id.fetch_naming_score);
                memory = itemView.findViewById(R.id.fetch_memory_score);
                attention = itemView.findViewById(R.id.fetch_attention_score);
                language = itemView.findViewById(R.id.fetch_language_score);
                abstraction = itemView.findViewById(R.id.fetch_abstraction_Score);
                orientation = itemView.findViewById(R.id.fetch_orientation_score);
                total = itemView.findViewById(R.id.fetch_total_Score);
                interpretation = itemView.findViewById(R.id.fetch_interpretation);
                comment = itemView.findViewById(R.id.fetch_comment);
                img1 = itemView.findViewById(R.id.fetch_image1);
                img2 = itemView.findViewById(R.id.fetch_image2);
            }
        }
    }

    private class FetchTestResultsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(Api.api+"view_result.php?id=" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    String status = jsonResponse.optString("status");
                    if ("success".equals(status)) {
                        JSONArray jsonData = jsonResponse.getJSONArray("data");
                        List<TestResult> testResults = new ArrayList<>();
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jsonObject = jsonData.getJSONObject(i);
                            String id = jsonObject.getString("submission_date");
                            String task1 = jsonObject.getString("task1");
                            String task2 = jsonObject.getString("task2");
                            String task3 = jsonObject.getString("task3");
                            String task4 = jsonObject.getString("task4");
                            String task5 = jsonObject.getString("task5");
                            String task6 = jsonObject.getString("task6");
                            String task7 = jsonObject.getString("task7");
                            String total = jsonObject.getString("total");
                            String interpretation = jsonObject.getString("interpretation");
                            String comment = jsonObject.getString("comment");
                            String img1 = jsonObject.getString("image1");
                            String img2 = jsonObject.getString("image2");

                            testResults.add(new TestResult(id, task1, task2, task3, task4, task5, task6, task7, total, interpretation, comment, img1, img2));
                        }
                        adapter.setTestResults(testResults);
                    } else {
                        Toast.makeText(testResults.this, "Error: " + jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(testResults.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class TestResult {
        private String id;
        private String task1;
        private String task2;
        private String task3;
        private String task4;
        private String task5;
        private String task6;
        private String task7;
        private String total;
        private String interpretation;
        private String comment;
        private String image1;
        private String image2;

        public TestResult(String id, String task1, String task2, String task3, String task4, String task5, String task6, String task7, String total, String interpretation, String comment, String image1, String image2) {
            this.id = id;
            this.task1 = task1;
            this.task2 = task2;
            this.task3 = task3;
            this.task4 = task4;
            this.task5 = task5;
            this.task6 = task6;
            this.task7 = task7;
            this.total = total;
            this.interpretation = interpretation;
            this.comment = comment;
            this.image1 = image1;
            this.image2 = image2;
        }

        // Getter methods for all fields
        public String getId() {
            return id;
        }

        public String getTask1() {
            return task1;
        }

        public String getTask2() {
            return task2;
        }

        public String getTask3() {
            return task3;
        }

        public String getTask4() {
            return task4;
        }

        public String getTask5() {
            return task5;
        }

        public String getTask6() {
            return task6;
        }

        public String getTask7() {
            return task7;
        }

        public String getTotal() {
            return total;
        }

        public String getInterpretation() {
            return interpretation;
        }

        public String getComment() {
            return comment;
        }

        public String getImage1() {
            return image1;
        }

        public String getImage2() {
            return image2;
        }
    }
}
