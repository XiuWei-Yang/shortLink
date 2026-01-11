package com.oBrway.shortLink.core.IDGenerator;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public interface IDGenerator {
    LinkedBlockingDeque<Long> getNextID();
}
