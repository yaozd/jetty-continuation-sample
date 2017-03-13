package sample.jetty.web;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/9.
 */
@Controller
public class T2Controller {
    @GetMapping("/t2")
    @ResponseBody
    public void t1(HttpServletRequest request, final HttpServletResponse response) throws IOException {
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
            //设置超时时间--毫秒--目录15秒
            continuation.setTimeout(15000L);
            continuation.suspend(response);
        }
        if (continuation.isSuspended()) {
            System.out.println("1-isResponseWrapped=" + continuation.isResponseWrapped());
            System.out.println("1-isExpired=" + continuation.isExpired());
            System.out.println("1-isSuspended=" + continuation.isSuspended());
            System.out.println("1-isResumed=" + continuation.isResumed());
            if (!continuation.isExpired()) {

                sendMyResultResponse((HttpServletResponse) continuation.getServletResponse(), "11111");

                //无论是否抛出异常信息--都必须关闭请求
                System.out.println("========================================");
                System.out.println("2-isResponseWrapped=" + continuation.isResponseWrapped());
                System.out.println("2-isExpired=" + continuation.isExpired());
                System.out.println("2-isSuspended=" + continuation.isSuspended());
                System.out.println("2-isResumed=" + continuation.isResumed());
                //todo continuation.complete();必须是没有超时的才可以关闭
                if (!continuation.isExpired() && continuation.isSuspended()) {
                    continuation.complete();
                }
                System.out.println("/------------------------------------------------/");

            }
        }
    }

    private void sendMyResultResponse(HttpServletResponse response,
                                      Object results) throws IOException {
        //response.setContentType("text/html");
        response.getWriter().write("results:" + results);
        response.getWriter().flush();

    }
}

