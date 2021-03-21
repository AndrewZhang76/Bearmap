package bearmaps.utils.ps;

import bearmaps.AugmentedStreetMapGraph;
import bearmaps.utils.ps.Point;
import bearmaps.utils.ps.PointSet;

import java.util.HashSet;
import java.util.List;

public class KDTree implements PointSet {
    private static final boolean rotationboolean = false;
    private Node kdtree;
    private HashSet<Point> pointSet;

    private class Node{
        private Point point;
        private boolean rotation  ;
        private Node leftChild;
        private Node rightChild;

        Node(Point point) {
            this.point = point;
        }
        public int compareTo(Node other){
            double ax = point.getX();
            double ay = point.getY();
            double bx = other.point.getX();
            double by = other.point.getY();

            if (rotation) {
                return Double.compare(this.point.getY(), other.point.getY());
            } else {
                return Double.compare(this.point.getX(), other.point.getX());
                }
        }
        public int compareTo(Point point){
            return compareTo(new Node(point));
        }
    }

    public KDTree(List<Point> points){
       for (Point point : points){
           kdtree = helper(point,kdtree,rotationboolean);
       }
    }

    private Node helper(Point p, Node n, boolean rotationboolean){
        if(n==null){
            n = new Node(p);
            n.rotation = rotationboolean;
            return n;

        }
        int c = n.compareTo(p);
        if(c>0){
            n.leftChild = helper(p,n.leftChild, !n.rotation);
        } else {
            n.rightChild = helper(p,n.rightChild,!n.rotation);
        }
        return n;
    }
    @Override
    public Point nearest(double x, double y) {
        return nearesthelper(kdtree, new Node(new Point(x,y)),kdtree).point;
    }

    private Node nearesthelper(Node n, Node target, Node nearestNode){
        if (n == null){
            return nearestNode;
        }
        if (Point.distance(n.point, target.point) < Point.distance(nearestNode.point, target.point)) {
            nearestNode = n;
        }
        int compare = n.compareTo(target);
        Node usefulside;
        Node badside;
        if(compare>0){
            usefulside = n.leftChild;
            badside = n.rightChild;
        }
        else {
            usefulside = n.rightChild;
            badside = n.leftChild;
        }
        nearestNode = nearesthelper(usefulside, target, nearestNode);
        Point temp;
        if (n.rotation){
            temp = new Point(target.point.getX(),n.point.getY());
        } else {
            temp = new Point(n.point.getX(),target.point.getY());
        }
        double distBad = Point.distance(temp,target.point);
        if(Point.distance(nearestNode.point,target.point)>distBad){
            nearestNode = nearesthelper(badside,target,nearestNode);
        }
        return nearestNode;
    }

}
