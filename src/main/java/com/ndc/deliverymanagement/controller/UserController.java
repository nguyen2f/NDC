package com.ndc.deliverymanagement.controller;

import com.ndc.deliverymanagement.model.User;
import com.ndc.deliverymanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Đăng ký người dùng
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/user/login"; // Đăng ký xong chuyển hướng về trang login
    }
    @Controller
    public class HomeController {
        @GetMapping("/user-home")
        public String userHome() {
            return "user/homeUser"; // Đường dẫn tới file homeUser.html
        }
    }

    // Đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String phoneNumber,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        if (userService.checkLogin(phoneNumber, password)) {
            User loggedInUser = userService.findByPhoneNumber(phoneNumber);
            session.setAttribute("loggedInUser", loggedInUser);
            return "redirect:/user/homeUser"; // Chuyển hướng về trang chủ người dùng
        } else {
            model.addAttribute("error", "Số điện thoại hoặc mật khẩu không chính xác.");
            return "user/login";
        }
    }
    // Đăng xuất
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }

    @GetMapping("/homeUser")
    public String userHome(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loggedInUser);
        return "user/homeUser";
    }

    // Cập nhật thông tin người dùng
    @GetMapping("/update-profile")
    public String showUpdateProfileForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loggedInUser);
        return "user/update-profile";
    }

    @PostMapping("/update-profile")
    public String updateUser(@ModelAttribute("user") User updatedUser, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            loggedInUser.setFullName(updatedUser.getFullName());
            loggedInUser.setPassword(updatedUser.getPassword());
            userService.save(loggedInUser);
            model.addAttribute("message", "Cập nhật thông tin thành công.");
            return "redirect:/user/homeUser";
        } else {
            model.addAttribute("error", "Chưa đăng nhập.");
            return "redirect:/user/login";
        }
    }
}
