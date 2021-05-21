package io.daiwei.rpc.router;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.router.common.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 随机权重负载策略
 * Created by Daiwei on 2021/4/30
 */
@Slf4j
public class WeightRandomLoadBalance extends LoadBalance {

    private final Random rnd;

    private static final int FULL_LOAD_FACTOR = 10;

    public WeightRandomLoadBalance() {
        this.rnd = new Random();
    }

    @Override
    public String select(List<String> healthUrls, List<String> allAvailableUrls) {
        List<String> urls = healthUrls;
        if (urls.isEmpty()) {
            log.warn("no health server!!");
            urls = allAvailableUrls;
        }
        if (urls.isEmpty()) {
            throw new DaiweiRpcException("no available server url");
        }
        List<String> weightList = getWeightList(urls);
        return weightList.get(rnd.nextInt(weightList.size()));
    }

    /**
     * 化简后的权重数组
     * @return
     */
    private List<String> getWeightList(List<String> availUrl) {
        Map<String, Integer> loadFactorMap = new HashMap<>();
        for (String s : availUrl) {
            if (!this.dataMap.containsKey(s)) {
                continue;
            }
            long value = Long.parseLong(this.dataMap.getOrDefault(s, "0"));
            long interval =  (System.currentTimeMillis() - value) / 1000;
            int loadFactor = FULL_LOAD_FACTOR;
            if (interval < FULL_LOAD_FACTOR) {
                loadFactor = (int) interval;
            }
            loadFactorMap.put(s, loadFactor);
        }
        int gcd = gcdN(loadFactorMap.values().stream().mapToInt(Integer::intValue).toArray(), loadFactorMap.size());
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : loadFactorMap.entrySet()) {
            for (int i = 0; i < (entry.getValue() / gcd); i++) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    private int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    private int gcdN(int[] nums, int len) {
        if (len == 1) return nums[0];
        return gcd(nums[len - 1], gcdN(nums, len - 1));
    }

}
