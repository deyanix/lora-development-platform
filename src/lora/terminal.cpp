#include "terminal.h"

LoRaTerminalClass LoRaTerminal;

void LoRaTerminalClass::read() {
    if (isTimeout()) {
        clear();
    }

    while (Serial.available() > 0) {
        lastActivity = millis();

        int value = Serial.read();
        if (bufferRequest > 0) {
            buffer[bufferSize++] = value;
            bufferRequest--;
            if (bufferRequest == 0) {
                bufferFinished = true;
                return;
            }
        } else if (value != '\n' && value != '\r') {
            buffer[bufferSize++] = value;
            if (value == ',') {
                return;
            }
        } else {
            bufferFinished = true;
            return;
        }

        if (bufferSize == LORA_TERMINAL_BUFFER_CAPACITY) {
            clear();
        }
    }
}

void LoRaTerminalClass::request(size_t len) {
    bufferRequest = len;
    bufferFinished = false;
}

void LoRaTerminalClass::clear() {
    if (bufferSize == 0)
        return;

    memset(buffer, 0, LORA_TERMINAL_BUFFER_CAPACITY);
    bufferSize = 0;
    bufferRequest = 0;
    bufferFinished = false;
}

bool LoRaTerminalClass::isTimeout() const {
    uint32_t now = millis();
    uint32_t time;
    if (now >= lastActivity) {
        time = now - lastActivity;
    } else {
        time = (UINT32_MAX - lastActivity) + now;
    }

    return time > LORA_TERMINAL_BUFFER_TIMEOUT;
}

String LoRaTerminalClass::getBuffer() const {
    return buffer;
}

StringReader LoRaTerminalClass::getReader() const {
    return StringReader(buffer);
}

bool LoRaTerminalClass::isFinished() const {
    return bufferFinished;
}
