package queue;

import java.security.KeyStore;

public interface Queue {
    // Model: a[0]..a[n - 1]
    // Inv: n >= 0 && for all i = 0..n - 1: a[i] != null
    // Let: Immutable(k): for all i = 0..k - 1: a'[i] = a[i]

    // Pred: element != null
    // Post: n' = n + 1 && a[n'] = element &&
    //       Immutable(n)
    void enqueue(Object element);

    // Pred: n > 0
    // Post: R = a[0] && n' = n && Immutable(n)
    Object element();

    // Pred: n > 0
    // Post: R = a[0] && n' = n - 1 &&
    //       Immutable(n')
    Object dequeue();


    // Pred: True
    // Post: R = (n = 0) && n' = n && Immutable(n)
    boolean isEmpty();

    // Pred: True
    // Post: n' = 0
    void clear();

    // Pred: True
    // Post: R = n && n' = n && Immutable(n)
    int size();

    // Pred: True
    // Post: R = a[0..n-1]
    Object[] toArray();

    // Pred: True
    // Post: R = (min i: a[i] = element) || (-1 if for all i: a[i] != element)
    int indexOf(Object element);

    // Pred: True
    // Post: R = (max i: a[i] = element) || (-1 if for all i: a[i] != element)
    int lastIndexOf(Object element);
}
