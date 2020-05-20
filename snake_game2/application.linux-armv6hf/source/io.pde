void webSocketEvent(String msg){

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

void newQRCode(String url){
  int qrCodeWidth = 200;
  int qrCodeHeight = qrCodeWidth;

  QRCode = zxing4p.generateQRCode(url, qrCodeWidth, qrCodeHeight);
}
