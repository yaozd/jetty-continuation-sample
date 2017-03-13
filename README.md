# jetty-continuation-sample
简单的实现请求异步处理-例子没有整理比较乱

    /**
     *
     * d-1
     *http://localhost:8080/hello
     * 连续输出结果
     * d-2
     *http://localhost:8080/world
     * 只输出一次结果
     * d-3
     *http://localhost:8080/t3
     * 简单的实现请求异步处理
     * 注：
     * 1.同一浏览器且同一窗口--可以连续发起请求
     * 2.同一浏览器且同时两个窗口必须等待第一个同窗口的请求都处理完成才可以处理第二个窗口的请求
     */
