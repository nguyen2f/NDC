package com.ndc.deliverymanagement.controller;

import com.ndc.deliverymanagement.model.Manager;
import com.ndc.deliverymanagement.model.Shipper;
import com.ndc.deliverymanagement.model.User;
import com.ndc.deliverymanagement.service.ManagerService;
import com.ndc.deliverymanagement.service.ShipperService;
import com.ndc.deliverymanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    public ManagerService managerService;

    @Autowired
    public ShipperService shipperService;

    @Autowired
    public UserService userService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }


    // Đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "manager/login";
    }

    @PostMapping("/login")
    public String loginManager(@RequestParam String phoneNumber,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        if (managerService.checkLogin(phoneNumber, password)) {
            Manager loggedInManager = managerService.findByPhoneNumber(phoneNumber);
            session.setAttribute("loggedInManager", loggedInManager);
            return "redirect:/manager/homeManager"; // Chuyển hướng về trang chủ người dùng
        } else {
            model.addAttribute("error", "Số điện thoại hoặc mật khẩu không chính xác.");
            return "manager/login";
        }
    }
    @GetMapping("/homeManager")
    public String managerHome(HttpSession session, Model model) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        model.addAttribute("user", loggedInManager);
        return "manager/homeManager";
    }

    // Đăng xuất
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/manager/login";
    }

    @GetMapping("/list-shippers")
    public String getAllShippers(Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        List<Shipper> shippers = shipperService.getAllShippers();
        model.addAttribute("shippers", shippers);
        return "manager/list-shippers"; // Trả về tên file template
    }
    // Trang để tạo một Shipper mới
    @GetMapping("/create-shipper")
    public String createShipperForm(Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        model.addAttribute("shipper", new Shipper());
        return "manager/create-shipper"; // Trả về template form
    }

    // Lưu một Shipper mới
    @PostMapping("/create-shipper")
    public String createShipper(@ModelAttribute Shipper shipper, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        try {
            shipperService.createShipper(shipper);
            return "redirect:/manager/list-shippers"; // Redirect sau khi thành công
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create shipper: " + e.getMessage());
            return "manager/create-shipper"; // Trả về trang hiện tại với lỗi
        }
    }

    // Trang chỉnh sửa Shipper
    @GetMapping("/update-shipper/{id}")
    public String updateShipperForm(@PathVariable long id, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        Shipper shipper = shipperService.getById(id); // Đảm bảo service có phương thức này
        model.addAttribute("shipper", shipper);
        return "manager/update-shipper";
    }

    // Lưu thay đổi của Shipper
    @PostMapping("/update-shipper/{id}")
    public String updateShipper(@PathVariable Long id, @ModelAttribute Shipper shipper, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        try {
            shipperService.updateShipper(id, shipper);
            return "redirect:/manager/list-shippers";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update shipper: " + e.getMessage());
            return "manager/update-shipper";
        }
    }

    // Xóa Shipper
    @GetMapping("/delete-shipper/{id}")
    public String deleteShipper(@PathVariable Long id, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }try {
            shipperService.deleteShipper(id);
            return "redirect:/manager/list-shippers";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete shipper: " + e.getMessage());
            return "manager/list-shippers"; // Quay lại danh sách kèm thông báo lỗi
        }
    }

    // Hiển thị danh sách User
    @GetMapping("/list-users")
    public String getAllUsers(Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "manager/list-users";
    }

    // Trang chỉnh sửa User
    @GetMapping("/update-user/{id}")
    public String updateUserForm(@PathVariable Long id, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        User user = userService.getById(id); // Đảm bảo service có phương thức này
        model.addAttribute("user", user);
        return "manager/update-user";
    }

    // Lưu thay đổi của User
    @PostMapping("/update-user/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        try {
            userService.updateUser(id, user);
            return "redirect:/manager/list-users";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update user: " + e.getMessage());
            return "manager/update-user";
        }
    }

    // Xóa User
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, Model model, HttpSession session) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/manager/login";
        }
        try {
            userService.deleteUser(id);
            return "redirect:/manager/list-users";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete user: " + e.getMessage());
            return "manager/list-users";
        }
    }
}
