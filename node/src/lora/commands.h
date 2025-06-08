#ifndef LORA_COMMANDS_H
#define LORA_COMMANDS_H

#include "terminal.h"

bool HandleSetFrequency(String args);
String HandleGetFrequency(String args);

bool HandleSetCrc(String args);
String HandleGetCrc(String args);

bool HandleSetIqInverted(String args);
String HandleGetIqInverted(String args);

#endif
