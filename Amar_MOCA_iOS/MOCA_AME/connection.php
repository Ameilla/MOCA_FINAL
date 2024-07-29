<?php
$conn=mysqli_connect("localhost","root","","moca");
if($conn->connect_error){
    die("Connection failed ".$conn->connect_error);
    }
?>