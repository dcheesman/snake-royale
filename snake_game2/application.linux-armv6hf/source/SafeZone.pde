class SafeZone {
  static final int WARNING = 0;
  static final int SHRINKING = 1;
  static final int WAITING = 2;

  float shrinkPercent = 0.75;
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

  void update() {
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

  void show(){
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

  void drawZones(){
    game.stroke(255, 0, 0, 50);
    game.noFill();
    game.rect(tx, ty, tw, th);

    game.stroke(255, 0, 0);
    game.noFill();
    game.rect(cx, cy, cw, ch);
  }


  void spawn() {
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
