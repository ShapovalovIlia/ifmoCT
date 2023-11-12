package queue;

import java.util.Objects;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[5];
    private int head;
    private int tail;


    public void enqueueImpl(Object element) {
        ensureCapacity(size() + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    private Object[] copyElements(int newArrayCapacity) {
        Object[] copyElementsArray = new Object[newArrayCapacity];
        int index = 0;
        for (int i = head; i != tail; i = (i + 1) % elements.length) {
            copyElementsArray[index++] = elements[i];
        }
        return copyElementsArray;
    }

    private void ensureCapacity(int capacity) {
        if (elements.length <= capacity) {
            int size = size();
            elements = copyElements(elements.length * 2);
            tail = size;
            head = 0;
        }
    }


    public Object elementImpl() {
        assert size() > 0;
        return elements[head];
    }


    public Object dequeueImpl() {

        Object element = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        return element;
    }


    public void clearImpl() {
        for (int i = head; i != tail; i = (i + 1) % elements.length) {
            elements[i] = null;
        }
        head = 0;
        tail = 0;
    }

    public Object[] toArrayImpl() {
        return copyElements(size());
    }

    @Override
    public int indexOfImpl(Object element) {
        int counter = 0;
        for (int i = head; i != tail; i = (i + 1) % elements.length) {
            if (elements[i].equals(element)) {
                return counter;
            }
            counter++;
        }
        return -1;
    }

    @Override
    public int lastIndexOfImpl(Object element) {
        int result = -1;
        int counter = 0;
        for (int i = head; i != tail; i = (i + 1) % elements.length) {
            if (elements[i].equals(element)) {
                result = counter;
            }
            counter++;
        }
        return result;
    }
}
