# EECE4371-MNicholas-final-project
TCP Tic-Tac-Toe

# Description
This is a real-time, multiplayer Tic-Tac-Toe game that uses TCP connections to allow two clients to play with each other. First, run Server.java to set up the server and wait for clients to join. Then, run Client.java to connect the first player to the server via TCP connection. A window will pop up on this player's screen with a message saying that they're waiting for another player to connect. Once a second player connects to the server, the Tic-Tac-Toe board will automatically load in both of the clients' windows.   
The first player to connect is X's and they get to go first.  

# Why I chose this project  
I chose this project because I'm interested in computer graphics and game design, so I thought creating a simple multiplayer game would be a fun way to apply networking to my own project. Tic-Tac-Toe was a good choice because it's a turn-based game, so the networking aspect of the project is somewhat similar to the chat room project we did earlier. Also, creating a GUI in Java for Tic-Tac-Toe seemed like it would be manageable without any prior experience with computer graphics.  

# Technical Challenges  
The biggest challenge I faced during the implementation of my project was creating the GUI for the client program. I started out my implementation by creating a Java client just for testing the server but I wanted to use this opportunity to learn more about graphics in Java. After looking into the java.awt.Graphics class, I decided to try adding a GUI to the client instead of making an Android client. I referenced documentation for this class online and this website was the most helpful to me when implementing the client program: https://www.ntu.edu.sg/home/ehchua/programming/java/J4b_CustomGraphics.html.  
Another technical challenge I faced was implementing the logic that allowed the two players to take turns, but only after both players have connected to the server. Most of my debugging time was spent trying to fix 
