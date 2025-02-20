package faang.school.projectservice.client;

import faang.school.projectservice.dto.project.client.PaymentRequest;
import faang.school.projectservice.dto.project.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${services.payment-service.host}:${services.payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest paymentRequest);
}
