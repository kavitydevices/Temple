<?php

$dbhost = "182.50.133.80";
$dbuser = "kaveri";
$dbpassword = "kaveri@123";
$dbname = "kaveri_db";
$conn = mysqli_connect($dbhost, $dbuser, $dbpassword) or die (mysqli_error($conn));
mysqli_select_db($conn,$dbname) or die(mysqli_error($conn));
?>