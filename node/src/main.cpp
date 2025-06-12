#include <Arduino.h>
#include "lora/terminal.h"
#include "lora/reader.h"

#ifndef LoraWan_RGB
#define LoraWan_RGB 0
#endif

#define LoraWan_RGB 1

#define RX_DATA_LEN (320)

uint64_t chipID = getID();
char rxpacket[RX_DATA_LEN];

void OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t snr) {
    memset(rxpacket, 0, RX_DATA_LEN);
    int offset = sprintf(rxpacket, "%d,%d,%d,", rssi, snr, size);
    memcpy(rxpacket+offset, payload, size);

    Serial.print("RX=DONE,");
    Serial.println(rxpacket);

    payload[size] = '\0';

    LoRaNode.OnRxDone(payload, size, rssi, snr);
}

void OnRxTimeout() {
    Serial.println("RX=TIMEOUT");
    LoRaNode.OnRxTimeout();
}

void OnRxError() {
    Serial.println("RX=ERROR");
    LoRaNode.OnRxError();
}

void OnTxDone() {
    //Serial.print("TX=DONE");
    LoRaNode.OnTxDone();
}

void OnTxTimeout() {
    Serial.println("TX=TIMEOUT");
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
            LoRaNode.Send((uint8_t*) payload.c_str(), payload.length());
            LoRaTerminal.clear();
            Serial.print("TX=OK,");
            Serial.println(payload.length());
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
                    Serial.println("FRQ=OK");
                } else {
                    Serial.println("FRQ=ERR");
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
                    Serial.println("BW=OK");
                } else {
                    Serial.println("BW=ERR");
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
                    Serial.println("SF=OK");
                } else {
                    Serial.println("SF=ERR");
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
                    Serial.println("PWR=OK");
                } else {
                    Serial.println("PWR=ERR");
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
                    Serial.println("CRT=OK");
                } else {
                    Serial.println("CRT=ERR");
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
                    Serial.println("PRLEN=OK");
                } else {
                    Serial.println("PRLEN=ERR");
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
                    Serial.println("PYLEN=OK");
                } else {
                    Serial.println("PYLEN=ERR");
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
                    Serial.println("CRC=OK");
                } else {
                    Serial.println("CRC=ERR");
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
                    Serial.println("IIQ=OK");
                } else {
                    Serial.println("IIQ=ERR");
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
                    Serial.println("TXTO=OK");
                } else {
                    Serial.println("TXTO=ERR");
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
                    Serial.println("STO=OK");
                } else {
                    Serial.println("STO=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("STO=");
                Serial.println(LoRaNode.Params.RxSymbolTimeout);
            }
        } else if (reader.with("MODE")) {
            if (reader.with('=')) {
                if (reader.with("RX")) {
                    LoRaNode.Mode = RX;
                    Serial.println("MODE=OK");
                } else if (reader.with("TX")) {
                    LoRaNode.Mode = TX;
                    Serial.println("MODE=OK");
                } else {
                    Serial.println("MODE=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("MODE=");
                switch (LoRaNode.Mode) {
                    case RX:
                        Serial.println("RX");
                        break;

                    case TX:
                        Serial.println("TX");
                        break;

                    default:
                        Serial.println("UNKNOWN");
                        break;
                }
            }
        } else if (reader.with("PUSH")) {
            LoRaNode.Configure();
            Serial.println("PUSH=OK");
        } else if (reader.with("STA")) {
            if (reader.with('?')) {
                Serial.print("STA=");
                switch (Radio.GetStatus()) {
                    case RF_IDLE:
                        Serial.println("IDLE");
                        break;
                    case RF_RX_RUNNING:
                        Serial.println("RX");
                        break;
                    case RF_TX_RUNNING:
                        Serial.println("TX");
                        break;
                    case RF_CAD:
                        Serial.println("CAD");
                        break;
                    default:
                        Serial.println("UNKNOWN");
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
                    Serial.println("AUTO=OK");
                } else if (reader.with("RANDOM")) {
                    LoRaNode.Auto = RANDOM;
                    Serial.println("AUTO=OK");
                } else if (reader.with("TURNBASED")) {
                    LoRaNode.Auto = TURNBASED;
                    Serial.println("AUTO=OK");
                } else {
                    Serial.println("AUTO=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("AUTO=");
                switch (LoRaNode.Auto) {
                case OFF:
                    Serial.println("OFF");
                    break;
                case RANDOM:
                    Serial.println("RANDOM");
                    break;
                case TURNBASED:
                    Serial.println("TURNBASED");
                    break;
                default:
                    Serial.println("UNKNOWN");
                    break;
                }
            }
        } else if (reader.with("INV")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= UINT32_MAX) {
                    LoRaNode.msgDelay = (uint32_t)value;
                    Serial.println("INV=OK");
                } else {
                    Serial.println("INV=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("INV=");
                Serial.println(LoRaNode.msgDelay);
            }
        } else if (reader.with("RTO"))
        {
            if (reader.with('=')) {
                String value = reader.untilEnd();
                int commaIndex = value.indexOf(',');
                if (commaIndex != -1) {
                    long minDelta = value.substring(0, commaIndex).toInt();
                    long maxDelta = value.substring(commaIndex + 1).toInt();
                    if (minDelta >= 0 && minDelta <= UINT32_MAX && maxDelta >= 0 && maxDelta <= UINT32_MAX && minDelta <= maxDelta) {
                        LoRaNode.minDelta = (uint32_t)minDelta;
                        LoRaNode.maxDelta = (uint32_t)maxDelta;
                        RandomGenerator random_generator = RandomGenerator();
                        LoRaNode.firstMsgDelay = random_generator.generateUniform(LoRaNode.minDelta, LoRaNode.maxDelta);
                        Serial.println("RTO=OK");
                    } else {
                        Serial.println("RTO=ERR");
                    }
                } else {
                    Serial.println("RTO=ERR");
                }
            } else if (reader.with('?'))
            {
                Serial.print("RTO=");
                Serial.print(LoRaNode.minDelta);
                Serial.print(",");
                Serial.println(LoRaNode.maxDelta);
            }
        } else if (reader.with("ACKLT")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= UINT32_MAX) {
                    LoRaNode.ackLifetimeInit = (uint32_t)value;
                    Serial.println("ACKLT=OK");
                } else {
                    Serial.println("ACKLT=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("ACKLT=");
                Serial.println(LoRaNode.ackLifetimeInit);
            }
        } else if (reader.with("ACKRQ")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.ackReq = (bool)value;
                    Serial.println("ACKRQ=OK");
                } else {
                    Serial.println("ACKRQ=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("ACKRQ=");
                Serial.println(LoRaNode.ackReq ? 1 : 0);
            }
        } else if (reader.with("LED")) {
            if (reader.with('=')) {
                long value = reader.untilEnd().toInt();
                if (value >= 0 && value <= 1) {
                    LoRaNode.ledState = (bool)value;
                    LoRaNode.SwitchLed(LoRaNode.ledState);
                    Serial.println("LED=OK");
                } else {
                    Serial.println("LED=ERR");
                }
            } else if (reader.with('?')) {
                Serial.print("LED=");
                Serial.println(LoRaNode.ledState ? 1 : 0);
            }
        } else
        {
            Serial.println("ERR");
        }
        LoRaTerminal.clear();
    }
}

void setup() {
    LoRaNode.SwitchLed(LoRaNode.ledState);
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

    delay(5);
}