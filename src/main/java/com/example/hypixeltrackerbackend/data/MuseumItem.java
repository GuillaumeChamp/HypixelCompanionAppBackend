package com.example.hypixeltrackerbackend.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class MuseumItem {
    private final String id;
    private final String name;
    private List<String> pieces;
    private String parent;
    private final int donationXp;
    private final String gameStage;
    private final String type;

    public MuseumItem(String id, JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.id = id;
        if(jsonObject.has("parent")) {
            String parentPayload = jsonObject.getJSONObject("parent").toString();
            if (!"{}".equals(parentPayload)) {
                // format : {"%id%#":"%parent_id%"} and we only want the parent id
                this.parent = parentPayload.substring(id.length()+5,parentPayload.length()-2);
            }
        }else {
            parent=null;
        }
        donationXp = jsonObject.getInt("donation_xp");
        gameStage = jsonObject.getString("game_stage");
        type = jsonObject.getString("type");
        if (jsonObject.has("pieces")) {
            pieces = jsonObject.getJSONArray("pieces").toList().stream().map(o -> (String) o).toList();
        }
    }

    @Override
    public String toString() {
        return "MuseumItem{" +
                "id='" + id + '\'' +
                ", name=" + name +
                ", pieces=" + pieces +
                ", parent=" + parent  +
                ", donationXp=" + donationXp +
                ", gameStage='" + gameStage + '\'' +
                ", type='" + type + '\'' +
                "}\n";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getPieces() {
        return pieces;
    }

    public String getParent() {
        return parent;
    }

    public int getDonationXp() {
        return donationXp;
    }

    public String getGameStage() {
        return gameStage;
    }

    public String getType() {
        return type;
    }
}
