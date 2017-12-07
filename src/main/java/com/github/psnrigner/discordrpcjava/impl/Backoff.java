package com.github.psnrigner.discordrpcjava.impl;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Backoff implementation
 */
public class Backoff
{
    private final long minAmount;
    private final long maxAmount;

    private final Random random;

    private long current;

    /**
     * Backoff constructor
     *
     * @param minAmount Minimum amount of time
     * @param maxAmount Maximum amount of time
     */
    public Backoff(long minAmount, long maxAmount)
    {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;

        this.random = new SecureRandom();

        this.current = minAmount;
    }

    /**
     * Reset the backoff
     */
    public void reset()
    {
        this.current = this.minAmount;
    }

    /**
     * Calculate the next delay based on previous results
     *
     * @return Next Delay
     */
    public long nextDelay()
    {
        long delay = (long)((double)this.current * 2.0D * this.random.nextDouble());

        this.current = Math.min(this.current + delay, this.maxAmount);

        return this.current;
    }
}
