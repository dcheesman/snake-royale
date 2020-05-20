## A multi-player shrinking-border battle-royale snake game

This is a single-room multi-player game. This means everyone is looking at the same screen but using their phones as a controller.

Players scan the on-screen QR code to join. The only options are turn left and turn right.

Your snake will die if it goes out of the safe zone (outlined in red), if it runs into itself, or runs into another player.

Your snake will grow longer if you eat food (green and blue dots) or if another player runs into your tail and dies.

Each round goes until there’s only one snake left! To keep rounds short there is a safe zone that shrinks periodically.


## How to use

Create your own game server by uploading to Heroku.

To run the game you will need Processing. There are two libraries you will need. The websockets library which can be downloaded through Processing’s Sketch→ Import Library → Add Library menu. Additionally you’ll need the [ZXing library](http://cagewebdev.com/zxing4processing-processing-library/). This needs be manually installed in your systems Processing/libraries folder. 
