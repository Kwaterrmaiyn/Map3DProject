package data.structures;

public class MyLinkedListNode<T> {
    T value;
    MyLinkedListNode<T> next;
    MyLinkedListNode<T> prev;

    public MyLinkedListNode(T info) {
        this.value = info;
        next = null;
        this.prev = null;
    }

    public void setNext(MyLinkedListNode<T> next) {
        this.next = next;
    }

    public void setPrev(MyLinkedListNode<T> prev) {
        this.prev = prev;
    }

    public MyLinkedListNode getNext() {
        return next;
    }
    public MyLinkedListNode getPrev() {
        return prev;
    }

    public T getValue() {
        return value;
    }
}

