package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.ShopItem;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class ShopService {

    @Autowired
    private UserRepository userRepository;

    public List<ShopItem> getAvailableItems(){
        List<ShopItem> items = new ArrayList<>();
        items.add(new ShopItem("badge_gold", "Gold Badge", "Adds a shiny gold badge to your profile.", 10));
        items.add(new ShopItem("badge_wizard", "Wizard Badge", "Shows you are a code wizard.", 5));
        items.add(new ShopItem("color_blue", "Blue Name", "Makes your username stand out in blue", 15));

        return items;
    }

    @Transactional
    public void purchaseItem(User user, String itemId){
        ShopItem item = getAvailableItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (user.getReputation() < item.getPrice()) {
            throw new RuntimeException("Not enough reputation to purchase this item.");
        }
        user.setReputation(user.getReputation() - item.getPrice());

        user.setActiveBadge(item.getName());

        userRepository.save(user);
    }
}
