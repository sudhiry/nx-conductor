package com.netflix.conductor.lock.config;

import com.netflix.conductor.core.sync.Lock;
import com.netflix.conductor.lock.LockImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableConfigurationProperties(LockProperties.class)
@Slf4j
@ConditionalOnProperty(name = "conductor.workflow-execution-lock.type", havingValue = "redis")
public class LockConfiguration {

    @Bean
    public Redisson getRedisson(LockProperties properties) {
        LockProperties.REDIS_SERVER_TYPE redisServerType;
        try {
            redisServerType = LockProperties.REDIS_SERVER_TYPE.valueOf(properties.getServerType());
        } catch (IllegalArgumentException ie) {
            final String message =
                    "Invalid Redis server type: "
                            + properties.getServerType()
                            + ", supported values are: "
                            + Arrays.toString(LockProperties.REDIS_SERVER_TYPE.values());
            log.error(message);
            throw new RuntimeException(message, ie);
        }
        String redisServerAddress = properties.getServerAddress();
        String redisServerPassword = properties.getServerPassword();
        String masterName = properties.getServerMasterName();

        Config redisConfig = new Config();
        if (properties.getNumNettyThreads() != null && properties.getNumNettyThreads() > 0) {
            redisConfig.setNettyThreads(properties.getNumNettyThreads());
        }

        int connectionTimeout = 10000;
        switch (redisServerType) {
            case SINGLE:
                log.info("Setting up Redis Single Server for RedisLockConfiguration");
                redisConfig
                        .useSingleServer()
                        .setAddress(redisServerAddress)
                        .setPassword(redisServerPassword)
                        .setTimeout(connectionTimeout);
                break;
            case CLUSTER:
                log.info("Setting up Redis Cluster for RedisLockConfiguration");
                redisConfig
                        .useClusterServers()
                        .setScanInterval(2000) // cluster state scan interval in milliseconds
                        .addNodeAddress(redisServerAddress.split(","))
                        .setPassword(redisServerPassword)
                        .setTimeout(connectionTimeout)
                        .setSlaveConnectionMinimumIdleSize(
                                properties.getClusterReplicaConnectionMinIdleSize())
                        .setSlaveConnectionPoolSize(
                                properties.getClusterReplicaConnectionPoolSize())
                        .setMasterConnectionMinimumIdleSize(
                                properties.getClusterPrimaryConnectionMinIdleSize())
                        .setMasterConnectionPoolSize(
                                properties.getClusterPrimaryConnectionPoolSize());
                break;
            case SENTINEL:
                log.info("Setting up Redis Sentinel Servers for RedisLockConfiguration");
                redisConfig
                        .useSentinelServers()
                        .setScanInterval(2000)
                        .setMasterName(masterName)
                        .addSentinelAddress(redisServerAddress)
                        .setPassword(redisServerPassword)
                        .setTimeout(connectionTimeout);
                break;
        }

        return (Redisson) Redisson.create(redisConfig);
    }

    @Bean
    public Lock provideLock(Redisson redisson, LockProperties properties) {
        return new LockImpl(properties, redisson);
    }

}
