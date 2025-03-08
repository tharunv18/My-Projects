let  currentPlayer = "X";
let gameStatus = "";
let numTurns = 0;
var cb = []; // current board
let idNames = ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"];
// reset board and all variables 
function newGame () {
	for (let i = 0; i < idNames.length; i++) {
		document.getElementById(idNames[i]).innerHTML = "";
	} //for
	numTurns = 0;
	currentPlayer = "X";
	gameStatus = "";
	changeVisibility("controls");
	
} //newGame

//randomly choose free box for computer
function computerTakeTurn () {
	let idName = "";

//choose random boxes until empty box is found

do {
	let rand = parseInt(Math.random() *9) + 1;
	idName = idNames [rand - 1]; //due to shift of one
	//assigning values in current board array
	cb[1] = document.getElementById("one");
	cb[2] = document.getElementById("two");
	cb[3] = document.getElementById("three");
	cb[4] = document.getElementById("four");
	cb[5] = document.getElementById("five");
	cb[6] = document.getElementById("six");
	cb[7] = document.getElementById("seven");
	cb[8] = document.getElementById("eight");
	cb[9] = document.getElementById("nine");
	
		if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[2].innerHTML && cb[3].innerHTML == "") {
		console.log("getting to if statement1");
		cb[3].innerHTML = "O";
		break;
	} else if (cb[4].innerHTML == "O" && cb[4].innerHTML == cb[5].innerHTML && cb[6].innerHTML == "") {
		console.log("getting to if statement2");
		cb[6].innerHTML = "O";
		break;
	} else if (cb[7].innerHTML == "O" && cb[7].innerHTML == cb[8].innerHTML && cb[9].innerHTML == "") {
		console.log("getting to if statement3");
		cb[9].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[4].innerHTML && cb[7].innerHTML == "") {
		console.log("getting to if statement4");
		cb[7].innerHTML = "O";
		break;
	} else if (cb[2].innerHTML == "O" && cb[2].innerHTML == cb[5].innerHTML && cb[8].innerHTML == "") {
		console.log("getting to if statement5");
		cb[8].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "O" && cb[3].innerHTML == cb[6].innerHTML && cb[9].innerHTML == "") {
		console.log("getting to if statement6");
		cb[9].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "O" && cb[3].innerHTML == cb[5].innerHTML && cb[7].innerHTML == "") {
		console.log("getting to if statement7");
		cb[7].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[5].innerHTML && cb[9].innerHTML == "") {
		console.log("getting to if statement8");
		cb[9].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[3].innerHTML && cb[2].innerHTML == "") {
		console.log("getting to if statement1");
		cb[2].innerHTML = "O";
		break;
	} else if (cb[4].innerHTML == "O" && cb[4].innerHTML == cb[6].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement2");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[7].innerHTML == "O" && cb[7].innerHTML == cb[9].innerHTML && cb[8].innerHTML == "") {
		console.log("getting to if statement3");
		cb[8].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[7].innerHTML && cb[4].innerHTML == "") {
		console.log("getting to if statement4");
		cb[4].innerHTML = "O";
		break;
	} else if (cb[2].innerHTML == "O" && cb[2].innerHTML == cb[8].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement5");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "O" && cb[3].innerHTML == cb[9].innerHTML && cb[6].innerHTML == "") {
		console.log("getting to if statement6");
		cb[6].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "O" && cb[3].innerHTML == cb[7].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement7");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[9].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement8");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[2].innerHTML == "O" && cb[2].innerHTML == cb[3].innerHTML && cb[1].innerHTML == "") {
		console.log("getting to if statement1");
		cb[1].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "O" && cb[5].innerHTML == cb[6].innerHTML && cb[4].innerHTML == "") {
		console.log("getting to if statement2");
		cb[4].innerHTML = "O";
		break;
	} else if (cb[8].innerHTML == "O" && cb[8].innerHTML == cb[9].innerHTML && cb[7].innerHTML == "") {
		console.log("getting to if statement3");
		cb[7].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "O" && cb[1].innerHTML == cb[4].innerHTML && cb[7].innerHTML == "") {
		console.log("getting to if statement4");
		cb[7].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "O" && cb[5].innerHTML == cb[8].innerHTML && cb[2].innerHTML == "") {
		console.log("getting to if statement5");
		cb[2].innerHTML = "O";
		break;
	} else if (cb[6].innerHTML == "O" && cb[6].innerHTML == cb[9].innerHTML && cb[3].innerHTML == "") {
		console.log("getting to if statement6");
		cb[3].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "O" && cb[5].innerHTML == cb[7].innerHTML && cb[3].innerHTML == "") {
		console.log("getting to if statement7");
		cb[3].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "O" && cb[5].innerHTML == cb[9].innerHTML && cb[1].innerHTML == "") {
		console.log("getting to if statement8");
		cb[1].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[2].innerHTML && cb[3].innerHTML == "") {
		cb[3].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[3].innerHTML && cb[2].innerHTML == "") {
		cb[2].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[7].innerHTML && cb[4].innerHTML == "") {
		cb[4].innerHTML = "O";
		break;
	} else if (cb[4].innerHTML == "X" && cb[4].innerHTML == cb[5].innerHTML && cb[6].innerHTML == "") {
		
		cb[6].innerHTML = "O";
		break;
	}  else if (cb[4].innerHTML == "X" && cb[4].innerHTML == cb[6].innerHTML && cb[5].innerHTML == "") {
		
		cb[5].innerHTML = "O";
		break;
	} else if (cb[7].innerHTML == "X" && cb[7].innerHTML == cb[8].innerHTML && cb[9].innerHTML == "") {
		
		cb[9].innerHTML = "O";
		break;
	} else if (cb[7].innerHTML == "X" && cb[7].innerHTML == cb[9].innerHTML && cb[8].innerHTML == "") {
		
		cb[8].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[4].innerHTML && cb[7].innerHTML == "") {
	
		cb[7].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[7].innerHTML && cb[4].innerHTML == "") {
	
		cb[4].innerHTML = "O";
		break;
	}else if (cb[2].innerHTML == "X" && cb[2].innerHTML == cb[5].innerHTML && cb[8].innerHTML == "") {

		cb[8].innerHTML = "O";
		break;
	} else if (cb[2].innerHTML == "X" && cb[2].innerHTML == cb[8].innerHTML && cb[5].innerHTML == "") {
	
		cb[5].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "X" && cb[3].innerHTML == cb[6].innerHTML && cb[9].innerHTML == "") {
	
		cb[9].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "X" && cb[3].innerHTML == cb[9].innerHTML && cb[6].innerHTML == "") {
	
		cb[6].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "X" && cb[3].innerHTML == cb[5].innerHTML && cb[7].innerHTML == "") {
	
		cb[7].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "X" && cb[3].innerHTML == cb[7].innerHTML && cb[5].innerHTML == "") {

		cb[5].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[5].innerHTML && cb[9].innerHTML == "") {
		
		cb[9].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[9].innerHTML && cb[5].innerHTML == "") {
		
		cb[5].innerHTML = "O";
		break;
	} else if (cb[2].innerHTML == "X" && cb[2].innerHTML == cb[3].innerHTML && cb[1].innerHTML == "") {
		console.log("getting to if statement1");
		cb[1].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "X" && cb[5].innerHTML == cb[6].innerHTML && cb[4].innerHTML == "") {
		console.log("getting to if statement2");
		cb[4].innerHTML = "O";
		break;
	} else if (cb[8].innerHTML == "X" && cb[8].innerHTML == cb[9].innerHTML && cb[7].innerHTML == "") {
		console.log("getting to if statement3");
		cb[7].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[4].innerHTML && cb[7].innerHTML == "") {
		console.log("getting to if statement4");
		cb[7].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "X" && cb[5].innerHTML == cb[8].innerHTML && cb[2].innerHTML == "") {
		console.log("getting to if statement5");
		cb[2].innerHTML = "O";
		break;
	} else if (cb[6].innerHTML == "X" && cb[6].innerHTML == cb[9].innerHTML && cb[3].innerHTML == "") {
		console.log("getting to if statement6");
		cb[3].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "X" && cb[5].innerHTML == cb[7].innerHTML && cb[3].innerHTML == "") {
		console.log("getting to if statement7");
		cb[3].innerHTML = "O";
		break;
	} else if (cb[5].innerHTML == "X" && cb[5].innerHTML == cb[9].innerHTML && cb[1].innerHTML == "") {
		console.log("getting to if statement8");
		cb[1].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[3].innerHTML && cb[2].innerHTML == "") {
		console.log("getting to if statement1");
		cb[2].innerHTML = "O";
		break;
	} else if (cb[4].innerHTML == "X" && cb[4].innerHTML == cb[6].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement2");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[7].innerHTML == "X" && cb[7].innerHTML == cb[9].innerHTML && cb[8].innerHTML == "") {
		console.log("getting to if statement3");
		cb[8].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[7].innerHTML && cb[4].innerHTML == "") {
		console.log("getting to if statement4");
		cb[4].innerHTML = "O";
		break;
	} else if (cb[2].innerHTML == "X" && cb[2].innerHTML == cb[8].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement5");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "X" && cb[3].innerHTML == cb[9].innerHTML && cb[6].innerHTML == "") {
		console.log("getting to if statement6");
		cb[6].innerHTML = "O";
		break;
	} else if (cb[3].innerHTML == "X" && cb[3].innerHTML == cb[7].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement7");
		cb[5].innerHTML = "O";
		break;
	} else if (cb[1].innerHTML == "X" && cb[1].innerHTML == cb[9].innerHTML && cb[5].innerHTML == "") {
		console.log("getting to if statement8");
		cb[5].innerHTML = "O";
		break;
	} else if (document.getElementById(idName).innerHTML != "") {
		continue;
	} else if (document.getElementById(idName).innerHTML == "") {
		document.getElementById(idName).innerHTML = currentPlayer;
		break;
	} // else if
	} while(true);
} //computerTakeTurn

function playerTakeTurn (e) {
	if (e.innerHTML == "") {
	e.innerHTML = currentPlayer;
	checkGameStatus();
	// if game not over computer goes
		if (gameStatus == "") {
			setTimeout(function() {
				computerTakeTurn();
				checkGameStatus();
		}, 750 //function
			); //setTimeout
		} // if
	} else {
		showLightBox("This box is already selected. ", "Please try another.");
		return;
	} //else
} //playerTakeTurn

function checkGameStatus () {
	numTurns++;
	
	if (numTurns == 9) {
		gameStatus = "Tie Game";
	} //numturns
	
	if (checkWin()) {
		
		gameStatus = currentPlayer + " wins!";
		console.log("Game status: " + gameStatus);
			
	} //if
	currentPlayer = (currentPlayer == "X" ? "O": "X");
	
	//game is over
		if (gameStatus != "") {
		setTimeout (function () {
		showLightBox(gameStatus, "Game Over. ");
			}, 500 //function
		); //timeout
	} // if
} //checkgamestatus

function checkWin () {
	let cb = []; // current board
	cb[1] = document.getElementById("one").innerHTML;
	cb[2] = document.getElementById("two").innerHTML;
	cb[3] = document.getElementById("three").innerHTML;
	cb[4] = document.getElementById("four").innerHTML;
	cb[5] = document.getElementById("five").innerHTML;
	cb[6] = document.getElementById("six").innerHTML;
	cb[7] = document.getElementById("seven").innerHTML;
	cb[8] = document.getElementById("eight").innerHTML;
	cb[9] = document.getElementById("nine").innerHTML;
	
	if (cb[1] != ""  && cb[1] == cb[2] && cb[2] == cb[3]) {
		return true;
		
	}
	if (cb[7] != ""  && cb[7] == cb[8] && cb[8] == cb[9]) {
		return true;
		
	}
	if (cb[4] != ""  && cb[4] == cb[5] && cb[5] == cb[6]) {
		return true;
		
	}
	if (cb[1] != ""  && cb[1] == cb[4] && cb[4] == cb[7]) {
		return true;
		
	}
	if (cb[2] != ""  && cb[2] == cb[5] && cb[5] == cb[8]) {
		return true;
		
	}
	if (cb[3] != ""  && cb[3] == cb[6] && cb[6] == cb[9]) {
		return true;
		
	}
	if (cb[1] != ""  && cb[1] == cb[5] && cb[5] == cb[9]) {
		return true;
		
	}
	if (cb[3] != ""  && cb[3] == cb[5] && cb[5] == cb[7]) {
		return true;
		
	}
	
	
} //checkWin

function changeVisibility(divID) {
	let element = document.getElementById(divID);
	
	// if element exits, switch it's class
	// between hidden and unhidden
	if (element) {
		element.className = (element.className == 'hidden')?'unhidden':'hidden';
		
	} // if
} //changeVisibility

function showLightBox (message, message2) {
	
	//set messages
	document.getElementById("message").innerHTML = message;
	document.getElementById("message2").innerHTML = message2;
	// show lightbox
	
	changeVisibility("lightbox");
	changeVisibility("boundaryMessage");
	
} //showLightBox

function continueGame () {
	changeVisibility("lightbox");
	changeVisibility("boundaryMessage");
	
	// if the game is over show controls
	if (gameStatus != "") {
		changeVisibility("controls");
	} //if
} //continue game