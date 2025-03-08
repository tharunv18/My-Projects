<?php

	$username = $password = ""; // password and username entered
	$usernameErr = $passwordErr = ""; // error message for password and username
	$usernameExists = false; // if username entered is already in system
	
	// check errors for username and password
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		
		if (empty($_POST["username"])) {
			$usernameErr = "Username required";
		} else {
			$username = checkInput($_POST["username"]);
			$password = checkInput($_POST["password"]);
			
			// check userprofiles for tutors
			if(file_exists("userprofiles.json")) {
				$jsonstring = file_get_contents("userprofiles.json");
			}else{
				$jsonstring = "";
			} // else
				
			//decode the string from json to PHP array
			$phparray = json_decode($jsonstring, true);
			
			// check if username and password match
			if(isset($phparray)){
				for($i = 0; $i < count($phparray); $i++){ 
					if($phparray[$i]["username"] == $username && password_verify($password, $phparray[$i]["password"])){
						$_SESSION['logged_in'] = true;
						$_SESSION['identity'] = "tutor";
						$_SESSION['jsonindex'] = $i;
						$_SESSION['username'] = $username;
						$_SESSION['fname'] = $phparray[$i]["fname"];
						$_SESSION['lname'] = $phparray[$i]["lname"];
						$_SESSION['grade'] = $phparray[$i]["grade"];
						$_SESSION['courses'] = $phparray[$i]["courses"];
						$_SESSION['aboutme'] = $phparray[$i]["aboutme"];
						$_SESSION['pfp'] = $phparray[$i]["uid"] . "." . $phparray[$i]["imagetype"];
						header("Location: index.php");
					} // if
				} // for
			} // if
			
			// check students for students
			if(file_exists("students.json")) {
				$jsonstring = file_get_contents("students.json");
			}else{
				$jsonstring = "";
			} // else
				
			//decode the string from json to PHP array
			$studentarray = json_decode($jsonstring, true);
			
			// check if username and password match
			if(isset($studentarray)){
				for($i = 0; $i < count($studentarray); $i++){ 
					if($studentarray[$i]["username"] == $username && password_verify($password, $studentarray[$i]["password"])){
						$_SESSION['logged_in'] = true;
						$_SESSION['username'] = $username;
						$_SESSION['identity'] = "student";
						$_SESSION['jsonindex'] = $i;
						$_SESSION['fname'] = $studentarray[$i]["fname"];
						$_SESSION['lname'] = $studentarray[$i]["lname"];
						$_SESSION['grade'] = $studentarray[$i]["grade"];
						$_SESSION['courses'] = $studentarray[$i]["courses"];
						header("Location: index.php");
					} // if
				} // for
			} // if
			
			if(!isset($_SESSION['logged_in'])){
				$passwordErr = "Incorrect password or username";
				$password = "";
			} // if
		} // else
	} // if
	
	include "loginform.inc";

?>