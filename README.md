# Multithreaded Client-Server Auction System

This project involves the design of a multi-threaded client-server application using Java for an auction system.

## Features
- List all items currently in the auction
- Add new items to the auction
- Place bids on items

## Commands
- _show_: Displays a table containing all items in the auction. The columns include the item name, current bid and the IP address of the client that made the bid.
- _item <string>_: Adds a new item to the auction with a bid price of 0 (meaning no bid has been made).
- _bid <item> <value>_: Attemps to make a bid of <value> for the item <item>.

## Usage
- This client-server application is run on the command line directory (CMD). Please open the CMD with 2 tabs.
- First cd to _Multi-thread-Auction-System/server_ and launch the server: _>java Server_
- Now in another tab, cd to _Multi-thread-Auction-System/client_ and run any of the 3 commands listed above. When initially running _java Client show_, it should display: 'There are currently no items in this auction'.
- You can add an item to the bid by running: _java Client item Table_. This adds a 'Table' to the auction.
- After this, you may bid the Table with a value: _java Client bid Table 20.0_. It will output 'Accepted'.
- When running _java Client show_, it should show this: 'Table : 20.0 :  127.0.0.1'.
- A log.txt file is created to log every valid client request.
