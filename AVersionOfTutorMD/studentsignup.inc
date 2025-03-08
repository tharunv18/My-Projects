<?php
	// define variables and set to empty values
	$firstNameErr = $lastNameErr = $gradeErr = $coursesErr = $aboutMeErr = "";
	$firstName = $lastName = $grade = $aboutMe = "";
	$username = $password = "";
	$usernameErr = $passwordErr = "";
	$chosenCourses = array();
	$hasError = false;
	$phparray = array();
	$studentarray = array();
	
	// form error checking
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		if (empty($_POST["fname"])) {
			$firstNameErr = "First name is required";
			$hasError = true;
		} else {
			$firstName = checkInput($_POST["fname"]);
		}// else

		if (empty($_POST["lname"])) {
			$lastNameErr = "Last name is required";
			$hasError = true;
		} else {
			$lastName = checkInput($_POST["lname"]);
		}// else
		
		if (empty($_POST["username"])) {
			$usernameErr = "Username required";
			$hasError = true;
		} else {
			$username = checkInput($_POST["username"]);
			
			// read json file into array of strings
			if (file_exists("userprofiles.json")) {
				$jsonstring = file_get_contents("userprofiles.json");
			} else {
				$jsonstring = '';
			}// else
			//decode the string from json to PHP array
			$phparray = json_decode($jsonstring, true);

			// check if username already exists in tutors
			if(isset($phparray)){
				for($i = 0; $i < count($phparray); $i++){ 
					if($phparray[$i]["username"] == $username){
						$usernameErr = "Username already exists";
						$username = "";
						$hasError = true;
					}// if
				}// for
			}// if
			
			// read json file into array of strings
			if (file_exists("students.json")) {
				$jsonstring = file_get_contents("students.json");
			} else {
				$jsonstring = '';
			}// else
			//decode the string from json to PHP array
			$studentarray = json_decode($jsonstring, true);

			// check if username already exists in students
			if(isset($studentarray)){
				for($i = 0; $i < count($studentarray); $i++){ 
					if($studentarray[$i]["username"] == $username){
						$usernameErr = "Username already exists";
						$username = "";
						$hasError = true;
					}// if
				}// for
			}// if
		}// else
			
		if (empty($_POST["password"])) {
			$passwordErr = "Password required";
			$hasError = true;
		} else {
			$password = checkInput($_POST["password"]);
			$hash = password_hash($password, PASSWORD_BCRYPT);
		}// else

		if (empty($_POST["grade"])) {
			$gradeErr = "Grade is required";
			$hasError = true;
		} else {
			$grade = checkInput($_POST["grade"]);
		}// if

		if (empty($_POST["courses"])) {
			$coursesErr = "Pick at least one course";
			$hasError = true;
		} else {
			$chosenCourses = $_POST["courses"];
		}// else
	}// if
	
	if($_SERVER["REQUEST_METHOD"] == "POST" && !$hasError){
		
		$_POST["password"] = $hash;
		
		// read json file into array of strings
		if (file_exists("students.json")) {
			$jsonstring = file_get_contents("students.json");
		} else {
			$jsonstring = '';
		}// else

		//decode the string from json to PHP array
		$studentarray = json_decode($jsonstring, true);

		// add form submission to data
		$studentarray[] = $_POST;

		// encode the php array to formatted json 
		$jsoncode = json_encode($studentarray, JSON_PRETTY_PRINT);
		
		// write the json to the file
		file_put_contents("students.json", $jsoncode);
		
		header("Location: index.php");
		
	} else{
		include "studentsignupform.inc";
	}// else
?>