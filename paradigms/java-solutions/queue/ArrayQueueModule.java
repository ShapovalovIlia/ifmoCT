package queue;

import java.util.Objects;

// Model: a[0]..a[n - 1]
// Inv: n >= 0 && for all i = 0..n - 1: a[i] != null
// Let: Immutable(k): for all i = 0..k - 1: a'[i] = a[i]

public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int head;
    private static int tail;

    // Pred: element != null
    // Post: n' = n + 1 && a[n'] = element &&
    //       Immutable(n)
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size() + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    // Pred: True
    // Post: n' = n && Immutable(n)

    private static Object[] copyElements(int newArrayCapacity) {
        Object[] copyElementsArray = new Object[newArrayCapacity];
        int index = 0;
        for (int i = head; i != tail; i = (i + 1) % elements.length) {
            copyElementsArray[index++] = elements[i];
        }
        return copyElementsArray;
    }

    // Pred: True
    // Post: n' = n && Immutable(n)
    private static void ensureCapacity(int capacity) {
        if (elements.length <= capacity) {
            int size = size();
            elements = copyElements(elements.length * 2);
            tail = size;
            head = 0;
        }
    }

    // Pred: n > 0
    // Post: R = a[0] && n' = n && Immutable(n)

    public static Object element() {
        assert size() > 0;
        return elements[head];
    }


    // Pred: n > 0
    // Post: R = a[0] && n' = n - 1 &&
    //       Immutable(n')
    public static Object dequeue() {
        assert size() > 0;

        Object element = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        return element;
    }

    // Pred: True
    // Post: R = n && n' = n && Immutable(n)

    public static int size() {
        if (head > tail) {
            return elements.length - head + tail;
        } else {
            return tail - head;
        }

    }

    // Pred: True
    // Post: R = (n = 0) && n' = n && Immutable(n)

    public static boolean isEmpty() {
        return head == tail;
    }

    // Pred: True
    // Post: n' = 0

    public static void clear() {
        for (int i = head; i != tail; i = (i + 1) % elements.length) {
            elements[i] = null;
        }
        head = 0;
        tail = 0;
    }

    // Pred: True
    // Post: R = a[0...n-1]

    public static Object[] toArray() {
        return copyElements(size());
    }
}
