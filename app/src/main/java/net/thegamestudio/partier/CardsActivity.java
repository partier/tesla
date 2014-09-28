package net.thegamestudio.partier;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DebugUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.logging.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.net.http.AndroidHttpClient;

import java.io.Console;
import java.io.IOException;


public class CardsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Fetch a new card from the server. */
    public void fetchNewCard(View view) {
        // TODO: Perform an HTTP Request to our endpoint (should be "server.ext/card") and pass the resulting JSONObject into the Card constructor.
        if (_httpGetCardClient == null)
            _httpGetCardClient = new HTTPGetCardClient(_serverAddr, this);

        if (!_httpGetCardClient.IsRunning())
            _httpGetCardClient.execute();
    }

    public void CreateNewCard(String jsonCardData)
    {
        Card c = new Card(jsonCardData);
        showCard(c);
    }

    protected void showCard(Card card) {
        // The title of the card.
        TextView titleView = (TextView) findViewById(R.id.titleView);

        // The body of the card.
        TextView bodyView = (TextView) findViewById(R.id.bodyView);

        // The help of the card.
        TextView helpView = (TextView) findViewById(R.id.helpView);

        titleView.setText(card.getTitle());
        bodyView.setText(card.getBody());
        helpView.setText(card.getHelp());
    }

    private String _serverAddr = "https://partier-emily-dev.herokuapp.com/card";
    private HTTPGetCardClient _httpGetCardClient = null;
}
