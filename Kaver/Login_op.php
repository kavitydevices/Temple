<?php

$response = array();
	//$umob="vvkiy";
	//$usrpwd = "123";
	if(isset($_POST['umob']) && isset($_POST['usrpwd']))
	{
		$umob = $_POST['umob'];
		$usrpwd = $_POST['usrpwd'];
		
		require_once('db_config.php');
		$result=mysqli_query($conn,"Select * from user where username ='$umob' and password ='$usrpwd'") or myslqi_die($conn);
		if(!empty($result))
		{	
			if(mysqli_num_rows($result) > 0)
			{	
				$result1=mysqli_query($conn,"Select location,id from user where username ='$umob'");
				$row = mysqli_fetch_array($result1);
				$usr = $row[0];
				$usrid = $row[1];
				$response["success"]=1;
				$response["message"]=$usr;
				$response["uid"]=$usrid;
				echo json_encode($response);
			}
			else
			{
				$response["success"]=3;
				$repsonse["message"]="Oops!! Login failed.Retry";
				echo json_encode($response);
			}
		}
		else
		{
			$response["success"]=0;
			$repsonse["message"]="Oops!! Login failed.Retry";
			echo json_encode($response);
		}
		
	}
	else
	{
	$response["success"]=0;
		$response["message"]="Required Field(s) is empty";
		
		echo json_encode($response);
	}
?>

