package com.smallplayz;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.*;

public class Client implements Runnable {

    private static Socket clientSocket = null;
    public static PrintStream os = null;
    private static DataInputStream is = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    static JFrame frame;

    public static void main(String []args) throws IOException {

        frame = new JFrame("Not Connected.");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Frame now exits on close.
        frame.setSize(100, 100); // Frame height and width is now set to variables 'width' and 'height' declared and initialized on line 5 and 6.
        frame.setResizable(false); // Frame can no longer be manually resized by user.
        frame.setLayout(null); // Frame layout set to null.
        frame.setVisible(true); // Frame is now visible when code run.


        int portNumber = 10334; //port
        String host = "localhost";

        System.out.println("Now using host = " + host + ", portNumber = " + portNumber);

        Scanner input=new Scanner (System.in);
        String message="";
        String full="";
        Boolean exit=false;

        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (clientSocket != null && os != null && is != null) {
            try {
                new Thread(new Client()).start();
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }

    }
    public void run() {
        frame.setTitle("Connected.");
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                if(responseLine.equalsIgnoreCase("a")) {
                    Desktop.getDesktop().browse(new URL("https://www.youtube.com/watch?v=dQw4w9WgXcQ").toURI());
                    frame.setTitle("Playing.");
                    System.out.println("a has been sent");
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}