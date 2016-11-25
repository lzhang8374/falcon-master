package org.trex.falcon.rpc.loadbalance;

import org.trex.falcon.common.URL;

import java.util.List;

public interface LoadBalance {

    URL doSelect(List<URL> providerUrls);
}
