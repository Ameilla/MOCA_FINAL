<?php
require "connection.php";
// $conn = new mysqli("localhost", "root", "", "moca");
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    // Validate or sanitize $data as needed

    // Retrieve user input from POST request
    // Adjust variable names as needed
    $sql = "SELECT * FROM p_details ";

    $stmt = mysqli_prepare($conn, $sql);

    if ($stmt) {
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        if ($result->num_rows > 0) {
            $response["status"] = "success";
            $response["message"] = "Data retrieved successfully";
            $response['data']=array();
            while ($row = $result->fetch_assoc()) {
              
                $data = array(
                    "id" => strval($row["id"]),
                    "Name" => $row["Name"],
                    "Age" => strval($row["Age"]),
                    "Gender" => $row["Gender"],
                    "Drug"=>$row["Drug"],
                    "Diagnosis" => $row["Diagnosis"],
                    "patient_img"=>$row["patient_img"],
                );
                array_push($response['data'],$data);
            }
           
            // $response["data"] = $data;
        } else {
            $response["status"] = "error";
            $response["message"] = "No results found";
        }

        mysqli_stmt_close($stmt);
    } else {
        $response["status"] = "error";
        $response["message"] = "Error preparing statement: " . mysqli_error($conn);
    }
} else {
    $response["status"] = "error";
    $response["message"] = "Invalid request method";
}

$conn->close();

echo json_encode($response);

?>
