<!DOCTYPE html>
  <html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
   
    <title>Tutor Signup</title>
    
    <!-- include an external style sheet -->
    <link rel="stylesheet" href="style.css">  
    
  </head>
  
  <body>
		<form method="post" class = "signupform" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>" enctype="multipart/form-data">
			<div class="tutor-signup">
			<h1>Tutor Signup</h1>
			<label>First name:</label>
				<span class="error" style="color:red">* <?php echo $firstNameErr;?></span><br>
			<input type="text" placeholder="Enter First Name" name="fname" value="<?php echo $firstName;?>"><br>
			<label>Last name:</label>
				<span class="error" style="color:red">* <?php echo $lastNameErr;?></span><br>
			<input type="text" placeholder="Enter Last Name" name="lname" value="<?php echo $lastName;?>"><br>
			<label>Username:</label>
				<span class="error" style="color:red">* <?php echo $usernameErr;?></span><br>
			<input type="text" placeholder="Enter Username" name="username" value="<?php echo $username;?>"><br>
			<label>Password:</label>
				<span class="error" style="color:red">* <?php echo $passwordErr;?></span><br>
			<input type="password" placeholder="Enter Password" name="password" value="<?php echo $password;?>"><br><br>
			<label>Profile Pic</label>
				<span class="error" style="color:red">* <?php echo $pfpErr;?></span><br>
			<input type="file" name="pfp"><br><br>
			<label>What grade are you in?</label>
				<span class="error" style="color:red">* <?php echo $gradeErr;?></span><br>
			<input type="radio" name="grade" <?php if (isset($grade) && $grade=="9") echo "checked";?> value="9">
				<label>Grade 9</label><br>
			<input type="radio" name="grade" <?php if (isset($grade) && $grade=="10") echo "checked";?> value="10">
				<label>Grade 10</label><br>
			<input type="radio" name="grade" <?php if (isset($grade) && $grade=="11") echo "checked";?> value="11">
				<label>Grade 11</label><br>
			<input type="radio" name="grade" <?php if (isset($grade) && $grade=="12") echo "checked";?> value="12">
				<label>Grade 12</label><br><br>
			<label>Courses I Can Tutor:</label>
				<span class="error" style="color:red">* <?php echo $coursesErr;?></span><br>
			
			<?php
				// print all courses, keep checked courses checked
				foreach($allCourses as $key => $value){
					$checked = (isset($_POST["courses"]) && in_array($key, $_POST["courses"])) ? "checked" : "";
					echo "<input type='checkbox' name='courses[]' value='" . $key . "' $checked >";
					echo "<label>" . $value . "</label><br>";
				}// foreach
			?>
			
			<br><label>About Me:</label>
			<span class="error" style="color:red">* <?php echo $aboutMeErr;?></span><br>
			<textarea placeholder="....." rows="5" cols="50" name="aboutme"><?php echo $aboutMe;?></textarea><br><br>
			<br><button type="submit">Signup</button>
			</div>
		</form>