import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import websockets.*; 
import com.cage.zxing4p3.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class snake_game2 extends PApplet {




boolean fullscreenPlay = true;

String appUrl = "battle-royale-snake.herokuapp.com";
// String appUrl = "localhost:3000";
String controllerURL, gameURL;

int gameID;

WebsocketClient wsc; // websocket client
ZXING4P zxing4p;

boolean death = true;

final int LOBBY = 0;
final int GAME = 1;
final int WINNER = 2;


int minPlayers = 2;
int lobbyCountdown = 50;
int winnerCountdown = 50;
int counter;
int gameMode = LOBBY;

float zoom = 4;
// how long to wait for zoom in. and how long to zoom
int roundFrames = 100;
int foodCount = 20;
int snakeCount = 12;
int nextColor = 0;

int winnerColor;

int[] colorOptions = {
  0xffa6cee3,
  0xff1f78b4,
  0xffb2df8a,
  0xff33a02c,
  0xfffb9a99,
  0xffe31a1c,
  0xfffdbf6f,
  0xffff7f00,
  0xffcab2d6,
  0xff6a3d9a,
  0xffffff99,
  0xffb15928
};

// calculated width and height after zoom
int w;
int h;

float windowX, windowY;

PImage  QRCode;

// game objects
// Snake[] snakes;
ArrayList<Snake> snakes;
Food[] food1, food2;
SafeZone safeZone;

// buffer to hold game
PGraphics game;
PGraphics overlay;

public void setup() {
  
  // size(1280 , 720);

  
  background(0);

  // imageMode(CENTER);

  frameRate(10);

  w = floor(width / zoom);
  h = floor(height/ zoom);

  gameID = floor(random(10000));
  println("GameID: ", gameID);

  game = createGraphics(w, h);
  game.noSmooth();

  overlay = createGraphics(width, height);

  overlay.beginDraw();
  overlay.textAlign(CENTER);
  overlay.textSize(33);
  overlay.endDraw();

  counter = lobbyCountdown;
  safeZone = new SafeZone();
  snakes = new ArrayList<Snake>();

  food1 = new Food[foodCount];
  food2 = new Food[foodCount];

  for (int i = 0; i < foodCount; ++i) {
    food1[i] = new Food(3, color(0, 255, 0));
  }

  for (int i = 0; i < foodCount; ++i) {
    food2[i] = new Food(8, color(0, 255, 255));
  }

  winnerColor = color(255);

  controllerURL = "https://" + appUrl;
  gameURL = "ws://" + appUrl+ "/game?gameid=" + gameID;

  connectSocket();

   // ZXING4P ENCODE/DECODER INSTANCE
  zxing4p = new ZXING4P();
  newQRCode(controllerURL + "/controller.html?gameid=" + gameID);
}

public void draw() {
  background(0);

  switch (gameMode) {
    case LOBBY :
      if(snakes.size() >= minPlayers) {
        counter--;
        if(counter <= 0){
          gameMode = GAME;
        }
      }

      overlay.beginDraw();
      overlay.clear();

      overlay.fill(255);
      overlay.text("Battle Royale SNAKES!", width/2, 40);
      overlay.text("Number of Snakes (min 2): " + snakes.size(), width/2, 100);
      overlay.text("Counter: " + counter, width/2, 150);
      overlay.text("Scan this to join!: ", width/2, 200);

      overlay.endDraw();

      image(overlay, 0, 0);
      // show QR code

      imageMode(CENTER);
      image(QRCode, width/2 , height/2);
      imageMode(CORNER);

    break;

    case GAME :
      // game mode
      w = floor(width / zoom);
      h = floor(height/ zoom);

      tint(255, 126);
      image(QRCode , 0, height-QRCode.height);
      noTint();

      overlay.beginDraw();
      overlay.clear();

      game.beginDraw();
      game.clear();

      safeZone.update();
      safeZone.show();

      for (Food f : food1) {
        f.update();
        f.show();
      }

      for (Food f : food2) {
        f.update();
        f.show();
      }

      int liveSnakes = 0;
      for(int i = 0; i < snakes.size(); i++){
        Snake snake = snakes.get(i);
        snake.update(i);
        if(snake.dead == false){
          liveSnakes++;
          snake.eat(food1);
          snake.eat(food2);
          snake.show();
        }
      }

      if(liveSnakes <= 1){
        for(int i = 0; i < snakes.size(); i++){
          Snake snake = snakes.get(i);
          if(snake.dead == false ){
            winnerColor = snake.c;
          }
        }
        reset();
        counter = winnerCountdown;
        gameMode = WINNER;
      }

      game.endDraw();
      overlay.endDraw();

      pushMatrix();
      scale(zoom);
      image(game, 0 , 0);
      popMatrix();
      image(overlay, 0, 0);

    break;

    case WINNER :
      if (counter >= 0 ){
        overlay.beginDraw();
        overlay.clear();
        overlay.fill(255);
        overlay.text("WINNER", width/2, 50);
        overlay.fill(winnerColor);
        overlay.rectMode(CENTER);
        overlay.rect(width/2, height/2, 300, 300);
        overlay.endDraw();
        counter--;

      } else {
        counter = lobbyCountdown;
        gameMode = LOBBY;

      }

      image(overlay, 0, 0);

    break;
  }
}

public void reset(){
  safeZone = new SafeZone();

  for (int i = 0; i < snakes.size(); ++i) {
    Snake snake = snakes.get(i);
    snake.spawn();
  }

  for (int i = 0; i < foodCount; ++i) {
    food1[i].spawn();
  }

  for (int i = 0; i < foodCount; ++i) {
    food2[i].spawn();
  }
}

public void connectSocket(){
  wsc= new WebsocketClient(this, gameURL);
}

public void keyPressed() {
  for (Snake s : snakes) {
    if (keyCode == LEFT) {
      s.turnLeft();
    } else if (keyCode == RIGHT) {
      s.turnRight();
    } else if (key == ' '){
      s.grow(3);
    }
  }

  if(key == 'q'){
    newQRCode("deancheesman.com");
  }
}
class Food {
  int x;
  int y;

  int growth;
  int c;

  Food( int _growth, int _c) {
    growth = _growth;
    c = _c;
    spawn();
  }

  public void update(){
    if(x < safeZone.cx || x > safeZone.cx + safeZone.cw ){
      spawn();
    }

    if(y < safeZone.cy || y > safeZone.cy + safeZone.ch ){
      spawn();
    }
  }

  public void spawn() {
    x = floor(random(safeZone.cx, safeZone.cx+safeZone.cw));
    y = floor(random(safeZone.cy, safeZone.cy+safeZone.ch));
  }

  public void show() {
    game.fill(c);
    game.noStroke();
    game.rect(x, y, 1, 1);
  }
}
class SafeZone {
  static final int WARNING = 0;
  static final int SHRINKING = 1;
  static final int WAITING = 2;

  float shrinkPercent = 0.75f;
  int mode = WARNING;
  int roundCount;

  // start zone
  float sx, sy;
  float sw, sh;


  // current zone
  float cx, cy;
  float cw, ch;

  // target zone
  float tx, ty;
  float tw, th;

  float progress;

  SafeZone() {
    // start by using the global positions and width/height
    cx = 0;
    cy = 0;
    cw = w-1;
    ch = h-1;

    spawn();
  }

  public void update() {
    roundCount++;

    // progress to next round
    if(roundCount > roundFrames){
      roundCount = 0;
      mode++;
      if(mode == 3){
        mode = 0;
        spawn();
      }
    }

    progress = (float)roundCount/(float)roundFrames;
  }

  public void show(){
    switch (mode) {
      case WARNING :
        overlay.fill(255, 0 , 0, 100);
        overlay.text("Get in the safe zone", width/2, 40);
        drawZones();

        // game.fill(255, 0, 0);
        // game.text("Get to safe zone", 10, 10);
      break;

      case SHRINKING :
        overlay.fill(255, 0 , 0, 100);
        overlay.text("Get in the safe zone", width/2, 40);
        // overlay.text("shrinking", 10, 80);
        // overlay.text("progress: " + floor(progress*100), 10, 40);
        drawZones();
        cx = map(progress, 0, 1, sx, tx);
        cy = map(progress, 0, 1, sy, ty);
        cw = map(progress, 0, 1, sw, tw);
        ch = map(progress, 0, 1, sh, th);
      break;

      case WAITING :
        drawZones();
        // game.fill(255, 0, 0);
        // game.text("Waiting for next reveal", 10, 10);
      break;

    }
  }

  public void drawZones(){
    game.stroke(255, 0, 0, 50);
    game.noFill();
    game.rect(tx, ty, tw, th);

    game.stroke(255, 0, 0);
    game.noFill();
    game.rect(cx, cy, cw, ch);
  }


  public void spawn() {
    sx = cx;
    sy = cy;
    sw = cw;
    sh = ch;

    tw = floor(ch * shrinkPercent)-1;
    th = tw;
    tx = floor(random(cx, cw - tw));
    ty = floor(random(cy, ch - th));

  }
}
class Snake {
  int x, y;
  int vx, vy;
  boolean dead;
  int length;

  String id;

  int c;

  boolean movedThisFrame = false;

  ArrayList<PVector> body;

  Snake (String clientID) {
    id = clientID;
    c = colorOptions[nextColor++];
    nextColor %= colorOptions.length;
    spawn();
  }

  public void spawn() {
    dead = false;

    body = new ArrayList<PVector>();

    x = floor(random(safeZone.tx, safeZone.tx + safeZone.tw));
    y = floor(random(safeZone.ty, safeZone.ty + safeZone.th));

    PVector head = new PVector(x, y);
    body.add(head);

    length = 5;

    vx = 0;
    vy = 1;
    int i =0;
    while( i < floor(random(4)) ) {
      turnLeft();
      i++;
    }
  }

  public void update(int myID){

    for(int i =0; i < snakes.size(); i++ ){ //todo: pass snakes through function instead of global?
      Snake snake = snakes.get(i);
      if(snake.dead == false){
        if( i != myID ){
          for(int t = 0; t < snake.body.size(); t++){
            PVector part = snake.body.get(t);
            if(part.x == x && part.y == y){
              snake.grow(body.size());
              die();
              break;
            }
          }
        }
      }
    }

    if(x + vx > safeZone.cx + safeZone.cw || x + vx < safeZone.cx){
      die();
    }

    if(y + vy > safeZone.cy + safeZone.ch || y + vy < safeZone.cy){
      die();
    }

    x += vx;
    y += vy;

    PVector newHead = new PVector(x, y);

    body.add(newHead);
    selfHitTest();

    while(body.size() > length){
      body.remove(0);
    }

    movedThisFrame = false;
  }

  public void show() {
    game.fill(c);
    game.noStroke();
    for (PVector p : body) {
      game.rect(p.x, p.y, 1, 1);
    }
  }

  public void grow(int parts) {
    length += parts;
  }

  public void eat(Food[] food){
    for (Food f : food) {
      if(f.x == x && f.y == y){
        grow(f.growth);                // <--- food grow amount
        f.spawn();
      }
    }
  }

  public void selfHitTest(){
    for(int i = 0; i < body.size() - 1; i++){ // skip the last one (head)
      PVector part = body.get(i);
      if(part.x == x && part.y == y){
        die();
      }
    }
  }

  public void die() {
    if(death == true){
      dead = true;
    } else {
      spawn(); //replace later with permanent death
    }
  }

  public void turnLeft() {
    if( movedThisFrame == false) {
      if(abs(vy)==1){
        vx = vy;
        vy = 0;
      } else {
        vy = vx * -1;
        vx = 0;
      }
      movedThisFrame = true;
    }
  }

  public void turnRight() {
    if( movedThisFrame == false) {
      if(abs(vx)==1){
        vy = vx;
        vx = 0;
      } else {
        vx = vy * -1;
        vy = 0;
      }
      movedThisFrame = true;
    }
  }
}
public void webSocketEvent(String msg){

 String[] splitText = split(msg, ",");

  if (splitText[0].equals("New Client")) {
    Snake snake = new Snake(splitText[1]);
    snakes.add(snake);

    String[] message = { snake.id, "color=" + hex(snake.c) };
    wsc.sendMessage( join(message, ",") );
    println("new snake added");

  } else if (splitText[0].equals("Client disconnected")) {
    println("disconnected: " + splitText[1]);
    for(int i = 0; i < snakes.size(); i++){
      Snake snake = snakes.get(i);
      if(snake.id.equals(splitText[1])){
        snakes.remove(i);
        break;
      }
    }
  } else if (splitText[0].equals("Move")){
    for( int i = 0; i < snakes.size(); i++ ){
      Snake snake = snakes.get(i);
      if( snake.id.equals(splitText[1])){
        if( splitText[2].equals("Left")){
          snake.turnLeft();
        } else if (splitText[2].equals("Right")){
          snake.turnRight();
        }
      }
    }
  }

}

public void newQRCode(String url){
  int qrCodeWidth = 200;
  int qrCodeHeight = qrCodeWidth;

  QRCode = zxing4p.generateQRCode(url, qrCodeWidth, qrCodeHeight);
}
  public void settings() {  fullScreen();  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "snake_game2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
