package com.ll.exam.Week_Mission.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.Week_Mission.app.cart.entity.CartItem;
import com.ll.exam.Week_Mission.app.cart.service.CartService;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.myBook.service.MyBookService;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import com.ll.exam.Week_Mission.app.order.exception.ActorCanNotPayOrderException;
import com.ll.exam.Week_Mission.app.order.exception.ActorCanNotSeeOrderException;
import com.ll.exam.Week_Mission.app.order.exception.OrderIdNotMatchedException;
import com.ll.exam.Week_Mission.app.order.exception.OrderNotEnoughRestCashException;
import com.ll.exam.Week_Mission.app.order.service.OrderService;
import com.ll.exam.Week_Mission.app.product.service.ProductService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final MyBookService myBookService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createOrder(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();
        Order order = orderService.createFromCart(member);
        return "redirect:/order/%d".formatted(order.getId()) + "?msg=" + Ut.url.encode("%d??? ????????? ?????????????????????.".formatted(order.getId()));
    }

    @PostMapping("/{id}/payByRestCashOnly")
    @PreAuthorize("isAuthenticated()")
    public String payByRestCashOnly(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (orderService.actorCanPayment(actor, order) == false) {
            throw new ActorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);
        myBookService.add(order);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("??????????????? ??????????????????."));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);
        model.addAttribute("actorRestCash", restCash);

        return "order/detail";
    }

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    private final String SECRET_KEY = "test_sk_YyZqmkKeP8gkv6KJKYK8bQRxB9lG";

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {

        Order order = orderService.findForPrintById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        // param id??? pathVariable id??? ????????????
        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 ?????? ???????????? ??????
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            orderService.payByTossPayments(order, payPriceRestCash);

            myBookService.add(order);
            return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("????????? ?????????????????????."));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }

    @GetMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancelOrder(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Member member = memberContext.getMember();
        Order order = orderService.findForPrintById(id).get();
        orderService.cancelOrder(order);
        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("????????? ?????????????????????."));
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<Order> orders = orderService.getOrderByBuyer(buyer);

        model.addAttribute("orders", orders);

        return "order/list";
    }

}