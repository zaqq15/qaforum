package com.blueseals.qaforum;

import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.UserRepository;
import com.blueseals.qaforum.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShopServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructor() {
        ShopService service = new ShopService();
        assertNotNull(service);
    }

    @Test
    void purchaseItem_Success() {
        User user = new User();
        user.setReputation(60);
        String itemId = "badge_gold"; // Cost 50

        shopService.purchaseItem(user, itemId);

        assertEquals(50, user.getReputation()); // 100 - 50
        assertEquals("badge_gold", user.getActiveBadge());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void purchaseItem_Fail_InsufficientFunds() {
        User user = new User();
        user.setReputation(10);
        String itemId = "badge_gold";

        assertThrows(RuntimeException.class, () -> {
            shopService.purchaseItem(user, itemId);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void purchaseItem_Fail_InvalidItem() {
        User user = new User();
        user.setReputation(100);

        assertThrows(RuntimeException.class, () -> {
            shopService.purchaseItem(user, "non_existent_item");
        });
    }
}