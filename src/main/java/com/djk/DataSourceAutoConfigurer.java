package com.djk;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dujinkai on 2018/12/7.
 * 数据源配置类
 */
@Configuration
public class DataSourceAutoConfigurer {

    /**
     * 主库
     */
    @Bean("master")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari.master")
    public javax.sql.DataSource master() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 从库
     */
    @Bean("slave1")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.slave1")
    public javax.sql.DataSource slave1() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 动态数据源
     */
    @Bean("dynamicDataSource")
    public javax.sql.DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setWriteDataSource(master());
        List<Object> reads = new ArrayList<>();
        reads.add(slave1());
        dynamicDataSource.setReadDataSources(reads);
        return dynamicDataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**Mapper.xml"));

        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return sqlSessionFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DynamicTransactionManager(dynamicDataSource());
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
