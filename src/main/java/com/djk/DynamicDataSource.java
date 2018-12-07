package com.djk;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dujinkai on  2018/12/7.
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 写数据源(主库)
     */
    private Object writeDataSource;

    /**
     * 多个读库(从库)可以有多个
     */
    private List<Object> readDataSources;

    /**
     * 读库的个数
     */
    private int readDataSourceSize; //读数据源个数

    /**
     * 读库的选择规则 0：随机，1：轮询
     */
    private int readDataSourcePollPattern = 0;

    private AtomicLong counter = new AtomicLong(0);

    private static final Long MAX_POOL = Long.MAX_VALUE;

    private final Lock lock = new ReentrantLock();

    @Override
    public void afterPropertiesSet() {
        if (this.writeDataSource == null) {
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }
        // 设置默认数据源为写库
        setDefaultTargetDataSource(writeDataSource);
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DynamicDataSourceEnum.WRITE.name(), writeDataSource);
        if (CollectionUtils.isEmpty(this.readDataSources)) {
            readDataSourceSize = 0;
        } else {
            for (int i = 0; i < readDataSources.size(); i++) {
                targetDataSources.put(DynamicDataSourceEnum.READ.name() + i, readDataSources.get(i));
            }
            readDataSourceSize = readDataSources.size();
        }
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {

        // 获得当前数据源
        DynamicDataSourceEnum dynamicDataSourceGlobal = DynamicDataSourceHolder.getDataSource();

        // 如果当前数据源没有设置 或者 没有从库 或者是主库 则走主库
        if (dynamicDataSourceGlobal == null
                || dynamicDataSourceGlobal == DynamicDataSourceEnum.WRITE
                || readDataSourceSize <= 0) {
            return DynamicDataSourceEnum.WRITE.name();
        }

        int index = 1;

        // 轮询方式
        if (readDataSourcePollPattern == 1) {
            long currValue = counter.incrementAndGet();
            if ((currValue + 1) >= MAX_POOL) {
                try {
                    lock.lock();
                    if ((currValue + 1) >= MAX_POOL) {
                        counter.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            index = (int) (currValue % readDataSourceSize);
        } else {
            //随机方式
            index = ThreadLocalRandom.current().nextInt(0, readDataSourceSize);
        }
        return dynamicDataSourceGlobal.name() + index;
    }

    public void setWriteDataSource(Object writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public void setReadDataSources(List<Object> readDataSources) {
        this.readDataSources = readDataSources;
    }

    public void setReadDataSourcePollPattern(int readDataSourcePollPattern) {
        this.readDataSourcePollPattern = readDataSourcePollPattern;
    }

}
