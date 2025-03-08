
//global variables
var speedOfPaddle1 = 0;
var positionOfPaddle1 = document.getElementById("paddle1").offsetTop;
const startPositionOfPaddle1 = document.getElementById("paddle1").offsetTop;
var speedOfPaddle2 = 0;
var positionOfPaddle2 = document.getElementById("paddle2").offsetTop;
const startPositionOfPaddle2 = document.getElementById("paddle2").offsetTop;
var paddleHeight = document.getElementById("paddle1").offsetHeight;
var paddleWidth = document.getElementById("paddle1").offsetWidth;
var gameBoardHeight = document.getElementById("gameBoard").offsetHeight;
var gameBoardWidth = document.getElementById("gameBoard").offsetWidth;
var paddleHeight2 = document.getElementById("paddle2").offsetHeight;
const originalPaddleHeight = document.getElementById("paddle2").offsetHeight;
var gameBoardHeight2 = document.getElementById("gameBoard").offsetHeight;
const startTopPositionOfBall = document.getElementById("ball").offsetTop;
const startLeftPositionOfBall= document.getElementById("ball").offsetLeft;
var topPositionOfBall = startTopPositionOfBall;
var leftPositionOfBall = startLeftPositionOfBall;
var topSpeedOfBall = 0;
var leftSpeedOfBall = 0;
const ballHeight = document.getElementById("ball").offsetHeight;
var score1 = 0;
var score2 = 0;
var playerTwoCounter = 1;
var bounce = new sound ("bonk.wav");
var goal = new sound ("goal.wav");
var playerTwoAbility = 0;
var playerOneAbility = 0;
var playerOneCounter = 1;
//startBallMotion

/*
window.onload = function () {
	console.log("above start ball");
	startBall();
};//startBall
*/

// used to control game start/stop
var controlPlay;

function showInstructions () {
	message1 = "Welcome to the Pong Game. In this game you and a friend will attempt to maneuver each of your paddles such that it will stop the ball from getting past it. Click W or the up arrow to move up and S or the down arrow to move down. First player to 10 points wins. Hint: Use abilities to double the size of your paddle for a turn! Click D or the left arrow key to use the ability";
	message2 = "Have Fun!";
	showLightBox(message1,message2);
}




//move paddles
document.addEventListener('keydown', function (e) {
	 console.log("keydown" + e.keyCode);
	if (e.keyCode == 87|| e.which == 87 ) {
		speedOfPaddle1 = -10;
	}//if 
		if (e.keyCode == 83|| e.which == 83 ) {
		speedOfPaddle1 = 10;
	}//if 
	if (e.keyCode == 38|| e.which == 38 ) {
		speedOfPaddle2 = -10;
	}//if 
	if (e.keyCode == 40|| e.which == 40 ) {
		speedOfPaddle2 = 10;
	}//if 
	if (e.keycode == 37|| e.which == 37 && playerTwoAbility <= 3) {
		playerTwoCounter--;
		show();
		// paddleHeightIncrease(2);
	} // if
	if (e.keycode == 68|| e.which == 68 && playerTwoAbility <= 3) {
		playerOneCounter--;
		show();
		// paddleHeightIncrease(2);
	} // if
});
//stop paddles
document.addEventListener('keyup', function (e) {
	// console.log("keyup" + e.keyCode);
	if (e.keyCode == 87|| e.which == 87 ) {
		speedOfPaddle1 = 0;
	}//if 
	if (e.keyCode == 38|| e.which == 38 ) {
		speedOfPaddle2 = 0;
	}//if 
	if (e.keyCode == 83|| e.which == 83 ) {
		speedOfPaddle1 = 0;
	} //if
	if (e.keyCode == 40|| e.which == 40 ) {
		speedOfPaddle2 = 0;
	}//if 

});

// object constructor to play sounds
//https://www.w3schools.com/graphics/game_sound.asp
function sound(src) {
  this.sound = document.createElement("audio");
  this.sound.src = src;
  this.sound.setAttribute("preload", "auto");
  this.sound.setAttribute("controls", "none");
  this.sound.style.display = "none";
  document.body.appendChild(this.sound);
  this.play = function(){
    this.sound.play();
  }
  this.stop = function(){
    this.sound.pause();
  }
}

// start the ball movement
function startBall () {
	let direction = 1;
	let directiony = 1;
	topPositionOfBall = startTopPositionOfBall;
	leftPositionOfBall = startLeftPositionOfBall;
	
	// 50% chance of starting either direction
	if (Math.random() < 0.5) {
		direction = 1;
	
	}else {
		direction = -1;
	}
	
	
	topSpeedOfBall = -1 * (Math.random() * 2 + 3); // 3-4
	leftSpeedOfBall = direction * (Math.random() * 2 + 3);
	
} // startball

function scoreReset() {
	score1 = 0;
	score2 = 0;
	document.getElementById("score1").innerHTML = score1;
	document.getElementById("score2").innerHTML = score2;
}





//update locations of paddles and ball
function show() {
	if (playerTwoCounter == 0) {
		paddleHeightIncrease(2);
		if (positionOfPaddle2 >= gameBoardHeight2 - paddleHeight2) {
		positionOfPaddle2 = gameBoardHeight2 - paddleHeight2;
	}//if
} //if
if (playerOneCounter == 0) {
		paddleHeightIncrease(1);
		if (positionOfPaddle1 >= gameBoardHeight - paddleHeight) {
		positionOfPaddle1 = gameBoardHeight - paddleHeight;
	}//if
} //if


	playerTwoCounter =1;
	positionOfPaddle1 += speedOfPaddle1;
	positionOfPaddle2 += speedOfPaddle2;
	
	 topPositionOfBall += topSpeedOfBall;
	 leftPositionOfBall += leftSpeedOfBall;

	if (positionOfPaddle1 <= 0) {
		positionOfPaddle1 = 0;
	} //if
	
	if (positionOfPaddle2 <= 0) {
		positionOfPaddle2 = 0;
	} //if
	
	//stops paddle from leaving bottom of board
	if (positionOfPaddle1 >= gameBoardHeight - paddleHeight) {
		positionOfPaddle1= gameBoardHeight - paddleHeight;
	}//if
	if (positionOfPaddle2 >= gameBoardHeight2 - paddleHeight2) {
		positionOfPaddle2 = gameBoardHeight2 - paddleHeight2;
	}//if
	
	if (topPositionOfBall <= 0 || topPositionOfBall >= gameBoardHeight - ballHeight) {
		topSpeedOfBall *= -1;
		
	}
	
	 if (leftPositionOfBall <= paddleWidth) {
		 // if ball hits left paddle, change direction
		 if (topPositionOfBall > positionOfPaddle1 && topPositionOfBall < positionOfPaddle1 +paddleHeight) {
			bounce.play();
			leftSpeedOfBall -= 1;
			topSpeedOfBall -= 1;
			leftSpeedOfBall *= -1;
		 } else {
			 score2++;
			  if (score2 == 10) {
				 stopGame();
			 }
			 document.getElementById("score2").innerHTML = score2;
			 goal.play();
			leftSpeedOfBall+= 1;
			topSpeedOfBall+= 1;
			paddleHeight = originalPaddleHeight;
			paddleHeight2 = originalPaddleHeight;
			document.getElementById("paddle1").style.height = paddleHeight + 'px';
			document.getElementById("paddle2").style.height = paddleHeight2 + 'px';
			 startBall();
		 }
	 }
	 
	 // ball on right side
	 if (leftPositionOfBall >= gameBoardWidth - paddleWidth - ballHeight) {
		 //if ball hits right paddle, change direction
		 if (topPositionOfBall > positionOfPaddle2 &&  topPositionOfBall < positionOfPaddle2 + paddleHeight2) {
			 bounce.play();
			 leftSpeedOfBall+= 1;
			 topSpeedOfBall+= 1;
			 leftSpeedOfBall *= -1;
		 } else {
			 goal.play();
			 score1++;
			 if (score1 == 10) {
				stopGame();
			 }
			 leftSpeedOfBall+= 1;
			 topSpeedOfBall+= 1;
			 document.getElementById("score1").innerHTML = score1;
			 	paddleHeight = originalPaddleHeight;
				paddleHeight2 = originalPaddleHeight;
				document.getElementById("paddle1").style.height = paddleHeight + 'px';
				document.getElementById("paddle2").style.height = paddleHeight2 + 'px';
			 startBall();
		 } // else
	 } //if
	
	// debug
	//console.log("moving ball to " + topPositionOfBall + " and " + leftPositionOfBall);
	document.getElementById("paddle1").style.top = positionOfPaddle1 + "px";
	document.getElementById("paddle2").style.top = positionOfPaddle2 + "px";
	document.getElementById("ball").style.top = topPositionOfBall + "px";
	document.getElementById("ball").style.left = leftPositionOfBall + "px";
} // show

//start game play
function startGame() {
	//reset scores, ball and paddle locations
	score1 = 0;
	score2 = 0;
	document.getElementById("score1").innerHTML = score1;
	document.getElementById("score2").innerHTML = score2;
	positionOfPaddle1 = startPositionOfPaddle1;
	positionOfPaddle2 = startPositionOfPaddle2;
	
	startBall();
	
	if (!controlPlay) {
		controlPlay = window.setInterval(show, 1000/60 );
	} // if
}

//stop game play
function stopGame() {
	pauseGame();
	
	let message1 = "Tie Game";
	let message2 = "Close to continue";
	
	if (score1 > score2) {
		message1 = "Player 1 wins with " + score1 + " points!";
		message2 = "Player 2 had " + score2 + " points";
	} else if (score2 > score1) {
		message1 = "Player 2 wins with " + score2 + " points!";
		message2 =  "Player 1 had " + score1 + " points";
	} // else
	
	showLightBox(message1, message2);
	
} //stopGame

// resume game play
function resumeGame() {
	if (!controlPlay) {
		controlPlay = window.setInterval(show, 1000/60 );
	} // if
	
} // resumeGame

function pauseGame() {
	window.clearInterval(controlPlay);
	controlPlay = false;
	
} // pausegame

/**** lightbox code ***/
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
} //continue game

function paddleHeightIncrease(p) {

 
	    playerOneCounter = 1;
	   playerTwoCounter = 1;
	if (p == 2 && controlPlay) {
		if (playerTwoAbility <= 3 && playerTwoAbility >= 0) {
		paddleHeight2 = 300;
		document.getElementById("paddle2").style.height = paddleHeight2 + 'px';
	document.getElementById("paddle1").style.top = positionOfPaddle1 + "px";
	document.getElementById("paddle2").style.top = positionOfPaddle2 + "px";
	document.getElementById("ball").style.top = topPositionOfBall + "px";
	document.getElementById("ball").style.left = leftPositionOfBall + "px";
		playerTwoAbility++;
		document.getElementById("ability2").innerHTML = 4 - playerTwoAbility;
		}
	} else if (playerOneAbility <=3)	{
	if (p == 1 && controlPlay) {
		if (playerOneAbility >= 0) {
		paddleHeight = 300;
		document.getElementById("paddle1").style.height = paddleHeight + 'px';
		playerOneAbility++;
		document.getElementById("ability1").innerHTML = 4 - playerOneAbility;
		} //if
	} // outer if
	
} //else if

} 

function returnToPaddleHeight (p) {
	console.log("getting to Paddle Height decrease");
	console.log(originalPaddleHeight);
	p==2? paddleHeight2 = 150 : paddleHeight = 150;
	document.getElementById("paddle2").style.height = paddleHeight2 + 'px';
}


