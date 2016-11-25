package org.trex.falcon.rpc.monitor;

import io.netty.channel.Channel;
import org.trex.falcon.common.MonitorService;
import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.registry.ZookeeperRegistry;
import org.trex.falcon.rpc.model.Request;
import org.trex.falcon.rpc.model.Response;
import org.trex.falcon.rpc.session.Session;
import org.trex.falcon.zookeeper.ChildListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Monitor {

    private Session session;
    private URL providerUrl;
    private Registry registry;
    private Class service = MonitorService.class;
    private static Monitor instance;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Monitor getInstance() {
        if (instance == null) {
            instance = new Monitor();
        }
        return instance;
    }

    private Monitor() {
        // 初始化注册中心
        this.registry = new ZookeeperRegistry(new URL("127.0.0.1", 2181));

        //初始化地址列表
        List<URL> urls = this.registry.getProviders(this.service);
        if (urls.size() == 1) {
            this.providerUrl = urls.get(0);
        }

        // 订阅
        registry.subscribe(this.service, new ChildListener() {
            @Override
            public void childChanged(String path, Map<String, String> children) {
                System.out.println("-----------------------------监控中心地址变更-------------------------------------");
                providerUrl = null;
                for (String url : children.keySet()) {
                    providerUrl = new URL(url);
                }
                session = createSession(providerUrl);
            }
        });

        this.session = createSession(this.providerUrl);
    }

    private Session createSession(final URL remoteAddress) {
        if (remoteAddress != null) {
            MonitorClient client = MonitorClient.getInstance();
            Session session = null;
            try {
                Channel channel = client.connect(remoteAddress);
                session = new Session(channel);
                if (session != null) {
                    this.registry.publish(this.service, session.localUrl(), Registry.CONSUMER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return session;
        } else {
            return null;
        }
    }

    public void commit(Request request, Response response, URL localUrl, URL remoteUrl, Long executeTime) {
        try {
            if (this.session != null && this.session.isConnected()) {
                StringBuilder sb = new StringBuilder();
                sb.append(sdf.format(new Date()));
                sb.append("\t");
                sb.append(localUrl.toString());
                sb.append("\t");
                sb.append(remoteUrl.toString());
                sb.append("\t");
                sb.append(request.getService());
                sb.append("\t");
                sb.append(request.getMethod());
                sb.append("\t");
                sb.append(response.getCode());
                sb.append("\t");
                sb.append(executeTime);
                this.session.write(sb.toString());
            } else if (this.session != null) {
                this.registry.unpublish(this.service, this.session.localUrl(), Registry.CONSUMER);
                this.session.close();
                this.session = null;
            }
        } catch (Exception e) {

        }
    }
}