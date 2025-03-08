<?php
include "createThumbnail.php";
// Link image type to correct image loader and saver
// - makes it easier to add additional types later on
// - makes the function easier to read
include "getData.php";
$dir = "profilepics/"; // file name for the profile images

// creates each a thumbnail for profile pictures submitted
if ($dh = opendir($dir)){
	while (($file = readdir($dh)) !== false){
		if ($file != "." && $file != "..") {
			createThumbnail("profilepics/" . $file, "thumbnails/". $file, 240, 240);
			createThumbnail("profilepics/" . $file, "lightboxpics/". $file, 600, 600);
		}// if
	}// while
	closedir($dh);
}// if

// make thumbnails if it does not exist
$dir2 = "thumbnails/";
if (!is_dir ($dir2)) {
	mkdir($dir2);
}// if

// make lightboxpics if it does not exist
if(!is_dir ("lightboxpics/")){
	mkdir("lightboxpics/");
}// if

// read json file into array of strings
if (file_exists("userprofiles.json")) {
	$jsonstring = file_get_contents("userprofiles.json");
} else {
	$jsonstring = '';
}// else

//decode the string from json to PHP array
$phparray = json_decode($jsonstring, true);

// display thumbnails on home page
echo '<div id="main">';
	if ($dh = opendir($dir2)){
		while (($file = readdir($dh)) !== false){
			if ($file != "." && $file != "..") {
				echo "<div class='main-profile'>";
				echo "<img class = 'thumbnail' src='" . $dir2 . $file . "' alt='profilepicture' onclick = 'displayLightBox(\"lightboxpics/\", \"" . $file . "\")'" . ">";
				for($i = 0; $i < count($phparray); $i++){
					if(explode(".", $file)[0] == $phparray[$i]["uid"]){
						echo "<br>";
						echo "<h4>" . $phparray[$i]["fname"] . " " . $phparray[$i]["lname"] . "</h4>";
						break;
					} // if
				} // for
				echo "</div>";
			}// if
		}// while
	closedir($dh);
	}// if
echo '</div>';

// lightbox shows when thumbnail is clicked
echo '<div id="lightbox" class="hidden"></div>';

// big image shows when thumbnail is clicked
echo "<div id='positionBigImage' class='hidden'>
	<div id='boundaryBigImage'>
		<img id='x' src='x.png' alt='close' onclick='displayLightBox(\"\", \"\")'>
		<img id='bigImage' src='empty.png' alt='empty'>
	</div>

	<div class='text' id='name'></div>
	<div class='text' id='grade'></div>
	<div class='text' id='about'></div>
	<div class='text' id='courses'></div>
	<div class='text'><a href='empty.png' id='download' class='text' download>Click Here to Download Image</a></div>
</div>";
?>