package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.SystemHealthInfo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 健康分析
 * 分析逻辑：
 *  健康分析分为 2 个部分，可用分析 和 心跳分析
 *  心跳分析：心跳判断为<b>不健康的状态</b>，需要后续心跳进行重新激活, 激活后可用统计将重新开始
 *  可用分析：可用率如果<b>小于0.8</b>，可以通过心跳进行激活，同样激活后可用统计将重新开始
 * Created by Daiwei on 2021/4/28
 */
@Slf4j
public class HealthAvailableAnalyzer {

    private final Map<String, AtomicInteger> invokeSuccessCnt;

    private final Map<String, AtomicInteger> invokeFailedCnt;

    private final Set<String> subHealthUrl;

    public HealthAvailableAnalyzer() {
        this.subHealthUrl = new CopyOnWriteArraySet<>();
        this.invokeSuccessCnt = new ConcurrentHashMap<>();
        this.invokeFailedCnt = new ConcurrentHashMap<>();
    }

    public void invokeSuccess(String url) {
        incrCnt(url, this.invokeSuccessCnt);
        calcAvailableRate(url);
    }

    public void invokeFailed(String url) {
        incrCnt(url, this.invokeFailedCnt);
        calcAvailableRate(url);
    }

    public void incrCnt(String url, Map<String, AtomicInteger> cntMap) {
        if (cntMap.containsKey(url)) {
            cntMap.get(url).incrementAndGet();
        } else {
            cntMap.put(url, new AtomicInteger());
        }
    }

    public void calcAvailableRate(String url) {
        BigDecimal availableRate = BigDecimal.ZERO;
        if (!invokeFailedCnt.containsKey(url)) {
            availableRate = BigDecimal.ONE;
        } else {
            int healthCnt = invokeSuccessCnt.get(url).get();
            availableRate = new BigDecimal(healthCnt).divide(new BigDecimal(invokeFailedCnt.get(url).get() + healthCnt),
                    2, BigDecimal.ROUND_HALF_UP);
        }
        log.trace("[daiwei-rpc] server[{}] current health available rate is {}", url, availableRate);
        if (NetConstant.SUB_HEALTH_AVAILABLE_RATE.compareTo(availableRate) > 0) {
            this.subHealthUrl.add(url);
        } else {
            this.subHealthUrl.remove(url);
        }
    }

    public void analyzeHeartBeatRes(SystemHealthInfo healthInfo, String url) {
        if (healthInfo.getRespSendTime() + TimeUnit.MILLISECONDS.convert(HeartBeat.BEAT_INTERVAL, TimeUnit.SECONDS)
                < System.currentTimeMillis()) {
            return;
        }
        boolean health = healthInfo.getLatency() < 500 && healthInfo.getCpuLoadPercent().compareTo(new BigDecimal("0.90")) < 0
                && healthInfo.getMemLoadPercent().compareTo(new BigDecimal("0.80")) < 0;
        if (health) {
            if (this.subHealthUrl.contains(url)) {
                this.subHealthUrl.remove(url);
                this.invokeSuccessCnt.put(url, new AtomicInteger());
                this.invokeFailedCnt.put(url, new AtomicInteger());
            }
        } else {
            this.subHealthUrl.add(url);
        }
        log.debug("[daiwei-rpc] remote server[{}], health status[{}]", url, health);
    }

    public void heartBeatTimeout(String url) {
        this.subHealthUrl.add(url);
    }

    public List<String> filerSubHealth(List<String> url) {
        List<String> all = new ArrayList<>(url);
        all.removeAll(this.subHealthUrl);
        return all;
    }

    public void removeUrl(String url) {
        this.subHealthUrl.remove(url);
        this.invokeFailedCnt.remove(url);
        this.invokeSuccessCnt.remove(url);
    }
}

