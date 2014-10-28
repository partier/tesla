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
    Card(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            // Only accept valid input.
            title = jsonObject.getString("title").equals("null") ? "Untitled Card" : jsonObject.getString("title");
            body = jsonObject.getString("body").equals("null") ? "" :jsonObject.getString("body");
            help = jsonObject.getString("help").equals("null") ? "" : jsonObject.getString("help");
            type = jsonObject.getString("type").equals("null") ? "default" : jsonObject.getString("type");

        } catch (JSONException e) {
            // TODO: Handle this on the client, don't just spit out text to a nonexistant console.
            e.printStackTrace();
        }
    }
}
