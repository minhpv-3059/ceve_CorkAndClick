package com.sun.wineshop.controller;

import com.sun.wineshop.dto.request.PlaceOrderRequest;
import com.sun.wineshop.dto.response.BaseApiResponse;
import com.sun.wineshop.dto.response.OrderDetailResponse;
import com.sun.wineshop.dto.response.OrderResponse;
import com.sun.wineshop.dto.response.OrderSummaryResponse;
import com.sun.wineshop.service.OrderService;
import com.sun.wineshop.utils.AppConstants;
import com.sun.wineshop.utils.JwtUtil;
import com.sun.wineshop.utils.MessageUtil;
import com.sun.wineshop.utils.api.OrderApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(OrderApiPaths.BASE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MessageUtil messageUtil;

    @PostMapping
    public ResponseEntity<BaseApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(JwtUtil.extractUserIdFromJwt(jwt), request);
        return ResponseEntity.ok(new BaseApiResponse<>(
                HttpStatus.OK.value(),
                response,
                messageUtil.getMessage("order.placed.success")
        ));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BaseApiResponse<OrderDetailResponse>> show(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = JwtUtil.extractUserIdFromJwt(jwt);
        OrderDetailResponse response = orderService.show(orderId, userId);
        return ResponseEntity.ok(new BaseApiResponse<>(
                HttpStatus.OK.value(),
                response,
                messageUtil.getMessage("order.detail.fetched.success")
        ));
    }

    @GetMapping
    public ResponseEntity<BaseApiResponse<Page<OrderSummaryResponse>>> getOrderHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize
    ) {
        Long userId = JwtUtil.extractUserIdFromJwt(jwt);
        Page<OrderSummaryResponse> orders = orderService.getOrderHistory(userId, pageNumber, pageSize);

        return ResponseEntity.ok(new BaseApiResponse<>(
                HttpStatus.OK.value(),
                orders,
                messageUtil.getMessage("order.history.fetched.success")
        ));
    }

    @PutMapping(OrderApiPaths.Endpoint.CANCEL)
    public ResponseEntity<BaseApiResponse<Void>> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = JwtUtil.extractUserIdFromJwt(jwt);
        orderService.cancelOrder(orderId, userId);

        return ResponseEntity.ok(new BaseApiResponse<>(
                HttpStatus.OK.value(),
                messageUtil.getMessage("order.cancel.success")
        ));
    }
}
