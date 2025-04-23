package fortume.algorithm;

public class RBTreeNode<T> {

    private T data;
    private RBTreeNode<T> left;
    private RBTreeNode<T> right;
    private RBTreeNode<T> previous;
    private RBTreeNode<T> next;
    private boolean red;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public RBTreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(RBTreeNode<T> left) {
        this.left = left;
    }

    public RBTreeNode<T> getRight() {
        return right;
    }

    public void setRight(RBTreeNode<T> right) {
        this.right = right;
    }

    private RBTreeNode<T> parent;

    public RBTreeNode<T> getParent() {
        return parent;
    }

    public void setParent(RBTreeNode<T> parent) {
        this.parent = parent;
    }

    public RBTreeNode<T> getPrevious() {
        return previous;
    }

    public void setPrevious(RBTreeNode<T> previous) {
        this.previous = previous;
    }

    public RBTreeNode<T> getNext() {
        return next;
    }

    public void setNext(RBTreeNode<T> next) {
        this.next = next;
    }

    public boolean isRed() {
        return red;
    }

    public void setRed(boolean red) {
        this.red = red;
    }
    public boolean hasNext(){return next!=null;}
    public boolean hasPrevious(){return previous!=null;}
    public boolean hasRight(){return right!=null;}
    public boolean hasLeft(){return left!=null;}

    public RBTreeNode(T data) {
        this.data = data;

    }
}

