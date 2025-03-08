<!DOCTYPE html>
	<html lang="en">
	<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Mount Doug Tutors</title>	
	</head>
	<body>
		<div id="header">
		<h1>Mount Doug Tutors</h1>
		</div>
		<div class = "topnav" id = "myTopnav">
			<a href = "index.php?action=viewprof" class ="active">View Profiles </a> &nbsp; &nbsp;  
			<a href = "index.php?action=delall">Reset Gallery   </a>&nbsp; &nbsp;  
			<a href = "downloadall.php" download> Download All  </a>&nbsp; &nbsp; 
			<a href = "index.php?action=myacc"> My Account    </a>&nbsp; &nbsp; 
			<a href = "?logout=true">
			<?php
				echo (isset($_SESSION['logged_in']) && $_SESSION['logged_in'])? "Logout": "Login"; 
			?>
			</a>&nbsp; &nbsp;
			<div class ="dropdown">
				<button class ="dropbtn">Sort by courses
				<i class="fa fa-caret-down"></i>
				</button>
				<div class = "dropdown-content">
				<?php
					foreach ($allCourses as $key => $value) {
						echo "<input type='checkbox' id = '" . $key . "' name='courses[]'";
						echo " value='" . $key . "'>" . $value. "<br>\n";
					} // foreach
				?>
				<button id = "btn">Filter</button>
				</div>
			</div>
	
			<div class="search">  
				<input id="searchbar" type="text" placeholder=" Search... " name="search" onkeyup="searchsite()">
			</div>
			<a href="javascript:void(0);" class="icon" onclick="myFunction()">
				<i class="fa fa-bars"></i>
			</a>  
		</div>	