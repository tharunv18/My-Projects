<?php
	$allCourses = array( "fr9" => "Core French 9", "engla9" => "English Language Arts 9", 
		"ict9" => "Information and Communications Technology 9", "mand9" => "Mandarin 9", 
		"math9" => "Mathematics 9", "num9" => "Numeracy 9", "sci9" => "Science 9", 
		"ss9" => "Social Studies 9", "span9" => "Spanish 9", "begell10" => "Beginners ELL 10", 
		"engcomp10" => "Composition 10", "cprog11" => "Computer Programming 11", 
		"cs11" => "Computer Science 11", "cstu10" => "Computer Studies 10", 
		"fr10" => "Core French 10", "engcw10" => "Creative Writing 10",
		"ell10lin" => "English 10 (ELL) Linear", "fmpc10" => "Foundations and Pre-calculus 10", 
		"webd10" => "Web Development 10", "engls10" => "Literary Studies 10", 
		"mand10" => "Mandarin 10", "sci10" => "Science 10", 
		"ss10" => "Social Studies 10", "span10" => "Spanish 10", 
		"wpmath10" => "Workplace Mathematics 10", "BCfp12" => "BC First Peoples 12", 
		"chem11" => "Chemistry 11", "climch12" => "Climate Change 12", 
		"engcomp11" => "Composition 11", "cprog12" => "Computer Programming 12", 
		"cs12" => "Computer Science 12", "fr11" => "Core French 11", 
		"engcw11" => "Creative Writing 11", "dcomm11" => "Digital Communications 11", 
		"engfplsnm11" => "English First Peoples 11: Literary Studies + New Media 11", 
		"engfplssl11" => "English First Peoples 11: Literary Studies + Spoken Language 11", 
		"engfplsw11" => "English First Peoples 11: Literary Studies + Writing 11", 
		"ss11" => "Explorations in Social Studies 11", "fom11" => "Foundations of Mathematics 11", 
		"geo12" => "Geometry 12", "introita11" => "Introductory Italian 11", 
		"ita11" => "Italian 11", "ls12" => "Life Sciences 12", 
		"engls11" => "Literary Studies 11", "mand11" => "Mandarin 11", 
		"phys11" => "Physics 11", "pc11" => "Pre-Calculus Mathematics 11", 
		"spa11" => "Spanish 11", "engsl11" => "Spoken Language 11", 
		"20thwh12" => "20th Century World History 12", "apcalc" => "Advanced Placement Calculus", 
		"apcs" => "Advanced Placement Computer Science", "apeng" => "Advanced Placement English", 
		"apphys" => "Advanced Placement Physics", "apstats" => "Advanced Placement Statistics", 
		"ap12" => "Anatomy and Physiology 12", "calc12" => "Calculus 12", 
		"chem12" => "Chemistry 12", "compcul12" => "Comparative Cultures 12", 
		"ell12" => "English 12 Studies Linear (ELL)", "engfp12" => "English First Peoples 12", 
		"engstu12" => "English Studies 12", "fom12" => "Foundations of Mathematics 12", 
		"law12" => "Law Studies 12", "engls12" => "Literary Studies 12", 
		"mand12" => "Mandarin 12", "physgeo12" => "Physical Geography 12", 
		"phys12" => "Physics 12", "pc12" => "Pre-Calculus Mathematics 12", "span12" => "Spanish 12");

// read json file into array of strings
$jsonstring = file_get_contents("userprofiles.json");
 
// save the json data as a PHP array
$phparray = json_decode($jsonstring, true);
 
// use GET to determine type of access
if (isset($_GET["searched"])){
	$searched = strtolower($_GET["searched"]);
} else {
	$searched = " "; 
}// else

// determine json information of searched tutors
$returnData = [];
$added = false;
	if ($searched != "") { 
		foreach($phparray as $entry) {
			$added = false;
			if (str_contains(strtolower($entry["fname"]), $searched) || str_contains(strtolower($entry["lname"]), $searched) || str_contains(strtolower($entry["aboutme"]), $searched) || str_contains($entry["grade"], $searched)) {
				$returnData[] = $entry;  
				$added = true;
			} // if
			for ($i = 0; $i < count($entry["courses"]); $i++) {
				if(str_contains(strtolower($allCourses[$entry["courses"][$i]]), $searched) && !$added){
					$returnData[] = $entry;
					$added = true;
				} // if
			} // for
		} // foreach
	} else {
		$returnData = $phparray;
	}// else


// encode the php array to json 
$jsoncode = json_encode($returnData, JSON_PRETTY_PRINT);
echo ($jsoncode);

?>