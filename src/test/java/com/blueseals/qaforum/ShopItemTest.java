package com.blueseals.qaforum;

import com.blueseals.qaforum.model.ShopItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShopItemTest {


    @Test
    void testNoArgsConstructor() {
        ShopItem item = new ShopItem();
        assertNotNull(item);
        assertNull(item.getId());
    }

    @Test
    void testAllArgsConstructor() {
        ShopItem item = new ShopItem("badge_gold", "Gold Badge", "Shiny", 50);
        assertNotNull(item);
        assertEquals("badge_gold", item.getId());
    }


    @Test
    void testSettersAndGetters_Id() {
        ShopItem item = new ShopItem();
        item.setId("test_id");
        assertEquals("test_id", item.getId());
    }

    @Test
    void testSettersAndGetters_Price() {

        ShopItem item = new ShopItem();
        item.setPrice(100);
        assertEquals(100, item.getPrice());
    }

    @Test
    void testSettersAndGetters_Description() {
        ShopItem item = new ShopItem();
        item.setDescription("Test Desc");
        assertEquals("Test Desc", item.getDescription());
    }
}
