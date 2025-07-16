#include "stopwatch.h"

Stopwatch::Stopwatch(uint32_t initialTimeout)
        : startTime(millis()),
          timeout(initialTimeout) {}

void Stopwatch::reset() {
    startTime = millis();
}

void Stopwatch::setTimeout(uint32_t newTimeout) {
    timeout = newTimeout;
}

uint32_t Stopwatch::getElapsed() const {
    uint32_t now = millis();
    uint32_t elapsedTime;

    if (now >= startTime) {
        elapsedTime = now - startTime;
    } else {
        elapsedTime = (UINT32_MAX - startTime) + now;
    }
    return elapsedTime;
}

bool Stopwatch::isTimeout() const {
    if (timeout == 0) {
        return false;
    }
    return getElapsed() > timeout;
}
