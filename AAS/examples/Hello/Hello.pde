import greyshed.lib.*; //import the library

ShapeMethods sm;

PVector pt = new PVector(20,20);
PVector center;

void setup() {
  size(200,200);
  sm = new ShapeMethods(this);
  center = new PVector(width/2,height/2);
}

void draw() {
  background(0);
  PVector rotatedPt = sm.rotatePoint(pt, center, PI);
  strokeWeight(10);
  stroke(255,0,0);
  point(rotatedPt.x,rotatedPt.y);
}