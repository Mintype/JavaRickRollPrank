package com.smallplayz;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static final int maxClientsCount = 50;
    private static int portNumber = 10334;

    static int ClientsConnected = 0;

    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String[] args){
        iport();
        try {
            serverSocket = new ServerSocket(portNumber);
            int i = 0;
            while (true) {

                clientSocket = serverSocket.accept();
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
                PrintStream os = new PrintStream(clientSocket.getOutputStream());
                BufferedReader inputLine = new BufferedReader(new InputStreamReader(System.in));
                os.println(inputLine.readLine().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void iport(){
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
class clientThread extends Thread {
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private final int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }
    public void run(){
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
        Server.ClientsConnected++;
        System.out.println(Server.ClientsConnected + " Clients have connected.");
        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    //threads[i].os.println( name + " has connected to the server!");

                }
            }
            while (true) {
                String line = is.readLine();
                System.out.println(line);
                if (line.startsWith("/exit")) {
                    System.out.println("Warning, a Client has left using cli");
                    break;
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null) {
                        threads[i].os.println(line);
                    }
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    //threads[i].os.println( name + " is disconnecting from the server.");
                    System.out.println("Warning, a Client has disconnected");
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}