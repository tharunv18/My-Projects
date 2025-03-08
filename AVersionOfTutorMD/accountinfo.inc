<!-- Displays the logged in user's information -->
<div id="myaccount">

<?php
	// makes round profile picture
	if (isset($_SESSION['identity']) && $_SESSION['identity'] == "tutor") {
		echo "<div id='myaccpfp'>";
		echo "<img id='pfp' src= 'thumbnails/";
		echo $_SESSION['pfp']; 
		echo "'>";
		echo "</div>";
	}// if
?>

<!-- displays information for account -->
<div style = 'padding-left:20px;' id="names">
	<h3><?php echo $_SESSION['fname'] . " " . $_SESSION['lname'] ?></h3>
	
	<p><?php echo '@' . $_SESSION['username'] ?></p>
	
	<h4>Grade: <?php echo $_SESSION['grade'] ?></h4>
	
	<p>Courses I Can Tutor: 
	<?php
		// iterates through courses to print
		for($i = 0; $i < count($_SESSION['courses']); $i++){
			if($i < count($_SESSION['courses']) - 1){
				echo $allCourses[$_SESSION['courses'][$i]] . ", ";
			} else{
				echo $allCourses[$_SESSION['courses'][$i]];
			}// else
		}// for
		echo "<br><a href='?action=myacc&courses=edit'>Click Here To Edit Courses</a>";
	?></p>
	
<?php
	// prints the about me when a tutor
	if (isset($_SESSION['identity']) && $_SESSION['identity'] == "tutor") {
		echo "<p>About Me: ";
		echo $_SESSION['aboutme'];
		echo "</p>";
	}// if
?>

</div>