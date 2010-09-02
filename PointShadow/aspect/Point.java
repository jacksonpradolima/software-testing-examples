package PointShadow;

  public class Point {
    protected int x, y;
    public Point(int _x, int _y) {
      x = _x;
      y = _y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public void setX(int _x) {
      x = _x;
    }

    public void setY(int _y) {
      y = _y;
    }

    public void printPosition() {
      System.out.println("Point at("+x+","+y+")");
    }

    public static void main(String[] args) {
      Point p = new Point(1,1);
      p.setX(2);
      p.setY(2);
    }
  }