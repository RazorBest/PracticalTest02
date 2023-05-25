package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        EditText field1 = findViewById(R.id.serverPort);

        findViewById(R.id.button1).setOnClickListener(view -> {
            Integer port = Integer.parseInt(field1.getText().toString());

            serverThread = new ServerThread(port);
            serverThread.start();


        });

        findViewById(R.id.button2).setOnClickListener(view -> {
            /*if (city == null || city.isEmpty()) {
                Toast.makeText(getApplicationContext(), "City is empty", Toast.LENGTH_LONG).show();
                return;
            }

            ClientThread thread = new ClientThread("127.0.0.1", 5100, city, informationType, (TextView) findViewById(R.id.text1));
            thread.start();*/
            /*
            try {
                thread.join();
                String result = thread.result;
                TextView textView = (TextView) findViewById(R.id.text1);
                textView.setText(result);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            */
        });
    }
}