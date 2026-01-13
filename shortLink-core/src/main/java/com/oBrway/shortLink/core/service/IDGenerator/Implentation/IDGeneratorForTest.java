package com.oBrway.shortLink.core.service.IDGenerator.Implentation;

import com.oBrway.shortLink.core.service.IDGenerator.IDGenerator;

import java.util.concurrent.LinkedBlockingDeque;

public class IDGeneratorForTest implements IDGenerator {
    @Override
    public LinkedBlockingDeque<Long> getNextID() {
        LinkedBlockingDeque<Long> idQueue = new LinkedBlockingDeque<>();
        idQueue.add(123456789L);
        return idQueue;
    }
}
