package com.sba301.payment_service.controller;

import com.sba301.payment_service.client.OrderServiceClient;
import com.sba301.payment_service.dto.request.CancelTransactionRequest;
import com.sba301.payment_service.dto.request.CheckoutRequest;
import com.sba301.payment_service.dto.response.ApiResponse;
import com.sba301.payment_service.dto.response.OrderResponse;
import com.sba301.payment_service.dto.response.ResponseUtil;
import com.sba301.payment_service.entity.Transaction;
import com.sba301.payment_service.entity.enumerate.TransactionStatus;
import com.sba301.payment_service.exception.AppException;
import com.sba301.payment_service.service.PaymentService;
import com.sba301.payment_service.service.TransactionService;
import com.sba301.payment_service.util.HeaderExtractor;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {
    @Value("${SUCCESS_URL}")
    private String SUCCESS_URL;
    @Value("${FAILURE_URL}")
    private String FAILURE_URL;

    private final TransactionService transactionService;
    private final PaymentService paymentService;
    private final PayOS payOS;
    private final OrderServiceClient orderServiceClient;

    @GetMapping("/test")
    public String test(){return "LOL";}

    /**
     * Called by PayOS after a successful payment.
     * @param transId: 1-1 mapping with db transaction ID (param currently ?orderCode=...)
     * @param id: payment ID
     * @return A redirect response to the success URL
     */
    @GetMapping(value = "/success")
    public ResponseEntity<Void> Success(@RequestParam("orderCode") String transId, @RequestParam String id) {
        //5 trans total
        Transaction successTrans = transactionService.updateTransactionStatusByTransId(Long.valueOf(transId), TransactionStatus.PAID);
        paymentService.createPaymentByTransaction(successTrans, id);

        // premium & ticket -> 2
        String orderId = String.valueOf(successTrans.getOrderId());
        orderServiceClient.completeOrder(Integer.valueOf(orderId));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(SUCCESS_URL))
                .build();
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<Void> Cancel(
            @RequestParam String orderCode,  // PayOS payment ID
            @RequestParam(required = false, defaultValue = "true") boolean isRedirect
    ) {
        // First update transaction status
        Transaction transaction = transactionService.updateTransactionStatusByTransId(Long.valueOf(orderCode), TransactionStatus.CANCEL);

        // Get the actual orderId from the transaction and abandon the order
        orderServiceClient.abandonOrder(transaction.getOrderId().intValue());
        System.out.println("isRedirect = " + isRedirect);
        if( isRedirect) {
            // If isRedirect is true, redirect to the failure URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(FAILURE_URL))
                    .build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(value = "/dummy")
    public String Dummy() {
        return "Dummy";
    }

    @PostMapping("/create-payment-link")
    public ResponseEntity<?> createPaymentLink(@RequestBody @Valid CheckoutRequest checkoutRequest, HttpServletRequest request) {
        try {
            // extract uid
            String userId = HeaderExtractor.getUserId(request);

            String currentTimeString = String.valueOf(System.currentTimeMillis());

            //Check order validity
            ResponseEntity<OrderResponse> ex = orderServiceClient.getOrderById(checkoutRequest.getOrderId());

            //if order not found, throw exception in order-service
            OrderResponse orderResponse = ex.getBody();
            if (!orderResponse.getStatus().toString().equals(TransactionStatus.PENDING.toString())) {
                // If order is not in pending status, throw an exception
                throw new AppException(400, "Order is not in pending status, cannot create payment link", HttpStatus.BAD_REQUEST);
            }

            final String baseUrl = getBaseUrl(request);
            final String description = "Thanh toán "+ currentTimeString;
            final String returnUrl = baseUrl + "/checkout/success";
            final String cancelUrl = baseUrl + "/checkout/cancel";

            List<ItemData> item = orderResponse.getItems().stream()
                    .map(itemResponse -> ItemData.builder()
                            .name(itemResponse.getItemName())
                            .quantity(itemResponse.getQuantity())
                            .price(itemResponse.getItemPrice().intValue())
                            .build())
                    .toList();

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(Long.valueOf(currentTimeString)) // id of transaction
                    .amount(orderResponse.getTotal().intValue())
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .build();
            item.forEach(paymentData::addItem);

            CheckoutResponseData response = payOS.createPaymentLink(paymentData);
            Transaction builtTransaction = Transaction.of(response);
            builtTransaction.setOrderId(Long.valueOf(orderResponse.getId()));
            builtTransaction.setUid(userId);

            transactionService.createTransaction(builtTransaction);
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "dummy",
                    "data", response
            ));

        } catch (FeignException e) {
            log.debug(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                    "code", 500,
                     "message", "Error occured " + e.getMessage()
            ));
        }
    }

    @PostMapping("/cancel-transaction/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> cancelTransaction(@PathVariable Long transactionId, @RequestBody @Valid CancelTransactionRequest cancelTransactionRequest) {
        return ResponseUtil.ok(transactionService.cancelTransaction(transactionId, cancelTransactionRequest.getCancellationReason()));
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        String url = scheme + "://" + serverName;
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url += ":" + serverPort;
        }
        url += contextPath;
        return url;
    }

}