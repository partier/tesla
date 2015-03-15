package net.thegamestudio.partier;


import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.util.UUID;
import java.util.Vector;

import javax.xml.transform.Result;

/**
 * Created by Zachary on 10/27/2014.
 */
public class ScreenSlidePagerActivity extends FragmentActivity {
    private static final int NUM_CARDS = 5;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private Vector<Card> cards;
    private Button newCardButton;

    private static UUID _uuid;
    private String _serverAddr = "https://partier-imb.herokuapp.com/card";
    private static final String UUID_KEY = "uuid";
    private String _pubnubSubscribeKey = "sub-c-1cb1defc-5fca-11e4-bbb1-02ee2ddab7fe";
    private String _pubnubPublishKey = "pub-c-eaf5336a-e3d9-46dc-9846-937899691650";
    private Pubnub _pubnub = null;
    private String _pubnubChannel = "a";

    private String _notifyTitle = "Partier";
    private String _notifyText = "New Card Available!";
    private String _notifyTicker = "Partier Update";

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

        newCardButton = (Button) findViewById(R.id.newCardButton);

        // Create a set of placeholder cards
        cards = new Vector<Card>();
        cards.add(new Card());

        // TODO: Transition to the new card when it arrives.
        // TODO: Implement the maximum card limit, NUM_CARDS. Old cards should be dropped from cards.

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Required API 11.
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        // Test the pubnub endpoint for push availability
        _pubnub = new Pubnub(_pubnubPublishKey, _pubnubSubscribeKey);

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

                            ProduceNotification();
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel +
                                    " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println("EXCEPTION : " + e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.screen_slide_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshCardState(View view) {
        // Refresh card state.
        newCardButton.setEnabled(false);
        try {
            HTTPGetCardClient httpGetCardClient = new HTTPGetCardClient(_serverAddr, this, _uuid);
            httpGetCardClient.execute();
        }
        catch (Exception e)
        {
            // TODO: Notify the user that requesting a card failed and why
            System.out.println(e.toString());
        }

        /*Callback networkTimeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                System.out.println(message.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        _pubnub.time(networkTimeCallback);*/

        /*Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        _pubnub.history(_pubnubChannel, 100, callback);*/
    }

    private void ProduceNotification()
    {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        notification.setContentTitle(_notifyTitle);

        notification.setContentText(_notifyText);

        notification.setTicker(_notifyTicker);

        notification.setSmallIcon(R.drawable.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Result.class);

        Intent resultIntent = new Intent(this, Result.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }

    public void CreateNewCard(String jsonCardData)
    {
        Card c = new Card(jsonCardData);

        // Add the new card at the head of the list if the card doesn't already exist.
        if (!cards.lastElement().equals(c)) {
            cards.add(c);

            // Remove the last card if we have too many.
            if (cards.size() > NUM_CARDS) {
                cards.remove(0);
            }

            // Alert the page adapter that data has changed.
            mPagerAdapter.notifyDataSetChanged();
            mPager.invalidate();
        }

        // Transition to the new card.
        mPager.setCurrentItem(cards.size() - 1, true);

        // Enable the refresh button again.
        newCardButton.setEnabled(true);
    }

    /** Adapter to handle card Vector data. */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Card c = cards.get(position);

            // Create the new fragment.
            ScreenSlideCardFragment s = new ScreenSlideCardFragment();

            // Provide the card data to the fragment.
            s.setCard(c);

            return s;
        }

        @Override
        public int getCount() {
            return cards.size();
        }
    }

    /** Animation to provide a "table of cards" feel. */
    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
