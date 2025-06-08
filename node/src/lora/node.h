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

typedef enum {
    RX = 1,
    TX = 2,
} LoRaNodeMode;

class LoRaNodeClass {
public:
    LoRaNodeParams_t Params = {
            .Frequency = 868000000,
            .Power = 12,
            .Bandwidth = 1,
            .SpreadingFactor = 7,
            .CodeRate = 3,
            .PayloadLength = 0,
            .PreambleLength = 8,
            .EnableCrc = false,
            .EnableIqInverted = true,
            .TxTimeout = 3000,
            .RxSymbolTimeout = 0,
    };
    RadioEvents_t Events = {};
    LoRaNodeMode Mode = TX;
    bool Idle = true;

    unsigned long rxLedOnTime; // Time when RX LED was turned on
    unsigned long rxLedOnDur;  // Duration for how long RX LED is turned on
    bool rxLedOn;

    void Init();
    void Configure();
    void Loop();
    void Send(uint8_t* data, size_t length);
    void Receive();
    void Stop();

    void OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t snr);
    void OnRxTimeout();
    void OnRxError();
    void OnTxDone();
    void OnTxTimeout();
};

extern LoRaNodeClass LoRaNode;

#endif //LORA_PLATFORM_NODE_NODE_H
