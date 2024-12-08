package com.example.hypixeltrackerbackend.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompleteItem {
    @JsonProperty(index=0)
    private String name;
    @JsonProperty(index=1)
    private final String category;
    @JsonProperty(index=2)
    private String tier;
    @JsonProperty(index=3)
    private final String tag;
    @JsonProperty(index=4)
    private final String imagePath;
    @JsonProperty(index=5)
    private ItemPricing pricing;
    @JsonProperty(index=6)
    private List<Craft> crafts;
    @JsonProperty(index = 7)
    private Float npcBuyPrice;

    public CompleteItem(String category, String tag, String imagePath) {
        this.category = category;
        this.tag = tag;
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPricing(ItemPricing pricing) {
        this.pricing = pricing;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getTier() {
        return tier;
    }

    public String getTag() {
        return tag;
    }

    @JsonProperty("image")
    public String getImagePath() {
        return imagePath;
    }

    @JsonProperty("pricing")
    public ItemPricing getPricing() {
        return pricing;
    }

    public void setCrafts(List<Craft> crafts) {
        this.crafts = crafts;
    }

    public List<Craft> getCrafts() {
        return crafts;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Float getNpcBuyPrice() {
        return npcBuyPrice;
    }

    public void setNpcBuyPrice(float npcBuyPrice) {
        if (Float.isNaN(npcBuyPrice)) {
            return;
        }
        this.npcBuyPrice = npcBuyPrice;
    }

    @Override
    public String toString() {
        return "CompleteItem{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", tier='" + tier + '\'' +
                ", tag='" + tag + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", pricing=" + pricing +
                ", crafts=" + crafts +
                ", npcBuyPrice=" + npcBuyPrice +
                '}';
    }
}
