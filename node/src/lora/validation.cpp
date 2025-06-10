//
// Created by Kuba on 10.06.2025.
//

#include "validation.h"

bool parseStandardMessageContent(const uint8_t* message_uint8, uint64_t* outNodeId, int* outSeq) {
    if (message_uint8 == nullptr) return false;
    const char* message = (const char*)message_uint8; // Cast to char* for string ops

    // Find the first hyphen (after NODEID)
    const char* firstHyphen = strchr(message, '-');
    if (firstHyphen == nullptr) {
        return false; // Should not happen if validateStandardMessage was called
    }

    // Extract NODEID
    char nodeIdStr[13]; // 12 chars + null terminator
    int nodeIdLength = firstHyphen - message;
    if (nodeIdLength != 12) {
        return false; // Should not happen if validateStandardMessage was called
    }
    strncpy(nodeIdStr, message, 12);
    nodeIdStr[12] = '\0';

    // Convert NODEID string to uint64_t
    *outNodeId = strtoull(nodeIdStr, nullptr, 16);

    // Find the second hyphen (after SEQ)
    const char* secondHyphen = strchr(firstHyphen + 1, '-');
    if (secondHyphen == nullptr) {
        return false; // Should not happen if validateStandardMessage was called
    }

    // Extract SEQ
    const char* seqStr = firstHyphen + 1;
    int seqLength = secondHyphen - seqStr;
    if (seqLength == 0) { // SEQ part cannot be empty
        return false; // Should not happen if validateStandardMessage was called
    }
    char seqBuffer[10]; // Max 9 digits for int + null terminator
    if (seqLength >= sizeof(seqBuffer)) return false; // Prevent buffer overflow
    strncpy(seqBuffer, seqStr, seqLength);
    seqBuffer[seqLength] = '\0';
    *outSeq = atoi(seqBuffer);

    // We don't need to parse the '?' itself, just validate it in validateStandardMessage

    return true;
}

// --- Function to validate if a received message is in expected standard format ---
// Expected format: "NODEID-SEQ-?" (12 hex chars, hyphen, digits, hyphen, ?)
bool validateStandardMessage(const uint8_t* message_uint8) {
    if (message_uint8 == nullptr) {
        //Serial.println("validateStandardMessage: Message is null.");
        return false;
    }
    const char* msg_char = (const char*)message_uint8; // Cast to char*
    size_t msgLen = strlen(msg_char);

    // Minimum length for "XXXXXXXXXXXX-Y-?" is 12 (NODEID) + 1 (-) + 1 (min SEQ) + 1 (-) + 1 (?) = 16
    if (msgLen < 16) {
        //Serial.println("validateStandardMessage: Message too short.");
        return false;
    }

    // Find the first hyphen (after NODEID)
    const char* firstHyphen = strchr(msg_char, '-');
    if (firstHyphen == nullptr) {
        //Serial.println("validateStandardMessage: First hyphen (after NODEID) not found.");
        return false;
    }

    // Validate NODEID part (before first hyphen)
    int nodeIdLength = firstHyphen - msg_char;
    if (nodeIdLength != 12) {
        //Serial.print("validateStandardMessage: NODEID length is ");
        //Serial.print(nodeIdLength);
        //Serial.println(", expected 12.");
        return false;
    }
    for (int i = 0; i < nodeIdLength; ++i) {
        if (!isxdigit(msg_char[i])) {
            //Serial.print("validateStandardMessage: Non-hex char in NODEID at pos ");
            //Serial.println(i);
            return false;
        }
    }

    // Find the second hyphen (after SEQ)
    const char* secondHyphen = strchr(firstHyphen + 1, '-');
    if (secondHyphen == nullptr) {
        //Serial.println("validateStandardMessage: Second hyphen (after SEQ) not found.");
        return false;
    }

    // Validate SEQ part (between first and second hyphen)
    const char* seqStr = firstHyphen + 1;
    int seqLength = secondHyphen - seqStr;
    if (seqLength == 0) { // SEQ part cannot be empty
        //Serial.println("validateStandardMessage: SEQ part is empty.");
        return false;
    }
    for (int i = 0; i < seqLength; ++i) {
        if (!isdigit(seqStr[i])) {
            //Serial.print("validateStandardMessage: Non-digit char in SEQ at pos ");
            //Serial.println(i);
            return false;
        }
    }

    // Validate the trailing "-?"
    const char* questionMarkChar = secondHyphen + 1;
    if (questionMarkChar[0] != '?' || questionMarkChar[1] != '\0') {
        //Serial.println("validateStandardMessage: Message does not end with '-?'.");
        return false;
    }

    //Serial.println("validateStandardMessage: Format valid (NODEID-SEQ-?).");
    return true;
}


// --- Function to validate if a received message is an ACK format and contains this chipID ---
// Expected format: "ACK-NODEID-SEQ" (ACK-, 12 hex chars, hyphen, digits)
// Note: ACK message format does NOT include the trailing "-?".
bool validateAckMessage(const uint8_t* message_uint8, uint64_t chipID) {
    if (message_uint8 == nullptr) {
        //Serial.println("validateAckMessage: Message is null.");
        return false;
    }
    const char* msg_char = (const char*)message_uint8; // Cast to char*
    size_t msgLen = strlen(msg_char);

    // Check for "ACK-" prefix
    if (strncmp(msg_char, "ACK-", 4) != 0) {
        //Serial.println("validateAckMessage: Does not start with 'ACK-'.");
        return false;
    }

    // The remaining part should be in "NODEID-SEQ" format (without the trailing -?)
    const char* content_start = msg_char + 4; // Skip "ACK-"

    // Minimum length for "ACK-XXXXXXXXXXXX-Y" -> 4 + 12 + 1 + 1 = 18 minimum
    if (msgLen < 18) {
        //Serial.println("validateAckMessage: Message too short after 'ACK-'.");
        return false;
    }

    // We need to parse NODEID and SEQ for comparison, but the content_start
    // does *not* include the trailing '-?'. So we'll use a slightly adapted parsing logic here
    // or ensure parseStandardMessageContent can handle it.
    // For ACK, we'll look for NODEID-SEQ and ensure no trailing '-?'.

    const char* firstHyphen = strchr(content_start, '-');
    if (firstHyphen == nullptr) {
        //Serial.println("validateAckMessage: No hyphen after ACK- prefix.");
        return false;
    }

    // Validate NODEID part (after ACK- and before first hyphen)
    int nodeIdLength = firstHyphen - content_start;
    if (nodeIdLength != 12) {
        //Serial.print("validateAckMessage: NODEID length after ACK- is ");
        //Serial.print(nodeIdLength);
        //Serial.println(", expected 12.");
        return false;
    }
    for (int i = 0; i < nodeIdLength; ++i) {
        if (!isxdigit(content_start[i])) {
            //Serial.print("validateAckMessage: Non-hex char in NODEID after ACK- at pos ");
            //Serial.println(i);
            return false;
        }
    }

    // Validate SEQ part (after first hyphen)
    const char* seqStr = firstHyphen + 1;
    if (*seqStr == '\0') {
        //Serial.println("validateAckMessage: SEQ part after ACK- is empty.");
        return false;
    }

    // Check if there's any *third* hyphen or other characters after SEQ in an ACK, which shouldn't be there
    const char* possibleThirdHyphen = strchr(seqStr, '-');
    if (possibleThirdHyphen != nullptr) {
         //Serial.println("validateAckMessage: Unexpected third hyphen or trailing data in ACK message.");
         return false; // ACK message shouldn't have -?
    }

    uint64_t receivedNodeId;
    int receivedSeq;

    // Manually parse for ACK message's NODEID and SEQ
    char nodeIdStr[13];
    strncpy(nodeIdStr, content_start, 12);
    nodeIdStr[12] = '\0';
    receivedNodeId = strtoull(nodeIdStr, nullptr, 16);
    receivedSeq = atoi(seqStr);


    // Final check: Does the NODEID in the ACK match THIS device's chipID?
    if (receivedNodeId != chipID) {
        //Serial.print("validateAckMessage: ACK NODEID (");
        //Serial.print((unsigned long)(receivedNodeId >> 32), HEX); // Print upper 32 bits
        //Serial.print((unsigned long)(receivedNodeId & 0xFFFFFFFF), HEX); // Print lower 32 bits
        //Serial.print(") does not match THIS chipID (");
        //Serial.print((unsigned long)(chipID >> 32), HEX);
        //Serial.print((unsigned long)(chipID & 0xFFFFFFFF), HEX);
        //Serial.println(").");
        return false;
    }

    //Serial.println("validateAckMessage: ACK format and chipID valid.");
    return true;
}
