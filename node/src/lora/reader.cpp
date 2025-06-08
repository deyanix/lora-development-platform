#include "reader.h"

StringReader::StringReader(const String& buffer) {
    this->buffer = buffer;
    this->offset = 0;
}

StringReader::StringReader(const StringReader& reader) : StringReader(reader.buffer) {
}

bool StringReader::with(char c) {
    if (offset+1 > buffer.length()) {
        return false;
    }

    if (buffer.charAt(offset) == c) {
        offset++;
        return true;
    }
    return false;
}

bool StringReader::with(const String& str) {
    size_t len = str.length();
    if (offset+len > buffer.length()) {
        return false;
    }

    if (buffer.startsWith(str, offset)) {
        offset += len;
        return true;
    }
    return false;
}

String StringReader::until(char delimiter) {
    int index = buffer.indexOf(delimiter, offset);
    if (index < 0) {
        return String();
    }

    size_t from = offset;
    offset = index;
    return buffer.substring(from, offset++);
}

String StringReader::untilEnd() {
    size_t from = offset;
    offset = buffer.length();
    return buffer.substring(from);
}

void StringReader::moveTo(size_t newOffset) {
    if (newOffset < buffer.length()) {
        offset = newOffset;
    }
}

void StringReader::shiftBy(size_t diff) {
    moveTo(offset + diff);
}

void StringReader::reset() {
    moveTo(0);
}