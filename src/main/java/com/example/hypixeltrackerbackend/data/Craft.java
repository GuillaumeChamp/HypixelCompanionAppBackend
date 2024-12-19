package com.example.hypixeltrackerbackend.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Craft {
    private final List<String> materialIdList;
    private final List<Float> quantityOfMaterial;
    private Double craftingCost;


    public Craft(List<String> materials, List<Float> quantities) {
        this.materialIdList = materials;
        this.quantityOfMaterial = quantities;
    }

    @JsonProperty("materials")
    public List<String> getMaterialIdList() {
        return materialIdList;
    }

    @JsonProperty("quantities")
    public List<Float> getQuantityOfMaterial() {
        return quantityOfMaterial;
    }

    public Double getCraftingCost() {
        return this.craftingCost;
    }
    public void setCraftingCost(Double craftingCost) {
        this.craftingCost = craftingCost;
    }
}
