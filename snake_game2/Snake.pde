class Snake {
  int x, y;
  int vx, vy;
  boolean dead;
  int length;

  String id;

  color c;

  boolean movedThisFrame = false;

  ArrayList<PVector> body;

  Snake (String clientID) {
    id = clientID;
    c = colorOptions[nextColor++];
    nextColor %= colorOptions.length;
    spawn();
  }

  void spawn() {
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

  void update(int myID){

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

  void show() {
    game.fill(c);
    game.noStroke();
    for (PVector p : body) {
      game.rect(p.x, p.y, 1, 1);
    }
  }

  void grow(int parts) {
    length += parts;
  }

  void eat(Food[] food){
    for (Food f : food) {
      if(f.x == x && f.y == y){
        grow(f.growth);                // <--- food grow amount
        f.spawn();
      }
    }
  }

  void selfHitTest(){
    for(int i = 0; i < body.size() - 1; i++){ // skip the last one (head)
      PVector part = body.get(i);
      if(part.x == x && part.y == y){
        die();
      }
    }
  }

  void die() {
    if(death == true){
      dead = true;
    } else {
      spawn(); //replace later with permanent death
    }
  }

  void turnLeft() {
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

  void turnRight() {
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