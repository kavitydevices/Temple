<?php

$response = array();
require_once('db_config.php');
		
		//$json1 ='[{"Btime":"04/00/27","Cmob":"9591057512","Uloc":"1","Item":"Mangalarthi","Usernm":"vvkiy","dnt":"28/02/2018","Bill_Id":"1","Itemp":"50"},{"Btime":"04/02/38","Cmob":"123","Uloc":"1","Item":"item 1","Usernm":"vvkiy","dnt":"28/02/2018","Bill_Id":"2","Itemp":"20"},{"Btime":"04/08/04","Cmob":"122","Uloc":"1","Item":"item 1","Usernm":"vvkiy","dnt":"28/02/2018","Bill_Id":"3","Itemp":"20"},{"Btime":"04/10/22","Cmob":"8197277956","Uloc":"1","Item":"item 1","Usernm":"vvkiy","dnt":"28/02/2018","Bill_Id":"4","Itemp":"20"}]';
		$json = $_POST["parmtr"];
        //$json1 = '[{"Itemq":"1","Cmob":"1234567890","Btime":"01:40:09","dnt":"18/03/20","Usernm":"1","ItemT":"50","Bill_Id":"1000","Itemp":"50","Item":"Appakajjaya","Uloc":"1"}]';

		//$json = $json1["parmtr"];
		//var_dump($json1);
		//Remove Slashes
		if (get_magic_quotes_gpc()){
		$json = stripslashes($json);
		}
		//Decode JSON into an Array
		$data = json_decode($json,true);
		//var_dump($data);
		//echo $data[0]['vnumber'];
		//Util arrays to create response JSON
		$a=array();
		$b=array();
		//echo count($data);
		for($i=0; $i<count($data) ; $i++)
		{	
		$Unm = $data[$i]['Usernm'];
		$Bill_Id = $data[$i]['Bill_Id'];
		$Cmob = $data[$i]['Cmob'];	
		$Item = $data[$i]['Item'];
		$Itemp = $data[$i]['Itemp'];
		$Itemq = $data[$i]['Itemq'];
		$ItemT = $data[$i]['ItemT'];
		$Btime = $data[$i]['Btime'];
		$dnt =  $data[$i]['dnt'];
		$Uloc = $data[$i]['Uloc'];
	
		$save1="Select * from billing where billedby='$Unm' and BYID='$Bill_Id'";
		$result1 = mysqli_query($conn,$save1) or die(mysqli_error($conn));
		//echo mysqli_num_rows($conn,$result1);
		//if(mysqli_num_rows($result1) > 0)
		//{ 
            
		//}
		//else
		//{
		$save="INSERT INTO billing(BYID,mobile,poojainame,rate,qty,total,billdate,billtime,billedby,location) values('$Bill_Id','$Cmob','$Item','$Itemp','$Itemq','$ItemT','$dnt','$Btime','$Unm','$Uloc')";
		$result=mysqli_query($conn,$save) or die(mysqli_error($conn)) ;
			if($result)
			{
				$response["Success"]=1;
				$response["message"]="Submitted successfully";	
				echo json_encode($response);
			}
			else
			{
				$response["Success"]=0;
				$repsonse["message"]="Oops!! An error occured";
				echo json_encode($response);
			}
		//}
		}

	
?>

