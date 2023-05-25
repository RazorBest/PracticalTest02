package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    ServerThread serverThread = null;
    Integer port = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        EditText field1 = findViewById(R.id.serverPort);

        findViewById(R.id.button1).setOnClickListener(view -> {
            port = Integer.parseInt(field1.getText().toString());

            serverThread = new ServerThread(port);
            serverThread.start();


        });

        findViewById(R.id.button2).setOnClickListener(view -> {
            String currency = ((EditText) findViewById(R.id.currency)).getText().toString();

            ClientThread thread = new ClientThread("127.0.0.1", port, currency, (TextView) findViewById(R.id.text1));
            thread.start();

            try {
                thread.join();
                //String result = thread.result;
                //TextView textView = (TextView) findViewById(R.id.text1);
                //textView.setText(result);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}