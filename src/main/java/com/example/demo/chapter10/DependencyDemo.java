package com.example.demo.chapter10;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.chapter9.ObjectFactory;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/26
 */
public class DependencyDemo {

    /**
     * 初始化完成的bean
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * 存放正在初始化的bean对应的工厂，此时对象已经被实例化
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /**
     * 存放bean工厂生产好的bean
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /**
     * 存放正在初始化的bean 对象还没被实例化就放进来了
     */
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    public <T> T getBean(Class<T> beanClass) throws Exception {
        // 类为bean的名称
        String beanName = beanClass.getSimpleName();
        // 已经初始化好了，或正在初始化
        Object initObject = getSingleton(beanName, true);
        if (initObject != null) {
            return (T)initObject;
        }
        // bean 正在被初始化
        singletonsCurrentlyInCreation.add(beanName);
        // 实例化bean
        Object object = beanClass.getDeclaredConstructor().newInstance();
        singletonFactories.put(beanName, () -> {
            return object;
        });

        // 开始初始化bean, 填充属性
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            field.set(object, getBean(fieldClass));
        }
        // 初始化完毕
        singletonObjects.put(beanName, object);
        singletonsCurrentlyInCreation.remove(beanName);
        earlySingletonObjects.remove(beanName);
        return (T)object;
    }

    public Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singleObject = this.singletonObjects.get(beanName);
        if (null == singleObject && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singleObject = this.earlySingletonObjects.get(beanName);
                if (singleObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singleObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singleObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singleObject;
    }

    /**
     * 判断bean是否正在初始化
     * 
     * @param beanName
     * @return
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }
}
