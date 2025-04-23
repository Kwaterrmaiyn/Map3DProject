package fortume.algorithm;

public class RBTree<T> {

    private RBTreeNode<T> root;

    public RBTreeNode<T> getRoot() {
        return root;
    }

    public RBTreeNode<T> InsertSuccessor(RBTreeNode<T> node, T successorData) {
        RBTreeNode<T> successor = new RBTreeNode<T>(successorData);
        RBTreeNode<T> parent;

        if (node != null) {
            //insert new node between node and its successor
            successor.setPrevious(node);
            successor.setNext(node.getNext());
            if (node.hasNext())
                node.getNext().setPrevious(successor);
            node.setNext(successor);

            //insert successor into the tree
            if (node.hasRight()) {
                node = GetFirst(node.getRight());
                node.setLeft(successor);
            } else {
                node.setRight(successor);
            }
            parent = node;
        } else if (root != null) {
            //if the node is null, successor must be inserted
            //into the left most part of the tree
            node = GetFirst(root);
            //successor.Previous = null;
            successor.setNext(node);
            node.setPrevious(successor);
            node.setLeft(successor);
            parent = node;
        } else {
            //first insert
            //successor.Previous = successor.Next = null;
            root = successor;
            parent = null;
        }

        //successor.Left = successor.Right = null;
        successor.setParent(parent);
        successor.setRed(true);

        //the magic of the red black tree
        RBTreeNode<T> grandma;
        RBTreeNode<T> aunt;
        node = successor;
        while (parent != null && parent.isRed()) {
            grandma = parent.getParent();
            if (parent == grandma.getLeft()) {
                aunt = grandma.getRight();
                if (aunt != null && aunt.isRed()) {
                    parent.setRed(false);
                    aunt.setRed(false);
                    grandma.setRed(true);
                    node = grandma;
                } else {
                    if (node == parent.getRight()) {
                        RotateLeft(parent);
                        node = parent;
                        parent = node.getParent();
                    }
                    parent.setRed(false);
                    grandma.setRed(true);
                    RotateRight(grandma);
                }
            } else {
                aunt = grandma.getLeft();
                if (aunt != null && aunt.isRed()) {
                    parent.setRed(false);
                    aunt.setRed(false);
                    grandma.setRed(true);
                    node = grandma;
                } else {
                    if (node == parent.getLeft()) {
                        RotateRight(parent);
                        node = parent;
                        parent = node.getParent();
                    }
                    parent.setRed(false);
                    grandma.setRed(true);
                    RotateLeft(grandma);
                }
            }
            parent = node.getParent();
        }
        root.setRed(false);
        return successor;
    }

    //TODO: Clean this up
    public void RemoveNode(RBTreeNode<T> node) {
        //fix up linked list structure
        if (node.hasNext())
            node.getNext().setPrevious(node.getPrevious());
        if (node.hasPrevious())
            node.getPrevious().setNext(node.getNext());

        //replace the node
        RBTreeNode<T> original = node;
        RBTreeNode<T> parent = node.getParent();
        RBTreeNode<T> left = node.getLeft();
        RBTreeNode<T> right = node.getRight();

        RBTreeNode<T> next;
        //figure out what to replace this node with
        if (left == null)
            next = right;
        else if (right == null)
            next = left;
        else
            next = GetFirst(right);

        //fix up the parent relation
        if (parent != null) {
            if (parent.getLeft() == node)
                parent.setLeft(next);
            else
                parent.setRight(next);
        } else {
            root = next;
        }

        boolean red;
        if (left != null && right != null) {
            red = next.isRed();
            next.setRed(node.isRed());
            next.setLeft(left);
            left.setParent(next);

            // if we reached down the tree
            if (next != right) {
                parent = next.getParent();
                next.setParent(node.getParent());

                node = next.getRight();
                parent.setLeft(node);

                next.setRight(right);
                right.setParent(next);
            } else {
                // the direct right will replace the node
                next.setParent(parent);
                parent = next;
                node = next.getRight();
            }
        } else {
            red = node.isRed();
            node = next;
        }

        if (node != null) {
            node.setParent(parent);
        }

        if (red) {
            return;
        }

        if (node != null && node.isRed()) {
            node.setRed(false);
            return;
        }

        //node is null or black
        // fair warning this code gets nasty
        //how do we guarantee sibling is not null
        RBTreeNode<T> sibling = null;
        do {
            if (node == root)
                break;
            if (node == parent.getLeft()) {
                sibling = parent.getRight();
                if (sibling.isRed()) {
                    sibling.setRed(false);
                    parent.setRed(true);
                    RotateLeft(parent);
                    sibling = parent.getRight();
                }
                if ((sibling.hasLeft() && sibling.getLeft().isRed()) ||
                        (sibling.hasRight() && sibling.getRight().isRed())) {
                    //pretty sure this can be sibling.Left!= null && sibling.Left.Red
                    if (!sibling.hasRight() || !sibling.getRight().isRed()) {
                        sibling.getLeft().setRed(false);
                        sibling.setRed(true);
                        RotateRight(sibling);
                        sibling = parent.getRight();
                    }
                    sibling.setRed(parent.isRed());
                    sibling.getRight().setRed(false);
                    parent.setRed(false);
                    RotateLeft(parent);
                    node = root;
                    break;
                }
            } else {
                sibling = parent.getLeft();
                if (sibling.isRed()) {
                    sibling.setRed(false);
                    parent.setRed(true);
                    RotateRight(parent);
                    sibling = parent.getLeft();
                }
                if ((sibling.hasLeft() && sibling.getLeft().isRed()) ||
                        (sibling.hasRight() && sibling.getRight().isRed())) {
                    if (!sibling.hasLeft() || !sibling.getLeft().isRed()) {
                        sibling.getRight().setRed(false);
                        sibling.setRed(true);
                        RotateLeft(sibling);
                        sibling = parent.getLeft();
                    }
                    sibling.setRed(parent.isRed());
                    parent.setRed(false);
                    sibling.getLeft().setRed(false);
                    RotateRight(parent);
                    node = root;
                    break;
                }
            }
            sibling.setRed(true);
            node = parent;
            parent = parent.getParent();
        } while (!node.isRed());

        if (node != null)
            node.setRed(false);

    }

    public  RBTreeNode<T> GetFirst(RBTreeNode<T> node) {
        if (node == null)
            return null;
        while (node.hasLeft())
            node = node.getLeft();
        return node;
    }

    public  RBTreeNode<T> GetLast(RBTreeNode<T> node) {
        if (node == null)
            return null;
        while (node.hasRight())
            node = node.getRight();
        return node;
    }

    private void RotateLeft(RBTreeNode<T> node) {
        RBTreeNode<T> p = node;
        RBTreeNode<T> q = node.getRight();
        RBTreeNode<T> parent = p.getParent();

        if (parent != null) {
            if (parent.getLeft() == p)
                parent.setLeft(q);
            else
                parent.setRight(q);
        } else
            root = q;
        q.setParent(parent);
        p.setParent(q);
        p.setRight(q.getLeft());
        if (p.hasRight())
            p.getRight().setParent(p);
        q.setLeft(p);
    }

    private void RotateRight(RBTreeNode<T> node) {
        RBTreeNode<T> p = node;
        RBTreeNode<T> q = node.getLeft();
        RBTreeNode<T> parent = p.getParent();
        if (parent != null) {
            if (parent.getLeft() == p)
                parent.setLeft(q);
            else
                parent.setRight(q);
        } else
            root = q;
        q.setParent(parent);
        p.setParent(q);
        p.setLeft(q.getRight());
        if (p.hasLeft())
            p.getLeft().setParent(p);
        q.setRight(p);
    }

}

