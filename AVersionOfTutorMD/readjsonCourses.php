<?php
// Name: readjsonCourses.php
// Authors: Tharun V, Jason Y, Ivy G, Natalie S
// Purpose: Through a fetch, it iterates through userprofiles.json to determine which elements of the "sort by courses" dropdown has been selected

// read json file into array of strings
$jsonstring = file_get_contents("userprofiles.json");
 
// save the json data as a PHP array
$phparray = json_decode($jsonstring, true);
  
// use GET to determine checked courses
if (isset($_GET["courses"])){
	$courses = $_GET["courses"];
} else {
	$courses = ""; 
}// else
  
// iterates through userprofiles.json finding the key associated with tutors that tutor the sorted courses
$returnData = []; // data of the relevant tutors
if ($courses != "") { 
	$classes = explode(" ", $courses);
	foreach($phparray as $entry) {
		for($i = 0; $i < count($classes); $i++){
			if ($entry["courses"] == $classes[$i]) {
				$returnData[] = $entry;  
			}// if
			for($j = 0; $j < count($entry["courses"]); $j++){
				if(str_contains($entry["courses"][$j], $classes[$i])){
					$returnData[] = $entry;
        			}//if
			}// for
		}// for
	}// foreach
} else {
	$returnData = $phparray;
} //else

// encode the php array to json 
$jsoncode = json_encode($returnData, JSON_PRETTY_PRINT);
echo ($jsoncode);
?>