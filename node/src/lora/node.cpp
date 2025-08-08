#include "node.h"

LoRaNodeClass LoRaNode;
CharPointerQueue serialPrintQueue;

void LoRaNodeClass::Init()
{
    Radio.Init(&Events);
    this->Configure();
    this->rxLedOn = false;
    this->rxLedOnDur = 500;

    this->lastSendTime = millis();
    this->msgDelay = 10000;

    this->backoffMaxInit = 10000;
    this->backoffMax = 10000;
    this->msgCounter = 0;

    this->ackLifetime = 0;
    this->ackReq = true;
    this->backOffIncrease = false;

    this->permanentDelta = false;
}

void LoRaNodeClass::Configure()
{
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
    if (this->ledState)
    {
        turnOnRGB(COLOR_FIND, 0);
    }
    else
    {
        turnOnRGB(0x000000, 0);
    }
}

void LoRaNodeClass::Loop()
{
    if (this->Idle)
    {
        if (this->Mode == SNK)
        {
            this->Receive();
        }
    }

    if (this->rxLedOn)
    {
        if (millis() - this->rxLedOnTime >= this->rxLedOnDur)
        {
            // Check if Dur has passed
            this->rxLedOn = false;
            if (this->ledState)
            {
                turnOnRGB(COLOR_FIND, 0);
            }
            else
            {
                turnOnRGB(0x000000, 0);
            }
        }
    }

    if (this->Mode == SRC && this->Auto == RANDOM)
    {
        unsigned long interval = this->permanentDelta ? this->msgDelay : (this->randomMsgDelay + this->msgDelay);
        if (millis() - this->lastSendTime >= interval)
        {
            char message[128];
            if (this->ackReq && this->ackLifetime <= 0)
            {
                snprintf(message, sizeof(message), "%012llx-%d-?", getID(), msgCounter);
                if (ackLifetime < 0)
                {
                    if (this->backOffIncrease)
                    {
                        this->backoffMax = this->backoffMax * 2;
                    }
                    this->randomMsgDelay = g_randomGenerator.getRandomValue(0, this->backoffMax);
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
            if (this->Idle)
            {
                this->Receive();
            }
        }
    }

    Radio.IrqProcess();
}

void LoRaNodeClass::Receive()
{
    this->OnRxStart();
    Radio.Rx(0);
    this->Idle = false;
    //turnOnRGB(0x000000, 0); too short duration
}

void LoRaNodeClass::Send(uint8_t* data, size_t length)
{
    if (Radio.GetStatus() == RF_TX_RUNNING)
    {
        this->OnTxBusy();
        return;
    }
    this->OnTxStart(data, length);
    this->SendStopwatch.reset();
    Radio.Send(data, length);
    this->Idle = false;
}

void LoRaNodeClass::Stop()
{
    Radio.Sleep();
    this->Idle = true;
    if (this->ledState)
    {
        turnOnRGB(COLOR_FIND, 0);
    }
    else
    {
        turnOnRGB(0x000000, 0);
    }
}

void LoRaNodeClass::OnRxDone(uint8_t* payload, uint16_t size, int16_t rssi, int8_t snr)
{
    this->Stop();
    turnOnRGB(COLOR_RECEIVED, 0);
    this->rxLedOnTime = millis();
    this->rxLedOn = true;

    if (this->Auto == RANDOM && this->ackReq)
    {
        if (this->Mode == SNK)
        {
            if (validateStandardMessage(payload))
            {
                char message[128];
                snprintf(message, sizeof(message), "%s-%s", "ACK", (char*)payload);
                unsigned int msgLen = strlen(message);
                if (msgLen >= 2 && message[msgLen - 2] == '-' && message[msgLen - 1] == '?')
                {
                    message[msgLen - 2] = '\0';
                }
                this->isSendingAck = true;
                this->Send((uint8_t*)message, strlen(message));
                this->isSendingAck = false;
            }
        }
        else if (this->Mode == SRC)
        {
            uint64_t chipID = getID();
            if (validateAckMessage(payload, chipID))
            {
                if (this->backOffIncrease)
                {
                    this->backoffMax = this->backoffMaxInit;
                }
                this->permanentDelta = true;
                this->ackLifetime = this->ackLifetimeInit;
            }
        }
    }
}

void LoRaNodeClass::OnRxTimeout()
{
    this->Stop();
}

void LoRaNodeClass::OnRxError()
{
    this->Stop();
}

void LoRaNodeClass::OnTxDone()
{
    this->Stop();
}

void LoRaNodeClass::OnTxTimeout()
{
    memset(this->lastSentData, 0, MAX_MSG_BUFFER_LENGTH);
    this->Stop();
}

void LoRaNodeClass::SwitchLed(bool state, uint32_t color)
{
    this->ledState = state;
    if (this->ledState)
    {
        turnOnRGB(color, 0);
    }
    else
    {
        turnOnRGB(0x000000, 0);
    }
}

void LoRaNodeClass::OnRxStart()
{
    serialPrintQueue.enqueue("RX=START");
}

void LoRaNodeClass::OnTxStart(uint8_t* data, size_t length)
{
    if (this->ackReq && (this->ackLifetime + 1) <= 0)
    {
        turnOnRGB(COLOR_SACK, 0);
    }
    else if (this->Mode == SNK && this->isSendingAck)
    {
        turnOnRGB(COLOR_RACK, 0);
    }
    else
    {
        turnOnRGB(COLOR_SEND, 0);
    }
    serialPrintQueue.enqueue_printf("TX=START,%d,%s", length, data);
}

void LoRaNodeClass::OnTxBusy()
{
    serialPrintQueue.enqueue("TX=BUSY");
}
