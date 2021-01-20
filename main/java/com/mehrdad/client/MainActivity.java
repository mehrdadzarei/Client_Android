package com.mehrdad.client;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private Thread Thread1;
    private boolean running;
    private Socket client;
    private PrintWriter writing;
    private BufferedReader reading;
    private String server; // = "192.168.56.1";
    private int port; // = 5050;
    private String message;
    private String lenMessage;
    private int header = 64;
    private String disconnectMessage = "!DISCONNECT";
    private EditText serverIP, portNo, inputMessage, receivedMessage;
    private Button connect, sendMessage, disconnect;
//    private send sendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverIP = (EditText) findViewById(R.id.serverId);
        portNo = (EditText) findViewById(R.id.portId);
        connect = (Button) findViewById(R.id.connectId);
        inputMessage = (EditText) findViewById(R.id.messageId);
        sendMessage = (Button) findViewById(R.id.sendId);
        receivedMessage = (EditText) findViewById(R.id.receivedId);
        disconnect = (Button) findViewById(R.id.disconnectId);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receivedMessage.setText("");
                server = serverIP.getText().toString();
                String portE = portNo.getText().toString();
                if (!server.isEmpty() && !portE.isEmpty()){

                    port = Integer.parseInt(portE);
                    Thread1 = new Thread(new Thread1());
                    Thread1.start();
                }else {

                    Toast.makeText(getApplicationContext(), "Enter Server IP and Port No.",Toast.LENGTH_LONG).show();
                }
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                message = inputMessage.getText().toString();
//                sendCode = new send();
//                sendCode.execute();
                if(!message.isEmpty()){

                    new Thread(new Thread3()).start();
                }else {

                    Toast.makeText(getApplicationContext(), "Enter your Message to Server",Toast.LENGTH_LONG).show();
                }
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                writing(disconnectMessage);
                if (client.isConnected()){

                    disconnecting();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void lenMessage(String msg){

        int len = msg.length();
        lenMessage = Integer.toString(len);
        lenMessage += String.join("", Collections.nCopies((header - lenMessage.length()), " "));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void disconnecting(){

        try {

            running = false;
            connect.setEnabled(true);
            connect.setText("Connect");
            sendMessage.setEnabled(false);
            disconnect.setEnabled(false);
            reading.close();
            writing.close();
            client.close();
            Toast.makeText(getApplicationContext(),"Server has been Disconnected!",Toast.LENGTH_LONG).show();
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(),"Server has been Disconnected!",Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writing(String msg){

        if (client.isConnected()){

            lenMessage(msg);
            writing.write(lenMessage);
            writing.write(msg);
            writing.flush();
        }else {

            disconnecting();
        }
    }

    class Thread1 implements Runnable{

        @Override
        public void run() {

            try {

                client = new Socket(server, port);
                writing = new PrintWriter(client.getOutputStream());
                reading = new BufferedReader(new InputStreamReader(client.getInputStream()));
                running = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        connect.setEnabled(false);
                        connect.setText("Connected...!");
                        sendMessage.setEnabled(true);
                        disconnect.setEnabled(true);
                    }
                });
                new Thread(new Thread2()).start();
            }catch (IOException e){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(),"Connection Failed!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    class Thread2 implements Runnable{

        @Override
        public void run() {

            while (running){
                try {

                    String recMsg = reading.readLine();
                    if (recMsg != ""){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                receivedMessage.setText(recMsg);
                            }
                        });
                    }
                } catch (IOException e) {

                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Receiving data failed!",Toast.LENGTH_LONG).show();
                            running = false;
                            disconnecting();
                        }
                    });
                }
            }
        }
    }

    class Thread3 implements Runnable{

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {

            writing(message);
        }
    }

//    class send extends AsyncTask<Void, Void, Void>{
//
//        Socket client;
//        PrintWriter pw;
//        String server = "192.168.56.1";
//        int port = 5050;
//
//        @Override
//        protected Void doInBackground(Void...params) {
//
//            try {
//
//                client = new Socket(server, port);
//                pw = new PrintWriter(client.getOutputStream());
//                pw.write(lenMessage);
//                pw.write(message);
//                pw.flush();
//                pw.close();
//                client.close();
//            }catch (IOException e){
//
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
}

