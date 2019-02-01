import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class NoteServer {
    // global Lists for all the Notes and Pins that will be created
    static List<Note> notes = Collections.synchronizedList(new ArrayList<Note>());
    static List<Pin> pins = Collections.synchronizedList(new ArrayList<Pin>());
    static NoteServer a = new NoteServer();
    // global variables for the server/ Description of the board
    static int port;
    static int brdWidth;
    static int brdHeight;
    static int numColors;
    static String[] color;
    static boolean changingList = false;
    static int clientChanging;
    static final String password = "wLMVtYaYORroH2ZRJgFdewUdRMaMWoXM3Tfr5r5CyKmUykVQYs77JZG8GNpj";

    // the data structure Note to store the notes posted to the board
    public class Note {
        // Coordinates of the bottom left corner
        int xCoor;
        int yCoor;

        // size of the post
        int height;
        int width;

        // color of the post
        String color;

        // text contained on the post
        String post;

        // Status of the post and the number of pins in it.
        Boolean pinned;
        ArrayList<Pin> pin = new ArrayList<Pin>();

        // constructor for the note, assumes zero pins are in it.
        public Note(int xCoor, int yCoor, int height, int width, String color, String post) {
            this.xCoor = xCoor;
            this.yCoor = yCoor;
            this.color = color;
            this.height = height;
            this.width = width;
            this.post = post;
            this.pinned = false;
        }

        // getters
        public int getXCoor() {
            return xCoor;
        }

        public int getYCoor() {
            return yCoor;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public String getPost() {
            return post;
        }

        public Boolean getPin() {
            return pinned;
        }

        // changes status of pinned
        public void togglePin() {
            pinned = !pinned;
        }

        // outputs the format of the note in a readable format
        public String toString() {
            String toStr = this.color + " note located at (" + this.xCoor + "," + this.yCoor + ")" + " of size "
                    + this.width + " x " + this.height + " and states '" + this.post + "'";

            return toStr;
        }
    }

    // a structure to hold Pins
    public class Pin {
        // coordinate of pin
        int xCoor;
        int yCoor;

        // constructor given coordinates
        public Pin(int xCoor, int yCoor) {
            this.xCoor = xCoor;
            this.yCoor = yCoor;
        }

        // getters for pin
        public int getXCoor() {
            return xCoor;
        }

        public int getYCoor() {
            return yCoor;
        }
    }

    // Main that runs on start of server takes in arguments to know
    // size and colour of board, along with the port to be accessed
    public static void main(String[] args) throws Exception {
        // setting the arguments into variables to be accessed
        System.out.println("The noteboard server is operational");
        port = Integer.parseInt(args[0]);
        brdWidth = Integer.parseInt(args[1]);
        brdHeight = Integer.parseInt(args[2]);
        numColors = args.length - 3;
        color = new String[numColors];

        // parse and save all of the colours in an array
        for (int i = 0; i < numColors; i++) {
            color[i] = args[3 + i];
        }
        // stores the number of total connections
        int connectionsNum = 0;

        // creates new serversocket
        ServerSocket listener = new ServerSocket(port);
        try {
            while (true) {
                // continously loops and for each new connection it starts a different thread
                new boardConnection(listener.accept(), connectionsNum++).start();
            }
        } finally {
            // closes the server socket upon leaving loop
            listener.close();
        }
    }

    // a class in which a separate instance is created for each client
    // defines the individual operations of the board
    private static class boardConnection extends Thread {
        private Socket socket;
        private int connectionNum;

        // constructor for the board connection
        public boardConnection(Socket socket, int connectionNum) {
            this.socket = socket;
            this.connectionNum = connectionNum;
            print("Client #" + connectionNum + " is connected through " + socket);
        }

        // the actual operations performed by the thread
        public void run() {
            try {
                // boolean to acknowledge if a server wishes to disconnect
                boolean requestDisconnect = false;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // welcome message to client saying connection number and the description of the
                // board/available colours
                out.println("Enter Password: ");
                String pass = in.readLine();
                if (!pass.equals(password)) {
                    out.println("Access Denied");
                    System.out.println("Client Entered incorrect password");
                    return;
                }
                out.println("Server Connection Established, Connection Number: " + connectionNum);
                out.println("The Board is " + brdWidth + " units wide and " + brdHeight + " units tall.");
                out.println("The available colours for posts are:");
                for (String col : color) {
                    out.println(col);
                }

                // loops until disconnect requested or a blank message is sent
                while (true) {
                    if (requestDisconnect) {
                        break;
                    }

                    // reads request from client
                    String clientMessage = in.readLine();
                    while (changingList) {
                        try {
                            Thread.sleep(10);
                            //System.out.println(connectionNum +" waiting");
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    changingList = true;
                    clientChanging = connectionNum;

                    if (clientMessage == null) {
                        changingList = false;
                        break;
                    }
                    // splits clients message by spaces and performs a switch statement upon the
                    // first word in the request
                    String[] split = clientMessage.split("\\W+");
                    System.out.println(split[0]);
                    switch (split[0]) {

                    // POST case is the client wishes to add a note to the board
                    case "POST":
                        
                        // copies the message portion of the split array into a single array
                        if (split.length >= 6) {
                            String[] message = Arrays.copyOfRange(split, 6, split.length);
                            // creates a new note with the given coordinates and joins the message array
                            // into a single string

                            a.newNote(strtol(split[1]), strtol(split[2]), strtol(split[3]), strtol(split[4]), split[5],
                                    (String.join(" ", message)));
                            // notifies server that the message was successfully posted
                            out.println("Message Posted");
                        } else {
                            out.println("Improper command");
                        }
                        changingList = false;
                        break;

                    // get case
                    case "GET":
                        // checks if specific GET if not returns all notes
                        if (split.length > 1) {

                            // checks if request is for pins and is so returns coordinates of all pins
                            if (split[1].equals("PINS")) {
                                out.println("X,Y");
                                for (Pin c : pins) {
                                    out.println("(" + c.getXCoor() + "," + c.getYCoor() + ")");
                                }
                            }
                        }
                        // sets the 3 search options to null
                        String findColor = null;
                        int xc = -1;
                        int yc = -1;
                        String refersTo = null;
                        // check if the color is one of the request conditions
                        int strt = clientMessage.indexOf("color=");

                        // if color not found then check for next request condition
                        if (strt != -1) {
                              // if not the only condition then parse the color
                                findColor = split[2];

                        }
                        // check if coordinates are one of the request conditions
                        int strtCoor = clientMessage.indexOf("contains=");

                        if (strtCoor != -1) {
                            // parses the coordinates depending on if it is the only condition
                            if (strt != -1) {
                                xc = strtol(split[4]);
                                yc = strtol(split[5]);
                            } else {
                                xc = strtol(split[2]);
                                yc = strtol(split[3]);
                            }
                        }
                        // checks if refersTo is one of the conditions
                        int strtWord = clientMessage.indexOf("refersTo=");
                        if (strtWord != -1) {
                            // parses the string to search for
                            refersTo = clientMessage.substring(strtWord + 10);
                        }
                        // calls function to return notes that meeet the 4 requirements
                        Note[] result = a.getNotes(findColor, xc, yc, refersTo);
                        if (result.length != 0) {
                            for (Note d : result) {
                                // sends the results to the client
                                out.println(d.toString()+"\n");
                            }
                        } else {
                            // returns no matches
                            out.println("No matches found");
                        }
                        changingList = false;
                        break;

                    case "PIN":
                        // outputs the result, either Pin Added or Pin already exists
                        if (split.length == 3) {
                            out.println(a.changePin(strtol(split[1]), strtol(split[2]),
                                    true));
                        } else {
                            out.println("Improper Command");
                        }
                        changingList = false;
                        break;

                    case "UNPIN":
                        // outputs the result, either Pin Removed or Pin not found
                        if (split.length == 3) {
                            out.println(a.changePin((strtol(split[1])), strtol(split[2]),
                                    false));
                        } else {
                            out.println("Improper Command");
                        }
                        changingList = false;
                        break;

                    case "CLEAR":
                        // goes through the list of notes and removes all the unpinned from the list.
                        int i = 0;
                        int removed = 0;
                        while (i < notes.size()) {
                            Note a = notes.get(i);
                            if (a.pinned) {
                                i++;
                            } else {
                                notes.remove(i);
                                removed++;
                            }

                        }
                        // tells system how many notes were removed
                        out.println("There was " + removed + " notes removed");
                        changingList = false;
                        break;

                    case "DISCONNECT":
                        // sets the disconnect boolean and notifies the client that it is being closed
                        out.println("Connection Closed");
                        requestDisconnect = true;
                        changingList = false;
                        break;

                    default:
                        out.println("Improper Command");
                        changingList = false;
                    }

                }

            } catch (IOException e) {
                print("Error with connection: " + connectionNum + ": " + e);
                if (connectionNum == clientChanging) {
                    changingList = false;
                }
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    print("Socket couldn't be closed");
                }
                print("Connection: " + connectionNum + " closed");
            }
        }

        // creates a function for easy system output
        private void print(String output) {
            System.out.println(output);
        }

        // a function for parsing a string to an integer
        private int strtol(String input) {
            return Integer.parseInt(input);
        }
    }

    // function to create a new note and add it to the list of notes, in order based
    // on x and then y
    public void newNote(int xCoor, int yCoor, int height, int width, String color, String post) {
        int i = 0;
        if (!notes.isEmpty()) {
            while (i < notes.size() && notes.get(i).getXCoor() < xCoor) {
                i++;
            }
            while (i < notes.size() && notes.get(i).getXCoor() == xCoor && notes.get(i).getYCoor() < yCoor) {
                i++;
            }

        }
        notes.add(i, new Note(xCoor, yCoor, height, width, color, post));
        return;
    }

    // function to add or remove pins based on boolean putIn
    public String changePin(int xCoor, int yCoor, boolean putIn) {
        int i = 0;
        if (!pins.isEmpty()) {
            // while loops to find pin location for insertion or deletion
            while (i < pins.size() && pins.get(i).getXCoor() < xCoor) {
                i++;
            }
            while (i < pins.size() && pins.get(i).getXCoor() == xCoor && pins.get(i).getYCoor() < yCoor) {
                i++;
            }
            // if found matching coordinates remove if !putIn or return already exists in
            // putting in.
            if (i < pins.size() && pins.get(i).getXCoor() == xCoor && pins.get(i).getYCoor() == yCoor) {
                if (!putIn) {
                    pins.remove(i);
                    changeNoteStatus(xCoor, yCoor, putIn);
                    return "Pin Removed";
                } else {
                    return "Pin already Exists";
                }
            }
        }
        // if putIn is true then insert pin in spot found, if not then return pin not
        // found
        if (putIn) {
            pins.add(i, new Pin(xCoor, yCoor));
            changeNoteStatus(xCoor, yCoor, putIn);
            return "Pin Added";
        } else {
            return "Pin Not Found";
        }
    }

    // updates the status of everynote after a pin is added or removed
    public void changeNoteStatus(int xCoor, int yCoor, boolean putIn) {
        for (Note a : notes) {
            // checks if the pin position is on the note
            if (a.getXCoor() < xCoor && a.getXCoor() + a.getWidth() > xCoor && a.getYCoor() < yCoor
                    && a.getYCoor() + a.getHeight() > yCoor) {
                if (putIn) {
                    // adds pin
                    a.pin.add(new Pin(xCoor,yCoor));
                    if (a.getPin() == false) {
                        a.togglePin();
                    }
                } else if (!putIn){
                    // remove pin
                    int k;
                    for (k=0;k<a.pin.size();k++){
                        if (a.pin.get(k).getXCoor()==xCoor&&a.pin.get(k).getYCoor()==yCoor){
                            pins.remove(k);
                            break;
                        }
                    }
                if (a.getPin() && a.pin.size() == 0) {
                    a.togglePin();
                }
            }

            }
        }return;

    }

    // get notes based on the requirements
    public Note[] getNotes(String color, int xCoor, int yCoor, String reference) {
        ArrayList<Note> found = new ArrayList<Note>();
        for (Note n : notes) {

            // checks if the color matches if its not null
            boolean match = true;
            if (color != null && (!n.color.equals(color))) {
                match = false;

                // checks if the coordinates matches if its not null
            } else if (xCoor != -1 && (n.getXCoor() > xCoor || n.getXCoor() + n.getWidth() < xCoor
                    || n.getYCoor() > yCoor || n.getYCoor() + n.getHeight() < yCoor)) {
                match = false;
                // checks if the substring is in the note if its not null
            } else if (reference != null && !n.post.contains(reference)) {
                match = false;
            }
            if (match) {
                // adds note to list of found notes if all the conditions were matched
                found.add(n);
            }
        }
        // moves list into an array and returns it.
        Note[] d = (new Note[found.size()]);
        d = found.toArray(d);
        return d;
    }
}