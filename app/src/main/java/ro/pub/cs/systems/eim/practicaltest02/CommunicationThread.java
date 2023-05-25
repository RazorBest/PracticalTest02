package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    private String currency;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    private void readPayload(BufferedReader bufferedReader) {
        currency = null;
        try {
            currency = bufferedReader.readLine();

            Log.d(Constants.TAG, "[COMMUNICATION THREAD] Received from client: " + currency);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (currency == null || currency.isEmpty()) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
        }
    }

    private CurrencyInformation getResultFromRequest() {
        if (!currency.equals("EUR") && !currency.equals("USD")) {
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] currency not supported: " + currency);
        }
        CurrencyInformation currencyInformation = null;
        HttpURLConnection httpURLConnection = null;
        String error = null;
        try {
            String webPageAddress = "https://api.coindesk.com/v1/bpi/currentprice/";
            webPageAddress += this.currency + ".json";

            HttpURLConnection connection = (HttpURLConnection) new URL(webPageAddress).openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());

            Log.d("test", "ServerThread: url " + webPageAddress);

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String response = "";
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }

            // Parse the page source code into a JSONObject and extract the needed information
            JSONObject content = new JSONObject(response);

            Log.d(Constants.TAG, "ServerThread: " + content.toString());

            String updated = content.getJSONObject("time").getString("updated");
            Double rate = content.getJSONObject("bpi").getJSONObject(currency).getDouble("rate_float");

            currencyInformation = new CurrencyInformation(Calendar.getInstance().getTime(), rate, this.currency);
        } catch(IOException e)  {
            Log.e("tag", "Error: " + e.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return currencyInformation;
    }

    private String getResult() {
        Date currentTime = Calendar.getInstance().getTime();

        HashMap<String, CurrencyInformation> data = serverThread.getData();

        if (data.containsKey(currency)) {
            CurrencyInformation information = data.get(currency);
            // If the information is older than 10 seconds, it is considered expired
            if (currentTime.getTime() - information.updated.getTime() < 10000) {
                Log.d(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                return information.toString();
            }
        }

        CurrencyInformation currencyInformation = getResultFromRequest();
        serverThread.setData(currency, currencyInformation);

        return currencyInformation.toString();
        /*
        // It checks whether the serverThread has already received the weather forecast information for the given city.
        HashMap<String, WeatherInformation> data = serverThread.getData();
        WeatherInformation weatherInformation;


        Log.d(Constants.TAG, "[COMMUNICATION THREAD] result: " + result);

        return result;
        */
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            readPayload(bufferedReader);

            // Get the result from the webservice or cache
            String result = getResult();

            // Send the result back to the client
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
