#include <Arduino.h>

#include "lora/terminal.h"
#include "lora/reader.h"
#include "lora/RandomGenerator.h"
#include "lora/CharPointerQueue.h"

#ifndef LoraWan_RGB
#define LoraWan_RGB 0
#endif

#define LoraWan_RGB 1

uint64_t chipID = getID();

CharPointerQueue serialPrintQueue;

void OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t snr) {
    Serial.print("RX=DONE,");
    Serial.printf("%d,%d,%d,", rssi, snr, size);
    Serial.write(payload, size);
    Serial.println();

    payload[size] = '\0';

    LoRaNode.OnRxDone(payload, size, rssi, snr);
}

void OnRxTimeout() {
    serialPrintQueue.enqueue("RX=TIMEOUT");
    LoRaNode.OnRxTimeout();
}

void OnRxError() {
    serialPrintQueue.enqueue("RX=ERROR");
    LoRaNode.OnRxError();
}

void OnTxDone() {
    uint32_t duration = LoRaNode.SendStopwatch.getElapsed();

    Serial.print("TX=DONE,");
    Serial.println(duration);
    LoRaNode.OnTxDone();
}

void OnTxTimeout() {
    serialPrintQueue.enqueue("TX=TIMEOUT");
    LoRaNode.OnTxTimeout();
}

void processTerminal() {
    StringReader reader = LoRaTerminal.getReader();

    if (!reader.with('+')) {
        LoRaTerminal.clear();
        return;
    }

    if (reader.with("TX=")) {
        size_t len = reader.until(',').toInt();
        String payload = reader.untilEnd();
        len -= payload.length();
        if (len > 0) {
            LoRaTerminal.request(len);
        } else {
            Serial.print("TX=OK,");
            Serial.println(payload.length());
            LoRaNode.Send((uint8_t*) payload.c_str(), payload.length());
            LoRaTerminal.clear();
        }
    } else if (LoRaTerminal.isFinished()) {
        if (reader.with("ID")) {
            if (reader.with('?')) {
                Serial.print("ID=");
                Serial.printf("%04X%08X", (uint32_t) (chipID>>32), (uint32_t) chipID);
                Serial.println();
            }
        } else if (reader.with("FRQ")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 864000000 && value <= 870000000) {
                    LoRaNode.Params.Frequency = (uint32_t)value;
                    serialPrintQueue.enqueue("FRQ=OK");
                } else {
                    serialPrintQueue.enqueue("FRQ=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("FRQ=");
                Serial.println(LoRaNode.Params.Frequency);
            }
        } else if (reader.with("BW")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 3) {
                    LoRaNode.Params.Bandwidth = (uint32_t)value;
                    serialPrintQueue.enqueue("BW=OK");
                } else {
                    serialPrintQueue.enqueue("BW=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("BW=");
                Serial.println(LoRaNode.Params.Bandwidth);
            }
        } else if (reader.with("SF")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 6 && value <= 12) {
                    LoRaNode.Params.SpreadingFactor = (uint32_t)value;
                    serialPrintQueue.enqueue("SF=OK");
                } else {
                    serialPrintQueue.enqueue("SF=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("SF=");
                Serial.println(LoRaNode.Params.SpreadingFactor);
            }
        } else if (reader.with("PWR")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= -3 && value <= 22) {
                    LoRaNode.Params.Power = (int8_t)value;
                    serialPrintQueue.enqueue("PWR=OK");
                } else {
                    serialPrintQueue.enqueue("PWR=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("PWR=");
                Serial.println(LoRaNode.Params.Power);
            }
        } else if (reader.with("CRT")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 1 && value <= 4) {
                    LoRaNode.Params.CodeRate = (uint8_t)value;
                    serialPrintQueue.enqueue("CRT=OK");
                } else {
                    serialPrintQueue.enqueue("CRT=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("CRT=");
                Serial.println(LoRaNode.Params.CodeRate);
            }
        } else if (reader.with("PRLEN")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 1 && value <= 8) {
                    LoRaNode.Params.PreambleLength = (uint16_t)value;
                    serialPrintQueue.enqueue("PRLEN=OK");
                } else {
                    serialPrintQueue.enqueue("PRLEN=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("PRLEN=");
                Serial.println(LoRaNode.Params.PreambleLength);
            }
        } else if (reader.with("PYLEN")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 255) {
                    LoRaNode.Params.PayloadLength = (uint8_t)value;
                    serialPrintQueue.enqueue("PYLEN=OK");
                } else {
                    serialPrintQueue.enqueue("PYLEN=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("PYLEN=");
                Serial.println(LoRaNode.Params.PayloadLength);
            }
        } else if (reader.with("CRC")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.Params.EnableCrc = (bool)value;
                    serialPrintQueue.enqueue("CRC=OK");
                } else {
                    serialPrintQueue.enqueue("CRC=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("CRC=");
                Serial.println(LoRaNode.Params.EnableCrc ? 1 : 0);
            }
        } else if (reader.with("IIQ")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.Params.EnableIqInverted = (bool)value;
                    serialPrintQueue.enqueue("IIQ=OK");
                } else {
                    serialPrintQueue.enqueue("IIQ=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("IIQ=");
                Serial.println(LoRaNode.Params.EnableIqInverted ? 1 : 0);
            }
        } else if (reader.with("TXTO")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= UINT32_MAX) {
                    LoRaNode.Params.TxTimeout = (uint32_t)value;
                    serialPrintQueue.enqueue("TXTO=OK");
                } else {
                    serialPrintQueue.enqueue("TXTO=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("TXTO=");
                Serial.println(LoRaNode.Params.TxTimeout);
            }
        } else if (reader.with("STO")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= UINT16_MAX) {
                    LoRaNode.Params.RxSymbolTimeout = (uint16_t)value;
                    serialPrintQueue.enqueue("STO=OK");
                } else {
                    serialPrintQueue.enqueue("STO=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("STO=");
                Serial.println(LoRaNode.Params.RxSymbolTimeout);
            }
        } else if (reader.with("MODE")) {
            if (reader.with('=')) {
                if (reader.with("SNK")) {
                    LoRaNode.Mode = SNK;
                    serialPrintQueue.enqueue("MODE=OK");
                } else if (reader.with("SRC")) {
                    LoRaNode.Mode = SRC;
                    serialPrintQueue.enqueue("MODE=OK");
                } else {
                    serialPrintQueue.enqueue("MODE=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("MODE=");
                switch (LoRaNode.Mode) {
                    case SNK:
                        serialPrintQueue.enqueue("SNK");
                        break;

                    case SRC:
                        serialPrintQueue.enqueue("SRC");
                        break;

                    default:
                        serialPrintQueue.enqueue("UNKNOWN");
                        break;
                }
            }
        } else if (reader.with("PUSH")) {
            LoRaNode.Configure();
            serialPrintQueue.enqueue("PUSH=OK");
        } else if (reader.with("STA")) {
            if (reader.with('?')) {
                Serial.print("STA=");
                switch (Radio.GetStatus()) {
                    case RF_IDLE:
                        serialPrintQueue.enqueue("IDLE");
                        break;
                    case RF_RX_RUNNING:
                        serialPrintQueue.enqueue("RX");
                        break;
                    case RF_TX_RUNNING:
                        serialPrintQueue.enqueue("TX");
                        break;
                    case RF_CAD:
                        serialPrintQueue.enqueue("CAD");
                        break;
                    default:
                        serialPrintQueue.enqueue("UNKNOWN");
                        break;
                }
            }
        } else if (reader.with("TOA")) {
            if (reader.with('=')) {
                uint32_t len = reader.until('?').toInt();

                Serial.print("TOA=");
                Serial.println(Radio.TimeOnAir(MODEM_LORA, len));
            }
        } else if (reader.with("AUTO")) {
            if (reader.with('=')) {
                if (reader.with("OFF")) {
                    LoRaNode.Auto = OFF;
                    serialPrintQueue.enqueue("AUTO=OK");
                } else if (reader.with("RANDOM")) {
                    LoRaNode.randomMsgDelay = RandomGenerator::uniformRand(0, LoRaNode.backoffMax);
                    LoRaNode.Auto = RANDOM;
                    serialPrintQueue.enqueue("AUTO=OK");
                } else if (reader.with("TURNBASED")) {
                    LoRaNode.Auto = TURNBASED;
                    serialPrintQueue.enqueue("AUTO=OK");
                } else if (reader.with("RST")) {
                    LoRaNode.msgCounter = 0;
                    LoRaNode.ackLifetime = 0;
                    LoRaNode.permanentDelta = false;
                    LoRaNode.backoffMax = LoRaNode.backoffMaxInit;
                    serialPrintQueue.enqueue("AUTO=OK");
                } else {
                    serialPrintQueue.enqueue("AUTO=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("AUTO=");
                switch (LoRaNode.Auto) {
                case OFF:
                    serialPrintQueue.enqueue("OFF");
                    break;
                case RANDOM:
                    serialPrintQueue.enqueue("RANDOM");
                    break;
                case TURNBASED:
                    serialPrintQueue.enqueue("TURNBASED");
                    break;
                default:
                    serialPrintQueue.enqueue("UNKNOWN");
                    break;
                }
            }
        } else if (reader.with("INV")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= UINT32_MAX) {
                    LoRaNode.msgDelay = (uint32_t)value;
                    serialPrintQueue.enqueue("INV=OK");
                } else {
                    serialPrintQueue.enqueue("INV=ERR");
                }
            } else if (reader.with('?')) {
                char buffer[10];
                snprintf(buffer, sizeof(buffer), "INV=%d", LoRaNode.msgDelay);
                serialPrintQueue.enqueue(buffer);
            }
        } else if (reader.with("IBM"))
        {
            if (reader.with('=')) {
                long delta = reader.untilEnd().toInt();
                if (delta >= 0 && delta <= UINT32_MAX) {
                    LoRaNode.backoffMaxInit = (uint32_t)delta;
                    LoRaNode.backoffMax = (uint32_t)delta;
                    serialPrintQueue.enqueue("IBM=OK");
                } else {
                    serialPrintQueue.enqueue("IBM=ERR");
                }
            } else if (reader.with('?'))
            {
                char buffer[10];
                snprintf(buffer, sizeof(buffer), "IBM=%d", LoRaNode.backoffMaxInit);
                serialPrintQueue.enqueue(buffer);
            }
        } else if (reader.with("ACKLT")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= UINT32_MAX) {
                    LoRaNode.ackLifetimeInit = (uint32_t)value;
                    serialPrintQueue.enqueue("ACKLT=OK");
                } else {
                    serialPrintQueue.enqueue("ACKLT=ERR");
                }
            } else if (reader.with('?')) {
                char buffer[10];
                snprintf(buffer, sizeof(buffer), "ACKLT=%d", LoRaNode.ackLifetimeInit);
                serialPrintQueue.enqueue(buffer);
            }
        } else if (reader.with("ACKRQ")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.ackReq = (bool)value;
                    serialPrintQueue.enqueue("ACKRQ=OK");
                } else {
                    serialPrintQueue.enqueue("ACKRQ=ERR");
                }
            } else if (reader.with('?')) {
                char buffer[10];
                snprintf(buffer, sizeof(buffer), "ACKRQ=%d", LoRaNode.ackReq ? 1 : 0);
                serialPrintQueue.enqueue(buffer);
            }
        } else if (reader.with("BIN")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.backOffIncrease = (bool)value;
                    serialPrintQueue.enqueue("BIN=OK");
                } else {
                    serialPrintQueue.enqueue("BIN=ERR");
                }
            } else if (reader.with('?')) {
                char buffer[10];
                snprintf(buffer, sizeof(buffer), "BIN=%d", LoRaNode.backOffIncrease ? 1 : 0);
                serialPrintQueue.enqueue(buffer);
            }
        } else if (reader.with("LED")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.SwitchLed(value, COLOR_FIND);
                    serialPrintQueue.enqueue("LED=OK");
                } else {
                    serialPrintQueue.enqueue("LED=ERR");
                }
            } else if (reader.with('?')) {
                char buffer[10];
                snprintf(buffer, sizeof(buffer), "LED=%d", LoRaNode.ledState ? 1 : 0);
                serialPrintQueue.enqueue(buffer);
            }
        } else
        {
            serialPrintQueue.enqueue("ERR");
        }
        LoRaTerminal.clear();
    }
}

void setup() {
    Serial.begin(115200);

    randomSeed(analogRead(0));

    LoRaNode.Events.TxDone = OnTxDone;
    LoRaNode.Events.TxTimeout = OnTxTimeout;
    LoRaNode.Events.RxDone = OnRxDone;
    LoRaNode.Events.RxTimeout = OnRxTimeout;
    LoRaNode.Events.RxError = OnRxError;
    LoRaNode.Init();

    if (false)
    {
        int max = 10000;

        Serial.print("max: ");
        Serial.println(max);

        Serial.print("\n\n Normal: ");
        for (int i = 0; i < 5000; ++i)
        {
            Serial.print(RandomGenerator::normalRandInt(max/2, max/2/3, 0, max));
            Serial.print(' ');
        }
        Serial.println();

        Serial.print("\n\n Uniform: ");
        for (int i = 0; i < 5000; ++i)
        {
            Serial.print(RandomGenerator::uniformRand(0, max));
            Serial.print(' ');
        }
        Serial.println();

        Serial.print("\n\n Exponential: ");
        for (int i = 0; i < 5000; ++i)
        {
            Serial.print(RandomGenerator::exponentialRandInt(5.0 / max, 0, max));
            Serial.print(' ');
        }
        Serial.println();
    }
}


void loop() {
    LoRaTerminal.read();
    processTerminal();

    LoRaNode.Loop();

    if (!serialPrintQueue.isEmpty()) {
        serialPrintQueue.dequeue();
    }

    delay(5);
}
