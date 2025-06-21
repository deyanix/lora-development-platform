#ifndef LORA_TERMINAL_H
#define LORA_TERMINAL_H

#include <Arduino.h>
#include "node.h"
//#include "commands.h"
#include "reader.h"

#define LORA_TERMINAL_BUFFER_CAPACITY (256)
#define LORA_TERMINAL_BUFFER_TIMEOUT (250)

class LoRaTerminalClass {
    uint32_t lastActivity = 0;
    char buffer[LORA_TERMINAL_BUFFER_CAPACITY] = {};
    size_t bufferSize = 0;
    size_t bufferRequest = 0;
    bool bufferFinished = false;

    bool isTimeout() const;
public:
    void read();
    void request(size_t);
    void clear();

    String getBuffer() const;
    StringReader getReader() const;
    bool isFinished() const;
};

extern LoRaTerminalClass LoRaTerminal;

#endif
