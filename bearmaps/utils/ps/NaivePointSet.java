package bearmaps.utils.ps;

import bearmaps.utils.ps.Point;
import bearmaps.utils.ps.PointSet;

import java.util.ArrayList;
import java.util.List;

public class     NaivePointSet implements PointSet {

    private List<Point> list;

    public NaivePointSet(List<Point> points){
        list = new ArrayList<>();
        list.addAll(points);
    }
    @Override
    public Point nearest(double x, double y) {
        Point point = list.get(0);
        Point target = new Point(x,y);
        double smallestdist= Point.distance(point,target);
        for(Point p:list){
            double currdist = Point.distance(p, target);
            if(currdist<smallestdist){
                point = p;
                smallestdist = currdist;
            }
        }
        return point;
    }
}
