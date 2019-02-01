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
    static JComboBox<String> colorComboBox = new JComboBox<>();

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

    public void clientPanelInit() throws Exception {
        add(clientPanel);
        clientPanel.setLayout(null);
        this.setSize(615, 600);
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
        clientPanel.add(resultsArea);
        clientPanel.add(colorComboBox);
        clientPanel.setVisible(true);

        getButton.setBounds(420, 440, 170, 30);
        getButton.setVisible(true);

        pinButton.setBounds(420, 480, 80, 30);
        pinButton.setVisible(true);

        unpinButton.setBounds(510, 480, 80, 30);
        unpinButton.setVisible(true);

        postButton.setBounds(420, 520, 170, 30);
        postButton.setVisible(true);

        postLabel.setBounds(0, 0, 0, 0);
        postLabel.setVisible(true);

        resultsArea.setBounds(10, 350, 390, 200);
        resultsArea.setVisible(true);
        resultsArea.setEditable(false);
        boolean free = true;
        resultsArea.setWrapStyleWord(free); // This is humorous because it is a play on words on freestyle rapping
        colorComboBox.setBounds(420, 400, 170, 30);
        colorComboBox.setVisible(true);
        colorComboBox.addItem("---color---");
        String input = "";
        int numLines = 0;
        do {
            String read = in.readLine();
            input = input + read + "\n";
            if (numLines >= 3) {
                colorComboBox.addItem((String) read);
            }
            numLines++;
        } while (in.ready());
        resultsArea.setText(input);
        getButton.addActionListener(this);
        pinButton.addActionListener(this);
        unpinButton.addActionListener(this);
        postButton.addActionListener(this);
    }

    public static void main(final String[] args) throws Exception {
        NoteClient mainView = new NoteClient();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        try {
            if (action.equals("Connect")) {
                System.out.println("Flag");
                Socket socket = new Socket(this.IPField.getText(), Integer.parseInt(this.portField.getText()));
                connectPanel.setVisible(false);
                System.out.println("Flag");
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Flag");
                in.readLine();
                out.println("wLMVtYaYORroH2ZRJgFdewUdRMaMWoXM3Tfr5r5CyKmUykVQYs77JZG8GNpj");
                clientPanelInit();
            } else if (action.equals("GET")) {
                String message = "GET ";
                if(!colorComboBox.getText().equals("---color---")){
                    message += "color="+colorComboBox.getText()+" ";
                }
                if(xField.getText().equals("")){
                    message+=(String)Integer.parseInt(xField.getText()) + " ";
                }
                resultsArea.setText(in.readLine());
                out.println(message);
            } else if (action.equals("PIN")) {
                out.printf("PIN %d %d", Integer.parseInt(xLabel.getText()), Integer.parseInt(yLabel.getText()));
            } else if (action.equals("UNPIN")) {
                
            } else if (action.equals("POST")) {

            } else if (action.equals("DISCONNECT")) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("eror");
            resultsArea.setText("Input error");
        } 
    }
}