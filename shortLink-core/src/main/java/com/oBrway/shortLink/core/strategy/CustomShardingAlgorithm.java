package com.oBrway.shortLink.core.strategy;

import com.oBrway.shortLink.core.service.Base62.Base62Encoder;
import com.oBrway.shortLink.core.util.ShardingUtil;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

public abstract class CustomShardingAlgorithm {

  //计算分片需要
  public long calSlot(PreciseShardingValue<String> preciseShardingValue) throws Exception {
    long hashCode = Base62Encoder.decodeToLong(preciseShardingValue.getValue());//十进制
    return Math.abs(hashCode % ShardingUtil.SUM_SLOT);//分片序号
  }

}
