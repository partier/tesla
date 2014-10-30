package net.thegamestudio.partier;


import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.util.UUID;
import java.util.Vector;

/**
 * Created by Zachary on 10/27/2014.
 */
public class ScreenSlidePagerActivity extends FragmentActivity {
    private static final int NUM_CARDS = 5;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private Vector<Card> cards;

    private static UUID _uuid;
    private String _serverAddr = "https://partier-halloween.herokuapp.com/card";
    private static final String UUID_KEY = "uuid";
    private String _pubnubSubscribeKey = "sub-c-1cb1defc-5fca-11e4-bbb1-02ee2ddab7fe";
    private String _pubnubPublishKey = "pub-c-eaf5336a-e3d9-46dc-9846-937899691650";
    private Pubnub _pubnub = null;
    private String _pubnubChannel = "halloween";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        // Cache the UUID for this device
        if (!prefs.contains(UUID_KEY))
        {
            UUID id = UUID.randomUUID();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(UUID_KEY, id.toString());
            editor.commit();
        }
        _uuid = UUID.fromString(prefs.getString(UUID_KEY, ""));

        // Create a set of placeholder cards
        cards = new Vector<Card>();
        cards.add(new Card());
        cards.add(new Card());
        cards.add(new Card());
        cards.add(new Card());

        // TODO: Transition to the new card when it arrives.
        // TODO: Implement the maximum card limit, NUM_CARDS. Old cards should be dropped from cards.

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Required API 11.
        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screen_slide_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()) {
            case R.id.action_refresh:
                refreshCardState();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshCardState() {
        // Go to the last card so we can repopulate the first card
        mPager.setCurrentItem(cards.size()-1, true);

        System.out.println("Refreshing card state.");
        // TODO: Actually refresh card state.

        // Test the pubnub endpoint for push availability
        /*_pubnub = new Pubnub(_pubnubPublishKey, _pubnubSubscribeKey);
        Callback networkTimeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                System.out.println(message.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        _pubnub.time(networkTimeCallback);

        // Subscribe to the pubnub channel
        try
        {
            _pubnub.subscribe(_pubnubChannel, new Callback() {

                        @Override
                        public void connectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : CONNECT on channel " + channel +
                                                 " : " + message.getClass() + " : " +
                                                 message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel " + channel +
                                    " : " + message.getClass() + " : " +
                                    message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel " + channel +
                                    " : " + message.getClass() + " : " +
                                    message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel +
                                    " : " + message.getClass() + " : " +
                                    message.toString());
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel +
                                    " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {

        }


        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        _pubnub.history(_pubnubChannel, 100, callback);*/

        // Fetch a new card
        try {
            HTTPGetCardClient httpGetCardClient = new HTTPGetCardClient(_serverAddr, this, _uuid);
            httpGetCardClient.execute();
        }
        catch (Exception e)
        {
            //TODO: Notify the user that requesting a card failed and why
            System.out.println(e.toString());
        }
    }

    public void CreateNewCard(String jsonCardData)
    {
        Card c = new Card(jsonCardData);

        /*cards.add(c);

        mPagerAdapter.setFragmentData(c, cards.size() - 1);

        cards.remove(cards.firstElement());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Fragment newCardFragment = new ScreenSlideCardFragment();

            Bundle bundle = new Bundle();
            bundle.putString("title", c.getTitle());
            bundle.putString("body", c.getBody());
            bundle.putString("help", c.getHelp());
            bundle.putString("type", c.getType());

            newCardFragment.setArguments(bundle);

        transaction.replace(mPagerAdapter.getFragmentAtPosition(0).getId(), newCardFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getFragmentManager().executePendingTransactions();*/

        // Remove the last card
        cards.remove(cards.lastElement());

        // Add the new card at the head of the list
        cards.insertElementAt(c, 0);

        // Auto transition to the new card
        mPager.setCurrentItem(0, true);
        System.out.println("Got dat card");
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Card c = cards.get(position);

            // Create a bundle with card information.
            Bundle bundle = new Bundle();
            bundle.putString("title", c.getTitle());
            bundle.putString("body", c.getBody());
            bundle.putString("help", c.getHelp());
            bundle.putString("type", c.getType());

            // Create the new fragment.
            ScreenSlideCardFragment s = new ScreenSlideCardFragment();

            // Provide the card data to the fragment.
            s.setArguments(bundle);

            return s;
        }

        @Override
        public int getCount() {
            return cards.size();
        }
    }

    /** Animation class to provide "stack of cards" feel. */
    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
