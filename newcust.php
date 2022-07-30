<?php

	$response = array();
	$ctype = $_POST['ctype'];
	$cname = $_POST['Cname'];
	$cmob = $_POST['Cmob'];
	$cadr = $_POST['Cadr'];
	$cdate = $_POST['date'];
    require_once('db_config.php');
    //echo 'val'.$ctype,$tbname;
    if($ctype == "Agent")
    {
        $tbname ="Agent";
        $tbname1 ="tb_agentmaster";
    }
    else if($ctype== "Adv agency") {
        $tbname = "adagency";
        $tbname1="adagency";
    }
    else if($ctype == "correspondent")
    {
        $tbname = "correspondent";
        $tbname1 = "corres";
    }
    $IDqury ="Select auto,prefix from auto where type='$tbname'";
    $IDqres = mysqli_query($conn,$IDqury)  or die(mysqli_error($conn));
    $IDres = mysqli_fetch_array($IDqres);
    $AID = $IDres[0] + 1;
    //echo $tbname1;
    $duppay ="Select * from $tbname1 where phone='$cmob'";
    $dupres = mysqli_query($conn,$duppay)  or die(mysqli_error($conn));
    if(mysqli_fetch_array($dupres)<=0)
	{
        //echo 'here';
        $save="Insert into $tbname1(AgentCode,Name,Address,phone,OBDate,AgencyBalance,Deposit,AdvtBalance) 
        values('$IDres[1]+$AID','$cname','$cadr','$cmob','$cdate','0','0','0')";
		$res=mysqli_query($conn,$save) or die(mysqli_error($conn));
		$dupres1 = mysqli_query($conn,$duppay) or die(mysqli_error($conn));
		if(mysqli_fetch_array($dupres1)>=1)
		{
			
				$response["success"]=1;
				$repsonse["message"]="Payment successful";
				echo json_encode($response);	
        
		}
		else
		{
			$response["success"]=2;
			$repsonse["message"]="Something went wrong";
			echo json_encode($response);	
		}
	}
	else
	{
		$response["success"]=3;
		$repsonse["message"]="Customer with Phone# " + $cmob + " already exists";
		echo json_encode($response);	
	}
?>

