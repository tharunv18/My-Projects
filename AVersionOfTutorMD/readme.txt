LINK TO HOMEPAGE: http://10.49.31.251/~janarunivy/milestone3/index.php

** thumbnails **
	Stores the 250 x 250 px thumbnails of the tutors. Displays
	at view profiles.

** profilepics **
	Stores the original images given by the tutors.

** lightboxpics **
	Stores the 600 x 600 px thumbnail of the tutors. Displays
	when the lightboxes are clicked.

** index.php **
	Contains functions and variables used in the rest of the code.
	Includes various inc files based on the query string and SESSION
	states.

** accountinfo.inc **
	Displays all of the user's relevant information from the
	json file. Allows them to edit their chosen courses.
	
** editcourses.inc **
	A checklist form for all of the courses. Allows the logged in user
	to edit their chosen courses.

** login.inc **
	Calls and processes login data from loginform. Checks if
	password and username match. If it matches, show the 
	profiles.

** loginform.inc **
	The form for logging into the site.

** loginscreen.inc **
	The page at the very start of the website. Allows use to
	login, signup, and view profiles.

** myaccount.inc **
	Processes user's commands on myaccount page. Checks the
	editcourses form for errors. Includes accountinf and 
	editcourses when necessary

** readjson.php **
** readjsonCourses.php **

** students.json **
	The json file where the data for the students
	is stored

** studentsignup.inc **
	Calls and processes the form data from the studentsignupform.
	Writes the data to userprofiles.

** studentsignupform.inc **
	The sign up form for the students.

** tutorsignup.inc **
	Calls and processes the form data from the tutorsignupform.
	Writes the data to userprofiles.

** tutorsignupform.inc **
	The sign up form for the tutors.

** userprofiles.json **
	The json file where the data for the tutors
	is stored

** home.inc **
	Is the starting page for the website and displays the 
	tutor profile pictures in an organized gride when they 
	are filled out. When thumbnails are clicked a lightbox 
	will appear showing the full sized photo, which can be 
	downloaded, and tutor information.

** header.inc **
	Contains the information displayed at the top of 
	the website such as the navagation bar information/
	links, and title. Includes the external style sheet 
	(lightbox.css), has opening HTML and body tags, and 
	defines basic information such as language and metadata.

** footer.inc **
	Closes both the body and HTML as well as contains 
	the copyright text desplayed at the bottom of the 
	website. Also includes the external javascript 
	file (lightbox.js).

** form.inc **
	Contains the "become a tutor" form that can be filled 
	out in the website. Also where all of the profile 
	information is collected from as it is user input. First 
	and last name, grade, profile picture, courses, and about 
	me are all mandatory. 

** functions.inc **
	Where all the php functions are stored for them to be 
	accessed in the code. Is included at the top of the 
	index.php file before any code executes.

** createThumbnail.php **
	Creates thumbnails of images on the server to fit given 
	dimensions. Resizes images without distortion and uses 
	same name and type for produced thumbnail.

** downloadall.php **
	Creates and downloads a zip file of the profileimages 
	folder for when "download all" is selected by user.

** getData.php **
	Recieves the tutor profile data from the json file after 
	being called from the fetch in the displayLightBox 
	function in lightbox.js. Uses sent uid to find the correct 
	tutor.

** HZip.php **
	Included in downloadall.php to add all of the files/images 
	and subb-directories into a folder. This folder then gets 
	zipped for download.

** id.txt **
	stores the most recently used identifier (uid) value 
	to keep track of the next number would be. File is 
	cleared when the profiles are deleted.

** style.css **
	Where all of the website styling is done, such as positions, 
	fonts, sizes, formating depending on screen size, and whether 
	something is hidden. Included in footer.

** lightbox.js **
	Where all javascript is located and can be accessed from. 