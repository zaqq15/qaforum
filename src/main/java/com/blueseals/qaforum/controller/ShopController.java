package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.service.ShopService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserService userService;

    @GetMapping("/shop")
    public String viewShop(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("items", shopService.getAvailableItems());
        return "shop";
    }

    @PostMapping("/shop/buy")
    public String buyItem(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam String itemId,
                          RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        try {
            shopService.purchaseItem(user, itemId);
            redirectAttributes.addFlashAttribute("success", "Item purchased successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Item not available!");

        }
        return "redirect:/shop";
    }
}
