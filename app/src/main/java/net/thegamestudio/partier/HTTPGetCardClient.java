package net.thegamestudio.partier;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Br4ndong on 9/28/2014.
 */
public class HTTPGetCardClient extends AsyncTask<Void, Void, String> {
    HTTPGetCardClient(String webAddress, CardsActivity cardsActivity)
    {
        _webAddress = webAddress;
        _cardsActivity = cardsActivity;
        _isRunning = false;
    }

    public boolean IsRunning()
    {
        return _isRunning;
    }

    // Invoked in its own thread so we can run background web services
    @Override
    protected String doInBackground(Void... params) {
        _isRunning = true;
        String jsonString = null;
        URL url = null;
        HttpURLConnection connection = null;

        try
        {
            // Open a connection to the web address
            url = new URL(_webAddress);
            connection = (HttpURLConnection) url.openConnection();

            // Don't follow redirects, you dingus!
            connection.setInstanceFollowRedirects(false);

            connection.connect();

            // Check the response code of the connection opened.
            int responseCode = connection.getResponseCode();

            // Determine if there was a redirect.
            if (responseCode == 301 || responseCode == 302)
            {
                // Yep. We got redirected. Refresh the connection.
                String location = connection.getHeaderField("location");
                connection = (HttpURLConnection) new URL(location).openConnection();
            }

            // Create a reader to read the content from the post
            InputStream content = new BufferedInputStream(connection.getInputStream());
            BufferedReader contentReader = new BufferedReader(new InputStreamReader(content));

            // Build a JSON string from the content received
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = contentReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            // Set the JSON object data
            jsonString = stringBuilder.toString();
        }
        catch (IOException e)
        {
            Reset();
            jsonString = e.toString();
        }
        catch (Exception e)
        {
            Reset();
            jsonString = e.toString();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }

        Reset();

        // Return the JSON object to onPostExecute
        return jsonString;
    }

    // Invoked on the UI thread so we can pass the data
    @Override
    protected void onPostExecute(String jsonCardData)
    {
        _cardsActivity.CreateNewCard(jsonCardData);
    }

    protected void Reset()
    {
        _isRunning = false;
    }

    protected String _webAddress;
    protected CardsActivity _cardsActivity;
    protected boolean _isRunning;
}
