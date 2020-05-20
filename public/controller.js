
var ws;
var myColor;

function setup() {
  // set my color here and maybe my ID?
  createCanvas(windowWidth, windowHeight);

  let params = getURLParams();
  console.log(params.gameid);

  var HOST = location.href.replace(/^http/, 'ws')
  ws = new WebSocket(HOST);
  myColor = color(10, 10, 10);

  ws.onopen = function (event) {
    console.log('socket open');
    ws.send("gameid="+params.gameid); 
  };

  ws.onmessage = function(message) {

    var messageText = message.data;
    var splitMessage = messageText.split("=");

    if(splitMessage[0] == "color"){
      myColor = color("#" + splitMessage[1].substring(2, 8));
      console.log("Color Set " + splitMessage[1]);
    }
  };

  frameRate(10);
}


function draw() {
  background(0);

  fill(myColor)
  stroke(255);
  rect(0 , 0 , width/2, height);
  rect(width/2 , 0 , width/2 , height);

  if(mousePressed == true){
    if(mouseX < width/2){
      fill(255, 100);
      rect(0 , 0 , width/2, height);
    } else {
      fill(255, 100);
      rect(width/2 , 0 , width/2 , height);
    }
  }
}


function mousePressed(){
  if(mouseX < width/2){
    console.log("Left");
    ws.send("Left");
  } else {
    console.log("Right");
    ws.send('Right');
  }
  return false;
}