import websockets.*;
import com.cage.zxing4p3.*;

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
int foodCount = 10;
int snakeCount = 12;
int nextColor = 0;

color winnerColor;

color[] colorOptions = {
  #a6cee3,
  #1f78b4,
  #b2df8a,
  #33a02c,
  #fb9a99,
  #e31a1c,
  #fdbf6f,
  #ff7f00,
  #cab2d6,
  #6a3d9a,
  #ffff99,
  #b15928
};

// calculated width and height after zoom
int w;
int h;

float windowX, windowY;

PImage  QRCode;

// game objects
// Snake[] snakes;
ArrayList<Snake> snakes;
Food[] food;
SafeZone safeZone;

// buffer to hold game
PGraphics game;
PGraphics overlay;

void setup() {
  size(1280 , 720);
  noSmooth();
  background(0);

  // imageMode(CENTER);

  frameRate(10);

  w = floor(width / zoom);
  h = floor(height/ zoom);

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

  food = new Food[foodCount];
  for (int i = 0; i < foodCount; ++i) {
    food[i] = new Food();
  }
  winnerColor = color(255);

  connectSocket();

   // ZXING4P ENCODE/DECODER INSTANCE
  zxing4p = new ZXING4P();
  newQRCode("https://battle-royale-snake.herokuapp.com/controller.html");
}

void draw() {
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

      for (Food f : food) {
        f.update();
        f.show();
      }

      int liveSnakes = 0;
      for(int i = 0; i < snakes.size(); i++){
        Snake snake = snakes.get(i);
        snake.update(i);
        if(snake.dead == false){
          liveSnakes++;
          snake.eat(food);
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

void reset(){
  safeZone = new SafeZone();

  for (int i = 0; i < snakes.size(); ++i) {
    Snake snake = snakes.get(i);
    snake.spawn();
  }
}

void connectSocket(){
  wsc= new WebsocketClient(this, "ws://battle-royale-snake.herokuapp.com/game");
}

void keyPressed() {
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

