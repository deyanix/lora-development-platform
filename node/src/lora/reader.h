#ifndef STRING_READER_H
#define STRING_READER_H

#include <Arduino.h>

class StringReader {
private:
    String buffer;
    size_t offset;

public:
    explicit StringReader(const String&);
    StringReader(const StringReader&);

    bool with(char);
    bool with(const String&);
    String until(char);
    String untilEnd();

    void moveTo(size_t);
    void shiftBy(size_t);
    void reset();
};


#endif
