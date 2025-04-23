package fortume.algorithm;

import data.structures.MyLinkedList;

import java.util.ArrayList;
import java.util.HashSet;
//import java.util.LinkedList;

public class BeachLine {

    private RBTree<BeachSection> beachLine;

    public BeachLine() {
        beachLine = new RBTree<BeachSection>();
    }

    public void AddBeachSection(FortuneSiteEvent siteEvent, MinHeap<FortuneEvent> eventQueue,
                                HashSet<FortuneCircleEvent> deleted, MyLinkedList<VEdge> edges) {
        FortuneSite site = siteEvent.getSite();
        double x = site.getX();
        double directrix = site.getY();

        RBTreeNode<BeachSection> leftSection = null;
        RBTreeNode<BeachSection> rightSection = null;
        RBTreeNode node = beachLine.getRoot();

        //find the parabola(s) above this site
        while (node != null && leftSection == null && rightSection == null) {
            double distanceLeft = LeftBreakpoint(node, directrix) - x;
            if (distanceLeft > 0) {
                //the new site is before the left breakpoint
                if (!node.hasLeft()) {
                    rightSection = node;
                } else {
                    node = node.getLeft();
                }
                continue;
            }

            double distanceRight = x - RightBreakpoint(node, directrix);
            if (distanceRight > 0) {
                //the new site is after the right breakpoint
                if (!node.hasRight()) {
                    leftSection = node;
                } else {
                    node = node.getRight();
                }
                continue;
            }

            //the point lies below the left breakpoint
            if (ParabolaMath.ApproxEqual(distanceLeft, 0)) {
                leftSection = node.getPrevious();
                rightSection = node;
                continue;
            }

            //the point lies below the right breakpoint
            if (ParabolaMath.ApproxEqual(distanceRight, 0)) {
                leftSection = node;
                rightSection = node.getNext();
                continue;
            }

            // distance Right < 0 and distance Left < 0
            // this section is above the new site
            leftSection = rightSection = node;
        }

        //our goal is to insert the new node between the
        //left and right sections
        BeachSection section = new BeachSection(site);

        //left section could be null, in which case this node is the first
        //in the tree
        RBTreeNode<BeachSection> newSection = beachLine.InsertSuccessor(leftSection, section);

        //new beach section is the first beach section to be added
        if (leftSection == null && rightSection == null) {
            return;
        }

        //main case:
        //if both left section and right section point to the same valid arc
        //we need to split the arc into a left arc and a right arc with our
        //new arc sitting in the middle
        if (leftSection != null && leftSection == rightSection) {
            //if the arc has a circle event, it was a false alarm.
            //remove it
            if (leftSection.getData().hasCircleEvent()) {
                deleted.add(leftSection.getData().getCircleEvent());
                leftSection.getData().setNullCircleEvent();
            }

            //we leave the existing arc as the left section in the tree
            //however we need to insert the right section defined by the arc
            BeachSection copy = new BeachSection(leftSection.getData().getSite());
            rightSection = beachLine.InsertSuccessor(newSection, copy);

            //grab the projection of this site onto the parabola
            double y = ParabolaMath.EvalParabola(leftSection.getData().getSite().getX(),
                    leftSection.getData().getSite().getY(), directrix, x);
            VPoint intersection = new VPoint(x, y);

            //create the two half edges corresponding to this intersection
            VEdge leftEdge = new VEdge(intersection, site, leftSection.getData().getSite());
            VEdge rightEdge = new VEdge(intersection, leftSection.getData().getSite(), site);
            leftEdge.setNeighbor(rightEdge);

            //put the edge in the list
            edges.addFirst(leftEdge);

            //store the left edge on each arc section
            newSection.getData().setEdge(leftEdge);
            rightSection.getData().setEdge(rightEdge);

            //store neighbors for delaunay
            leftSection.getData().getSite().getNeighbors().add(newSection.getData().getSite());
            newSection.getData().getSite().getNeighbors().add(leftSection.getData().getSite());

            //create circle events
            CheckCircle(leftSection, eventQueue);
            CheckCircle(rightSection, eventQueue);
        }

        //site is the last beach section on the beach line
        //this can only happen if all previous sites
        //had the same y value
        else if (leftSection != null && rightSection == null) {
            VPoint start = new VPoint((leftSection.getData().getSite().getX() + site.getX()) / 2,
                    -Double.MAX_VALUE);
            VEdge infEdge = new VEdge(start, leftSection.getData().getSite(), site);
            VEdge newEdge = new VEdge(start, site, leftSection.getData().getSite());

            newEdge.setNeighbor(infEdge);
            edges.addFirst(newEdge);

            leftSection.getData().getSite().getNeighbors().add(newSection.getData().getSite());
            newSection.getData().getSite().getNeighbors().add(leftSection.getData().getSite());

            newSection.getData().setEdge(newEdge);

            //cant check circles since they are colinear
        }

        //site is directly above a break point
        else if (leftSection != null && leftSection != rightSection) {
            //remove false alarms
            if (leftSection.getData().hasCircleEvent()) {
                deleted.add(leftSection.getData().getCircleEvent());
                leftSection.getData().setNullCircleEvent();
            }

            if (rightSection.getData().hasCircleEvent()) {
                deleted.add(rightSection.getData().getCircleEvent());
                rightSection.getData().setNullCircleEvent();
            }

            //the breakpoint will dissapear if we add this site
            //which means we will create an edge
            //we treat this very similar to a circle event since
            //an edge is finishing at the center of the circle
            //created by circumscribing the left center and right
            //sites

            //bring a to the origin
            FortuneSite leftSite = leftSection.getData().getSite();
            double ax = leftSite.getX();
            double ay = leftSite.getY();
            double bx = site.getX() - ax;
            double by = site.getY() - ay;

            FortuneSite rightSite = rightSection.getData().getSite();
            double cx = rightSite.getX() - ax;
            double cy = rightSite.getY() - ay;
            double d = bx * cy - by * cx;
            double magnitudeB = bx * bx + by * by;
            double magnitudeC = cx * cx + cy * cy;
            VPoint vertex = new VPoint(
                    (cy * magnitudeB - by * magnitudeC) / (2 * d) + ax,
                    (bx * magnitudeC - cx * magnitudeB) / (2 * d) + ay);

            rightSection.getData().getEdge().setEnd(vertex);

            //next we create a two new edges
            newSection.getData().setEdge(new VEdge(vertex, site, leftSection.getData().getSite()));
            rightSection.getData().setEdge(new VEdge(vertex, rightSection.getData().getSite(), site));

            edges.addFirst(newSection.getData().getEdge());
            edges.addFirst(rightSection.getData().getEdge());

            //add neighbors for delaunay
            newSection.getData().getSite().getNeighbors().add(leftSection.getData().getSite());
            leftSection.getData().getSite().getNeighbors().add(newSection.getData().getSite());

            newSection.getData().getSite().getNeighbors().add(rightSection.getData().getSite());
            rightSection.getData().getSite().getNeighbors().add(newSection.getData().getSite());

            CheckCircle(leftSection, eventQueue);
            CheckCircle(rightSection, eventQueue);
        }
    }

    public void RemoveBeachSection(FortuneCircleEvent circle, MinHeap<FortuneEvent> eventQueue,
                                   HashSet<FortuneCircleEvent> deleted, MyLinkedList<VEdge> edges) {
        RBTreeNode<BeachSection> section = circle.getNodeToDelete();
        double x = circle.getX();
        double y = circle.getyCenter();
        VPoint vertex = new VPoint(x, y);

        //multiple edges could end here
        ArrayList<RBTreeNode<BeachSection>> toBeRemoved = new ArrayList<RBTreeNode<BeachSection>>();

        //look left
        RBTreeNode<BeachSection> prev = section.getPrevious();
        while (prev.getData().hasCircleEvent() &&
                ParabolaMath.ApproxEqual(x - prev.getData().getCircleEvent().getX(), 0) &&
                ParabolaMath.ApproxEqual(y - prev.getData().getCircleEvent().getY(), 0)) {
            toBeRemoved.add(prev);
            prev = prev.getPrevious();
        }

        RBTreeNode<BeachSection> next = section.getNext();
        while (next.getData().hasCircleEvent() &&
                ParabolaMath.ApproxEqual(x - next.getData().getCircleEvent().getX(), 0) &&
                ParabolaMath.ApproxEqual(y - next.getData().getCircleEvent().getY(), 0)) {
            toBeRemoved.add(next);
            next = next.getNext();
        }

        section.getData().getEdge().setEnd(vertex);
        section.getNext().getData().getEdge().setEnd(vertex);
        section.getData().setNullCircleEvent();

        //odds are this double writes a few edges but this is clean...
        for (RBTreeNode<BeachSection> remove : toBeRemoved) {
            remove.getData().getEdge().setEnd(vertex);
            remove.getNext().getData().getEdge().setEnd(vertex);
            deleted.add(remove.getData().getCircleEvent());
            remove.getData().setNullCircleEvent();
        }


        //need to delete all upcoming circle events with this node
        if (prev.getData().hasCircleEvent()) {
            deleted.add(prev.getData().getCircleEvent());
            prev.getData().setNullCircleEvent();
        }
        if (next.getData().hasCircleEvent()) {
            deleted.add(next.getData().getCircleEvent());
            next.getData().setNullCircleEvent();
        }


        //create a new edge with start point at the vertex and assign it to next
        VEdge newEdge = new VEdge(vertex, next.getData().getSite(), prev.getData().getSite());
        next.getData().setEdge(newEdge);
        edges.addFirst(newEdge);

        //add neighbors for delaunay
        prev.getData().getSite().getNeighbors().add(next.getData().getSite());
        next.getData().getSite().getNeighbors().add(prev.getData().getSite());

        //remove the sectionfrom the tree
        beachLine.RemoveNode(section);
        for (RBTreeNode<BeachSection> remove : toBeRemoved) {
            beachLine.RemoveNode(remove);
        }

        CheckCircle(prev, eventQueue);
        CheckCircle(next, eventQueue);
    }

    private static double LeftBreakpoint(RBTreeNode<BeachSection> node, double directrix) {
        RBTreeNode<BeachSection> leftNode = node.getPrevious();
        //degenerate parabola
        if (ParabolaMath.ApproxEqual(node.getData().getSite().getY() - directrix, 0))
            return node.getData().getSite().getX();
        //node is the first piece of the beach line
        if (leftNode == null)
            return Double.NEGATIVE_INFINITY;
        //left node is degenerate
        if (ParabolaMath.ApproxEqual(leftNode.getData().getSite().getY() - directrix, 0))
            return leftNode.getData().getSite().getX();
        FortuneSite site = node.getData().getSite();
        FortuneSite leftSite = leftNode.getData().getSite();
        return ParabolaMath.IntersectParabolaX(leftSite.getX(), leftSite.getY(),
                site.getX(), site.getY(), directrix);
    }

    private static double RightBreakpoint(RBTreeNode<BeachSection> node, double directrix) {
        RBTreeNode<BeachSection> rightNode = node.getNext();
        //degenerate parabola
        if (ParabolaMath.ApproxEqual(node.getData().getSite().getY() - directrix, 0))
            return node.getData().getSite().getX();
        //node is the last piece of the beach line
        if (rightNode == null)
            return Double.POSITIVE_INFINITY;
        //left node is degenerate
        if (ParabolaMath.ApproxEqual(rightNode.getData().getSite().getY() - directrix, 0))
            return rightNode.getData().getSite().getX();
        FortuneSite site = node.getData().getSite();
        FortuneSite rightSite = rightNode.getData().getSite();
        return ParabolaMath.IntersectParabolaX(site.getX(), site.getY(),
                rightSite.getX(), rightSite.getY(), directrix);
    }

    private static void CheckCircle(RBTreeNode<BeachSection> section, MinHeap<FortuneEvent> eventQueue) {
        //if (section == null)
        //    return;
        RBTreeNode<BeachSection> left = section.getPrevious();
        RBTreeNode<BeachSection> right = section.getNext();
        if (left == null || right == null)
            return;

        FortuneSite leftSite = left.getData().getSite();
        FortuneSite centerSite = section.getData().getSite();
        FortuneSite rightSite = right.getData().getSite();

        //if the left arc and right arc are defined by the same
        //focus, the two arcs cannot converge
        if (leftSite == rightSite)
            return;

        // http://mathforum.org/library/drmath/view/55002.html
        // because every piece of this program needs to be demoed in maple >.<

        //MATH HACKS: place center at origin and
        //draw vectors a and c to
        //left and right respectively
        double bx = centerSite.getX(),
                by = centerSite.getY(),
                ax = leftSite.getX() - bx,
                ay = leftSite.getY() - by,
                cx = rightSite.getX() - bx,
                cy = rightSite.getY() - by;

        //The center beach section can only dissapear when
        //the angle between a and c is negative
        double d = ax * cy - ay * cx;
        if (ParabolaMath.ApproxGreaterThanOrEqualTo(d, 0))
            return;

        double magnitudeA = ax * ax + ay * ay;
        double magnitudeC = cx * cx + cy * cy;
        double x = (cy * magnitudeA - ay * magnitudeC) / (2 * d);
        double y = (ax * magnitudeC - cx * magnitudeA) / (2 * d);

        //add back offset
        double ycenter = y + by;
        //y center is off
        FortuneCircleEvent circleEvent = new FortuneCircleEvent(
                new VPoint(x + bx, ycenter + Math.sqrt(x * x + y * y)),
                ycenter, section
        );
        section.getData().setCircleEvent(circleEvent);
        eventQueue.Insert(circleEvent);
    }
}
