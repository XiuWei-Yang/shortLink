package com.oBrway.shortLink.core.strategy;

import com.oBrway.shortLink.core.util.ShardingUtil;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class UrlTableShardingAlgorithm extends CustomShardingAlgorithm implements
    PreciseShardingAlgorithm<String> {

  @Override
  public String doSharding(Collection<String> tableNames,
      PreciseShardingValue<String> preciseShardingValue) {

    for (String tableName : tableNames) {//url_mapping_0 ... url_mapping_3
      try {
        if (tableName.endsWith(
            String.valueOf(calSlot(preciseShardingValue) % ShardingUtil.TBL_CNT))) {
          return tableName;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    throw new IllegalArgumentException("分表计算时异常" + preciseShardingValue.toString());
  }
}
