package net.thegamestudio.partier;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Zachary on 10/27/2014.
 */
public class ScreenSlideCardFragment extends Fragment {

    protected Card card;
    protected TextView titleView;
    protected TextView bodyView;
    protected TextView helpView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_card, container, false);
        titleView = (TextView) rootView.findViewById(R.id.titleView);
        bodyView = (TextView) rootView.findViewById(R.id.bodyView);
        helpView = (TextView) rootView.findViewById(R.id.helpView);

        refreshView(card);

        return rootView;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card c) {
        card = c;
        if (titleView != null) {
            refreshView(card);
        }
    }

    protected void refreshView(Card c)
    {
        // Set TextView data.
        titleView.setText(c.getTitle());
        bodyView.setText(c.getBody());
        helpView.setText(c.getHelp());

        // Update card color by type.
        int bgColor = 0xff757575;
        String type = c.getType().toLowerCase();

        if (type.equals("acting")) {
            bgColor = 0xff3f51b5;
        } else if (type.equals("daring")) {
            bgColor = 0xffe51c23;
        } else if (type.equals("moving")) {
            bgColor = 0xff259b24;
        } else if (type.equals("speaking")) {
            bgColor = 0xff673ab7;
        }

        titleView.setBackgroundColor(bgColor);
    }
}
