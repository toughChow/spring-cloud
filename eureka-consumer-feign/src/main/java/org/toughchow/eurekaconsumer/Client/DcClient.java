package org.toughchow.eurekaconsumer.Client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("eureka-client-tough")
public interface DcClient {

    @GetMapping("/dc")
    String consumer();

}
