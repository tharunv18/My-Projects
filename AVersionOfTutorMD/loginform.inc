<!DOCTYPE html>
  <html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
   
    <title>Login Form</title>
    
    <!-- include an external style sheet -->
    <link rel="stylesheet" href="style.css">  
    
    <!-- include an external JavaScript file -->
    <!-- <script src="myscript.js"></script> -->
  </head>
  
  <body>

		<form method="post" class = "signupform" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>" enctype="multipart/form-data">
			<div class="login-container">
			<h1>Login</h1>
			<label>Username:</label>
			<span class="error" style="color:red">* <?php echo $usernameErr;?></span><br>
			<input type="text" placeholder="Enter Username" name="username" value="<?php echo $username;?>"><br>
			<label>Password:</label>
			<span class="error" style="color:red">* <?php echo $passwordErr;?></span><br>
			<input type="password" placeholder="Enter Password" name="password" value="<?php echo $password;?>"><br><br>
			<button type="submit">Login</button>
			<a href='index.php'>Back to Home</a><br>
			</div>
		</form>