#include "node.h"

LoRaNodeClass LoRaNode;

void LoRaNodeClass::Init() {
    Radio.Init(&Events);
    this->Configure();
}

void LoRaNodeClass::Configure() {
    Radio.SetChannel(Params.Frequency);
    Radio.SetTxConfig(MODEM_LORA, Params.Power, 0, Params.Bandwidth,
                      Params.SpreadingFactor, Params.CodeRate,
                      Params.PreambleLength, Params.PayloadLength != 0,
                      Params.EnableCrc, false, 0, Params.EnableIqInverted, Params.TxTimeout);

    Radio.SetRxConfig(MODEM_LORA, Params.Bandwidth, Params.SpreadingFactor,
                      Params.CodeRate, 0, Params.PreambleLength,
                      Params.RxSymbolTimeout, Params.PayloadLength != 0,
                      Params.PayloadLength, Params.EnableCrc, false, 0, Params.EnableIqInverted, true);

    this->Idle = true;
    Radio.Sleep();
}

void LoRaNodeClass::Loop() {
    if (this->Idle) {
        if (this->Mode == RX) {
            this->Receive();
        }
    }

    Radio.IrqProcess();
}

void LoRaNodeClass::Receive() {
    Radio.Rx(0);
    this->Idle = false;
}

void LoRaNodeClass::Send(uint8_t *data, size_t length) {
    Radio.Send(data, length);
    this->Idle = false;
}

void LoRaNodeClass::OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t snr) {
    Radio.Sleep();
    this->Idle = true;
}

void LoRaNodeClass::OnRxTimeout() {
    Radio.Sleep();
    this->Idle = true;
}

void LoRaNodeClass::OnRxError() {
    Radio.Sleep();
    this->Idle = true;
}

void LoRaNodeClass::OnTxDone() {
    Radio.Sleep();
    this->Idle = true;
}

void LoRaNodeClass::OnTxTimeout() {
    Radio.Sleep();
    this->Idle = true;
}
