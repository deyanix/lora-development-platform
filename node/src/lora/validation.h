//
// Created by Kuba on 10.06.2025.
//

#ifndef VALIDATION_H
#define VALIDATION_H

#include "terminal.h"

bool validateStandardMessage(const uint8_t* message);
bool validateAckMessage(const uint8_t* message, uint64_t chipID);

#endif //VALIDATION_H
