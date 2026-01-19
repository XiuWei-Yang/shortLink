package com.oBrway.shortLink.core.service.IDGenerator;

import com.oBrway.shortLink.common.enums.ResponseCode;
import com.oBrway.shortLink.common.enums.ServiceDistributorKey;
import com.oBrway.shortLink.common.exception.BaseException;
import com.oBrway.shortLink.core.config.Config;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 这个类有很多选择的实现方式，这里提供一个基于WebClient调用远程ID生成服务的示例。
 * 实现方式有：gRPC调用、消息队列调用、直接访问
 * 值得一提的是，如果有多个ID生成服务实例，可以在这里实现负载均衡逻辑。
 * 这里是性能要求变化时的一个变更点。
 */
@Component
public class IDGeneratorInvoker implements IDGenerator {
    private final WebClient webClient;

    private Config config = new Config();

    public IDGeneratorInvoker() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8091") // Number Distributor service URL
                .build();
    }
    @Override
    public LinkedBlockingDeque<Long> getNextID() throws Exception{
        int batchSize = config.getIdGenerator_step();
        try{
            Mono<List<Long>> responseMono = fetchIDsFromService(batchSize);
            /**
             * 这里实现比较简单，直接阻塞等待响应。在高性能要求下，应该考虑使用异步处理。
             * 异步使用Mono或者CompletableFuture等方式。
             * 这里设置超时时间，防止调用阻塞过久。超时后会抛出异常。
             */
            List<Long> idList = responseMono.block(Duration.ofSeconds(5));
            LinkedBlockingDeque<Long> idQueue = new LinkedBlockingDeque<>(idList);
            for(long i = idList.get(0); i <= idList.get(1); i++){
                idQueue.add(i);
            }
            return idQueue;
        } catch (Exception e) {
            throw new BaseException(e.getMessage(), ResponseCode.ID_GENERATOR_INVOKE_TIMEOUT);
        }

    }

    private Mono<List<Long>> fetchIDsFromService(int batchSize) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/redisDistributor/getIDs")
                        .queryParam("key", ServiceDistributorKey.coreService)
                        .queryParam("batchSize", batchSize)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Long>>() {});
    }

}
