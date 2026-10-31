package com.glz.spring.helloworld.controller;

public class SharedContext {
    // 你的代码：全局共享变量
    public static ThreadLocal<String> userId ;

    public static void main(String[] args) {
        // 模拟两个用户同时发请求
        for (int i = 1; i <= 2; i++) {
            final String currentUser = "用户" + i;
            new Thread(() -> {
                // 模拟 Controller 接收请求，设置用户
//                SharedContext.userId = currentUser;

                // 模拟 Service 正在执行耗时操作（比如查数据库花了 100 毫秒）
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                // 模拟 Util 去读用户 ID 发邮件
                System.out.println("线程: " + Thread.currentThread().getName() +
                        " 准备发邮件，读取到的 userId 是: " + SharedContext.userId);
            }).start();
        }
    }
}
