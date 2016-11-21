package org.trex.falcon.rpc.proxy;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.rpc.ChannelHandler;
import org.trex.falcon.rpc.Client;
import org.trex.falcon.rpc.logger.LogHelper;
import org.trex.falcon.rpc.model.Request;
import org.trex.falcon.rpc.model.Response;
import org.trex.falcon.rpc.session.Session;
import org.trex.falcon.rpc.session.SessionManager;
import org.trex.falcon.zookeeper.ChildListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RpcProxy implements InvocationHandler {

    private Class<?> service;
    private Response response;
    private Registry registry;
    private CountDownLatch latch = null;
    private List<URL> providerUrls = null;
    private int count = 0;

    public RpcProxy(Class<?> service, Registry registry) {
        this.service = service;
        this.registry = registry;
        // 初始化通讯录
        this.providerUrls = this.registry.getProviders(service);
        // 订阅
        this.registry.subscribe(this.service, new ChildListener() {
            @Override
            public void childChanged(String path, List<String> children) {
                LogHelper.getInstance().getLogger(RpcProxy.class).info("--------------------------------通讯地址变更-------------------------------");
                providerUrls.clear();
                for (String url : children) {
                    LogHelper.getInstance().getLogger(RpcProxy.class).info(url.toString());
                    providerUrls.add(new URL(url));
                }
                LogHelper.getInstance().getLogger(RpcProxy.class).info("--------------------------------------------------------------------------");
            }
        });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 负载均衡
        URL remoteAddress = this.election();

        LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------1、选举出来的提供者:" + remoteAddress.toString() + "----------------------------------");


        // 获取session
        Session session = SessionManager.getSession(remoteAddress);

        if (session == null || !session.isConnected()) { //需要重新创建session

            LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------2、需要重新创建session:" + (session == null) + "----------------------------------");


            //取消订阅
            if (session != null) {
                this.registry.unpublish(service, session.localUrl(), Registry.CONSUMER);


                LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------2.1、我他吗的要删除" + service.getName() + "    " + session.localUrl().toString() + "----------------------------------");


                session.close();
                SessionManager.removeSession(remoteAddress);
                this.providerUrls.remove(remoteAddress);


                LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------3、session是否被移除：" + (SessionManager.getSession(remoteAddress) == null) + "----------------------------------");

                LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------4、此时的通讯录----------------------------------");
                for (URL aaa : this.providerUrls) {
                    LogHelper.getInstance().getLogger(RpcProxy.class).info(aaa.toString());
                }
                LogHelper.getInstance().getLogger(RpcProxy.class).info("--------------------------------------------------------------------------");


                remoteAddress = election();
                LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------5、从新选举出来的提供者:" + remoteAddress.toString() + "----------------------------------");
            }

            session = this.createSession(remoteAddress);

            LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------6、从新创建的session是否成功:" + (session != null) + "----------------------------------");

            SessionManager.addSession(remoteAddress, session);

            LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------7、是否真正加入到sessionManager中：" + (SessionManager.getSession(remoteAddress) != null) + "----------------------------------");

            // 订阅
            this.registry.publish(service, session.localUrl(), Registry.CONSUMER);
        }

        // 发送请求
        Request request = new Request();
        request.setService(this.service.getName());
        request.setArgs(args);
        request.setMethod(method.getName());
        session.write(request);


        LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------7、数据已经发出----------------------------------");


        // 阻塞在此，直到得到响应
        this.latch = new CountDownLatch(1);
        this.latch.await();

        LogHelper.getInstance().getLogger(RpcProxy.class).info("----------------------------8、线程卡关已经放行----------------------------------");

        // 返回数据
        Object result = this.response.getReturnValue();
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
            LogHelper.getInstance().getLogger(RpcProxy.class).info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx创建session失败" + remoteAddress.toString() + "xxxxxxxxxxxx");
            e.printStackTrace();
        }
        return session;
    }

    private URL election() {
        if (this.providerUrls.size() > 0) {
            this.count++;
            int index = count % this.providerUrls.size();
            return this.providerUrls.get(index);
        }
        throw new RuntimeException("没有服务提供者...");
    }
}
