//package com.glz.spring.helloworld.demo;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class DemoRunner implements CommandLineRunner {
//
//    @Autowired
//    private List<Demo> demoList;
//
//    @Override
//    public void run(String... args) throws Exception {
//        if(demoList == null || demoList.isEmpty()){
//            return;
//        }
//        System.out.println("有哪些demo" +  demoList.toString());
//
//        System.out.println("\n========== 开始执行 Demo ==========");
//
//        //按照order排序依次执行
//        demoList.stream().sorted((d1,d2) -> Integer.compare(d1.order(),d2.order()))
//                .forEach(demo -> {
//                    try {
//                        System.out.println("\n--- 执行: " + demo.title() + " ---");
//                        demo.run();
//                    }catch (Exception e){
//                        System.out.println("执行出错： " + e.getMessage());
//                    }
//                });
//    }
//}
