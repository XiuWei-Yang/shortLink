package com.oBrway.shortLink.numberDistributor.controller;

import com.oBrway.shortLink.common.enums.ServiceDistributorKey;
import com.oBrway.shortLink.numberDistributor.service.Distributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/redisDistributor")
public class RedisDistributorController {

    @Autowired
    @Qualifier("redisDistributor")
    Distributor distributor;

    @GetMapping("/getIDs")
    @ResponseBody
    public List<Long> getIDs(ServiceDistributorKey key, int batchSize) throws Exception {
        try {
            return distributor.getBatchNumberFromDistributor(key, batchSize);
        } catch (Exception e) {
            throw new Exception("Error retrieving IDs: " + e.getMessage());
        }
    }
}
