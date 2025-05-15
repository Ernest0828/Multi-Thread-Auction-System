import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.Socket;

public class Server {
    private static final String LOG_FILE_NAME = "log.txt";
    private static final Map<String, Double> items = new ConcurrentHashMap<>();
    private static final Map<String, String> bids = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        ExecutorService executor = null;

        try {
            serverSocket = new ServerSocket(6000); //creates server on same port with client
            executor = Executors.newFixedThreadPool(30); //creates a fixed thread pool of 30 threads
            //Runs continuously until the server is terminated
            while (true) {
                Socket clientSocket = serverSocket.accept(); //accepts a connection from a client
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    //Handles requests from a single client connection
    private static class ClientHandler implements Runnable {
        //initialize the client socket and get the clients IP address
        private final Socket clientSocket;
        private final String clientAddress;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.clientAddress = clientSocket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try (Socket socket = clientSocket;) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Creates a PrintWriter to send data to the server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Creates a buffered reader to read data from the server

                String request = in.readLine(); //reads the first line of the request from the server
                if (request == null) {
                    return;
                }
                log(clientAddress + "|" + request); //logs the request from the client
                String[] tokens = request.split(" "); //split the request into tokens

                //handles the show command
                if (tokens[0].equals("show")) {
                    if (items.isEmpty()) {
                        out.println("There are currently no items in this auction.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, Double> entry : items.entrySet()) { //loops through the items map
                            String item = entry.getKey();
                            Double bid = entry.getValue();
                            String bidder = bids.getOrDefault(item, "<no bids>");
                            sb.append(item).append(" : ").append(bid).append(" : ").append(bidder).append("\n");
                        }
                        out.println(sb.toString());
                    }
                    //handles the item command
                } else if (tokens[0].equals("item")) {
                    String item = tokens[1];
                    if (items.containsKey(item)) {
                        out.println("Failure.");
                    } else {
                        items.put(item, 0.0);
                        out.println("Success.");
                    }
                    //handles the bid command
                } else if (tokens[0].equals("bid")) {
                    String item = tokens[1];
                    Double value = Double.parseDouble(tokens[2]);
                    if (!items.containsKey(item) || value <= 0) {
                        out.println("Failure");
                    } else {
                        Double currentBid = items.get(item);
                        String currentBidder = bids.getOrDefault(item, "");
                        //places the bid if the value is greater than the current bid
                        if (value > currentBid) {
                            items.put(item, value);
                            bids.put(item, clientAddress);
                            out.println("Accepted.");
                            log(clientAddress + "|Accepted: " + item + " " + value);
                        } else {
                            out.println("Rejected.");
                            log(clientAddress + "|Rejected: " + item + " " + value);
                        }
                    }
                } else {
                    out.println("Invalid command");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        //logs the request from the client and prints out the formatted date and time
        private void log(String message) {
            Date now = new Date();
            String timestamp = String.format("%tF|%tT", now, now);
            try (FileWriter fw = new FileWriter(LOG_FILE_NAME, true)) {
                fw.write(timestamp + "|" + message + "\n");
            } catch (IOException e) {
                System.err.println("Error writing to log file: " + e.getMessage());
            }
        }
    }
}