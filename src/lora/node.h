#ifndef LORA_PLATFORM_NODE_NODE_H
#define LORA_PLATFORM_NODE_NODE_H

#include <LoRaWan_APP.h>
#include <sx126x.h>

typedef struct {
    uint32_t Frequency;
    int8_t Power;
    uint32_t Bandwidth;
    uint32_t SpreadingFactor;
    uint8_t CodeRate;
    uint8_t PayloadLength;
    uint16_t PreambleLength;
    bool EnableCrc;
    bool EnableIqInverted;
    uint32_t TxTimeout;
    uint16_t RxSymbolTimeout;
} LoRaNodeParams_t;


class LoRaNodeClass {
public:
    RadioEvents_t Events;
    LoRaNodeParams_t Params;
    void Init();
    void Configure();
};

extern LoRaNodeClass LoRaNode;

#endif //LORA_PLATFORM_NODE_NODE_H
