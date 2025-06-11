#include "node.h"

LoRaNodeClass LoRaNode;

void LoRaNodeClass::Init() {
    Radio.Init(&Events);
    this->Configure();
    this->rxLedOn = false;
    this->rxLedOnDur = 500;

    this->lastSendTime = millis();
    this->msgDelay = 10000;

    this->minDelta = 100;
    this->maxDelta = 10000;
    this->msgCounter = 0;

    this->ackLifetime = 0;
    this->ackReq = true;

    this->permanentDelta = false;
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
    turnOnRGB(0x000000, 0);
}

void LoRaNodeClass::Loop() {
    if (this->Idle) {
        if (this->Mode == RX) {
            this->Receive();
        }
    }

    if (this->rxLedOn) {
        if (millis() - this->rxLedOnTime >= this->rxLedOnDur) { // Check if Dur has passed
            turnOnRGB(0x000000, 0);      // Turn off the LED
            this->rxLedOn = false;                 // Reset the flag
        }
    }

    if (this->Mode == TX && this->Auto == RANDOM)
    {
        unsigned long interval = this->permanentDelta ? this->msgDelay: this->firstMsgDelay;
        if (millis() - this->lastSendTime >= interval)
        {
            char message[128];
            if (this->ackReq && this->ackLifetime <= 0)
            {
                snprintf(message, sizeof(message), "%012llx-%d-?", getID(), msgCounter);

                if(ackLifetime<0)
                {
                    RandomGenerator randomGenerator;
                    this->firstMsgDelay = randomGenerator.generateUniform(this->minDelta, this->maxDelta);
                    this->permanentDelta = false;
                }
            }
            else
            {
                snprintf(message, sizeof(message), "%012llx-%d", getID(), msgCounter);
            }
            if (this->ackReq)
            {
                this->ackLifetime--;
            }
            else
            {
                this->permanentDelta = true;
            }

            this->Send((uint8_t*)message, strlen(message));

            this->lastSendTime = millis();
            msgCounter++;
        }
        if (this->ackReq && this->ackLifetime < 0)
        {
            if (this->Idle) {
                this->Receive();
            }
        }
    }

    Radio.IrqProcess();
}

void LoRaNodeClass::Receive() {
    Radio.Rx(0);
    this->Idle = false;
    //turnOnRGB(0x000000, 0); too short duration
}

void LoRaNodeClass::Send(uint8_t *data, size_t length) {
    Radio.Send(data, length);
    this->Idle = false;
    turnOnRGB(COLOR_SEND, 0);
    memcpy(this->lastSentData, data, length);
    this->lastSentData[length] = '\0';
}

void LoRaNodeClass::Stop() {
    Radio.Sleep();
    this->Idle = true;
    turnOnRGB(0x000000, 0);
}

void LoRaNodeClass::OnRxDone(uint8_t *payload, uint16_t size, int16_t rssi, int8_t snr) {
    this->Stop();
    turnOnRGB(COLOR_RECEIVED, 0);
    this->rxLedOnTime = millis();
    this->rxLedOn = true;

    if (this->Auto == RANDOM && this->ackReq) {
        if (this->Mode == RX)
        {
            if (validateStandardMessage(payload))
            {
                char message[128];
                snprintf(message, sizeof(message), "%s-%s", "ACK", (char*)payload);
                if (strlen(message) >= 2 && message[strlen(message) - 2] == '-' && message[strlen(message) - 1] == '?') {
                    message[strlen(message) - 2] = '\0';
                }
                this->Send((uint8_t*)message, strlen(message));
            }
        }
        else if (this->Mode == TX)
        {
            uint64_t chipID = getID();
            if (validateAckMessage(payload, chipID))
            {
                this->permanentDelta = true;
                this->ackLifetime = this->ackLifetimeInit;
            }
        }
    }
}

void LoRaNodeClass::OnRxTimeout() {
    this->Stop();
}

void LoRaNodeClass::OnRxError() {
    this->Stop();
}

void LoRaNodeClass::OnTxDone() {
    Serial.print("TX=DONE,");
    Serial.print(strlen(this->lastSentData));
    Serial.print(",");
    Serial.println(this->lastSentData);
    memset(this->lastSentData, 0, MAX_MSG_BUFFER_LENGTH);
    this->Stop();
}

void LoRaNodeClass::OnTxTimeout() {
    memset(this->lastSentData, 0, MAX_MSG_BUFFER_LENGTH);
    this->Stop();
}

void LoRaNodeClass::SwitchLed(bool state) {
    if (state) {
        turnOnRGB(COLOR_FIND, 0);
    }
    else    {
        turnOnRGB(0x000000, 0);
    }
}