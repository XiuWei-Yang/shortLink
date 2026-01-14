package com.oBrway.shortLink.core.service.IDGenerator;

import com.oBrway.shortLink.core.service.IDGenerator.IDGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;

@Component
public class IDGeneratorForTest implements IDGenerator {
    @Override
    public LinkedBlockingDeque<Long> getNextID() {
        LinkedBlockingDeque<Long> idQueue = new LinkedBlockingDeque<>();
        idQueue.add(123456789L);
        return idQueue;
    }
}
