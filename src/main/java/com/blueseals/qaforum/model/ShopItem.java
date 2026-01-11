package com.blueseals.qaforum.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ShopItem {
    private String id;
    private String name;
    private String description;
    private int price;
}
