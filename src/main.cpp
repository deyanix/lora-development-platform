#include <Arduino.h>
#include "lora/terminal.h"

void setup() {
    Serial.begin(115200);
    LoRaNode.Init();
}


void loop() {
    LoRaTerminal.Read();
    delay(5);
}