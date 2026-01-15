package com.blueseals.qaforum;

import com.blueseals.qaforum.model.ShopItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShopItemTest {

    // --- Constructor Tests ---

    @Test
    void testNoArgsConstructor() {
        // Requirement: Test No-Args Constructor
        ShopItem item = new ShopItem();
        assertNotNull(item);
        assertNull(item.getId());
    }

    @Test
    void testAllArgsConstructor() {
        // Requirement: Test All-Args Constructor
        ShopItem item = new ShopItem("badge_gold", "Gold Badge", "Shiny", 50);
        assertNotNull(item);
        assertEquals("badge_gold", item.getId());
    }

    // --- Functionality Tests (At least 3) ---

    @Test
    void testSettersAndGetters_Id() {
        // Functionality 1: ID handling
        ShopItem item = new ShopItem();
        item.setId("test_id");
        assertEquals("test_id", item.getId());
    }

    @Test
    void testSettersAndGetters_Price() {
        // Functionality 2: Price handling
        ShopItem item = new ShopItem();
        item.setPrice(100);
        assertEquals(100, item.getPrice());
    }

    @Test
    void testSettersAndGetters_Description() {
        // Functionality 3: Description handling
        ShopItem item = new ShopItem();
        item.setDescription("Test Desc");
        assertEquals("Test Desc", item.getDescription());
    }
}
