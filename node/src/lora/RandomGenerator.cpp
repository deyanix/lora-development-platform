//
// Created by Kuba on 08.06.2025.
//

#include "RandomGenerator.h"

#include "RandomGenerator.h"

RandomGenerator::RandomGenerator() : _hasSpare(false), _spare(0.0f) {
    // Note: The global randomSeed(analogRead(0)) in lora_controller.ino's setup()
    // seeds the underlying Arduino random number generator used by random().
    // We don't need to re-seed here.
}

long RandomGenerator::generateUniform(long minVal, long maxVal) {
    if (minVal >= maxVal) {
        // Handle invalid range, e.g., return minVal or an error indicator
        return minVal;
    }
    // random(min, max) generates a number from min (inclusive) up to max (exclusive)
    return random(minVal, maxVal);
}

// Implements the Box-Muller transform to generate normally distributed random numbers.
// This function generates two standard normal variates (mean=0, stdDev=1) from two
// uniform variates. It stores one ("spare") for the next call if only one is needed.
float RandomGenerator::generateNormal(float mean, float stdDev) {
    if (_hasSpare) {
        _hasSpare = false;
        return _spare * stdDev + mean;
    } else {
        float u1, u2;
        // Generate two uniform random numbers between 0 and 1 (exclusive)
        // Ensure they are not zero to avoid log(0)
        do {
            u1 = (float)random(1, 1000000) / 1000000.0f; // 0.000001 to 0.999999
            u2 = (float)random(1, 1000000) / 1000000.0f;
        } while (u1 == 0.0f || u2 == 0.0f); // Make sure they are not zero

        float mag = std::sqrt(-2.0f * std::log(u1));
        float z1 = mag * std::cos(2.0f * PI * u2);
        float z2 = mag * std::sin(2.0f * PI * u2);

        _spare = z2;
        _hasSpare = true;

        return z1 * stdDev + mean;
    }
}