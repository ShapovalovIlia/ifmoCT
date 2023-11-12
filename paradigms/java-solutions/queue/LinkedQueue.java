package queue;

import java.util.Objects;

public class LinkedQueue extends AbstractQueue {

    private Node head;
    private Node tail;

    @Override
    public void enqueueImpl(Object element) {
        Node newNode = tail;
        tail = new Node(Objects.requireNonNull(element), null);
        if (isEmpty()) {
            head = tail;
        } else {
            newNode.next = tail;
        }
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public int size() {
        return super.size();
    }



    @Override

    public Object elementImpl() {
        return head.value;
    }

    @Override
    protected int indexOfImpl(Object element) {
        int counter = 0;
        if (head == null) {
            return -1;
        }
        Node index = new Node(head.value, head.next);
        while (counter < size) {
            if (index == null) {
                return -1;
            }
            if (index.value.equals(element)) {
                return counter;
            }
            index = index.next;
            counter++;
        }
        return -1;
    }

    @Override
    protected int lastIndexOfImpl(Object element) {
        int counter = 0;
        int result = -1;
        if (head == null) {
            return -1;
        }
        Node index = new Node(head.value, head.next);
        while (counter < size) {
            if (index == null) {
                return -1;
            }
            if (index.value.equals(element)) {
                result = counter;
            }
            index = index.next;
            counter++;
        }
        return result;
    }

    public Object dequeueImpl() {
        Node newNode = head;
        head = head.next;
        return newNode.value;
    }


    public void clearImpl() {
        head = null;
        tail = null;
    }

    public Object[] toArrayImpl() {
        Object[] arr = new Object[size];
        for (int i = 0; i < size; i++) {
            arr[i] = head.value;
            head = head.next;
        }
        return arr;
    }

    private static class Node {
        private final Object value;
        private Node next;

        public Node(Object value, Node next) {
            assert value != null;

            this.value = value;
            this.next = next;
        }
    }


}
