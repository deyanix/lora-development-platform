//
// Created by Kuba on 08.06.2025.
//

#ifndef RANDOMGENERATOR_H
#define RANDOMGENERATOR_H

#include <cstdlib> // For rand(), srand(), RAND_MAX
#include <cmath>   // For log(), sqrt(), cos(), M_PI
#include <ctime>   // For time()
#include "terminal.h"

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

enum class DistributionType {
    UNIFORM,
    NORMAL,
    EXPONENTIAL
};

/**
 * @brief A class for generating various types of random numbers.
 * Uses standard C library random functions.
 */
class RandomGenerator {
public:
    RandomGenerator();
    void setDistribution(DistributionType type);
    long getRandomValue(long min, long max);

private:
    DistributionType _currentDistribution;
    static long uniformRand(long min, long max);
    static long exponentialRandInt(double lambda, int min, int max);
    static double normalRand();
    static long normalRandInt(double mean, double stddev, long min, long max);
};

extern RandomGenerator g_randomGenerator;

#endif //RANDOMGENERATOR_H
