import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        // check for correct number of arguments
        if (args.length == 0) {
            System.err.println("Usage: java Client [show | item <string> | bid <item> <value>]");
            System.exit(1);
        }

        // First argument is the command to be executed
        String command = args[0]; 
        // Connect to the server
        Socket socket = new Socket("localhost", 6000); 

        try {
            // Creates a PrintWriter to send data to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
            // Creates a buffered reader to read data from the server                                                                   
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    socket.getInputStream())); 
            // Create a Scanner to read data from the console
            Scanner scanner = new Scanner(System.in); 

            // Executes the appropriate command based on the users inputted first argument
            switch (command) {
                // if the user wants to show all items
                case "show":
                    out.println("show");
                    String response = in.readLine(); //reads the first line of the response from the server
                    if (response == null) {
                        System.out.println("There are currently no items in this auction.");
                        break;
                    }
                    while (!response.equals("END")) {
                        System.out.println(response);
                        response = in.readLine();
                        if (response == null) {
                            break;
                        }
                    }
                    break;

                // if the user wants to add an item to the auction
                case "item":
                    if (args.length < 2) {
                        System.err.println("Usage: java Client item <string>");
                        break;
                    }
                    String item = args[1]; //gets the name of the item to be added
                    out.println("item " + item);
                    System.out.println(in.readLine());
                    break;

                // if the user wants to place a bid on an item
                case "bid":
                    if (args.length < 3) {
                        System.err.println("Usage: java Client bid <item> <value>");
                        break;
                    }
                    String item2 = args[1];
                    String value = args[2];
                    out.println("bid " + item2 + " " + value); // Sends the bid command, the item name, and the bid amount to the server
                    System.out.println(in.readLine());
                    break;

                // if the user enters an invalid command
                default:
                    System.err.println("Invalid command: " + command);
                    System.err.println("Usage: java Client [show | item <string> | bid <item> <value>]");
                    break;
            }
        } finally {
            socket.close(); // close the socket when the program is finished
        }
    }
}