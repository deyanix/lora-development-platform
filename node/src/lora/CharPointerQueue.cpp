// CharPointerQueue.cpp
//
// Created by kuba on 7/23/25.
//

#include "CharPointerQueue.h"
#include <Arduino.h> // Required for Serial.println() and malloc/free (implicitly often included)
#include <string.h>  // Required for strncpy

CharPointerQueue::CharPointerQueue() : _head(0), _tail(0), _count(0) {
    for (size_t i = 0; i < MAX_QUEUE_SIZE; ++i) {
        _data[i] = nullptr;
    }
}

/**
 * @brief Destructor for CharPointerQueue.
 * Frees all memory allocated for strings currently in the queue.
 * It iterates through the elements in the queue and frees the memory
 * pointed to by each `char*` before the queue object is destroyed.
 */
CharPointerQueue::~CharPointerQueue() {
    size_t current = _head;
    for (size_t i = 0; i < _count; ++i) {
        if (_data[current] != nullptr) {
            free(_data[current]); // Free memory for the string
            _data[current] = nullptr; // Set pointer to nullptr after freeing to be safe
        }
        current = (current + 1) % MAX_QUEUE_SIZE;
    }
    _head = 0;
    _tail = 0;
    _count = 0;
}

/**
 * @brief Enqueues a new string into the queue.
 * Allocates memory for a copy of the item and stores its pointer.
 * @param item A C-style string to be added to the queue.
 * @return True if the item was successfully enqueued, false if the queue is full
 * or memory allocation failed.
 */
bool CharPointerQueue::enqueue(const char* item) {
    if (isFull()) {
        return false; // Queue is full
    }

    char* newString = (char*)malloc(MAX_STRING_LENGTH * sizeof(char));
    if (newString == nullptr) {
        return false; // Memory allocation failed
    }

    strncpy(newString, item, MAX_STRING_LENGTH - 1);
    newString[MAX_STRING_LENGTH - 1] = '\0';

    _data[_tail] = newString;
    _tail = (_tail + 1) % MAX_QUEUE_SIZE;
    _count++;
    return true;
}

bool CharPointerQueue::enqueue_printf(const char* format, ...) {
    if (isFull()) {
        Serial.println("Queue is full. Cannot enqueue formatted string.");
        return false;
    }

    // Declare a temporary buffer on the stack for formatting.
    // Its size is MAX_STRING_LENGTH to ensure the formatted string
    // will fit into the dynamically allocated memory later.
    char tempBuffer[MAX_STRING_LENGTH];
    va_list args; // Declare a variable argument list

    va_start(args, format); // Initialize va_list with the variable arguments

    // Use vsnprintf to safely format the string into tempBuffer.
    // MAX_STRING_LENGTH is used as the buffer size, ensuring null termination
    // if the formatted string is exactly MAX_STRING_LENGTH-1 characters long.
    int charsWritten = vsnprintf(tempBuffer, MAX_STRING_LENGTH, format, args);

    va_end(args); // Clean up the va_list

    if (charsWritten < 0) {
        // vsnprintf returns a negative value on encoding error
        Serial.println("Error formatting string.");
        return false;
    }
    if (charsWritten >= MAX_STRING_LENGTH) {
        // This means the formatted string was truncated because it exceeded
        // the MAX_STRING_LENGTH buffer. A warning is printed, but the truncated
        // string is still enqueued. You might choose to return false here
        // if truncated strings are not acceptable.
        Serial.print("Formatted string truncated (length >= ");
        Serial.print(MAX_STRING_LENGTH);
        Serial.println(").");
    }

    // Now, enqueue the formatted string using the existing enqueue method.
    // This handles memory allocation and queue management for the formatted string.
    return enqueue(tempBuffer);
}

/**
 * @brief Dequeues an item from the front of the queue.
 * Prints the dequeued string to Serial and frees its allocated memory.
 * If the queue is empty, this function does nothing.
 */
void CharPointerQueue::dequeue() {
    if (isEmpty()) {
        return; // Queue is empty, nothing to dequeue
    }

    char* itemToPrint = _data[_head];

    _data[_head] = nullptr;
    _head = (_head + 1) % MAX_QUEUE_SIZE;
    _count--;

    if (itemToPrint != nullptr) {
        Serial.println(itemToPrint); // ONLY print the item itself to the Arduino Serial monitor
        free(itemToPrint); // Free the memory associated with the string
    } else {
        // This block could be used for debugging or error logging if a nullptr is unexpectedly found.
        // Serial.println("Warning: Dequeued a nullptr. Queue state might be inconsistent.");
    }
}

/**
 * @brief Dequeues and processes all elements currently in the queue.
 * Calls `dequeue()` repeatedly until the queue is empty, printing and
 * freeing each element.
 */
void CharPointerQueue::dequeueAllElements() {
    while (!isEmpty()) {
        dequeue(); // Call the existing dequeue method to print and free each element
    }
}

/**
 * @brief Checks if the queue is empty.
 * @return True if the queue contains no elements, false otherwise.
 */
bool CharPointerQueue::isEmpty() const {
    return _count == 0;
}

/**
 * @brief Checks if the queue is full.
 * @return True if the queue has reached its maximum capacity, false otherwise.
 */
bool CharPointerQueue::isFull() const {
    return _count == MAX_QUEUE_SIZE;
}

/**
 * @brief Returns the current number of elements in the queue.
 * @return The number of elements currently stored in the queue.
 */
size_t CharPointerQueue::size() const {
    return _count;
}