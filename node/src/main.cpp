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

void OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t snr) {
    //TODO: Implement serial.write type functionality
    //Serial.write(payload, size);
    serialPrintQueue.enqueue_printf("RX=DONE,%d,%d,%d,%s", rssi, snr, size, payload);

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

    serialPrintQueue.enqueue_printf("TX=DONE,%lu", duration);

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
            serialPrintQueue.enqueue_printf("TX=OK,%d", payload.length());
            LoRaNode.Send((uint8_t*) payload.c_str(), payload.length());
            LoRaTerminal.clear();
        }
    } else if (LoRaTerminal.isFinished()) {
        if (reader.with("ID")) {
            if (reader.with('?')) {
                serialPrintQueue.enqueue_printf("ID=%04X%08X", (uint32_t)(chipID >> 32), (uint32_t)chipID);
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
                serialPrintQueue.enqueue_printf("FRQ=%d", LoRaNode.Params.Frequency);
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
                serialPrintQueue.enqueue_printf("BW=%d", LoRaNode.Params.Bandwidth);
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
                serialPrintQueue.enqueue_printf("SF=%d", LoRaNode.Params.SpreadingFactor);
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
                serialPrintQueue.enqueue_printf("PWR=%d", LoRaNode.Params.Power);
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
                serialPrintQueue.enqueue_printf("CRT=%d", LoRaNode.Params.CodeRate);
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
                serialPrintQueue.enqueue_printf("PRLEN=%d", LoRaNode.Params.PreambleLength);
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
                serialPrintQueue.enqueue_printf("PYLEN=%d", LoRaNode.Params.PayloadLength);
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
                serialPrintQueue.enqueue_printf("CRC=%d", LoRaNode.Params.EnableCrc ? 1 : 0);
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
                serialPrintQueue.enqueue_printf("IIQ=%d", LoRaNode.Params.EnableIqInverted ? 1 : 0);
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
                serialPrintQueue.enqueue_printf("TXTO=%d", LoRaNode.Params.TxTimeout);
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
                serialPrintQueue.enqueue_printf("STO=%d", LoRaNode.Params.RxSymbolTimeout);
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
                const char* modeName;
                switch (LoRaNode.Mode) {
                    case SNK:
                        modeName = "SNK";
                        break;
                    case SRC:
                        modeName = "SRC";
                        break;
                    default:
                        modeName = "UNKNOWN";
                        break;
                }
                serialPrintQueue.enqueue_printf("MODE=%s", modeName);
            }
        } else if (reader.with("PUSH")) {
            LoRaNode.Configure();
            serialPrintQueue.enqueue("PUSH=OK");
        } else if (reader.with("STA")) {
            if (reader.with('?')) {
                const char* stateName;
                switch (Radio.GetStatus()) {
                    case RF_IDLE:
                        stateName = "IDLE";
                        break;
                    case RF_RX_RUNNING:
                        stateName = "RX";
                        break;
                    case RF_TX_RUNNING:
                        stateName = "TX";
                        break;
                    case RF_CAD:
                        stateName = "CAD";
                        break;
                    default:
                        stateName = "UNKNOWN";
                        break;
                }
                serialPrintQueue.enqueue_printf("STA=%s", stateName);
            }
        } else if (reader.with("TOA")) {
            if (reader.with('=')) {
                uint32_t len = reader.until('?').toInt();

                serialPrintQueue.enqueue_printf("TOA=%u", len);
            }
        } else if (reader.with("AUTO")) {
            if (reader.with('=')) {
                if (reader.with("OFF")) {
                    LoRaNode.Auto = OFF;
                    serialPrintQueue.enqueue("AUTO=OK");
                } else if (reader.with("RANDOM")) {
                    LoRaNode.randomMsgDelay = g_randomGenerator.getRandomValue(0, LoRaNode.backoffMax);
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
                const char* autoModeName;
                switch (LoRaNode.Auto) {
                    case OFF:
                        autoModeName = "OFF";
                        break;
                    case RANDOM:
                        autoModeName = "RANDOM";
                        break;
                    case TURNBASED:
                        autoModeName = "TURNBASED";
                        break;
                    default:
                        autoModeName = "UNKNOWN";
                        break;
                }
                serialPrintQueue.enqueue_printf("AUTO=%s", autoModeName);
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
                serialPrintQueue.enqueue_printf("INV=%d", LoRaNode.msgDelay);
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
                serialPrintQueue.enqueue_printf("IBM=%d", LoRaNode.backoffMaxInit);
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
                serialPrintQueue.enqueue_printf("ACKLT=%d", LoRaNode.ackLifetimeInit);
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
                serialPrintQueue.enqueue_printf("ACKRQ=%d", LoRaNode.ackReq ? 1 : 0);
            }
        } else if (reader.with("RNDST")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 2) {
                    LoRaNode.rndDist = value;
                    serialPrintQueue.enqueue("RNDST=OK");
                } else {
                    serialPrintQueue.enqueue("RNDST=ERR");
                }
            } else if (reader.with('?')) {
                serialPrintQueue.enqueue_printf("RNDST=%d", LoRaNode.rndDist);
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
                serialPrintQueue.enqueue_printf("BIN=%d", LoRaNode.backOffIncrease ? 1: 0);
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
                serialPrintQueue.enqueue_printf("LED=%d", LoRaNode.ledState ? 1 : 0);
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
