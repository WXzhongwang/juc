package com.example.demo.chapter9;

/**
 * @author dick <18668485565@163.com>
 * @version V1.0.0
 * @description
 * @date created on 2021/3/26
 */
public class DependencyDemoTests {

    public static void main(String[] args) throws Exception {
        DependencyDemo dependencyDemo = new DependencyDemo();
        // 假装扫描出来的对象
        Class[] classes = {A.class, B.class};
        for (Class aClass : classes) {
            dependencyDemo.getBean(aClass);
        }

        System.out.println(dependencyDemo.getBean(B.class).getA() == dependencyDemo.getBean(A.class));

        System.out.println(dependencyDemo.getBean(A.class).getB() == dependencyDemo.getBean(B.class));
    }
}
