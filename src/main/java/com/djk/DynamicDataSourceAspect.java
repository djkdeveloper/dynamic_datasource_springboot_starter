package com.djk;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by dujinkai on 2018/12/7.
 * 动态数据库切面
 */
@Aspect
public class DynamicDataSourceAspect {

    /**
     * 调试日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * 切点
     */
    @Pointcut("@annotation(com.djk.DataSource)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint point) {
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Class<?>[] clazz = target.getClass().getInterfaces();
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
            Method method = clazz[0].getMethod(methodName, parameterTypes);
            if (method != null && method.isAnnotationPresent(DataSource.class)) {
                DataSource data = method.getAnnotation(DataSource.class);
                DynamicDataSourceHolder.putDataSource(data.value());
            }
        } catch (Exception e) {
            logger.error(String.format("Choose DataSource error, method:%s, msg:%s", methodName, e.getMessage()));
        }
    }

    /**
     * mapper层执行结束和清理当前线程
     */
    public void after() {
        DynamicDataSourceHolder.clearDataSource();
    }
}
