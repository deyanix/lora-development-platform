//
// Created by Kuba on 08.06.2025.
//

#ifndef RANDOMGENERATOR_H
#define RANDOMGENERATOR_H

#include <cstdlib> // For rand(), srand(), RAND_MAX
#include <cmath>   // For log(), sqrt(), cos(), M_PI
#include <ctime>   // For time()
#include "terminal.h"

// Define M_PI if it's not already defined (e.g., on some compilers or platforms)
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

/**
 * @brief A class for generating various types of random numbers.
 * Uses standard C library random functions.
 */
class RandomGenerator {
public:
    /**
     * @brief Constructor for RandomGenerator.
     * Seeds the random number generator using the current time.
     * Note: For Arduino, you might want to use analogRead(0) for better seeding.
     */
    RandomGenerator();

    /**
     * @brief Generates a uniformly distributed random long integer within a specified range.
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return A random long integer between min and max (inclusive).
     */
    static long uniformRand(long min, long max);

    /**
     * @brief Generates an exponentially distributed random long integer.
     * @param lambda The rate parameter of the exponential distribution.
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return An exponentially distributed random long integer, clamped within [min, max].
     */
    static long exponentialRandInt(double lambda, int min, int max);

    /**
     * @brief Generates a normally (Gaussian) distributed random double using the Box-Muller transform.
     * @return A random double from a standard normal distribution (mean=0, stddev=1).
     */
    static double normalRand();

    /**
     * @brief Generates a normally (Gaussian) distributed random long integer within a specified range.
     * @param mean The mean of the normal distribution.
     * @param stddev The standard deviation of the normal distribution.
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return A normally distributed random long integer, clamped within [min, max].
     */
    static long normalRandInt(double mean, double stddev, long min, long max);
};

#endif //RANDOMGENERATOR_H
