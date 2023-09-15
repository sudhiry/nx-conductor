package com.netflix.conductor.queue.dao;

import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.dao.QueueDAO;

import java.util.List;
import java.util.Map;

public class RedisQueueDAO implements QueueDAO {

    @Override
    public void push(String queueName, String id, long offsetTimeInSecond) {

    }

    @Override
    public void push(String queueName, String id, int priority, long offsetTimeInSecond) {

    }

    @Override
    public void push(String queueName, List<Message> messages) {

    }

    @Override
    public boolean pushIfNotExists(String queueName, String id, long offsetTimeInSecond) {
        return false;
    }

    @Override
    public boolean pushIfNotExists(String queueName, String id, int priority, long offsetTimeInSecond) {
        return false;
    }

    @Override
    public List<String> pop(String queueName, int count, int timeout) {
        return null;
    }

    @Override
    public List<Message> pollMessages(String queueName, int count, int timeout) {
        return null;
    }

    @Override
    public void remove(String queueName, String messageId) {

    }

    @Override
    public int getSize(String queueName) {
        return 0;
    }

    @Override
    public boolean ack(String queueName, String messageId) {
        return false;
    }

    @Override
    public boolean setUnackTimeout(String queueName, String messageId, long unackTimeout) {
        return false;
    }

    @Override
    public void flush(String queueName) {

    }

    @Override
    public Map<String, Long> queuesDetail() {
        return null;
    }

    @Override
    public Map<String, Map<String, Map<String, Long>>> queuesDetailVerbose() {
        return null;
    }

    @Override
    public void processUnacks(String queueName) {
        QueueDAO.super.processUnacks(queueName);
    }

    @Override
    public boolean resetOffsetTime(String queueName, String id) {
        return false;
    }

    @Override
    public boolean postpone(String queueName, String messageId, int priority, long postponeDurationInSeconds) {
        return QueueDAO.super.postpone(queueName, messageId, priority, postponeDurationInSeconds);
    }

    @Override
    public boolean containsMessage(String queueName, String messageId) {
        return QueueDAO.super.containsMessage(queueName, messageId);
    }
}
