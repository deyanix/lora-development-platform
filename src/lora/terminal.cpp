#include "terminal.h"

LoRaTerminalClass LoRaTerminal;

void LoRaTerminalClass::Read() {
    if (IsTimeout()) {
        ResetBuffer();
    }

    while (Serial.available() > 0) {
        lastActivity = millis();

        int value = Serial.read();
        if (bufferRequest > 0) {
            buffer[bufferSize++] = value;
            bufferRequest--;
        } else if (value != '\n' && value != '\r') {
            buffer[bufferSize++] = value;
        } else {
            Handle();
        }

        if (bufferSize == LORA_TERMINAL_BUFFER_CAPACITY) {
            Handle();
            ResetBuffer();
        }
    }
}

void LoRaTerminalClass::Handle() {

    if (bufferSize == 0 || buffer[0] != '+')
        return;

    HandleCommand("FREQ=", HandleSetFrequency);
    HandleCommand("FREQ?", HandleGetFrequency);
    HandleCommand("CRC=", HandleSetCrc);
    HandleCommand("CRC?", HandleGetCrc);
    HandleCommand("IQINV=", HandleSetIqInverted);
    HandleCommand("IQINV?", HandleGetIqInverted);
}

void LoRaTerminalClass::HandleCommand(const char *cmd, LoRaTerminalHandler handler) {
    size_t cmdLen = strlen(cmd);
    if (strncmp(cmd, buffer+1, cmdLen) == 0) {
        bool result = handler(String(buffer+cmdLen+1));

        Serial.print(cmd);
        if (result) {
            Serial.println("OK");
        } else {
            Serial.println("ERROR");
        }
    }
}

void LoRaTerminalClass::ResetBuffer() {
    if (bufferSize == 0)
        return;

    memset(buffer, 0, LORA_TERMINAL_BUFFER_CAPACITY);
    bufferSize = 0;
    bufferRequest = 0;
}

bool LoRaTerminalClass::IsTimeout() {
    uint32_t now = millis();
    uint32_t time;
    if (now >= lastActivity) {
        time = now - lastActivity;
    } else {
        time = (UINT32_MAX - lastActivity) + now;
    }

    return time > LORA_TERMINAL_BUFFER_TIMEOUT;
}
