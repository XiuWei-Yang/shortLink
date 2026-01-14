package com.oBrway.shortLink.core.IDGeneratorTest;

import com.oBrway.shortLink.core.config.Config;
import com.oBrway.shortLink.core.service.IDGenerator.IDGeneratorInvoker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试前请确保ID生成器服务已启动并可连接
 */
@SpringBootTest
public class IDGeneratorInvokerTest {
    IDGeneratorInvoker idGeneratorInvoker = new IDGeneratorInvoker();

    Config config = new Config();

    @Test
    public void connectTest() throws Exception {
        LinkedBlockingDeque<Long> q = idGeneratorInvoker.getNextID();
        assertTrue(q.size() == config.getIdGenerator_step());
    }
}
