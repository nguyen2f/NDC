package com.ndc.deliverymanagement.controller;
import com.ndc.deliverymanagement.service.ShipperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.ndc.deliverymanagement.model.*;
import com.ndc.deliverymanagement.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ShipperService shipperService;

    // Hiển thị thống kê đơn hàng (dành cho User)

    // Hiển thị form tạo đơn hàng
    @GetMapping("/user/create-order")
    public String showCreateOrderForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("order", new Order()); // Khởi tạo đối tượng Order rỗng
        return "user/create-order";
    }

    // Xử lý tạo đơn hàng
    @PostMapping("/user/create-order")
    public String createOrder(@ModelAttribute("order") Order order,
                              HttpSession session,
                              Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/user/login";
        }
        try {
            Order newOrder = new Order();
            newOrder.setSenderName(order.getSenderName());
            newOrder.setSenderPhoneNumber(order.getSenderPhoneNumber());
            newOrder.setSenderAddress(order.getSenderAddress());
            newOrder.setReceiverName(order.getReceiverName());
            newOrder.setReceiverPhoneNumber(order.getReceiverPhoneNumber());
            newOrder.setReceiverAddress(order.getReceiverAddress());
            newOrder.setOrderName(order.getOrderName());
            newOrder.setOrderWeight(order.getOrderWeight());
            newOrder.setOrderQuantity(order.getOrderQuantity());

            double shippingCost = orderService.calculateShippingCost(
                    order.getOrderWeight(),
                    order.getOrderQuantity()
            );
            newOrder.setShippingCost(shippingCost);

            Order savedOrder = orderService.save(newOrder);

            // Thêm đơn hàng vào Model để hiển thị trong giao diện
            model.addAttribute("order", savedOrder);
            model.addAttribute("message", "Đơn hàng đã được tạo thành công!");
            return "redirect:/user/homeUser";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create order: " + e.getMessage());
            return "/user/create-order";
        }
    }
    // Hiển thị danh sách đơn hàng đã gửi của User
    @GetMapping("/user/sent-orders")
    public String getSentOrders(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        List<Order> sentOrders = orderService.findOrdersBySenderPhoneNumber(loggedInUser.getPhoneNumber());
        model.addAttribute("sentOrders", sentOrders);

        return "user/sent-orders";
    }

    @GetMapping("/manager/list-orders")
    public String findAllOrders(HttpSession session, Model model) {
        Manager loggedInManager = (Manager) session.getAttribute("loggedInManager");
        if (loggedInManager == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.findAll();
        List<Shipper> activeShippers = shipperService.findActiveShippers(); // Giả sử bạn có phương thức này
        model.addAttribute("orders", orders);
        model.addAttribute("activeShippers", activeShippers);

        return "manager/list-orders";
    }
    // Phân công người nhận hàng (pickup shipper)
    @PostMapping("/manager/assign-pickupshipper")
    @ResponseBody
    public ResponseEntity<String> assignPickupShipper(@RequestParam Long orderId, @RequestParam Long shipperId) {
        try {
            Order order = orderService.assignPickupShipper(orderId, shipperId);
            // Trả về số điện thoại người shipper đã được phân công
            return ResponseEntity.ok("Phân công người nhận thành công! Số điện thoại: " + order.getPickupShipperPhoneNumber());
        } catch (Exception e) {
            // Trả lỗi với thông tin chi tiết hơn
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phân công không thành công: " + e.getMessage());
        }
    }

    // Phân công người giao hàng (delivery shipper)
    @PostMapping("/manager/assign-delivershipper")
    @ResponseBody
    public ResponseEntity<String> assignDeliverShipper(@RequestParam Long orderId, @RequestParam Long shipperId) {
        try {
            Order order = orderService.assignDeliverShipper(orderId, shipperId);
            // Trả về số điện thoại người shipper đã được phân công
            return ResponseEntity.ok("Phân công người giao thành công! Số điện thoại: " + order.getDeliverShipperPhoneNumber());
        } catch (Exception e) {
            // Trả lỗi với thông tin chi tiết hơn
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phân công không thành công: " + e.getMessage());
        }
    }

    @PutMapping("/shipper/{orderId}/update-status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Shipper loggedInShipper = (Shipper) session.getAttribute("loggedInShipper");
        if (loggedInShipper == null) {
            return "redirect:/shipper/login";
        }
        try {
            orderService.updateOrderStatus(orderId, status, description);
            redirectAttributes.addFlashAttribute("message", "Status updated successfully.");
            return "redirect:/shipper/deliver-orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/shipper/deliver-orders";
        }
    }
    //manager
    @GetMapping("/shipper/pickup-orders")
    public String getPickupOrders(HttpSession session, Model model) {
        Shipper loggedInShipper = (Shipper) session.getAttribute("loggedInShipper");
        if (loggedInShipper == null) {
            return "redirect:/shipper/login";
        }

        List<Order> pickupOrders = orderService.findByPickupShipperPhoneNumber(loggedInShipper.getPhoneNumber());
        model.addAttribute("pickupOrders", pickupOrders);
        model.addAttribute("loggedInShipper", loggedInShipper); // Đảm bảo luôn thêm biến này
        return "shipper/pickup-orders";
    }
    @GetMapping("/shipper/deliver-orders")
    public String getDeliverOrders(HttpSession session, Model model) {
        Shipper loggedInShipper = (Shipper) session.getAttribute("loggedInShipper");
        if (loggedInShipper == null) {
            return "redirect:/shipper/login";
        }

        List<Order> deliverOrders = orderService.findByDeliverShipperPhoneNumber(loggedInShipper.getPhoneNumber());
        model.addAttribute("deliverOrders", deliverOrders);
        model.addAttribute("loggedInShipper", loggedInShipper); // Đảm bảo luôn thêm biến này
        return "shipper/deliver-orders";
    }



}
