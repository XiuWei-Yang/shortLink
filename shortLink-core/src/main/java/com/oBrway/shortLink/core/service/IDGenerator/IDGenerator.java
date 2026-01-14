package com.oBrway.shortLink.core.service.IDGenerator;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public interface IDGenerator {
    LinkedBlockingDeque<Long> getNextID() throws Exception;
}
