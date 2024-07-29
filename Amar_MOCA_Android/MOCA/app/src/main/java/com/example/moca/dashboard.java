package com.example.moca;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class dashboard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<PatientInfo> dataList;
    private List<PatientInfo> filteredList;
    TextView seemore;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    Button addpatients;

    String url = Api.api+"dashboard.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);


        // Set up the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Initialize ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);
        dataList = new ArrayList<>();
        filteredList = new ArrayList<>(dataList);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        fetchfromPHP();
        SearchView searchView = findViewById(R.id.searchview);
        searchView.setFocusable(true); // Ensure the SearchView is focusable
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something with the query, if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list as the user types
                filter(newText);
                return true;
            }
        });
        seemore = findViewById(R.id.seemoreText);
        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, full_patients.class);
                startActivity(intent);
            }
        });
        addpatients = findViewById(R.id.addingpatients);
        addpatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, add_patient.class);
                startActivity(intent);
            }
        });
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile_doc) {
                    Intent intent = new Intent(dashboard.this, doctor_profile.class);

                    startActivity(intent);
                } else if (id == R.id.dashboard_doc) {
                    Intent intent = new Intent(dashboard.this, dashboard.class);

                    startActivity(intent);
                } else if (id == R.id.patients) {
                    Intent intent = new Intent(dashboard.this, full_patients.class);
                    startActivity(intent);
                } else if (id == R.id.about) {
                    Intent intent = new Intent(dashboard.this, topics_page.class);

                    startActivity(intent);
                }
                return true;
            }
        });

        // Fetch patient details when the page is initially loaded

        filter("");
    }

    public void fetchfromPHP() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            // Check if the status is "success"
                            if (jsonResponse.getString("status").equals("success")) {
                                // Get the data array
                                JSONArray dataArray = jsonResponse.getJSONArray("data");

                                // Clear existing data
                                dataList.clear();

                                // Loop through the array and extract patient information
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject patientObject = dataArray.getJSONObject(i);
                                    String id = patientObject.optString("id");
                                    String name = patientObject.optString("Name");
                                    String age = patientObject.optString("Age"); // New field for age
                                    String diagnosis = patientObject.optString("Diagnosis");
                                    String phone = patientObject.optString("phone_no");
                                    String alterphone = patientObject.optString("alter_ph_no");
                                    String profile = patientObject.optString("patient_img");
                                    String mri_before = patientObject.optString("mri_before");
                                    String mri_after = patientObject.optString("mri_after");
                                    String review = patientObject.optString("Discription");
                                    String sex = patientObject.optString("Gender"); // New field for sex
                                    String drug = patientObject.optString("Drug"); // New field for drug

                                    dataList.add(new PatientInfo(id, name, age, diagnosis, sex, drug, profile, phone, alterphone, mri_before, mri_after, review));
                                }

                                adapter.notifyDataSetChanged();
                                filter("");
                            } else {
                                // Handle the case where the status is not "success"
                                String message = jsonResponse.getString("message");
                                Toast.makeText(dashboard.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handleError(new VolleyError("Error parsing JSON"));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        });

        queue.add(stringRequest);
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(dataList);

        } else {
            text = text.toLowerCase().trim();
            for (PatientInfo item : dataList) {
                if (item.getId() != null && item.getName() != null && item.getDiagnosis() != null) {
                    if (item.getId().toLowerCase().contains(text)
                            || item.getName().toLowerCase().contains(text)
                            || item.getDiagnosis().toLowerCase().contains(text)) {
                        filteredList.add(item);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<PatientInfo> dataList;

        public CustomAdapter(List<PatientInfo> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PatientInfo patient = dataList.get(position);

            holder.nameTextView.setText("Name        :  " + (patient.getName() != null ? patient.getName() : ""));
            holder.ageTextView.setText("Age           :  " + (patient.getAge() != null ? patient.getAge() : ""));
            holder.diagnosisTextView.setText("Diagnosis :  " + (patient.getDiagnosis() != null ? patient.getDiagnosis() : ""));
            if (patient.getProfilePhoto() != null && !patient.getProfilePhoto().isEmpty()) {
                String completeImageUrl = Api.api + patient.getProfilePhoto();
                Picasso.get().load(completeImageUrl).into(holder.profilepic);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedItem = patient.getId() != null ? patient.getId() : "";

                    Intent intent = new Intent(dashboard.this, Patient_info.class);
                    intent.putExtra("id", patient.getId());
                    intent.putExtra("name", patient.getName());
                    intent.putExtra("age", patient.getAge());
                    intent.putExtra("sex", patient.getSex());
                    intent.putExtra("Diagnosis", patient.getDiagnosis());
                    intent.putExtra("patient", patient.getProfilePhoto());// Pass sex to next page
                    intent.putExtra("drug", patient.getDrug()); // Pass drug to next page
//                    Toast.makeText(dashboard.this, "sex_drg"+patient.getSex()+patient.getDrug(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, ageTextView, diagnosisTextView;
            CircleImageView profilepic;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.patientName);
                ageTextView = itemView.findViewById(R.id.patientAge);
                diagnosisTextView = itemView.findViewById(R.id.patientDiagnosis);
                profilepic = itemView.findViewById(R.id.patientprofile);
            }
        }
    }

    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            Toast.makeText(this, "Request timed out. Check your internet connection.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, error.toString().trim(), Toast.LENGTH_SHORT).show();
        }
    }
}

class PatientInfo {
    private String id;
    private String name;
    private String age;
    private String diagnosis;
    private String sex;
    private String drug;
    private String profilePhoto;
    private String phone;
    private String alterPhone;
    private String mriBefore;
    private String mriAfter;
    private String review;

    public PatientInfo(String id, String name, String age, String diagnosis, String sex, String drug, String profilePhoto, String phone, String alterPhone, String mriBefore, String mriAfter, String review) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.diagnosis = diagnosis;
        this.sex = sex;
        this.drug = drug;
        this.profilePhoto = profilePhoto;
        this.phone = phone;
        this.alterPhone = alterPhone;
        this.mriBefore = mriBefore;
        this.mriAfter = mriAfter;
        this.review = review;
    }

    // Add getters for each field

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getSex() {
        return sex;
    }

    public String getDrug() {
        return drug;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getPhone() {
        return phone;
    }

    public String getAlterPhone() {
        return alterPhone;
    }

    public String getMriBefore() {
        return mriBefore;
    }

    public String getMriAfter() {
        return mriAfter;
    }

    public String getReview() {
        return review;
    }
}

