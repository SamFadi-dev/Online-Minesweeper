# Minesweeper Game

## Overview
This project is a networked implementation of the classic Minesweeper game. It includes both a server and a client component, allowing multiple players to connect to the server and play the game simultaneously.

## Features
- **Multiplayer Support**: Multiple clients can connect to the server and play the game.
- **Recursive Reveal**: Automatically reveals adjacent cells when a cell with no adjacent mines is revealed.
- **Command-Based Interaction**: Clients interact with the server using text-based commands.
- **Cheat Mode**: Allows players to reveal the entire board.

## Components
### Server
The server component is responsible for:
- Managing the game state.
- Handling client connections.
- Processing client commands.
- Broadcasting game updates to all connected clients.

### Client
The client component allows players to:
- Connect to the server.
- Send commands to reveal cells, flag cells, and quit the game.
- Receive updates from the server.

## Commands
- `TRY x y`: Reveals the cell at coordinates (x, y).
- `FLAG x y`: Flags the cell at coordinates (x, y) as a mine.
- `CHEAT`: Reveals the entire board.
- `QUIT`: Disconnects from the server.

## How to Run
### Server
1. Compile the server code:
   ```sh
   javac MinesweeperServer.java

2. Run the server:
    ```sh
   java MinesweeperServer

### Client
1. Compile the client code:
    ```sh
    javac MinesweeperClient.java

2. Run the client:
    ```sh
    java MinesweeperClient

