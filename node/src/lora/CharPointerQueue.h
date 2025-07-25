// CharPointerQueue.h
//
// Created by kuba on 7/23/25.
//
#ifndef CHAR_POINTER_QUEUE_H
#define CHAR_POINTER_QUEUE_H

#include <stddef.h> // For size_t

// Define constants for queue size and string length
#define MAX_QUEUE_SIZE 10
#define MAX_STRING_LENGTH 32

/**
 * @class CharPointerQueue
 * @brief A circular queue for storing C-style strings (char pointers).
 *
 * This queue manages char* pointers, allocating memory for new strings
 * when enqueued and freeing that memory when dequeued. It's designed
 * for embedded systems like Arduino due to its use of `malloc` and `free`
 * and fixed-size buffer.
 */
class CharPointerQueue {
public:
    /**
     * @brief Constructor for CharPointerQueue.
     * Initializes the queue, setting head, tail, and count to zero,
     * and all data pointers to nullptr.
     */
    CharPointerQueue();

    /**
     * @brief Destructor for CharPointerQueue.
     * Frees all memory allocated for strings currently in the queue.
     */
    ~CharPointerQueue();

    /**
     * @brief Enqueues a new string into the queue.
     * Allocates memory for a copy of the item and stores its pointer.
     * @param item A C-style string to be added to the queue.
     * @return True if the item was successfully enqueued, false if the queue is full
     * or memory allocation failed.
     */
    bool enqueue(const char* item);

    /**
     * @brief Dequeues an item from the front of the queue.
     * Prints the dequeued string to Serial and frees its allocated memory.
     * If the queue is empty, this function does nothing.
     */
    void dequeue();

    /**
     * @brief Dequeues and processes all elements currently in the queue.
     * Calls `dequeue()` repeatedly until the queue is empty, printing and
     * freeing each element.
     */
    void dequeueAllElements();

    /**
     * @brief Checks if the queue is empty.
     * @return True if the queue contains no elements, false otherwise.
     */
    bool isEmpty() const;

    /**
     * @brief Checks if the queue is full.
     * @return True if the queue has reached its maximum capacity, false otherwise.
     */
    bool isFull() const;

    /**
     * @brief Returns the current number of elements in the queue.
     * @return The number of elements currently stored in the queue.
     */
    size_t size() const;

private:
    char* _data[MAX_QUEUE_SIZE]; // Array to store char* pointers
    // Using volatile for these members, as suggested in the original code,
    // implies they might be accessed by ISRs or other threads.
    // For typical Arduino single-threaded loop, 'volatile' might not be strictly
    // necessary for size_t but doesn't hurt.
    volatile size_t _head;       // Index of the front element
    volatile size_t _tail;       // Index where the next element will be added
    volatile size_t _count;      // Current number of elements
};

#endif // CHAR_POINTER_QUEUE_H