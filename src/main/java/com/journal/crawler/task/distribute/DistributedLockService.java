//package com.journal.crawler.task.distribute;
//
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//public class DistributedLockService {
//    private final RedissonClient redissonClient;
//
//    @Autowired
//    public DistributedLockService(RedissonClient redissonClient) {
//        this.redissonClient = redissonClient;
//    }
//
//    /**
//     * 尝试获取分布式锁
//     *
//     * @param lockKey 锁的唯一标识
//     * @param waitTime 等待获取锁的最大时间
//     * @param leaseTime 锁的持有时间
//     * @param timeUnit 时间单位
//     * @return 是否成功获取锁
//     */
//    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
//        try {
//            RLock lock = redissonClient.getLock(lockKey);
//            return lock.tryLock(waitTime, leaseTime, timeUnit);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return false;
//        }
//    }
//
//    /**
//     * 简化版获取锁方法，默认等待时间和锁定时间
//     *
//     * @param lockKey 锁的唯一标识
//     * @param leaseTime 锁的持有时间
//     * @param timeUnit 时间单位
//     * @return 是否成功获取锁
//     */
//    public boolean tryLock(String lockKey, long leaseTime, TimeUnit timeUnit) {
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            return lock.tryLock(leaseTime, timeUnit);
//        } catch (InterruptedException e) {
//            log.error("获取redis锁出现异常，{}", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 释放锁
//     *
//     * @param lockKey 需要释放的锁标识
//     */
//    public void unlock(String lockKey) {
//        RLock lock = redissonClient.getLock(lockKey);
//        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 强制释放锁（谨慎使用）
//     *
//     * @param lockKey 需要强制释放的锁标识
//     */
//    public void forceUnlock(String lockKey) {
//        RLock lock = redissonClient.getLock(lockKey);
//        lock.forceUnlock();
//    }
//}
