package com.djk;

/**
 * Created by dujinkai on  2018/12/7.
 * 本地线程设置和获取数据源信息
 */
public class DynamicDataSourceHolder {

    private static final ThreadLocal<DynamicDataSourceEnum> holder = new ThreadLocal<>();

    public static void putDataSource(DynamicDataSourceEnum dataSource) {
        holder.set(dataSource);
    }

    public static DynamicDataSourceEnum getDataSource() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }
}
