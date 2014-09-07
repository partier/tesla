package net.thegamestudio.partier;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zachary on 9/7/2014.
 */
public class Card {

    protected String title;
    protected String body;
    protected String help;
    protected String type;
    protected static int debugIncrement = 1;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getHelp() {
        return help;
    }

    public String getType() {
        return type;
    }

    /** Empty constructor. Creates a default card. */
    Card() {
        title = "Default Title " + debugIncrement;
        body = "Default body copy " + debugIncrement + ".";
        help = "Default help copy " + debugIncrement + ".";
        type = "default";
        debugIncrement++;
    }

    /** Create a card from a JSONObject. */
    Card(JSONObject json) {
        try {
            title = (String) json.get("title");
            body = (String) json.get("body");
            help = (String) json.get("help");
            type = (String) json.get("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
