// Name: lightbox.js
// Authors: Tharun V, Jason Y, Ivy G, Natalie S
// Purpose: Includes all javascript functions that are called throughout the rat of the code

const courseNames ={
	"fr9" : "Core French 9", "engla9" : "English Language Arts 9", 
		"ict9" : "Information and Communications Technology 9", "mand9" : "Mandarin 9", 
		"math9" : "Mathematics 9", "num9" : "Numeracy 9", "sci9" : "Science 9", 
		"ss9" : "Social Studies 9", "span9" : "Spanish 9", "begell10" : "Beginners ELL 10", 
		"engcomp10" : "Composition 10", "cprog11" : "Computer Programming 11", 
		"cs11" : "Computer Science 11", "cstu10" : "Computer Studies 10", 
		"fr10" : "Core French 10", "engcw10" : "Creative Writing 10",
		"ell10lin" : "English 10 (ELL) Linear", "fmpc10" : "Foundations and Pre-calculus 10", 
		"webd10" : "Web Development 10", "engls10" : "Literary Studies 10", 
		"mand10" : "Mandarin 10", "sci10" : "Science 10", 
		"ss10" : "Social Studies 10", "span10" : "Spanish 10", 
		"wpmath10" : "Workplace Mathematics 10", "BCfp12" : "BC First Peoples 12", 
		"chem11" : "Chemistry 11", "climch12" : "Climate Change 12", 
		"engcomp11" : "Composition 11", "cprog12" : "Computer Programming 12", 
		"cs12" : "Computer Science 12", "fr11" : "Core French 11", 
		"engcw11" : "Creative Writing 11", "dcomm11" : "Digital Communications 11", 
		"engfplsnm11" : "English First Peoples 11: Literary Studies + New Media 11", 
		"engfplssl11" : "English First Peoples 11: Literary Studies + Spoken Language 11", 
		"engfplsw11" : "English First Peoples 11: Literary Studies + Writing 11", 
		"ss11" : "Explorations in Social Studies 11", "fom11" : "Foundations of Mathematics 11", 
		"geo12" : "Geometry 12", "introita11" : "Introductory Italian 11", 
		"ita11" : "Italian 11", "ls12" : "Life Sciences 12", 
		"engls11" : "Literary Studies 11", "mand11" : "Mandarin 11", 
		"phys11" : "Physics 11", "pc11" : "Pre-Calculus Mathematics 11", 
		"spa11" : "Spanish 11", "engsl11" : "Spoken Language 11", 
		"20thwh12" : "20th Century World History 12", "apcalc" : "Advanced Placement Calculus", 
		"apcs" : "Advanced Placement Computer Science", "apeng" : "Advanced Placement English", 
		"apphys" : "Advanced Placement Physics", "apstats" : "Advanced Placement Statistics", 
		"ap12" : "Anatomy and Physiology 12", "calc12" : "Calculus 12", 
		"chem12" : "Chemistry 12", "compcul12" : "Comparative Cultures 12", 
		"ell12" : "English 12 Studies Linear (ELL)", "engfp12" : "English First Peoples 12", 
		"engstu12" : "English Studies 12", "fom12" : "Foundations of Mathematics 12", 
		"law12" : "Law Studies 12", "engls12" : "Literary Studies 12", 
		"mand12" : "Mandarin 12", "physgeo12" : "Physical Geography 12", 
		"phys12" : "Physics 12", "pc12" : "Pre-Calculus Mathematics 12", "span12" : "Spanish 12"};

// when window finishes loading initialize page
window.onload = function () {
	// Adds EventListener to checkboxes and determines what values are checked
	const btn = document.querySelector('#btn'); // filter button
	btn.addEventListener('click', (event) =>{
		let checkboxes = document.querySelectorAll('input[name="courses[]"]:checked'); // checkboxes in courses filter
		let values = []; // courses checked
		checkboxes.forEach((checkbox) => {
		values.push(checkbox.value);
		});
	// sends the checked values to load the relevent tutor's thumbnails
	loadImages(values);
	});
}; // window.onload


// change visibiity of divID
function changeVisibility(divID) {
	let element = document.getElementById(divID); // div tag to make visible
	
	// if element exits, switch it's class between hidden and unhidden
	if (element) {
		element.className = (element.className == 'hidden')?'unhidden':'hidden';
	} // if
} //changeVisibility

//display light box with image in it
function displayLightBox(alt, imageFile) {
	console.log(displayLightBox);
	console.log(alt);
	let bigImage = document.getElementById("bigImage");
    let download = document.getElementById("download"); // download link
    let image = new Image(); // image for profile
	let requestedUID = imageFile.split(".");
	if (imageFile != "") {
	     fetch ("http://10.49.31.251/~janarunivy/milestone3/getData.php?uid=" + requestedUID[0])
		.then(response => response.json())
		.then(data => updateContents(data))
		.catch(err => console.log("error occurred " + err));
	}// if
	
    // update the big image to access
    image.src = "lightboxpics/" + imageFile;

    // force big image to preload so we can access width to center image on page
    image.onload = function () {
        let width = image.width;
        document.getElementById("boundaryBigImage").style.width = width + "px";
    };

    bigImage.src = image.src;
    bigImage.alt = alt;
    download.href = image.src;
    changeVisibility("lightbox");
    changeVisibility("positionBigImage");
} //displayLightBox

// update the contents and display information when lightbox is clicked
function updateContents(data, imageFile) {
	console.log(data);
	let courseList = ""; // courses selected

	document.getElementById("name").innerHTML = "Name: " + data.fname + " " + data.lname;
	document.getElementById("grade").innerHTML = "Grade: " + data.grade;
	document.getElementById("about").innerHTML = "About Me: " + data.aboutme;
	document.getElementById("courses").innerHTML = "Courses I can Tutor: ";

  for (let i = 0; i < data.courses.length; i++) {
        if(i < data.courses.length - 1){
		document.getElementById("courses").innerHTML += courseNames[data.courses[i]] + ", ";
	} else {
		document.getElementById("courses").innerHTML += courseNames[data.courses[i]];
	}// else
  }// for
}// updateContents

// Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon
function myFunction() {
	var x = document.getElementById("myTopnav");
	if (x.className === "topnav") {
		x.className += " responsive";
	} else {
		x.className = "topnav";
	}// else
}// myFunction

// called when the search bar is used
function searchsite() {
	let main = document.getElementById("main");
	let searched = document.getElementById('searchbar').value;
	searched = searched.toLowerCase(); // value of what is searched
	
	// fetch the data
	fetch("./readjson.php?searched=" + searched).
    then(function(resp){ 
		return resp.json();
    })
    .then(function(data){
		let i;  // counter     
		
		// remove all existing children of main
		while (main.firstChild) {
			main.removeChild(main.firstChild);
		}// while
		
		// for every image, create a new image object and add to main
		for (i in data){
			let img = new Image();
			console.log(img.src);
			fileName = data[i].uid + "." + data[i].imagetype;
			img.src = "thumbnails/" + fileName;
			img.alt = data[i].aboutme;
			img.setAttribute('onclick', 'displayLightBox(\"' + img.alt + '\",\"' + fileName + '\")');
			img.className = "thumbnail";
			main.appendChild(img);
		}// for
    });
} // searchsite

// Load's relevent tutor's thumbnail based on "sort by courses" dropdown form
function loadImages(classes){
	let main = document.getElementById('main');
	let courses = ""; // courses selected
	for(let i = 0; i < classes.length; i++){
		courses += (classes[i] + " ");
	}// for
	
	// fetch the data
	fetch("./readjsonCourses.php?courses=" + courses).
	then(function(resp){ 
		return resp.json();
	})
	.then(function(data){
		let i;  // counter
	
		// remove all existing children of main
		while (main.firstChild) {
			main.removeChild(main.firstChild);
		}// while

		// for every image, create a new image object and add to main
		for (i in data){
			let img = new Image();
				console.log(img.src);
				fileName = data[i].uid + "." + data[i].imagetype;
				img.src = "thumbnails/" + fileName;
				img.alt = data[i].aboutme;
				img.setAttribute('onclick', 'displayLightBox(\"' + img.alt + '\",\"' + fileName + '\")');
				img.className = "thumbnail";
				main.appendChild(img);
		}// for
	});
} // loadImages




 