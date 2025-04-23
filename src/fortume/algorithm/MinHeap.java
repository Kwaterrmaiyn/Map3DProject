package fortume.algorithm;

import java.lang.reflect.Array;

public class MinHeap <T extends Comparable<T>> {


    private T[] items;
    private int capacity;
    private int count;

    public int getCapacity() {
        return capacity;
    }

    public int getCount() {
        return count;
    }

    public MinHeap(int capacity, Class<T> cls) {
        if (capacity < 2) {
            capacity = 2;
        }
        this.capacity = capacity;
        @SuppressWarnings("unchecked") final T[] temp = (T[]) Array.newInstance(cls, capacity);
        items = temp;
        count = 0;
    }

    public boolean Insert(T obj) {
        if (count == capacity)
            return false;
        items[count] = obj;
        count++;
        PercolateUp(count - 1);
        return true;
    }

    public T Pop() throws Exception {
        if (count == 0)
            throw new Exception("Min heap is empty");
        if (count == 1) {
            count--;
            return items[count];
        }

        T min = items[0];
        items[0] = items[count - 1];
        count--;
        PercolateDown(0);
        return min;
    }

    public T Peek() throws Exception {
        if (count == 0)
            throw new Exception("Min heap is empty");
        return items[0];
    }

    //TODO: stop using the remove on the heap as it goes o(N^2)

    public boolean Remove(T item) {
        int index = -1;
        for (int i = 0; i < count; i++) {
            if (items[i].equals(item)) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return false;

        count--;
        Swap(index, count);
        if (LeftLessThanRight(index, (index - 1) / 2))
            PercolateUp(index);
        else
            PercolateDown(index);
        return true;
    }

    private void PercolateDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < count && LeftLessThanRight(left, largest))
                largest = left;
            if (right < count && LeftLessThanRight(right, largest))
                largest = right;
            if (largest == index)
                return;
            Swap(index, largest);
            index = largest;
        }
    }

    private void PercolateUp(int index) {
        while (true) {
            if (index >= count || index <= 0)
                return;
            int parent = (index - 1) / 2;

            if (LeftLessThanRight(parent, index))
                return;

            Swap(index, parent);
            index = parent;
        }
    }

    private boolean LeftLessThanRight(int left, int right) {
        return items[left].compareTo(items[right]) < 0;
    }

    private void Swap(int left, int right) {
        T temp = items[left];
        items[left] = items[right];
        items[right] = temp;
    }
}
