package com.hnust;

import java.util.concurrent.*;

class MyThread extends Thread {
    private int id;
    public MyThread(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(String.format("T%d:%d", id, i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Producter implements Runnable {
    private BlockingQueue<String> q;
    public Producter(BlockingQueue<String> q) {
        this.q = q;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }
    @Override
    public void run() {
        while(true) {
            try {
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



public class MultiThread {
    public static void testThread() {
        for (int i = 0; i < 10; i++) {
//            new MyThread(i).start();
        }

        for (int i = 0; i < 10; i++) {
            final int id =i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                            System.out.println(String.format("T2%d:%d", id, i));
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    private static Object obj = new Object();
    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3%d",  i));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void testSynchronized2() {
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4%d",  i));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void testSynchronized() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    /**
     * 阻塞队列
     * http://www.infoq.com/cn/articles/java-blocking-queue
     */
    public static void testBlockingQueue() {
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producter(q)).start();
        new Thread(new Consumer(q), "consumer01").start();
        new Thread(new Consumer(q), "consumer02").start();
    }

    /**
     * Executor 提供了一个执行任务的框架
     */
    public static void testExecutor() throws InterruptedException {
//        ExecutorService service = Executors.newSingleThreadExecutor();    //单线程Executor
        ExecutorService service = Executors.newFixedThreadPool(2); //线程池Executor(指定2个线程)
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Executor1 " + i);
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Executor2 " + i);
                }
            }
        });
        service.shutdown();  //关掉service
        while(!service.isTerminated()) {    //如果关闭service后所有任务都已完成，则返回 true
            Thread.sleep(1000);
            System.out.println("Wait for shutdown");
        }
    }

    /**
     *Future----表示异步计算的结果.它提供了检查计算是否完成的方法，以等待计算的完成，并获取计算的
     * 结果。计算完成后只能使用 get 方法来获取结果，如有必要，计算完成前可以阻塞此方法。
     *
     * public interface Callable<V>返回结果并且可能抛出异常的任务。实现者定义了一个不带任何参数的叫做 call 的方法。
     Callable 接口类似于 Runnable，两者都是为那些其实例可能被另一个线程执行的类设计的。但是 Runnable 不会返回结果，
     并且无法抛出经过检查的异常
     */
    public static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();    //单线程Executor
//        ExecutorService service = Executors.newFixedThreadPool(2); //线程池Executor(指定2个线程)
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 1;
//                throw new IllegalArgumentException("异常");
            }
        });

        service.shutdown();
        try {
            //get()方法: 如有必要，等待计算完成(可以设置等待时间)，然后获取其结果。
            System.out.println(future.get(999, TimeUnit.MILLISECONDS)); //设置等待是时间为999毫秒
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        testThread();
//        testSynchronized();
//        testBlockingQueue();
        testFuture();
//        try {
//            testExecutor();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
