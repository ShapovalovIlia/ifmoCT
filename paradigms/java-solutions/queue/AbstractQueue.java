package queue;

public abstract class AbstractQueue implements Queue {

    protected int size;

    protected abstract int indexOfImpl(Object element);

    protected abstract int lastIndexOfImpl(Object element);

    protected abstract Object dequeueImpl();

    protected abstract void enqueueImpl(Object element);

    protected abstract void clearImpl();

    protected abstract Object elementImpl();

    protected abstract Object[] toArrayImpl();

    public void enqueue(Object element) {
        assert element != null;
        enqueueImpl(element);
        size++;
    }

    public Object dequeue() {
        assert size > 0;
        size--;
        return dequeueImpl();
    }

    public void clear() {
        size = 0;
        clearImpl();
    }

    public int size() {
        return size;
    }

    public Object element() {
        assert size > 0;
        return elementImpl();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Object[] toArray() {
        return toArrayImpl();
    }

    public int indexOf(Object element) {
        assert element != null;
        return indexOfImpl(element);
    }
    public int lastIndexOf(Object element) {
        assert element != null;
        return lastIndexOfImpl(element);
    }

}
