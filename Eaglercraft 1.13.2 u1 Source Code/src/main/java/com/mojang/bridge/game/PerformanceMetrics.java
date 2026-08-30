package com.mojang.bridge.game;

/**
 * A snapshot of performance metrics from the game.
 *
 * <p>"Time", used for most methods here, is defined as the amount of milliseconds it took to perform one frame or tick.</p>
 *
 * <p>Hypothetically, {@code 1000 / getAverageTime()} can be used to guess the frames-per-second. However, that unit
 * of measurement is fairly flawed and won't accurately represent lag spikes, vsync, etc. It is recommended to stick
 * to min/avg/max times, instead.</p>
 */
public interface PerformanceMetrics {
    /**
     * Gets the lowest time in the latest sample data.
     *
     * @return Smallest recent time
     */
    int getMinTime();

    /**
     * Gets the maximum time in the latest sample data.
     *
     * @return Largest recent time
     */
    int getMaxTime();

    /**
     * Gets the average time in the latest sample data.
     *
     * @return Average recent time
     */
    int getAverageTime();

    /**
     * Gets the amount of samples used for these metrics.
     *
     * @return Sample count
     */
    int getSampleCount();
}
