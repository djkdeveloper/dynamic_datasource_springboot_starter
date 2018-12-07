package com.djk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dujinkai on 2018/12/7.
 * 数据源注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSource {

    /**
     * 默认使用写库
     */
    DynamicDataSourceEnum value() default DynamicDataSourceEnum.WRITE;
}
