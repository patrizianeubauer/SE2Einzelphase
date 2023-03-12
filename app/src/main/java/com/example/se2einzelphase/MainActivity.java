package com.example.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button btnAbschicken, btnSort;
    private TextView tvResponse;
    private Thread thread;
    private EditText etnInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAbschicken = findViewById(R.id.btnAbschicken);
        tvResponse = findViewById(R.id.tvServerResponse);
        etnInput = findViewById(R.id.etnInput);
        btnSort = findViewById(R.id.btnSort);

        btnAbschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Network network = new Network();
                thread = new Thread(network);
                thread.start();
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = etnInput.getText().toString();

                int number, count = 0;
                for (int i = 0; i < input.length(); i++) {
                    number = Integer.parseInt(input.charAt(i) + "");
                    if (!isPrim(number)) {
                        count++;
                    }
                }

                int[] array = new int[count];
                for (int i = 0, j = 0; i < input.length() && j < count; i++, j++) {
                    number = Integer.parseInt(input.charAt(i) + "");
                    if (!isPrim(number)) {
                        array[j] = number;
                        System.out.println(number);
                    } else {
                        j--;
                    }
                }
                System.out.println(array.length);

                tvResponse.setText(Arrays.toString(bubbleSort(array)).replaceAll("\\[|\\]|,|\\s", ""));
            }
        });
    }

    public static boolean isPrim(int value) {
        if (value <= 2) {
            return (value == 2);
        }
        for (int i = 2; i * i <= value; i++) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int[] bubbleSort(int[] array) {
        int help;
        for (int i = 1; i < array.length; i++) {
            for (int j = 0; j < array.length - i; j++) {
                if (array[j] > array[j + 1]) {
                    help = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = help;
                }
            }
        }
        return array;
    }

    class Network implements Runnable {
        public PrintWriter out;
        public BufferedReader in;

        public void run() {
            int port = 53212;
            String host = "se2-isys.aau.at";
            try {
                InetAddress serverAddress = InetAddress.getByName(host);
                Socket socket = new Socket(serverAddress, port);

                // connection -> setup the read (in) and write (out)
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                try {
                    // send message to server
                    out.println(etnInput.getText());

                    // read message from server
                    String response = in.readLine();
                    tvResponse.setText(response);
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                } finally {
                    in.close();
                    out.close();
                    socket.close();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
}