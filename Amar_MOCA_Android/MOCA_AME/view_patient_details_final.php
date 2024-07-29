<?php
// Check if 'num' parameter is set in the request
require "connection.php";
// $conn = new mysqli("localhost", "root", "", "moca");
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
header('Content-Type: application/json; charset=UTF-8');
$response = array();

if (isset($_POST['num'])) {
    // Retrieve the 'num' parameter value
    $num = $_POST['num'];

    $sql = "SELECT * 
            FROM p_details 
            WHERE id = $num";
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result->num_rows > 0) {
        $response['status'] = "success";
        $response['message'] = "Data found";
        $response['data'] = array();

        while ($row = $result->fetch_assoc()) {
           

            $data = array(
                "id" => strval($row["id"]),
                "Name" => $row["Name"],
                "Age" => strval($row["Age"]),
                "Gender" => $row["Gender"],
                "Phone number" => $row['phone_no'],
                "Alternate mobile num" => $row['alter_ph_no'],
                "Diagnosis" => $row['Diagnosis'],
                "Drug" => $row['Drug'],
                "Hippocampal" => $row['hippocampal'],
                "Discription" => $row['Discription'],
                "patient_img" => $row['patient_img'],
                "mri_before"=>$row['mri_before'],
                "mri_after"=>$row["mri_after"],
            );

            // Check if image paths are not empty before attempting to read and encode images
           
            array_push($response['data'], $data);
        }
    } else {
        $response['status'] = "error";
        $response['message'] = "No results found";
    }
} else {
    // If 'num' parameter is not set, handle the error accordingly
    $response['status'] = "error";
    $response['message'] = "'num' parameter not found in the request.";
}

$conn->close();

// Convert PHP array to JSON and output the response
echo json_encode($response);
?>
