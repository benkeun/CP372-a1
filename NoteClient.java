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
    static JButton displayButton = new JButton("DISPLAY");
    static JLabel IPLabel = new JLabel("IP Address");
    static JLabel portLabel = new JLabel("Port Number");
    static JLabel postLabel = new JLabel("Text to be Posted");
    static JLabel xLabel = new JLabel("X Coordinate");
    static JLabel yLabel = new JLabel("Y  Coordinate");
    static JLabel widthLabel = new JLabel("Width");
    static JLabel heightLabel = new JLabel("Height");
    static JLabel searchLabel = new JLabel("Search for String");
    static JLabel errorLabel = new JLabel("ERROR:");
    static JTextField IPField = new JTextField("");
    static JTextField portField = new JTextField("");
    static JTextField xField = new JTextField("");
    static JTextField yField = new JTextField("");
    static JTextField widthField = new JTextField("");
    static JTextField heightField = new JTextField("");
    static JTextArea postArea = new JTextArea("");
    static JTextArea searchArea = new JTextArea("");
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
        this.setSize(575, 400);
        this.setTitle("Client");

        clientPanel.setLayout(null);
        clientPanel.add(getButton);
        clientPanel.add(pinButton);
        clientPanel.add(unpinButton);
        clientPanel.add(postButton);
        clientPanel.add(displayButton);
        clientPanel.add(postLabel);
        clientPanel.add(xLabel);
        clientPanel.add(yLabel);
        clientPanel.add(widthLabel);
        clientPanel.add(heightLabel);
        clientPanel.add(searchLabel);
        clientPanel.add(xField);
        clientPanel.add(yField);
        clientPanel.add(widthField);
        clientPanel.add(heightField);
        clientPanel.add(postArea);
        clientPanel.add(searchArea);
        clientPanel.add(resultsArea);
        clientPanel.add(colorComboBox);
        clientPanel.setVisible(true);

        postLabel.setBounds(10, 10, 350, 30);
        postLabel.setVisible(true);

        postArea.setBounds(10, 40, 350, 80);
        postArea.setVisible(true);

        searchLabel.setBounds(380, 10, 170, 30);
        searchLabel.setVisible(true);

        searchArea.setBounds(380, 40, 170, 80);
        searchArea.setVisible(true);

        xLabel.setBounds(10, 130, 80, 30);
        xLabel.setVisible(true);

        yLabel.setBounds(100, 130, 80, 30);
        yLabel.setVisible(true);

        widthLabel.setBounds(190, 130, 80, 30);
        widthLabel.setVisible(true);

        heightLabel.setBounds(280, 130, 80, 30);
        heightLabel.setVisible(true);

        xField.setBounds(10, 160, 80, 30);
        xField.setVisible(true);

        yField.setBounds(100, 160, 80, 30);
        yField.setVisible(true);

        widthField.setBounds(190, 160, 80, 30);
        widthField.setVisible(true);

        heightField.setBounds(280, 160, 80, 30);
        heightField.setVisible(true);

        resultsArea.setBounds(10, 200, 350, 150);
        resultsArea.setVisible(true);
        resultsArea.setEditable(false);
        boolean free = true;
        resultsArea.setWrapStyleWord(free); // This is humorous because it is a play on words on freestyle rapping

        colorComboBox.setBounds(380, 160, 170, 30);
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

        getButton.setBounds(380, 200, 170, 30);
        getButton.setVisible(true);

        pinButton.setBounds(380, 240, 80, 30);
        pinButton.setVisible(true);

        unpinButton.setBounds(470, 240, 80, 30);
        unpinButton.setVisible(true);

        postButton.setBounds(380, 280, 170, 30);
        postButton.setVisible(true);

        displayButton.setBounds(380, 320, 170, 30);
        displayButton.setVisible(true);

        getButton.addActionListener(this);
        pinButton.addActionListener(this);
        unpinButton.addActionListener(this);
        postButton.addActionListener(this);
        displayButton.addActionListener(this);
    }

    public static void main(final String[] args) throws Exception {
        NoteClient mainView = new NoteClient();
        connection.close();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        try {
            if (action.equals("Connect")) {
                Socket socket = new Socket(this.IPField.getText(), Integer.parseInt(this.portField.getText()));
                connectPanel.setVisible(false);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
                in.readLine();
                out.println("wLMVtYaYORroH2ZRJgFdewUdRMaMWoXM3Tfr5r5CyKmUykVQYs77JZG8GNpj");
                clientPanelInit();
            } else if (action.equals("GET")) {
                String message = "GET ";
                if (!colorComboBox.getSelectedItem().equals("---color---")) {
                    message += "color= " + colorComboBox.getSelectedItem() + " ";
                }
                if (!xField.getText().equals("") && !yField.getText().equals("")) {
                    message += "contains= " + Integer.parseInt(xField.getText()) + " "
                            + Integer.parseInt(yField.getText()) + " ";
                }
                if (!searchArea.getText().equals("")) {
                    message += "refersTo= " + searchArea.getText() + " ";
                }
                out.println(message);
                resultsArea.setText(in.readLine());
            } else if (action.equals("PIN")) {
                out.printf("PIN %d %d", Integer.parseInt(xLabel.getText()), Integer.parseInt(yLabel.getText()));
            } else if (action.equals("UNPIN")) {
                out.printf("UNPIN %d %d", Integer.parseInt(xLabel.getText()), Integer.parseInt(yLabel.getText()));
            } else if (action.equals("POST")) {
                String message = null;
                if (!xField.getText().equals("") && !yField.getText().equals("") && !widthField.getText().equals("")
                        && !heightField.getText().equals("")) {
                    message = "POST " + Integer.parseInt(xField.getText()) + " " + Integer.parseInt(yField.getText())
                            + " " + Integer.parseInt(widthField.getText()) + " "
                            + Integer.parseInt(heightField.getText()) + " ";
                    if (((String) colorComboBox.getSelectedItem()).equals("---color---")) {
                        message += (String) colorComboBox.getItemAt(1);
                    } else {
                        message += (String) colorComboBox.getSelectedItem();
                    }
                    message += " " + postArea.getText();
                } else {
                    resultsArea.setText("Input error");
                }
                out.println(message);
                resultsArea.setText(in.readLine());
            } else if (action.equals("DISPLAY")) {
                out.println("DISPLAY");
            }
        } catch (Exception e) {
            System.out.println("eror");
            resultsArea.setText("Input error");
        }
    }
}