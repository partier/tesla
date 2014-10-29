package net.thegamestudio.partier;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.Vector;

/**
 * Created by Zachary on 10/27/2014.
 */
public class ScreenSlidePagerActivity extends FragmentActivity {
    private static final int NUM_CARDS = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Vector<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        cards = new Vector<Card>();
        cards.add(new Card());
        cards.add(new Card());
        cards.add(new Card());

        // TODO: Move code from CardsActivity to here to populate cards Vector.
        // TODO: Transition to the new card when it arrives.
        // TODO: Implement the maximum card limit, NUM_CARDS. Old cards should be dropped from cards.

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Required API 11.
        mPager.setPageTransformer(true, new DepthPageTransformer());
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
