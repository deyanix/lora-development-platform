#ifndef LORA_TERMINAL_H
#define LORA_TERMINAL_H

#include <Arduino.h>
#include "node.h"
#include "commands.h"

#define LORA_TERMINAL_BUFFER_CAPACITY (256)
#define LORA_TERMINAL_BUFFER_TIMEOUT (5000)

typedef bool (*LoRaTerminalHandler)(String);
typedef String (*LoRaTerminalActionHandler)(String);

class LoRaTerminalClass {
    uint32_t lastActivity = 0;

    void ResetBuffer();
    bool IsTimeout();

    void HandleCommand(const char* cmd, LoRaTerminalHandler handler);
public:
    char buffer[LORA_TERMINAL_BUFFER_CAPACITY];
    size_t bufferSize = 0;
    size_t bufferRequest = 0;
    void Read();
    void Handle();
};

extern LoRaTerminalClass LoRaTerminal;

#endif
