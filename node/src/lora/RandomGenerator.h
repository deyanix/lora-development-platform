//
// Created by Kuba on 08.06.2025.
//

#ifndef RANDOMGENERATOR_H
#define RANDOMGENERATOR_H

#include <Arduino.h> // For random(), millis(), etc.
#include <math.h>    // For log(), sqrt(), cos(), PI

class RandomGenerator {
public:
    RandomGenerator();

    // Generates a uniform random integer between min (inclusive) and max (exclusive)
    long generateUniform(long minVal, long maxVal);

    // Generates a random float following a normal (Gaussian) distribution
    float generateNormal(float mean, float stdDev);

private:
    // Variables for Box-Muller transform to avoid re-calculating two random numbers
    // if only one is needed per call.
    bool _hasSpare;
    float _spare;
};

#endif //RANDOMGENERATOR_H
