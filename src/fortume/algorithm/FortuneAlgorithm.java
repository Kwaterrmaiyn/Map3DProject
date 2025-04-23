package fortume.algorithm;

// https://github.com/Zalgo2462/VoronoiLib/tree/master/VoronoiLib

import data.structures.MyLinkedList;
import data.structures.MyLinkedListNode;
import map.structures.HeightMap;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;;

public class FortuneAlgorithm<PointType extends ToFortuneSiteConvertable> {
    private ArrayList<FortuneSite> sites;
    private MyLinkedList<VEdge> edges;
    public static int numPoints;

    public FortuneAlgorithm(ArrayList<PointType> points) {
        sites = new ArrayList<>();
        numPoints = 0;
        if (points != null)
            for (PointType point : points) {
                sites.add(point.toFortuneSite());
            }
    }

    public void Run( double minX, double minY,
                                          double maxX, double maxY) {
        MinHeap<FortuneEvent> eventQueue = new MinHeap<FortuneEvent>(5 * sites.size(), FortuneEvent.class);
        for(FortuneSite s : sites)
        {
            eventQueue.Insert(new FortuneSiteEvent(s));
        }
        //init tree
        BeachLine beachLine = new BeachLine();
        edges = new MyLinkedList<>();
        HashSet<FortuneCircleEvent> deleted = new HashSet<>();

        //init edge list
        while (eventQueue.getCount() != 0) {
            FortuneEvent fEvent = null;
            try {
                fEvent = eventQueue.Pop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fEvent.getClass().getSimpleName().equals("FortuneSiteEvent"))
                beachLine.AddBeachSection((FortuneSiteEvent) fEvent, eventQueue, deleted, edges);
            else
            {
                if (deleted.contains((FortuneCircleEvent) fEvent)) {
                    deleted.remove((FortuneCircleEvent) fEvent);
                } else {
                    beachLine.RemoveBeachSection((FortuneCircleEvent) fEvent, eventQueue, deleted, edges);
                }
            }
        }


        //clip edges
        MyLinkedListNode<VEdge> edgeNode = edges.getFirst();
        while (edgeNode != null) {
            VEdge edge = edgeNode.getValue();
            MyLinkedListNode<VEdge> next = edgeNode.getNext();

            boolean valid = ClipEdge(edge, minX, minY, maxX, maxY);
            if (!valid)
                edges.remove(edgeNode);
            //advance
            edgeNode = next;
        }
    }

    public void addEdgesToImage(BufferedImage img)
    {
        Graphics2D gr = img.createGraphics();
        for (VEdge edge:edges){
            edge.draw(gr);
        }
    }

    //combination of personal ray clipping alg and cohen sutherland
    private static boolean ClipEdge(VEdge edge, double minX, double minY, double maxX, double maxY) {
        boolean accept = false;

        //if its a ray
        if (edge.getEnd() == null) {
            accept = ClipRay(edge, minX, minY, maxX, maxY);
        } else {
            //Cohen–Sutherland
            int start = ComputeOutCode(edge.getStart().getX(), edge.getStart().getY(), minX, minY, maxX, maxY);
            int end = ComputeOutCode(edge.getEnd().getX(), edge.getEnd().getY(), minX, minY, maxX, maxY);

            while (true) {
                if ((start | end) == 0) {
                    accept = true;
                    break;
                }
                if ((start & end) != 0) {
                    break;
                }

                double x = -1, y = -1;
                int outcode = start != 0 ? start : end;

                if ((outcode & 0x8) != 0) // top
                {
                    x = edge.getStart().getX() + (edge.getEnd().getX() - edge.getStart().getX()) * (maxY - edge.getStart().getY()) /
                            (edge.getEnd().getY() - edge.getStart().getY());
                    y = maxY;
                } else if ((outcode & 0x4) != 0) // bottom
                {
                    x = edge.getStart().getX() + (edge.getEnd().getX() - edge.getStart().getX()) * (minY - edge.getStart().getY()) /
                            (edge.getEnd().getY() - edge.getStart().getY());
                    y = minY;
                } else if ((outcode & 0x2) != 0) //right
                {
                    y = edge.getStart().getY() + (edge.getEnd().getY() - edge.getStart().getY()) * (maxX - edge.getStart().getX()) /
                            (edge.getEnd().getX() - edge.getStart().getX());
                    x = maxX;
                } else if ((outcode & 0x1) != 0) //left
                {
                    y = edge.getStart().getY() + (edge.getEnd().getY() - edge.getStart().getY()) * (minX - edge.getStart().getX()) /
                            (edge.getEnd().getX() - edge.getStart().getX());
                    x = minX;
                }

                if (outcode == start) {
                    edge.setStart (new VPoint(x, y));
                    start = ComputeOutCode(x, y, minX, minY, maxX, maxY);
                } else {
                    edge.setEnd( new VPoint(x, y));
                    end = ComputeOutCode(x, y, minX, minY, maxX, maxY);
                }
            }
        }
        //if we have a neighbor
        if (edge.getNeighbor() != null) {
            //check it
            boolean valid = ClipEdge(edge.getNeighbor(), minX, minY, maxX, maxY);
            //both are valid
            if (accept && valid) {
                edge.setStart(edge.getNeighbor().getEnd());
            }
            //this edge isn't valid, but the neighbor is
            //flip and set
            if (!accept && valid) {
                edge.setStart(edge.getNeighbor().getEnd());
                edge.setEnd (edge.getNeighbor().getStart());
                accept = true;
            }
        }
        return accept;
    }

    private static int ComputeOutCode(double x, double y, double minX, double minY, double maxX, double maxY) {
        int code = 0;
        if (ParabolaMath.ApproxEqual(x, minX) || ParabolaMath.ApproxEqual(x, maxX)) {
        } else if (x < minX)
            code |= 0x1;
        else if (x > maxX)
            code |= 0x2;

        if (ParabolaMath.ApproxEqual(y, minY) || ParabolaMath.ApproxEqual(y, maxY)) {
        } else if (y < minY)
            code |= 0x4;
        else if (y > maxY)
            code |= 0x8;
        return code;
    }

    private static boolean ClipRay(VEdge edge, double minX, double minY, double maxX, double maxY) {
        VPoint start = edge.getStart();
        //horizontal ray
        if (ParabolaMath.ApproxEqual(edge.getSlopeRise(), 0)) {
            if (!Within(start.getY(), minY, maxY))
                return false;
            if (edge.getSlopeRun() > 0 && start.getX() > maxX)
                return false;
            if (edge.getSlopeRun() < 0 && start.getX() < minX)
                return false;
            if (Within(start.getX(), minX, maxX)) {
                if (edge.getSlopeRun() > 0)
                    edge.setEnd(new VPoint(maxX, start.getY()));
                else
                    edge.setEnd(new VPoint(minX, start.getY()));
            } else {
                if (edge.getSlopeRun() > 0) {
                    edge.setStart (new VPoint(minX, start.getY()));
                    edge.setEnd (new VPoint(maxX, start.getY()));
                } else {
                    edge.setStart( new VPoint(maxX, start.getY()));
                    edge.setEnd(new VPoint(minX, start.getY()));
                }
            }
            return true;
        }
        //vertical ray
        if (ParabolaMath.ApproxEqual(edge.getSlopeRun(), 0)) {
            if (start.getX() < minX || start.getX() > maxX)
                return false;
            if (edge.getSlopeRise() > 0 && start.getY() > maxY)
                return false;
            if (edge.getSlopeRise() < 0 && start.getY() < minY)
                return false;
            if (Within(start.getY(), minY, maxY)) {
                if (edge.getSlopeRise() > 0)
                    edge.setEnd( new VPoint(start.getX(), maxY));
                else
                    edge.setEnd(new VPoint(start.getX(), minY));
            } else {
                if (edge.getSlopeRise() > 0) {
                    edge.setStart( new VPoint(start.getX(), minY));
                    edge.setEnd(new VPoint(start.getX(), maxY));
                } else {
                    edge.setStart (new VPoint(start.getX(), maxY));
                    edge.setEnd(new VPoint(start.getX(), minY));
                }
            }
            return true;
        }

        //works for outside
        if (edge.getSlope() != null)
            System.out.println("edge.Slope != null");
        if (edge.getIntercept() != null)
            System.out.println("edge.Intercept != null");
        double slope = edge.getSlope().doubleValue();
        double intercept =  edge.getIntercept().doubleValue();
        VPoint topX = new VPoint(CalcX(slope, maxY, intercept), maxY);
        VPoint bottomX = new VPoint(CalcX(slope, minY,intercept), minY);
        VPoint leftY = new VPoint(minX, CalcY(slope, minX, intercept));
        VPoint rightY = new VPoint(maxX, CalcY(slope, maxX, intercept));

        //reject intersections not within bounds
        ArrayList<VPoint> candidates = new ArrayList<>();
        if (Within(topX.getX(), minX, maxX))
            candidates.add(topX);
        if (Within(bottomX.getX(), minX, maxX))
            candidates.add(bottomX);
        if (Within(leftY.getY(), minY, maxY))
            candidates.add(leftY);
        if (Within(rightY.getY(), minY, maxY))
            candidates.add(rightY);

        //reject candidates which don't align with the slope
        for (int i = candidates.size() - 1; i > -1; i--) {
            VPoint candidate = candidates.get(i);
            //grab vector representing the edge
            double ax = candidate.getX() - start.getX();
            double ay = candidate.getY() - start.getY();
            if (slope * ax + edge.getSlopeRise() * ay < 0)
                candidates.remove(i);
        }

        //if there are two candidates we are outside the closer one is start
        //the further one is the end
        if (candidates.size() == 2) {
            double ax = candidates.get(0).getX() - start.getX();
            double ay = candidates.get(0).getY() - start.getY();
            double bx = candidates.get(1).getX() - start.getX();
            double by = candidates.get(1).getY() - start.getY();
            if (ax * ax + ay * ay > bx * bx + by * by) {
                edge.setStart(candidates.get(1));
                edge.setEnd(candidates.get(0));
            } else {
                edge.setStart(candidates.get(0));
                edge.setEnd(candidates.get(1));
            }
        }

        //if there is one candidate we are inside
        if (candidates.size() == 1)
            edge.setEnd ( candidates.get(0));
        //there were no candidates
        return edge.getEnd() != null;
    }

    private static boolean Within(double x, double a, double b) {
        return ParabolaMath.ApproxGreaterThanOrEqualTo(x, a) && ParabolaMath.ApproxLessThanOrEqualTo(x, b);
    }

    private static double CalcY(double m, double x, double b) {
        return m * x + b;
    }

    private static double CalcX(double m, double y, double b) {
        return (y - b) / m;
    }
    
    public ResultForOpenGL getResultForOpenGL(HeightMap map){
        ArrayList<Vector3f> vertexes = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector3i> indexes = new ArrayList<>();
        int i=0;
        for (FortuneSite site: sites){
            if (!site.isSeen()) {
                vertexes.add(site.getVector());
                site.setIndex(i++);
            }
        }
        /*for(VEdge edge:edges) {
            VPoint start = edge.getStart();
            VPoint end = edge.getEnd();
            VPoint left = edge.getLeft();
            VPoint right = edge.getRight();
            if (!start.isSeen()) {
                vertexes.add(start.getVector());
                start.setIndex(i++);
            }
            if (!end.isSeen()) {
                vertexes.add(end.getVector());
                end.setIndex(i++);
            }
            if (!left.isSeen()) {
                vertexes.add(left.getVector());
                left.setIndex(i++);
            }
            if (!right.isSeen()) {
                vertexes.add(right.getVector());
                right.setIndex(i++);
            }
        }

         */
        ArrayList<Vector3f> colors = map.updateVertexes(vertexes);
        //FortuneSite site = sites.get(6);
        for (FortuneSite site :sites)
        {
            site.reOrderNeighbours();
            ArrayList<FortuneSite> neighbours = site.getNeighbors();
            int ind2 = site.getIndex();
            int n = neighbours.size();
            for (int j=1; j<n; j++){
                int ind1 = neighbours.get(j-1).getIndex(), ind3 = neighbours.get(j).getIndex();
                indexes.add(new Vector3i(ind1, ind2, ind3));
                normals.add(ResultForOpenGL.getNormal(vertexes.get(ind1), vertexes.get(ind2), vertexes.get(ind3)));
                indexes.add(new Vector3i(ind3, ind2, ind1));
                normals.add(ResultForOpenGL.getNormal(vertexes.get(ind3), vertexes.get(ind2), vertexes.get(ind1)));
            }
            int ind1 = neighbours.get(0).getIndex(), ind3 = neighbours.get(n-1).getIndex();
            indexes.add(new Vector3i(ind1, ind2, ind3));
            normals.add(ResultForOpenGL.getNormal(vertexes.get(ind1), vertexes.get(ind2), vertexes.get(ind3)));
            indexes.add(new Vector3i(ind3, ind2, ind1));
            normals.add(ResultForOpenGL.getNormal(vertexes.get(ind3), vertexes.get(ind2), vertexes.get(ind1)));
        }
       // VEdge edge = edges.getFirst().getValue();
        /*for(VEdge edge:edges) {
            VPoint start = edge.getStart();
            VPoint end = edge.getEnd();
            VPoint left = edge.getLeft();
            VPoint right = edge.getRight();
            int ind1 = start.getIndex(), ind2 = end.getIndex(), ind3 = left.getIndex();
            indexes.add(new Vector3i(ind1, ind2, ind3));
            normals.add(ResultForOpenGL.getNormal(vertexes.get(ind1), vertexes.get(ind2), vertexes.get(ind3)));
            indexes.add(new Vector3i(ind3, ind2, ind1));
            normals.add(ResultForOpenGL.getNormal(vertexes.get(ind3), vertexes.get(ind2), vertexes.get(ind1)));
            ind3 = right.getIndex();
            indexes.add(new Vector3i(ind2, ind1, ind3));
            normals.add(ResultForOpenGL.getNormal(vertexes.get(ind2), vertexes.get(ind1), vertexes.get(ind3)));
            indexes.add(new Vector3i(ind3, ind1, ind2));
            normals.add(ResultForOpenGL.getNormal(vertexes.get(ind3), vertexes.get(ind1), vertexes.get(ind2)));
        }

         */


       /* vertexes = new ArrayList<>();
        vertexes.add(new Vector3f(-0.9f, 0.0f, 0.9f));
        vertexes.add(new Vector3f(0.9f, 0.0f, 0.9f));
        vertexes.add(new Vector3f(0.9f, 0.0f, -0.9f));
        vertexes.add(new Vector3f(-0.9f, 0.0f, -0.9f));
        vertexes.add(new Vector3f(0.0f, 1.5f, 0.0f));
        indexes = new ArrayList<>();
        normals = new ArrayList<>();
        indexes.add(new Vector3i(0,4,1));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(0), vertexes.get(4), vertexes.get(1)));
         indexes.add(new Vector3i(1,4,2));
       normals.add(ResultForOpenGL.getNormal(vertexes.get(1), vertexes.get(4), vertexes.get(2)));
        indexes.add(new Vector3i(2,4,3));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(2), vertexes.get(4), vertexes.get(3)));
        indexes.add(new Vector3i(3,4,0));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(3), vertexes.get(4), vertexes.get(0)));

        indexes.add(new Vector3i(1,4,0));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(0), vertexes.get(4), vertexes.get(1)));

        indexes.add(new Vector3i(2,4,1));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(2), vertexes.get(4), vertexes.get(1)));
        indexes.add(new Vector3i(3,4,2));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(3), vertexes.get(4), vertexes.get(2)));
        indexes.add(new Vector3i(0,4,3));
        normals.add(ResultForOpenGL.getNormal(vertexes.get(0), vertexes.get(4), vertexes.get(3)));
*/

        ResultForOpenGL res = new ResultForOpenGL(vertexes, normals, indexes, /*colors*/null);
        return res;
    }
}
