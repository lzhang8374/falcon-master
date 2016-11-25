package org.trex.falcon.monitor.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Component
public class RedisHelper {

    @Autowired
    private ShardedJedisPool shardedJedisPool;


    public void put(String key, String value) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = this.shardedJedisPool.getResource();
            shardedJedis.set(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            returnBrokenResource(shardedJedis);
        } finally {
            returnResource(shardedJedis);
        }
    }


    private void returnBrokenResource(ShardedJedis shardedJedis) {
        try {
            this.shardedJedisPool.returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnResource(ShardedJedis shardedJedis) {
        try {
            this.shardedJedisPool.returnResource(shardedJedis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
