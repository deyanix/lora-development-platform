//
// Created by Kuba on 08.06.2025.
//

#include "RandomGenerator.h"

RandomGenerator g_randomGenerator;

RandomGenerator::RandomGenerator()
{
    srand(time(NULL));
    _currentDistribution = DistributionType::UNIFORM;
}

long RandomGenerator::uniformRand(long min, long max)
{
    if (min >= max)
    {
        return min;
    }
    return min + (long)((double)rand() / (RAND_MAX + 1.0) * (max - min + 1));
}

long RandomGenerator::exponentialRandInt(double lambda, int min, int max)
{
    if (min >= max)
    {
        return min;
    }
    double range_size = max - min;
    double u;
    double x;

    do {
        do {
            u = static_cast<double>(rand()) / RAND_MAX;
        } while (u == 0.0);

        x = -log(1.0 - u) / lambda;
    } while (x > range_size);

    return min + static_cast<long>(x + 0.5);
}

double RandomGenerator::normalRand()
{
    double u1, u2;
    do {
        u1 = (double)rand() / RAND_MAX;
        u2 = (double)rand() / RAND_MAX;
    } while (u1 <= 1e-10);

    return sqrt(-2.0 * log(u1)) * cos(2 * M_PI * u2);
}

long RandomGenerator::normalRandInt(double mean, double stddev, long min, long max){
    if (min >= max)
    {
        return min;
    }
    double val = mean + stddev * RandomGenerator::normalRand();

    if (val < min) val = min;
    if (val > max) val = max;

    return (long)(val + 0.5);
}

long RandomGenerator::getRandomValue(long min, long max)
{
    switch (_currentDistribution) {
    case DistributionType::UNIFORM:
        return uniformRand(min, max);
    case DistributionType::NORMAL:
        return normalRandInt(max/2, max/6, min, max);
    case DistributionType::EXPONENTIAL:
        return exponentialRandInt(5.0 / max, min, max);
    default:
        return uniformRand(min, max);
    }
}

void RandomGenerator::setDistribution(DistributionType distribution)
{
    _currentDistribution = distribution;
}

DistributionType RandomGenerator::getDistribution()
{
    return _currentDistribution;
}