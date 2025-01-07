package com.ndc.deliverymanagement.controller;

import com.ndc.deliverymanagement.model.Shipper;
import com.ndc.deliverymanagement.model.User;
import com.ndc.deliverymanagement.service.ShipperService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shipper")
public class ShipperController {
    @Autowired
    private ShipperService shipperService;

    // Đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "shipper/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String phoneNumber,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        if (shipperService.checkLogin(phoneNumber, password)) {
            Shipper loggedInShipper = shipperService.findByPhoneNumber(phoneNumber);
            session.setAttribute("loggedInShipper", loggedInShipper);
            return "redirect:/shipper/homeShipper"; // Chuyển hướng về trang chủ người dùng
        } else {
            model.addAttribute("error", "Số điện thoại hoặc mật khẩu không chính xác.");
            return "shipper/login";
        }
    }
    @GetMapping("/homeShipper")
    public String shipperHome(HttpSession session, Model model) {
        Shipper loggedInShipper = (Shipper) session.getAttribute("loggedInShipper");
        if (loggedInShipper == null) {
            return "redirect:/shipper/login";
        }
        model.addAttribute("shipper", loggedInShipper);
        return "shipper/homeShipper";
    }
    // Đăng xuất
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/shipper/login";
    }

}

