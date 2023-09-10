package com.netflix.conductor.lock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("conductor.lock")
@Data
public class LockProperties {
    public enum REDIS_SERVER_TYPE {
        SINGLE,
        CLUSTER,
        SENTINEL
    }

    /** The redis server configuration to be used. */
    private String serverType = REDIS_SERVER_TYPE.SINGLE.toString();

    private String serverAddress = "redis://127.0.0.1:6379";

    /** The password for redis authentication */
    private String serverPassword = null;

    /** The master server name used by Redis Sentinel servers and master change monitoring task */
    private String serverMasterName = "master";

    /** The namespace to use to prepend keys used for locking in redis */
    private String namespace = "";

    /** The number of natty threads to use */
    private Integer numNettyThreads;

    /** If using Cluster Mode, you can use this to set num of min idle connections for replica */
    private int clusterReplicaConnectionMinIdleSize = 24;

    /** If using Cluster Mode, you can use this to set num of min idle connections for replica */
    private int clusterReplicaConnectionPoolSize = 64;

    /** If using Cluster Mode, you can use this to set num of min idle connections for replica */
    private int clusterPrimaryConnectionMinIdleSize = 24;

    /** If using Cluster Mode, you can use this to set num of min idle connections for replica */
    private int clusterPrimaryConnectionPoolSize = 64;

    /**
     * Enable to optionally continue without a lock to not block executions until the locking service
     * becomes available
     */
    private boolean ignoreLockingExceptions = false;

}
