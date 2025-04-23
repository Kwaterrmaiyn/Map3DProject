package data.structures;

import java.util.*;

public class MyLinkedList<T> implements Iterable<T> {
    MyLinkedListNode<T> first;
    MyLinkedListNode<T> last;
    int count;

    public MyLinkedList() {
        first = last = null;
        count = 0;
    }

    public MyLinkedListNode<T> getFirst(){return first;}

    public void addFirst(T info){
        count++;
        MyLinkedListNode<T> node = new MyLinkedListNode<>(info);
        if (first == null){
            first = last = node;
            return;
        }
        first.setPrev(node);
        node.setNext(first);
        first = node;
    }


    public ArrayList<String> toList() {
        ArrayList<String> res = new ArrayList<>();
        for (MyLinkedListNode<T> cur = first; cur != null; cur = cur.getNext())
            res.add(cur.getValue().toString());
        return res;
    }

    public void remove(MyLinkedListNode<T> node){
        count --;
        MyLinkedListNode<T> prev = node.getPrev();
        MyLinkedListNode<T> next = node.getNext();
        if (prev == null && next == null) {
            first = last = null;
            count = 0;
            return;
        }
        if (prev == null){
            first = next;
            next.setPrev(null);
            return;
        }
        if (next == null){
            last = prev;
            prev.setNext(null);
            return;
        }
        prev.setNext(next);
        next.setPrev(prev);
    }



    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private MyLinkedListNode<T> cur = first;

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public T next() {
                MyLinkedListNode<T> prev = cur;
                cur = cur.getNext();
                return prev.getValue();
            }
        };
    }

    public int size(){return count;}
}



