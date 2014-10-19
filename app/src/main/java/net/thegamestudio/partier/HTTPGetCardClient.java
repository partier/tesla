package net.thegamestudio.partier;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
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

        _authorization = 0;
        _accept = "1";
        _userAgent = "Tesla";
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

        try
        {
            // Create the client
            HttpClient httpClient = new DefaultHttpClient();

            // Build the get request string
            String getRequestString = _webAddress;
            getRequestString += AddKey("Authorization", "0");
            getRequestString += AddKey("Accept", _accept);
            getRequestString += AddKey("User-Agent", _userAgent);

            // Create the get request at the endpoint
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(getRequestString));
            HttpResponse response = httpClient.execute(httpGet);

            // Disable auto-redirect (for now)
            HttpClientParams.setRedirecting(new BasicHttpParams(), false);

            // Get the status code
            int responseCode = response.getStatusLine().getStatusCode();

            // Redirect
            if (responseCode == 301 || responseCode == 302)
            {
                String redirectURL = "";
            }

            // Create a reader to read the content from the post
            InputStream content = new BufferedInputStream(response.getEntity().getContent());
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

    protected String AddKey(String keyName, String value)
    {
        return "?" + keyName + "=" + value;
    }

    protected void Reset()
    {
        _isRunning = false;
    }

    protected String _webAddress;
    protected int _authorization;
    protected String _accept;
    protected String _userAgent;

    protected CardsActivity _cardsActivity;
    protected boolean _isRunning;
}
