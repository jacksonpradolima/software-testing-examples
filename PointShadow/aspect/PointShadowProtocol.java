package PointShadow;

aspect PointShadowProtocol {
  private int shadowCount = 0;

  public static int getShadowCount() {
    return PointShadowProtocol.aspectOf().shadowCount;
  }

  private Shadow Point.shadow;
    public static void associate(Point p, Shadow s){
    p.shadow = s;
  }

  public static Shadow getShadow(Point p) {
    return p.shadow;
  }

  pointcut settingX(Point p): target(p) && call(void Point.setX(int));

  pointcut settingY(Point p): target(p) && call(void Point.setY(int));

  after(int x, int y) returning (Point p): args(x, y) && call(Point+.new(int, int)) {
    Shadow s = new Shadow(x, y);
    associate(p,s);
    s.x = x + Shadow.offset;
    s.y = y + Shadow.offset;
    shadowCount++;
  }

  after(Point p): settingX(p) {
    Shadow s = getShadow(p);
    s.x = p.getX() + Shadow.offset;
    p.printPosition();
    s.printPosition();
  }

  after(Point p): settingY(p) {
    Shadow s = getShadow(p);
    s.y = p.getY() + Shadow.offset;
    p.printPosition();
    s.printPosition();
  }
}