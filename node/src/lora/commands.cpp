#include "commands.h"

bool HandleSetFrequency(String args) {
    long value = args.toInt();
    if (value >= 868000000 && value <= 870000000) {
        LoRaNode.Params.Frequency = value;
        return true;
    }
    return false;
}

String HandleGetFrequency(String args) {
    return String(LoRaNode.Params.Frequency);
}

bool HandleSetCrc(String args) {
    long value = args.toInt();
    if (value == 0 || value == 1) {
        LoRaNode.Params.EnableCrc = value;
        return true;
    }
    return false;
}

String HandleGetCrc(String args) {
    return String(LoRaNode.Params.EnableCrc);
}

bool HandleSetIqInverted(String args) {
    long value = args.toInt();
    if (value == 0 || value == 1) {
        LoRaNode.Params.EnableIqInverted = value;
        return true;
    }
    return false;
}

String HandleGetIqInverted(String args) {
    return String(LoRaNode.Params.EnableIqInverted);
}