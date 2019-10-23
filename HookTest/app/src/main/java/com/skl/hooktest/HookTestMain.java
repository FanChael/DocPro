package com.skl.hooktest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookTestMain {

    private static class MyView{
        // 通过反射替换对象的该对象变量
        private MyTestView myTestView; // = new MyTestView();

        static class MyTestView{
            public void test(){
                System.out.println("啊啊啊");
            }
        }

        // 通过Proxy生成代理对象，然后替换该变量
        private ProcInterface other2;
    }

    /**
     * 重写旧有的某个方法，作为新的对象注入替换掉MyView的myTestView变量
     */
    private static class Other extends MyView.MyTestView{
        @Override
        public void test(){
            System.out.println("BBBBB");
            super.test();
        }
    }


    /**
     * Proxy生成代理对象必须是某个接口
     */
    private interface ProcInterface{
        void test();
    }

    public static void main(String[] args){
        try {
            // 创建一个对象 - 我们即将替换这个对象的某个变量，达到替换方法的效果；
            // --我是不是可以想象一下，如果要做方法热修复，是不是也可以呢？
            // --但是这个是限于我们有该对象的前提，如果是其他情况，可能就需要你去找到某个对象 它的某个方法，进而实现替换？
            MyView myView = new MyView();
            // myView.myTestView.test();

            // 这是内部静态类类的表示方法
            Field field = MyView.class.getDeclaredField("myTestView");
            field.setAccessible(true);
            // System.out.println(field.getName());

            // 用新的对象替换掉myView对象内部的对象变量
            Other other = new Other();
            field.set(myView, other);

            // 或者用Proxy方法生成代理对象，这种方式下，代理的对象必须实现某个接口
            Field field2 = MyView.class.getDeclaredField("other2");
            field2.setAccessible(true);
            ProcInterface other2 = (ProcInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                    new Class[]{ProcInterface.class}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            System.out.println("这是代理的对象呀 method=" + method);
                            return null;
                        }
                    });
            // 替换对象变量，然后运行
            field2.set(myView, other2);
            myView.other2.test();

            // 重新运行该方法，达到替换方法的效果!
            myView.myTestView.test();
        } catch (NoSuchFieldException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }
}
