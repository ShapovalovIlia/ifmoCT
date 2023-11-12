package queue;

import java.util.Objects;

// Model: a[0]..a[n - 1]
// Inv: n >= 0 && for all i = 0..n - 1: a[i] != null
// Let: Immutable(k): for all i = 0..k - 1: a'[i] = a[i]

public class ArrayQueueADT {
    private Object[] elements = new Object[5];
    private int head;
    private int tail;



    // Pred: element != null && queue != null;
    // Post: n' = n + 1 && a[n'] = element &&
    //       Immutable(n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(queue,size(queue) + 1);
        queue.elements[queue.tail] = element;
        queue.tail = (queue.tail + 1) % queue.elements.length;
    }

    // Pred: True
    // Post: n' = n && Immutable(n)

    private static Object[] copyElements(ArrayQueueADT queue, int newArrayCapacity) {
        Object[] copyElementsArray = new Object[newArrayCapacity];
        int index = 0;
        for (int i = queue.head; i != queue.tail; i = (i + 1) % queue.elements.length) {
            copyElementsArray[index++] = queue.elements[i];
        }
        return copyElementsArray;
    }

    // Pred: True
    // Post: n' = n && Immutable(n)
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (queue.elements.length <= capacity) {
            int size = size(queue);
            queue.elements = copyElements(queue, queue.elements.length * 2);
            queue.tail = size;
            queue.head = 0;
        }
    }

    // Pred: n > 0
    // Post: R = a[0] && n' = n && Immutable(n)

    public static Object element(ArrayQueueADT queue) {
        assert size(queue) > 0;
        return queue.elements[queue.head];
    }


    // Pred: n > 0
    // Post: R = a[0] && n' = n - 1 &&
    //       Immutable(n')
    public static Object dequeue(ArrayQueueADT queue) {
        assert size(queue) > 0;

        Object element = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        return element;
    }

    // Pred: True
    // Post: R = n && n' = n && Immutable(n)

    public static int size(ArrayQueueADT queue) {
        if (queue.head > queue.tail) {
            return queue.elements.length - queue.head + queue.tail;
        } else {
            return queue.tail - queue.head;
        }

    }

    // Pred: True
    // Post: R = (n = 0) && n' = n && Immutable(n)

    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.head == queue.tail;
    }

    // Pred: True
    // Post: n' = 0

    public static void clear(ArrayQueueADT queue) {
        for (int i = queue.head; i != queue.tail; i = (i + 1) % queue.elements.length) {
            queue.elements[i] = null;
        }
        queue.head = 0;
        queue.tail = 0;
    }

    // Pred: True
    // Post: R = a[0...n-1]

    public static Object[] toArray(ArrayQueueADT queue) {
        return copyElements(queue, size(queue));
    }
}
