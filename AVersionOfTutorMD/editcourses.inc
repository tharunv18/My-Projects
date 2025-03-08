<!-- lists all the courses with previously chosen courses checked -->
<form method="post" class = "editcourses" action="<?php htmlspecialchars($_SERVER["PHP_SELF"]);?>" enctype="multipart/form-data">
<h3> Course Editor </h3>
<span class="error" style="color:red">* <?php echo $coursesErr;?></span><br>
<?php
foreach($allCourses as $key => $value){
	$checked = in_array($key, $_SESSION["courses"]) ? "checked" : "";
	echo "<input type='checkbox' name='courses[]' value='" . $key . "' $checked >";
	echo "<label>" . $value . "</label><br>";
}// foreach
?>

<br><button type="submit">Submit</button>
</form>