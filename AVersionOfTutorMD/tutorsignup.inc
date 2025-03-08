<?php
	// define variables and set to empty values
	$firstNameErr = $lastNameErr = $pfpErr = $gradeErr = $coursesErr = $aboutMeErr = "";
	$firstName = $lastName = $grade = $aboutMe = "";
	$username = $password = "";
	$usernameErr = $passwordErr = "";
	$chosenCourses = array();
	$hasError = false;
	$phparray = array();
	$id = 0;
	$imagetype = "";
	$target_dir = "profilepics/";
	
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
		
		if (empty($_FILES["pfp"]["name"])) {
			$pfpErr = "Profile picture is required";
			$hasError = true;
		}// if
		
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

			// check if username already exists
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

			// check if username already exists
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
		}// else

		if (empty($_POST["courses"])) {
			$coursesErr = "Pick at least one course";
			$hasError = true;
		} else {
			$chosenCourses = $_POST["courses"];
		}// else

		if (empty($_POST["aboutme"])) {
			$aboutMeErr = "Must fill in about me";
			$hasError = true;
		} else {
			$aboutMe = checkInput($_POST["aboutme"]);
		}// else
		
		// create uploadedimages if it doesn't exist
		if(!is_dir($target_dir)){
			mkdir($target_dir);
			echo "<br>" . $target_dir . " created";
		}// if
		
		if(!$hasError){
			$id = file_get_contents('id.txt', true);
			$imagetype = explode(".", basename($_FILES["pfp"]["name"]))[1];
			$target_file = $target_dir . $id . "." . $imagetype;
			$uploadOk = 1;
			
			// Check if image file is a actual image or fake image
			if(isset($_POST["submit"])) {
				$check = getimagesize($_FILES["pfp"]["tmp_name"]);
				if($check !== false) {
					echo "File is an image - " . $check["mime"] . ".";
					$uploadOk = 1;
				} else {
					$pfpErr = "File is not an image.";
					$uploadOk = 0;
				}// else
			}// if

			// Check if file already exists
			if (file_exists($target_file)) {
				$pfpErr = "Sorry, file already exists.";
				$uploadOk = 0;
			}// if

			// Check file size
			if ($_FILES["pfp"]["size"] > 4000000) {
				$pfpErr = "Sorry, your file is too large.";
				$uploadOk = 0;
			}// if

			// Allow certain file formats
			if($imagetype != "jpg" && $imagetype != "png") {
				$pfpErr = "Sorry, only JPG, JPEG, PNG & GIF files are allowed.";
				$uploadOk = 0;
			}
			// Check if $uploadOk is set to 0 by an error
			if ($uploadOk == 0) {
				// $pfpErr .= " " . "Sorry, your file was not uploaded.";
				$hasError = true;
			// if everything is ok, try to upload file
			} else {
				// upload the file
				if (move_uploaded_file($_FILES["pfp"]["tmp_name"], $target_file)) {
					//echo "The file ". htmlspecialchars( basename( $_FILES["pfp"]["name"])). " has been uploaded.";
					
				} else {
					echo "Sorry, there was an error uploading your file.";
				}// else
			}// else
		}// if
	}// if
	
	if($_SERVER["REQUEST_METHOD"] == "POST" && !$hasError){
		
		$_POST["password"] = $hash;
		$_POST['uid'] = $id;
		$_POST['imagetype'] = $imagetype;
		
		// read json file into array of strings
		if (file_exists("userprofiles.json")) {
			$jsonstring = file_get_contents("userprofiles.json");
		} else {
			$jsonstring = '';
		}// else

		//decode the string from json to PHP array
		$phparray = json_decode($jsonstring, true);

		// add form submission to data
		$phparray[] = $_POST;

		// encode the php array to formatted json 
		$jsoncode = json_encode($phparray, JSON_PRETTY_PRINT);
		
		// write the json to the file
		file_put_contents("userprofiles.json", $jsoncode);
		
		$id = $id + 1;
		file_put_contents('id.txt', $id);
		
		header("Location: index.php");
		
	} else{
		include "tutorsignupform.inc";
	}// else
?>