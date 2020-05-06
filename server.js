'use strict';
const { v4: uuidv4 } = require('uuid');

const express = require('express');
const { Server } = require('ws');
const path = require('path');

const PORT = process.env.PORT || 3000;
const INDEX = '/index.html';

// const server = express()
//   .use((req, res) => {
//     res.sendFile(INDEX, { root: __dirname });
//   })
//   .listen(PORT, () => console.log(`Listening on ${PORT}`));

const server = express()
  .use(express.static('public'))
  .listen(PORT, () => console.log(`Listening on ${PORT}`));


const wss = new Server({ server });

let connections = {};
let game = {};

wss.on('connection', (ws, req) => {
  console.log('Client connected');

  var url = req.url;
  console.log(`URL => ${url}`);

  if(url == '/game'){

    ws.id = 'game';
    console.log('game connected');
    game = ws;

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

    // give the client a unique ID
    ws.id = uuidv4();
    console.log(ws.id);
    connections[ws.id]= ws;

    // tell the game the client's
    game.send("New Client," + ws.id);

    // listen for messages
    ws.on('message', message => {
      // send all controller messages to the game
      if(game){
        game.send("Move" + "," + ws.id + "," + message);
      }

      console.log(`Received message from ${ws.id} => ${message}`)
    })

    ws.on('close', () => {
      delete connections[ws.id];
      game.send("Client disconnected" + "," + ws.id);
      console.log('Client disconnected: ' + ws.id);
    });
  }

});

// setInterval(() => {
//   wss.clients.forEach((client) => {
//     client.send(new Date().toTimeString());
//   });
// }, 1000);