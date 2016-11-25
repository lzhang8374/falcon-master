package org.trex.falcon.rpc.proxy;

import io.netty.channel.Channel;
import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.rpc.ChannelHandler;
import org.trex.falcon.rpc.Client;
import org.trex.falcon.rpc.loadbalance.LoadBalance;
import org.trex.falcon.rpc.loadbalance.RoundRobinLoadBalance;
import org.trex.falcon.rpc.model.Request;
import org.trex.falcon.rpc.model.Response;
import org.trex.falcon.rpc.monitor.Monitor;
import org.trex.falcon.rpc.session.Session;
import org.trex.falcon.rpc.session.SessionManager;
import org.trex.falcon.zookeeper.ChildListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RpcProxy implements InvocationHandler {

    private Class<?> service;
    private Response response;
    private Registry registry;
    private CountDownLatch latch = null;
    private List<URL> providerUrls = null;

    private LoadBalance loadBalance = null;

    public RpcProxy(Class<?> service, Registry registry) {
        this.service = service;
        this.registry = registry;
        this.loadBalance = new RoundRobinLoadBalance();
        // 初始化通讯录
        this.providerUrls = this.registry.getProviders(service);
        // 订阅
        this.registry.subscribe(this.service, new ChildListener() {
            @Override
            public void childChanged(String path, Map<String, String> children) {
                providerUrls.clear();
                for (String key : children.keySet()) {
                    URL url = new URL(key);
                    url.setPriority(Integer.parseInt(children.get(key)));
                    providerUrls.add(url);
                }

                System.out.println("------------------------通讯录地址变更------------------------------");
                for (URL url : providerUrls) {
                    System.out.println(url.toString() + "----------" + url.getPriority());
                }
            }
        });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 负载均衡
        URL remoteAddress = this.loadBalance.doSelect(this.providerUrls);

        // 获取session
        Session session = SessionManager.getSession(remoteAddress);

        if (session == null || !session.isConnected()) { //需要重新创建session
            //取消订阅
            if (session != null) {
                this.registry.unpublish(service, session.localUrl(), Registry.CONSUMER);
                session.close();
                SessionManager.removeSession(remoteAddress);
                this.providerUrls.remove(remoteAddress);
                remoteAddress = this.loadBalance.doSelect(this.providerUrls);
            }
            session = SessionManager.getSession(remoteAddress);
            if (session == null) {
                session = this.createSession(remoteAddress);
                SessionManager.addSession(remoteAddress, session);
                this.registry.publish(service, session.localUrl(), Registry.CONSUMER);
            }

        }

        // 发送请求
        Request request = new Request();
        request.setService(this.service.getName());
        request.setArgs(args);
        request.setMethod(method.getName());
        session.write(request);

        // 阻塞在此，直到得到响应
        this.latch = new CountDownLatch(1);
        this.latch.await();

        // 返回数据
        Object result = this.response.getReturnValue();

        // 监控中心
        Monitor.getInstance().commit(request, response, session.localUrl(), session.remoteUrl(), 1000l);

        return result;
    }

    private Session createSession(final URL remoteAddress) {
        Session session = null;
        Client client = Client.getInstance(new ChannelHandler() {
            @Override
            public void received(Channel channel, Response message) {
                response = message;
                //当前线程调用此方法，则计数减一
                latch.countDown();
            }

            @Override
            public void exceptionCaught(Channel channel) {
                System.out.println("------------------------------error0---------------------------------");
                response = new Response(500, null);
                //当前线程调用此方法，则计数减一
                latch.countDown();
            }

            @Override
            public void channelInactive(Channel channel) {
                System.out.println("------------------------------error1---------------------------------");
                response = new Response(500, null);
                //当前线程调用此方法，则计数减一
                latch.countDown();
            }
        });

        try {
            Channel channel = client.connect(remoteAddress);
            session = new Session(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return session;
    }
}
