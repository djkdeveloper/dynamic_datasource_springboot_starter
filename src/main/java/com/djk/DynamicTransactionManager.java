package com.djk;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.*;

/**
 * Created by dujinkai on  2018/12/7.
 * 自定义事务管理器
 */
public class DynamicTransactionManager extends DataSourceTransactionManager {

    public DynamicTransactionManager(javax.sql.DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void doBegin(Object transaction, TransactionDefinition definition) {
        // 当遇到事务的时候 强制选择写数据库
        DynamicDataSourceHolder.putDataSource(DynamicDataSourceEnum.WRITE);
        super.doBegin(transaction, definition);
    }

    @Override
    public void doCommit(DefaultTransactionStatus status) {
        DynamicDataSourceHolder.clearDataSource();
        super.doCommit(status);
    }
}
