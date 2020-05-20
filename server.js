'use strict';
const { v4: uuidv4 } = require('uuid');

const express = require('express');
const { Server } = require('ws');
const path = require('path');
const url = require('url');

const PORT = process.env.PORT || 3000;

const server = express()
  .use(express.static('public'))
  .listen(PORT, () => console.log(`Listening on ${PORT}`));


const wss = new Server({ server });

let connections = {};
let games = {};

wss.on('connection', (ws, req) => {
  console.log('Client connected');

  var url_str = req.url;
  // console.log(`URL => ${url_str}`);
  console.log(url_str);
  
  var reqURL = url.parse(url_str, true);
  var gameid = reqURL.query.gameid;
  console.log("gameid=" + gameid);

  if(reqURL.pathname == '/game'){

    ws.id = gameid;
    ws.gameid = gameid;
    console.log('game connected');
    games[gameid] = ws;

    ws.on('message', message => {
      console.log(`Received message from ${ws.id} => ${message}`);
      // forward game messages with ids to controllers

      var splitMessage = message.split(",");

      var user = splitMessage[0];
      var message = splitMessage[1];

      // send messages from the game to controller clients
      connections[user].send(message);

    })


  } else {

    if(games){
      // give the client a unique ID
      ws.id = uuidv4();
      ws.gameid = gameid;
      console.log(ws.id);
      connections[ws.id]= ws;

      // tell the game the client's
      if(ws.gameid > 0 && games[gameid]){
        games[gameid].send("New Client," + ws.id);
        console.log("Let game know there's a new client");
      }

      // listen for messages
      ws.on('message', message => {
        // send all controller messages to the game
        if(message.split("=")[0] == "gameid"){
          ws.gameid = message.split("=")[1];
        }

        if(ws.gameid > 0 && games[gameid]){
          games[ws.gameid].send("Move" + "," + ws.id + "," + message);
        }

        console.log(`Received message from ${ws.id} => ${message}`)
      })

      ws.on('close', () => {
        delete connections[ws.id];
        if(ws.gameid > 0 && games[gameid]){
          games[ws.gameid].send("Client disconnected" + "," + ws.id);
        }
        ws.gameid = 0;
        console.log('Client disconnected: ' + ws.id);
      });
    }
  }

});

// setInterval(() => {
//   wss.clients.forEach((client) => {
//     client.send(new Date().toTimeString());
//   });
// }, 1000);