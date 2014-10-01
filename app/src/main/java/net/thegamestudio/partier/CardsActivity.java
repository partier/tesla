package net.thegamestudio.partier;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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

        enableNewCardButton(false);

        try {
            HTTPGetCardClient httpGetCardClient = new HTTPGetCardClient(_serverAddr, this);
            httpGetCardClient.execute();
        }
        catch (Exception e)
        {
            //TODO: Notify the user that requesting a card failed and why
            String exception = e.toString();
            enableNewCardButton(true);
        }
    }

    public void CreateNewCard(String jsonCardData)
    {
        enableNewCardButton(true);

        Card c = new Card(jsonCardData);
        showCard(c);
    }

    protected void enableNewCardButton(boolean enable)
    {
        Button newCardButton = (Button) findViewById(R.id.newCardButton);
        if (newCardButton == null)
        {
            //TODO: Handle this
            return;
        }

        newCardButton.setEnabled(enable);
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
}
