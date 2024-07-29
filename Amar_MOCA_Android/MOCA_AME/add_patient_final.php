<?php
require "connection.php";
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Database configuration
    // $conn = new mysqli("localhost", "root", "", "moca");
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    

    // Function to upload image and return the file path
    function uploadImage($fieldName) {
        $targetDirectory = "uploads/";
        $targetFile = $targetDirectory . basename($_FILES[$fieldName]["name"]);
        if (move_uploaded_file($_FILES[$fieldName]["tmp_name"], $targetFile)) {
            return $targetFile;
        } else {
            return null;
        }
    }

    // Get the posted data
    $name = $_POST["name"];
    $age = $_POST["age"];
    $gender = $_POST["gender"];
    $phoneNo = $_POST["phoneNo"];
    $alterPhoneNo = $_POST["alterPhoneNo"];
    $diagnosis = $_POST["diagnosis"];
    $drug = $_POST["drug"];
    $hippoCampal = $_POST["hippoCampal"];

    // Upload profile pic and MRI images
    $profilePicPath = isset($_FILES["patient_img"]) ? uploadImage("patient_img") : null;
    $mriPath = isset($_FILES["mri_before"]) ? uploadImage("mri_before") : null;

    // Prepare SQL statement to insert data into database
    $stmt = $conn->prepare("INSERT INTO p_details (name, age, gender, phone_no, alter_ph_no, Diagnosis, Drug, hippoCampal, patient_img, mri_before) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sissssssss", $name, $age, $gender, $phoneNo, $alterPhoneNo, $diagnosis, $drug, $hippoCampal, $profilePicPath, $mriPath);

    // Execute the statement
    if ($stmt->execute()) {
        // Record inserted successfully
        $response["status"] = "success";
        $response["message"] = "Patient information inserted successfully.";
    } else {
        // Error occurred while inserting record
        $response["status"] = "error";
        $response["message"] = "Error: " . $conn->error;
    }

    // Close statement and connection
    $stmt->close();
    $conn->close();

    // Encode the response array to JSON and echo it
    echo json_encode($response);
} else {
    // Invalid request
    echo json_encode(array("status" => "error", "message" => "Invalid request."));
}

?>
