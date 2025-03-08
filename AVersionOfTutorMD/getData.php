<?php
// Name:getData.php
// Authors: Tharun V, Jason Y, Ivy G, Natalie S
// Purpose: Gets the contents of the json in order to be displayed in the lightbox

$UID; // uid for profile
	
	// gets the data from the profile
	if (isset($_GET["uid"])) {
		$UID =  intval($_GET["uid"]);
		$jsonstring = file_get_contents("userprofiles.json");
		//decode the string from json to PHP array
		$phparray = json_decode($jsonstring, true);
		for ($i = 0; $i < sizeof ($phparray); $i++) {
			if (intval($phparray[$i]["uid"]) == $UID) {
				echo json_encode($phparray[$i]);
			}// if
		}// for
	}//if
?>