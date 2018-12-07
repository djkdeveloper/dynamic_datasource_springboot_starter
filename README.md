# dynamic_datasource_springboot_starter
springboot mybatis 读写分离 支持事务

## 版本:
- springboot 1.0+
- jdk8
- maven 3.0+

## 使用方法:
- git clone https://github.com/djkdeveloper/-dynamic_datasource_springboot_starter.git
- 在dynamic_datasource_springboot_starter目录下执行mvn clean install  打包到本地
- 添加pom到自己的项目
```bash
        <dependency>
            <groupId>com.djk</groupId>
            <artifactId>dynamic_datasource_springboot_starter</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```

## 目前默认一个主库 一个从库 在项目的application.properties 文件中 添加
```bash
spring.datasource.type=com.zaxxer.hikari.HikariDataSource

spring.datasource.hikari.master.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.master.jdbc-url=jdbc:mysql://192.168.0.11:3306/tests?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull
spring.datasource.hikari.master.username=root
spring.datasource.hikari.master.password=123456
spring.datasource.hikari.master.minimum-idle=5
spring.datasource.hikari.master.maximum-pool-size=15
spring.datasource.hikari.master.auto-commit=true
spring.datasource.hikari.master.idle-timeout=30000
spring.datasource.hikari.master.pool-name=DatebookHikariCP
spring.datasource.hikari.master.max-lifetime=1800000
spring.datasource.hikari.master.connection-timeout=30001
spring.datasource.hikari.master.connection-test-query=SELECT 1


spring.datasource.hikari.slave1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.slave1.jdbc-url=jdbc:mysql://192.168.0.11:3306/tests?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull
spring.datasource.hikari.slave1.username=root
spring.datasource.hikari.slave1.password=123456
spring.datasource.hikari.slave1.minimum-idle=5
spring.datasource.hikari.slave1.maximum-pool-size=15
spring.datasource.hikari.slave1.auto-commit=true
spring.datasource.hikari.slave1.idle-timeout=30000
spring.datasource.hikari.slave1.pool-name=DatebookHikariCP
spring.datasource.hikari.slave1.max-lifetime=1800000
spring.datasource.hikari.slave1.connection-timeout=30001
spring.datasource.hikari.slave1.connection-test-query=SELECT 1
```

## 代码中使用
- 在代码的数据库层添加注解
``` bash
主库注解   @DataSource(value = DynamicDataSourceEnum.WRITE)
从库注解   @DataSource(value = DynamicDataSourceEnum.READ)
```

- 如果遇到事务 则会强制走主库

## 配置多个从库
参照DataSourceAutoConfigurer.java中配置的slave1即可
