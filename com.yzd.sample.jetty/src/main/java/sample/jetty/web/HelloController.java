package sample.jetty.web;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sample.jetty.ExampleServletContextListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/9.
 */
@Controller
public class HelloController {
    /**
     * 连续的输出--------结果
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/hello")
    @ResponseBody
    public void helloWorld(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        System.out.println(1);
        final Continuation continuation = ContinuationSupport.getContinuation(request);

        if (!continuation.isSuspended()) {
            continuation.suspend();
        }
        if (continuation.isSuspended()) {
            continuation.setAttribute("results", "test");
            continuation.resume();
        }
        String r = continuation.getAttribute("results").toString();
        response.getWriter().write("results:" + r);
        response.getWriter().flush();
//        if(continuation.isResumed()){
//            continuation.undispatch();
//            return;
//        }
        return;
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                    sendMyResultResponse(response, "test");
//                    System.out.println(2);
//                    continuation.resume();
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();

        //return "OK";
    }

    /**
     * 只输出一次--------结果
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/world")
    @ResponseBody
    public void world(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        System.out.println("1-开始");
        final Continuation continuation = ContinuationSupport.getContinuation(request);
        //监听事件
        continuation.addContinuationListener(new ContinuationListener() {
            public void onTimeout(Continuation continuation) {
                System.out.println("1-1");
            } // 超时事件

            public void onComplete(Continuation continuation) {
                System.out.println("1-2");
            } // 完成事件
        });
        if (continuation.isExpired()) {
            System.out.println("2-超时");
            sendMyResultResponse((HttpServletResponse) continuation.getServletResponse(), "Timeout");
            //TODO 如果执行continuation.complete()就会出现异常，准确讲应该是警告
            //continuation.complete();
            return;
        }
        if (!continuation.isSuspended()) {
            //设置超时时间--毫秒--目录5秒
            continuation.setTimeout(5000L);
            continuation.suspend(response);
        }
        if (continuation.isSuspended()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //模拟程序处理时间--等待3秒
                        Thread.sleep(3000);
                        System.out.println("3-Thread");
                        //故意-抛出异常信息
                        if (1 == 1) throw new IllegalArgumentException("test");
//                    if(!continuation.isExpired()){
//                        sendMyResultResponse((HttpServletResponse) continuation.getServletResponse(), "test");
//                        continuation.complete();
//                    }
                    } catch (Exception e) {
                        // TODO 打印出所有的错误日志信息
                        e.printStackTrace();
                    } finally {
                        System.out.println("1-isResponseWrapped="+continuation.isResponseWrapped());
                        System.out.println("1-isExpired="+continuation.isExpired());
                        System.out.println("1-isSuspended="+continuation.isSuspended());
                        System.out.println("1-isResumed="+continuation.isResumed());
                        if (!continuation.isExpired()) {
                            try {
                                sendMyResultResponse((HttpServletResponse) continuation.getServletResponse(), "test");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //无论是否抛出异常信息--都必须关闭请求
                            System.out.println("========================================");
                            System.out.println("2-isResponseWrapped="+continuation.isResponseWrapped());
                            System.out.println("2-isExpired="+continuation.isExpired());
                            System.out.println("2-isSuspended="+continuation.isSuspended());
                            System.out.println("2-isResumed="+continuation.isResumed());
                            //todo continuation.complete();必须是没有超时的才可以关闭
                            if(!continuation.isExpired()){
                                continuation.complete();
                            }
                            System.out.println("/------------------------------------------------/");
                        }
                    }

                }
            }).start();
        }
    }

    private void sendMyResultResponse(HttpServletResponse response,
                                      Object results) throws IOException {
        //response.setContentType("text/html");
        response.getWriter().write("results:" + results);
        response.getWriter().flush();

    }
}
