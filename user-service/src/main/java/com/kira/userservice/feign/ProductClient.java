package com.kira.userservice.feign;


import com.kira.userservice.dto.ProductFeignResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "product-service", url = "${services.product.url}") // or use service discovery
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductFeignResponseDTO getProductById(@PathVariable("id") String id);
}
