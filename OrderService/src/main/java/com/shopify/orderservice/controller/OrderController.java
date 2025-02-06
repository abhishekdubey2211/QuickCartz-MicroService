package com.shopify.orderservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shopify.orderservice.dto.OrderDto;
import com.shopify.orderservice.dto.ResponseApi;
import com.shopify.orderservice.service.implementation.OrderServiceImplementation;


@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderServiceImplementation orderService;

    @PostMapping("/{userid}")
    public ResponseEntity<ResponseApi> pushOrder(@PathVariable("userid") Long userid, @RequestBody OrderDto order) {
        OrderDto savedOrder = orderService.placeOrder(userid, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseApi.createResponse(1,savedOrder));
    }
    
    @GetMapping("/details/{orderid}")
    public ResponseEntity<ResponseApi> getOrderByOrderId(@PathVariable("orderid") Long orderid) {
        OrderDto retrievedOrder = orderService.getPlacedOrderDetailsByOrderId(orderid);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseApi.createResponse(1, retrievedOrder));
    }
    

    @GetMapping("/user/{userid}")
    public ResponseEntity<ResponseApi> getOrderByUserId(@PathVariable("userid") Long userid) {
        List<OrderDto> orderList = orderService.getOrdersByUserId(userid);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseApi.createResponse(1,  orderList));
    }
    

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseApi> cancelOrder(@PathVariable("orderId") Long orderId) {
        OrderDto canceledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseApi.createResponse(1, canceledOrder));
    }
    

    @DeleteMapping("/cancel_item/{orderId}/{itemId}")
    public ResponseEntity<ResponseApi> cancelOrderItem(@PathVariable("orderId") Long orderId, 
                                                       @PathVariable("itemId") Long itemId) {
        OrderDto updatedOrder = orderService.cancelOrderItem(orderId, itemId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseApi.createResponse(1,  updatedOrder));
    }
}
