

/**
 * Rotating calipers is used by the ShapeMethods class for finding the minimum bounding rectangle of a 2D Convex Hull
 * Adapted to processing from code by Bart Kiers:
 * //https://github.com/bkiers/RotatingCalipers/blob/master/src/main/cg/RotatingCalipers.java
 * 
 */

package greyshed.lib;

import processing.core.*;
//=======Utils=========
import java.util.ArrayList;

public class RotatingCalipers {

    protected enum Corner { UPPER_RIGHT, UPPER_LEFT, LOWER_LEFT, LOWER_RIGHT }

    float getArea(PVector[] rectangle) {

        float deltaXAB = rectangle[0].x - rectangle[1].x;
        float deltaYAB = rectangle[0].y - rectangle[1].y;

        float deltaXBC = rectangle[1].x - rectangle[2].x;
        float deltaYBC = rectangle[1].y - rectangle[2].y;

        float lengthAB = PApplet.sqrt((deltaXAB * deltaXAB) + (deltaYAB * deltaYAB));
        float lengthBC = PApplet.sqrt((deltaXBC * deltaXBC) + (deltaYBC * deltaYBC));

        return lengthAB * lengthBC;
    }

    ArrayList<PVector[]> getAllBoundingRectangles(int[] xs, int[] ys) throws IllegalArgumentException {

        if(xs.length != ys.length) {
            throw new IllegalArgumentException("xs and ys don't have the same size");
        }

        ArrayList<PVector> points = new ArrayList<PVector>();

        for(int i = 0; i < xs.length; i++) {
            points.add(new PVector(xs[i], ys[i]));
        }

        return getAllBoundingRectangles(points);
    }

    ArrayList<PVector[]> getAllBoundingRectangles(ArrayList<PVector> convexHull) throws IllegalArgumentException {

        ArrayList<PVector[]> rectangles = new ArrayList<PVector[]>();

        //ArrayList<PVector> convexHull = GrahamScan.getConvexHull(points);

        Caliper I = new Caliper(convexHull, getIndex(convexHull, Corner.UPPER_RIGHT), 90);
        Caliper J = new Caliper(convexHull, getIndex(convexHull, Corner.UPPER_LEFT), 180);
        Caliper K = new Caliper(convexHull, getIndex(convexHull, Corner.LOWER_LEFT), 270);
        Caliper L = new Caliper(convexHull, getIndex(convexHull, Corner.LOWER_RIGHT), 0);

        while(L.currentAngle < 90.0) {

            rectangles.add(new PVector[]{
                    L.getIntersection(I),
                    I.getIntersection(J),
                    J.getIntersection(K),
                    K.getIntersection(L)
            });

            float smallestTheta = getSmallestTheta(I, J, K, L);

            I.rotateBy(smallestTheta);
            J.rotateBy(smallestTheta);
            K.rotateBy(smallestTheta);
            L.rotateBy(smallestTheta);
        }

        return rectangles;
    }
  
    PVector[] getMinimumBoundingRectangle(int[] xs, int[] ys){


        ArrayList<PVector> points = new ArrayList<PVector>();

        for(int i = 0; i < xs.length; i++) {
            points.add(new PVector(xs[i], ys[i]));
        }

        return getMinimumBoundingRectangle(points);
    }

    PVector[] getMinimumBoundingRectangle(ArrayList<PVector> convexHull){
        PVector s = convexHull.get(0);
        PVector e = convexHull.get(convexHull.size()-1);
        if(s.x != e.x && s.y != e.y){
          //if start and end point are not the same, add a duplicate start point
          convexHull.add(s);
        }
          
        ArrayList<PVector[]> rectangles = getAllBoundingRectangles(convexHull);

        PVector[] minimum = null;
        float area = Float.MAX_VALUE;

        for (PVector[] rectangle : rectangles) {

            float tempArea = getArea(rectangle);

            if (minimum == null || tempArea < area) {
                minimum = rectangle;
                area = tempArea;
            }
        }

        return minimum;
    }

    float getSmallestTheta(Caliper I, Caliper J, Caliper K, Caliper L) {

        float thetaI = I.getDeltaAngleNextPoint();
        float thetaJ = J.getDeltaAngleNextPoint();
        float thetaK = K.getDeltaAngleNextPoint();
        float thetaL = L.getDeltaAngleNextPoint();

        if(thetaI <= thetaJ && thetaI <= thetaK && thetaI <= thetaL) {
            return thetaI;
        }
        else if(thetaJ <= thetaK && thetaJ <= thetaL) {
            return thetaJ;
        }
        else if(thetaK <= thetaL) {
            return thetaK;
        }
        else {
            return thetaL;
        }
    }

    int getIndex(ArrayList<PVector> convexHull, Corner corner) {

        int index = 0;
        PVector point = convexHull.get(index);

        for(int i = 1; i < convexHull.size() - 1; i++) {

            PVector temp = convexHull.get(i);
            boolean change = false;

            switch(corner) {
                case UPPER_RIGHT:
                    change = (temp.x > point.x || (temp.x == point.x && temp.y > point.y));
                    break;
                case UPPER_LEFT:
                    change = (temp.y > point.y || (temp.y == point.y && temp.x < point.x));
                    break;
                case LOWER_LEFT:
                    change = (temp.x < point.x || (temp.x == point.x && temp.y < point.y));
                    break;
                case LOWER_RIGHT:
                    change = (temp.y < point.y || (temp.y == point.y && temp.x > point.x));
                    break;
            }

            if(change) {
                index = i;
                point = temp;
            }
        }

        return index;
    }

    class Caliper {

        double SIGMA = 0.00000000001;

        ArrayList<PVector> convexHull;
        int pointIndex;
        float currentAngle;

        Caliper(ArrayList<PVector> convexHull, int pointIndex, float currentAngle) {
            this.convexHull = convexHull;
            this.pointIndex = pointIndex;
            this.currentAngle = currentAngle;
        }

        float getAngleNextPoint() {

            PVector p1 = convexHull.get(pointIndex);
            PVector p2 = convexHull.get((pointIndex + 1) % convexHull.size());

            float deltaX = p2.x - p1.x;
            float deltaY = p2.y - p1.y;

            float angle = PApplet.atan2(deltaY, deltaX) * 180 / PApplet.PI;

            return angle < 0 ? 360 + angle : angle;
        }

        float getConstant() {

            PVector p = convexHull.get(pointIndex);

            return p.y - (getSlope() * p.x);
        }

        float getDeltaAngleNextPoint() {

            float angle = getAngleNextPoint();

            angle = angle < 0 ? 360 + angle - currentAngle : angle - currentAngle;

            return angle < 0 ? 360 : angle;
        }

        PVector getIntersection(Caliper that) {

            // the x-intercept of 'this' and 'that': x = ((c2 - c1) / (m1 - m2))
            float x;
            // the y-intercept of 'this' and 'that', given 'x': (m*x) + c
            float y;

            if(this.isVertical()) {
                x = convexHull.get(pointIndex).x;
            }
            else if(this.isHorizontal()) {
                x = that.convexHull.get(that.pointIndex).x;
            }
            else {
                x = (that.getConstant() -  this.getConstant()) / (this.getSlope() - that.getSlope());
            }

            if(this.isVertical()) {
                y = that.getConstant();
            }
            else if(this.isHorizontal()) {
                y = this.getConstant();
            }
            else {
                y = (this.getSlope() * x) + this.getConstant();
            }

            return new PVector(x, y);
        }

        float getSlope() {
            return PApplet.tan(PApplet.radians(currentAngle));
        }

        boolean isHorizontal() {
            return (Math.abs(currentAngle) < SIGMA) || (Math.abs(currentAngle - 180.0) < SIGMA);
        }

        boolean isVertical() {
            return (Math.abs(currentAngle - 90.0) < SIGMA) || (Math.abs(currentAngle - 270.0) < SIGMA);
        }

        void rotateBy(double angle) {

            if(this.getDeltaAngleNextPoint() == angle) {
                pointIndex++;
            }

            this.currentAngle += angle;
        }
    }
}