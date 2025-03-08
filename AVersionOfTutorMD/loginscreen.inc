<!DOCTYPE html>
  <html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
   
    <title>Login Screen</title>
    
    <!-- include an external style sheet -->
    <link rel="stylesheet" href="style.css">  
    
    <!-- include an external JavaScript file -->
    <!-- <script src="myscript.js"></script> -->
  </head>
  
  <body>
    <div class="startpage">
      <div class="loginpage-top">
	<h1>Mount Doug Tutors</h1>
	<div class="row">
      		<h2 style="text-align:center">Login or Signup</h2>
      		<div class="vl">
        		<span class="vl-innertext">or</span>
      		</div>
		<div class ="col">
		  <button onclick = "window.location.href = 'http://10.49.31.251/~janarunivy/milestone3/index.php?security=studentsignup'" class="signup">Student</button>
		</div>
		<div class="col">
		  <div class="hide-md-lg">  
		    <p>OR</p>
		  </div>
		  <button onclick = "window.location.href = 'http://10.49.31.251/~janarunivy/milestone3/index.php?security=tutorsignup'" class="signup" id="tutorbtn">Tutor</button>
    		</div>
	</div> 
      </div>
      <div class="loginpage-bottom"> 
        <div class="row">
	<div class="col">   
	  <button onclick = "window.location.href = 'http://10.49.31.251/~janarunivy/milestone3/index.php?security=login'" class = "login">Login</button>
	</div>
	<div class="col">	  
	  <button onclick = "window.location.href = 'http://10.49.31.251/~janarunivy/milestone3/index.php?security=home'" class = "home">View Profiles</button>
	</div>
        </div>
      </div>
     </div>

  
</html>