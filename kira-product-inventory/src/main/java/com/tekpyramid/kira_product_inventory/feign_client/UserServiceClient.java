package com.tekpyramid.kira_product_inventory.feign_client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "USER-SERVICE",
        contextId = "categoryClient",
        path = "/api/v1/vendors"
)
public interface UserServiceClient {

}
