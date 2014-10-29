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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_card, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        TextView bodyView = (TextView) rootView.findViewById(R.id.bodyView);
        TextView helpView = (TextView) rootView.findViewById(R.id.helpView);

        titleView.setText(getArguments().getString("title"));
        bodyView.setText(getArguments().getString("body"));
        helpView.setText(getArguments().getString("help"));

        int bgColor = 0xffaaaaaa;
        String type = getArguments().getString("type").toLowerCase();

        if (type == "acting") {
            bgColor = 0xff3d81ff;
        } else if (type == "daring") {
            bgColor = 0xffff3d3d;
        } else if (type == "moving") {
            bgColor = 0xff81ff3d;
        } else if (type == "speaking") {
            bgColor = 0xff8b3dff;
        }

        titleView.setBackgroundColor(bgColor);

        return rootView;
    }
}
