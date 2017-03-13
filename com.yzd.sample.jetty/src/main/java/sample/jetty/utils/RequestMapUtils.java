package sample.jetty.utils;

import org.eclipse.jetty.continuation.Continuation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zd.yao on 2017/3/13.
 */

public class RequestMapUtils {
    private static RequestMapUtils instance;
    private RequestMapUtils (){
    }
    public static RequestMapUtils getInstance(){    //对获取实例的方法进行同步
        if (instance == null){
            synchronized(RequestMapUtils.class){
                if (instance == null)
                    instance = new RequestMapUtils();
            }
        }
        return instance;
    }
    private final Map<String, Continuation> requestMaps = new ConcurrentHashMap<String, Continuation>();
    public void add(String key ,Continuation continuation){
        requestMaps.put(key,continuation);
    }
    public Continuation get(String key){
        Continuation continuation=requestMaps.get(key);
        remove(key);
        return continuation;
    }
    public int size(){
        return requestMaps.size();
    }
    public void remove(String key){
        requestMaps.remove(key);
    }
}