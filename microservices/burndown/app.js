// Import the Express.js module
const express = require('express');

// Create an instance of the Express application
const app = express();

// Define a route for the homepage
app.get('/heartbeat', (req, res) => {
  res.send('Yes, I am alive!'); // Send a response to the client
});