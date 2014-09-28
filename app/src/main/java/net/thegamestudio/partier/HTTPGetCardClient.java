package net.thegamestudio.partier;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        try
        {
            // Create the http post client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(_webAddress);
            //TODO:Do we need to set header info here?
            /*httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Content-length", "0");*/

            // Execute the post request
            HttpResponse responseHandler = httpClient.execute(httpPost);

            // Get a handle to the content that was received
            HttpEntity entity = responseHandler.getEntity();
            InputStream content = entity.getContent();
            BufferedReader contentReader = new BufferedReader(new InputStreamReader(content));

            // Build a JSON object from the content received
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
