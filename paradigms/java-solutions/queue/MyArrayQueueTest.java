package queue;

public class MyArrayQueueTest {
    public static void fill(ArrayQueue queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(prefix + i);
        }
    }

    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.dequeue());
        }
        System.out.println("------");
    }

    public static void main(String[] args) {
        ArrayQueue queue1 = new ArrayQueue();
        ArrayQueue queue2 = new ArrayQueue();
        fill(queue1, "q1_");
        fill(queue2, "q2_");
        dump(queue1);
        dump(queue2);
    }
}
