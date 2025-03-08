<?php
$coursesErr = "";
$hasError = false;
$chosenCourses = array();

if(isset($_GET["courses"]) && $_GET["courses"] == "edit"){
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		if (empty($_POST["courses"])) {
			$coursesErr = "Pick at least one course";
			$hasError = true;
		} else {
			$chosenCourses = $_POST["courses"];
		}// else
	}// if	
}// if

if ($_SERVER["REQUEST_METHOD"] == "POST" && !$hasError){

	// read json file into array of strings
	if (file_exists("students.json")) {
		$jsonstring = file_get_contents("students.json");
	} else {
		$jsonstring = '';
	}// else

	//decode the string from json to PHP array
	$studentarray = json_decode($jsonstring, true);
	
	// find the profile from userprofiles
	for($i = 0; $i < count($studentarray); $i++){
		if($_SESSION["username"] == $studentarray[$i]["username"]){
		
			$studentarray[$i]["courses"] = $chosenCourses;
			
			// encode the php array to formatted json 
			$jsoncode = json_encode($studentarray, JSON_PRETTY_PRINT);
			
			// write the json to the file
			file_put_contents("students.json", $jsoncode);
			
			$_SESSION['courses'] = $chosenCourses;
			
			break;
		}// if
	}// for
	
	// read json file into array of strings
	if (file_exists("userprofiles.json")) {
		$jsonstring = file_get_contents("userprofiles.json");
	} else {
		$jsonstring = '';
	}// else

	//decode the string from json to PHP array
	$phparray = json_decode($jsonstring, true);
	
	for($i = 0; $i < count($phparray); $i++){
		if($_SESSION["username"] == $phparray[$i]["username"]){
		
			$phparray[$i]["courses"] = $chosenCourses;
			
			// encode the php array to formatted json 
			$jsoncode = json_encode($phparray, JSON_PRETTY_PRINT);
			
			// write the json to the file
			file_put_contents("userprofiles.json", $jsoncode);
			
			$_SESSION['courses'] = $chosenCourses;
			
			break;
		}// if
	}// for
	include "accountinfo.inc";
	
} elseif(isset($_GET["courses"]) && $_GET["courses"] == "edit"){
	include "editcourses.inc";
}else{
	include "accountinfo.inc";
}// else

?>