class Food {
  int x;
  int y;

  int growth;
  color c;

  Food( int _growth, color _c) {
    growth = _growth;
    c = _c;
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
    game.fill(c);
    game.noStroke();
    game.rect(x, y, 1, 1);
  }
}
