package com.ife.chowdome.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ife.chowdome.model.CartItem;
import com.ife.chowdome.model.Order;
import com.ife.chowdome.model.Order.OrderStatus;
import com.ife.chowdome.model.OrderItem;
import com.ife.chowdome.model.Users;
import com.ife.chowdome.repository.CartItemRepo;
import com.ife.chowdome.repository.OrderItemRepo;
import com.ife.chowdome.repository.OrderRepo;
import com.ife.chowdome.repository.UserRepo;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @PostMapping("/place")
    public Object placeOrder( Authentication auth) {
        Users user = (Users) auth.getPrincipal();
        Optional<Users> userOptional = userRepo.findById(user.getId());
        if (!userOptional.isPresent()) {
            return Map.of("error", true, "message", "User not found");
        }
        List<CartItem> cartItems = cartItemRepo.findByUser(user);
        if (cartItems.isEmpty()) {
            return Map.of("error", true, "message", "Cart is empty");
        }
        Double totalAmount = 0.0;
        
        for (CartItem item : cartItems) {
            totalAmount += item.getFood().getPrice() * item.getQuantity();
            


        }

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotal(totalAmount);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setCreatedAt(LocalDateTime.now());
        orderRepo.save(newOrder);
        newOrder.setItems(new ArrayList<>());

        for( CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(item.getFood());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(item.getFood().getPrice() * item.getQuantity());
            orderItem.setOrder(newOrder);
            orderItemRepo.save(orderItem);
            newOrder.getItems().add(orderItem);
        }
        cartItemRepo.deleteByUser(user);
        
        
        
        

        return Map.of("error", false, "message", "Order placed successfully", "orderId", newOrder.getId());
        
    }

    

    @GetMapping("/history")
    public Object getOrderHistory(Authentication auth) {
        Users user = (Users) auth.getPrincipal();
        List<Order> orders = orderRepo.findByUser(user);
        if (orders.isEmpty()) {
            return Map.of("error", true, "message", "No orders found");
        }
        List<Map<String, Object>> simplifiedOrders = orders.stream().map(ord -> {
            List<Map<String, Object>> items = ord.getItems().stream().map(item -> {
               Map<String, Object> simplifiedItem = Map.of(
                    "id", item.getId(),
                    "food", item.getFood().getName(),
                    "quantity", item.getQuantity(),
                    "subtotal", item.getSubtotal()
                );
                return simplifiedItem;
            }).toList();
            Map<String, Object> simplifiedOrder = Map.of(
                "id", ord.getId(),
                "total", ord.getTotal(),
                "status", ord.getStatus(),
                "items", items
            );
            return simplifiedOrder;
        }).toList();


        
        
        return Map.of("error", false, "message", "orders retrieved successfully","orders", simplifiedOrders);
    }
    
    
    @GetMapping("/{id}")
    public Object getOrderById(@PathVariable Long id, Authentication auth) {
        Users user = (Users) auth.getPrincipal();
        Optional<Order> orderOptional = orderRepo.findByIdAndUserId(id, user.getId());
        if (!orderOptional.isPresent()) {
            return Map.of("error", true, "message", "Order not found");
        }
        Order order = orderOptional.get();
        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> simplifiedItem = Map.of(
                "id", item.getId(),
                "food", item.getFood().getName(),
                "quantity", item.getQuantity(),
                "subtotal", item.getSubtotal()
            );
            return simplifiedItem;
        }).toList();
        
        Map<String, Object> simplifiedOrder = Map.of(
            "id", order.getId(),
            "total", order.getTotal(),
            "status", order.getStatus(),
            "items", items
        );
        
        return Map.of("error", false, "message", "Order retrieved successfully", "order", simplifiedOrder);
    }

    @GetMapping("/status/{id}")
    public Object getOrderStatus(@PathVariable Long id, Authentication auth) {
        Users user = (Users) auth.getPrincipal();
        Optional<Order> orderOptional = orderRepo.findByIdAndUserId(id, user.getId());
        if (!orderOptional.isPresent()) {
            return Map.of("error", true, "message", "Order not found");
        }
        Order order = orderOptional.get();
        return Map.of("error", false, "message", "Order status retrieved successfully", "status", order.getStatus());
    }

    @PostMapping("/{id}/reorder")
    public Object reorder(@PathVariable Long id, Authentication auth) {
        Users user = (Users) auth.getPrincipal();
        Optional<Order> orderOptional = orderRepo.findByIdAndUserId(id, user.getId());
        if (!orderOptional.isPresent()) {
            return Map.of("error", true, "message", "Order not found");
        }
        Order order = orderOptional.get();
        List<CartItem> cartItems = cartItemRepo.findByUser(user);
        
        if (!cartItems.isEmpty()) {
            return Map.of("error", true, "message", "Cart is not empty. Please clear your cart before reordering.");
        }
        
        for (OrderItem item : order.getItems()) {
            CartItem newCartItem = new CartItem();
            newCartItem.setFood(item.getFood());
            newCartItem.setQuantity(item.getQuantity());
            newCartItem.setUser(user);
            cartItemRepo.save(newCartItem);
        }
        
        return Map.of("error", false, "message", "Reorder successful. Items added to cart.");
    }

    @GetMapping("/store-orders")
public Object getStoreOrders(Authentication auth) {
    Users user = (Users) auth.getPrincipal();
    
    if (!"STORE".equals(user.getRole())) {
        return Map.of(
            "error", true,
            "message", "Unauthorized access. Only store users can view store orders."
        );
    }

    // Fetch all order items
    List<OrderItem> allOrderItems = orderItemRepo.findAll(); // You can optimize later with pagination or filters
    
    
    // Only include items that belong to this store user
    Map<Long, Map<String, Object>> orderMap = new HashMap<>();

    for (OrderItem item : allOrderItems) {
        // Check if the item's food's store belongs to the logged-in user
        if (item.getFood().getStore().getUser().getId() == user.getId()) {
            Order order = item.getOrder();
            Long orderId = order.getId();

            // Initialize the order group if not present
            if (!orderMap.containsKey(orderId)) {
                Map<String, Object> simplifiedOrder = new HashMap<>();
                simplifiedOrder.put("id", orderId);
                simplifiedOrder.put("total", order.getTotal());
                simplifiedOrder.put("status", order.getStatus());
                simplifiedOrder.put("items", new ArrayList<Map<String, Object>>());
                orderMap.put(orderId, simplifiedOrder);
            }

            // Add the item to the list inside the order group
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderMap.get(orderId).get("items");
            Map<String, Object> simplifiedItem = new HashMap<>();
            simplifiedItem.put("id", item.getId());
            simplifiedItem.put("food", item.getFood().getName());
            simplifiedItem.put("quantity", item.getQuantity());
            simplifiedItem.put("subtotal", item.getSubtotal());

            items.add(simplifiedItem);
        }
    }

    List<Map<String, Object>> simplifiedOrders = new ArrayList<>(orderMap.values());

    Map<String, Object> response = new HashMap<>();
    response.put("error", false);
    response.put("message", "Store orders retrieved successfully");
    response.put("orders", simplifiedOrders);
    
    return response;
}



    @PutMapping("/{id}/status")
    public Object updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        Users user = (Users) auth.getPrincipal();

        // Ensure only STORE role can access
        if (!user.getRole().equals("STORE")) {
            return Map.of("error", true, "message", "Only store users can update order status.");
        }

        // Fetch the order
        Optional<Order> orderOptional = orderRepo.findById(id);
        if (orderOptional.isEmpty()) {
            return Map.of("error", true, "message", "Order not found.");
        }

        Order order = orderOptional.get();

        // Confirm the store owns at least one item in the order
        boolean ownsItem = order.getItems().stream()
                .anyMatch(item -> item.getFood().getStore().getUser().getId() == user.getId());

        if (!ownsItem) {
            return Map.of("error", true, "message", "Unauthorized to update this order.");
        }

        // Get new status from request body
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return Map.of("error", true, "message", "Status is required.");
        }

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Map.of("error", true, "message", "Invalid status value.");
        }

        // Update and save
        order.setStatus(newStatus);
        orderRepo.save(order);

        return Map.of("error", false, "message", "Order status updated successfully.");
    }

    @GetMapping("/admin")
    public Object getOrder(Authentication auth){
        Users user = (Users) auth.getPrincipal();
        if(!user.getRole().equals("ADMIN")){
            return Map.of("error", true, "message", "Unauthorized access. Admins only.");
        }
        List<Order> orders = orderRepo.findAll();
    List<Map<String, Object>> simplifiedOrders = orders.stream().map(order -> {
        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("food", item.getFood().getName());
            map.put("quantity", item.getQuantity());
            map.put("subtotal", item.getSubtotal());
            return map;
        }).toList();

        return Map.of(
                "id", order.getId(),
                "user", Map.of(
                        "id", order.getUser().getId(),
                        "name", order.getUser().getFirstName() + " " + order.getUser().getLastName()
                ),
                "total", order.getTotal(),
                "status", order.getStatus(),
                "items", items
        );
    }).toList();

    return Map.of(
            "error", false,
            "message", "All orders retrieved successfully",
            "orders", simplifiedOrders
    );

    }

    @GetMapping("/admin/{id}")
    public Object getOrderByID(@PathVariable Long id, Authentication auth) {
        Users user = (Users) auth.getPrincipal();
        if (!user.getRole().equals("ADMIN")) {
            return Map.of("error", true, "message", "Unauthorized access. Admins only.");
        }
        Optional<Order> orderOptional = orderRepo.findById(id);
        if (orderOptional.isEmpty()) {
            return Map.of("error", true, "message", "Order not found");
        }
        Order order = orderOptional.get();
        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("food", item.getFood().getName());
            map.put("quantity", item.getQuantity());
            map.put("subtotal", item.getSubtotal());
            return map;
        }).toList();

        Map<String, Object> simplifiedOrder = Map.of(
                "id", order.getId(),
                "user", Map.of(
                        "id", order.getUser().getId(),
                        "name", order.getUser().getFirstName() + " " + order.getUser().getLastName()
                ),
                "total", order.getTotal(),
                "status", order.getStatus(),
                "items", items
        );

        return Map.of(
                "error", false,
                "message", "Order retrieved successfully",
                "order", simplifiedOrder
        );

    }



    

}
