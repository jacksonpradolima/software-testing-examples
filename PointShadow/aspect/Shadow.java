package PointShadow;

class Shadow {
  public static final int offset = 10;
  public int x, y;

  Shadow(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void printPosition() {
    System.out.println("Shadow at ("+x+","+y+")");
  }
}