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
    protected String id;
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

    public String getId() { return id; }

    /** Empty constructor. Creates a default card. */
    Card() {
        title = "Welcome To Partier!";
        body = "Partier provides fun prompts to inject controlled chaos into any social gathering. By following the directions on the cards whenever there's a lull in the action, you'll not only breathe life into the party, but become the life of the party!";
        help = "Whenever you're ready for your first card, tap the \"Next Card\" button. Our boy Rutherford will fetch one hot off the presses.";
        type = "default";
        id = "00000000-0000-0000-0000-000000000000";
    }

    /** Create a card from a JSONObject. */
    Card(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            // Only accept valid input.
            title = jsonObject.getString("title").equals("null") ? "Untitled Card" : jsonObject.getString("title");
            body = jsonObject.getString("body").equals("null") ? "" :jsonObject.getString("body");
            help = jsonObject.getString("help").equals("null") ? "" : jsonObject.getString("help");
            type = jsonObject.getString("type").equals("null") ? "default" : jsonObject.getString("type").toLowerCase();
            id = jsonObject.getString("id").equals("null") ? "" : jsonObject.getString("id");

        } catch (JSONException e) {
            // TODO: Handle this on the client, don't just spit out text to a nonexistent console.
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object card) {
        return ((Card)card).getId().equals(id);
    }

    @Override
    public String toString() {
        return "\nTITLE: " + title + "\nBODY:  " + body + "\nHELP:  " + help + "\nTYPE:  " + type + "\nID:    <" + id + ">";
    }
}
