package queue;

import java.util.Arrays;

public class MyQueueTest {
    public static void fill(Queue queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(prefix + i);
        }
    }

    public static void dump(Queue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.dequeue());
        }
        System.out.println("------");
    }

    public static void main(String[] args) {
        Queue queue1 = new ArrayQueue();
        Queue queue2 = new LinkedQueue();
        fill(queue1, "q1_");
        fill(queue2, "q2_");
        dump(queue1);
        dump(queue2);
    }
}
