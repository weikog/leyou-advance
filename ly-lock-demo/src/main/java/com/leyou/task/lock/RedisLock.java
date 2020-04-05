package com.leyou.task.lock;

/**
 * @author 黑马程序员
 */
public interface RedisLock {
    boolean lock(long releaseTime);
    void unlock();
}