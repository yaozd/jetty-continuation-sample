package sample.jetty.web;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sample.jetty.utils.RequestMapUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by zd.yao on 2017/3/10.
 */
@Controller
public class T3Controller {
    String key = "1-2-3";
    String RequestMapUtilsKeyName = "RequestMapUtilsKey";

    /**
     * todo 测试--对请求进行挂起
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/t3")
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
            Object keyName = continuation.getAttribute(RequestMapUtilsKeyName);
            if (keyName != null) {
                System.out.println("keyName=" + keyName);
                RequestMapUtils.getInstance().remove(keyName.toString());
            }
            sendMyResultResponse((HttpServletResponse) continuation.getServletResponse(), "Timeout"+keyName);
            //TODO 如果执行continuation.complete()就会出现异常，准确讲应该是警告
            //continuation.complete();
            return;
        }
        String keyValue=UUID.randomUUID().toString();
        System.out.println("String keyValue=UUID.randomUUID().toString();" +keyValue);
        if (!continuation.isSuspended()) {
            //设置超时时间--毫秒--目录15秒
            continuation.setTimeout(15000L);
            //todo 在请求集合里的key的名字放到continuation方便在请求超时时删除
            //continuation.setAttribute(RequestMapUtilsKeyName, key);
            continuation.setAttribute(RequestMapUtilsKeyName, keyValue);
            continuation.suspend(response);
        }
        if (continuation.isSuspended()) {
            Object keyName = continuation.getAttribute(RequestMapUtilsKeyName);
            if(keyName!=null) RequestMapUtils.getInstance().add(keyName.toString(), continuation);
/*            System.out.println("1-isResponseWrapped=" + continuation.isResponseWrapped());
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

            }*/
        }
    }

    /**
     * todo 测试--唤醒挂起的请求
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/t3/notify")
    @ResponseBody
    public void t2(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        System.out.println("/t3/notify=start");
        System.out.println("/t3/notify-RequestMapUtils.getInstance().size()=" + RequestMapUtils.getInstance().size());
        Continuation continuation = RequestMapUtils.getInstance().get(key);
        if (continuation != null && continuation.isSuspended()) {
            RequestMapUtils.getInstance().add(key, continuation);
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

    /**
     * 请求集合的大小
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/t3/size")
    @ResponseBody
    public void t3(HttpServletRequest request, final HttpServletResponse response) throws IOException {

        int size = RequestMapUtils.getInstance().size();
        sendMyResultResponse(response, size);

    }

    private void sendMyResultResponse(HttpServletResponse response,
                                      Object results) throws IOException {
        //response.setContentType("text/html");
        response.getWriter().write("results:" + results);
        response.getWriter().flush();

    }
}
