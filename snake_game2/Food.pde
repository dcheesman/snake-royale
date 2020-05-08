class Food {
  int x;
  int y;

  Food() {
    spawn();
  }

  void update(){
    if(x < safeZone.cx || x > safeZone.cx + safeZone.cw ){
      spawn();
    }

    if(y < safeZone.cy || y > safeZone.cy + safeZone.ch ){
      spawn();
    }
  }

  void spawn() {
    x = floor(random(safeZone.cx, safeZone.cx+safeZone.cw));
    y = floor(random(safeZone.cy, safeZone.cy+safeZone.ch));
  }

  void show() {
    game.fill(0, 255, 0);
    game.noStroke();
    game.rect(x, y, 1, 1);
  }
}