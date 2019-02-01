import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoteClient extends JFrame implements ActionListener {
    static JPanel connectPanel = new JPanel();
    static JPanel clientPanel = new JPanel();
    static JPanel dialogPanel = new JPanel();
    static JButton connectButton = new JButton("Connect");
    static JButton getButton = new JButton("GET");
    static JButton pinButton = new JButton("PIN");
    static JButton unpinButton = new JButton("UNPIN");
    static JButton postButton = new JButton("POST");
    static JLabel IPLabel = new JLabel("IP Address");
    static JLabel portLabel = new JLabel("Port Number");
    static JLabel postLabel = new JLabel("Text to be Posted");
    static JLabel xLabel = new JLabel("X Coordinate");
    static JLabel yLabel = new JLabel("Y  Coordinate");
    static JLabel colorLabel = new JLabel("Color");
    static JLabel widthLabel = new JLabel("Width");
    static JLabel heightLabel = new JLabel("Height");
    static JLabel searchLabel = new JLabel("Search for String");
    static JLabel resultsLabel = new JLabel("Results");
    static JLabel errorLabel = new JLabel("ERROR:");
    static JTextField IPField = new JTextField("");
    static JTextField portField = new JTextField("");
    static JTextField postField = new JTextField("");
    static JTextField xField = new JTextField("");
    static JTextField yField = new JTextField("");
    static JTextField widthField = new JTextField("");
    static JTextField heightField = new JTextField("");
    static JTextField searchField = new JTextField("");
    static JTextArea resultsArea = new JTextArea("");
    static JComboBox <String> colorComboBox = new JComboBox<>();
    static Socket connection = null;
    static BufferedReader in = null;
    static PrintWriter out = null;

    public NoteClient() {
        this.setLocation(200, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        connectPanelInit();
    }

    public void connectPanelInit() {
        add(connectPanel);
        connectPanel.setLayout(null);
        this.setSize(400, 150);
        this.setTitle("Connect");

        connectPanel.add(connectButton);
        connectPanel.add(IPLabel);
        connectPanel.add(portLabel);
        connectPanel.add(IPField);
        connectPanel.add(portField);
        connectPanel.add(errorLabel);
        connectPanel.setVisible(true);

        connectButton.setBounds(250, 35, 100, 25);
        connectButton.setVisible(true);

        IPField.setBounds(20, 35, 100, 25);
        IPField.setVisible(true);

        portField.setBounds(130, 35, 100, 25);
        portField.setVisible(true);

        IPLabel.setBounds(20, 10, 80, 20);
        IPLabel.setVisible(true);

        portLabel.setBounds(130, 10, 80, 20);
        portLabel.setVisible(true);

        errorLabel.setBounds(20, 60, 180, 20);
        errorLabel.setVisible(true);

        connectButton.addActionListener(this);
    }

    public void clientPanelInit() {
        add(clientPanel);
        clientPanel.setLayout(null);
        this.setSize(500, 500);
        this.setTitle("Client");

        clientPanel.setLayout(null);
        clientPanel.add(getButton);
        clientPanel.add(pinButton);
        clientPanel.add(unpinButton);
        clientPanel.add(postButton);
        clientPanel.add(postLabel);
        clientPanel.add(xLabel);
        clientPanel.add(yLabel);
        clientPanel.add(colorLabel);
        clientPanel.add(widthLabel);
        clientPanel.add(heightLabel);
        clientPanel.add(searchLabel);
        clientPanel.add(resultsLabel);
        clientPanel.add(postField);
        clientPanel.add(xField);
        clientPanel.add(yField);
        clientPanel.add(widthField);
        clientPanel.add(heightField);
        clientPanel.add(searchField);
        clientPanel.add( resultsArea);
        clientPanel.add(colorComboBox);
        clientPanel.setVisible(true);

        getButton.setBounds(300, 350, 170, 25);
        getButton.setVisible(true);

        pinButton.setBounds(300, 385, 80, 25);
        pinButton.setVisible(true);

        unpinButton.setBounds(390, 385, 80, 25);
        unpinButton.setVisible(true);

        postButton.setBounds(300, 420, 170, 25);
        postButton.setVisible(true);

        postLabel.setBounds(0, 0, 0, 0);

        resultsArea.setBounds(5,200,300,200);
        resultsArea.setVisible(true);
        resultsArea.setEditable(false);

        colorComboBox.setSize(50,50);
        colorComboBox.setLocation(50,50);
        colorComboBox.setVisible(true);

        getButton.addActionListener(this);
        pinButton.addActionListener(this);
        unpinButton.addActionListener(this);
        postButton.addActionListener(this);
    }

    public void dialogPanelInit() {

    }

    public static void main(final String[] args) throws Exception {
        NoteClient mainView = new NoteClient();
    }

    public void actionPerformed(ActionEvent ae) {
        try{
        String action = ae.getActionCommand();
        
        if (action.equals("Connect")) {
            try {
                connection = serverConnect(this.IPField.getText(), Integer.parseInt(this.portField.getText()));
                this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                this.out = new PrintWriter(connection.getOutputStream(), true);
                in.readLine();
                out.println("wLMVtYaYORroH2ZRJgFdewUdRMaMWoXM3Tfr5r5CyKmUykVQYs77JZG8GNpj");
                String input="";
                int col=0;
                do {
                    String read= in.readLine();
                    input= input+ read+"\n";
                    if (col>=3){
                        colorComboBox.addItem((String)read);
                    }
                    col++;
                } while (in.ready());
                resultsArea.setText(input);
            } catch (Exception e) {
                System.out.println("eror");
                errorLabel.setText("Error occurred");
            }
        } else if (action.equals("GET")) {
            out.println("GET");
            resultsArea.setText(in.readLine());
        } else if (action.equals("PIN")) {

        } else if (action.equals("UNPIN")) {

        } else if (action.equals("POST")) {

        } else if (action.equals("DISCONNECT")) {
            try{connection.close();
            }
            catch(Exception e){
                System.out.println("erorr");
            }
        }
    }catch(Exception e) {
        System.out.println("Unknown Error");
    }
}

    public Socket serverConnect(String serverAddress, int port) throws Exception {
        Socket socket = new Socket(serverAddress, port);

        // Streams for conversing with server

        // Consume and display welcome message from the server
        connectPanel.setVisible(false);

        clientPanelInit();
        // communicate();
        return socket;
    }
    /*
     * public void communicate(){ while (true) { try {
     * System.out.println("\nEnter a request to send to the server (empty to quit):"
     * ); String message = scanner.nextLine(); if (message == null ||
     * message.isEmpty()) { break; } out.println(message); do {
     * System.out.println(in.readLine()); } while (in.ready());
     * 
     * } catch (Exception e) { System.out.println("Server Closed"); break; } } }
     */
}