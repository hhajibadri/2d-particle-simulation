package com.hhajibadri.Shapes;

public class Circle {

  public double radius;
  public double x, y;
  public double vx, vy;

  public Circle(double px, double py, double radius) {
    this.x = px;
    this.y = py;
    this.radius = radius;
    vx = vy = 0.0;
  }

  public void update(double dt, double gravity) {
    vy += gravity * dt;
    x += vx * dt;
    y += vy * dt;
    vx *= 0.999;
    vy *= 0.999;
  }

  public static void resolveCircleCollisions(Circle a, Circle b) {

    double dx = b.x - a.x;
    double dy = b.y - a.y;
    double currentDistance = Math.sqrt(dx * dx + dy * dy);
    double minimumCollisionDistance = a.radius + b.radius;

    if (currentDistance > minimumCollisionDistance) {
      return;
    }

    double nx = dx / currentDistance;
    double ny = dy / currentDistance;

    double overlap = minimumCollisionDistance - currentDistance;

    a.x -= nx * overlap * 0.5;
    a.y -= ny * overlap * 0.5;

    b.x += nx * overlap * 0.5;
    b.y += ny * overlap * 0.5;

    double rvx = b.vx - a.vx;
    double rvy = b.vy - a.vy;
    double velAlongNormal = rvx * nx + rvy * ny;

    if (velAlongNormal > 0) {
      return;
    }

    double j = -velAlongNormal;
    double impulseX = j * nx;
    double impulseY = j * ny;

    a.vx -= impulseX;
    a.vy -= impulseY;
    b.vx += impulseX;
    b.vy += impulseY;

  }

  public static void resolveWallCollision(Circle a, int endX, int endY) {
    if (a.x - a.radius < 0) {
      a.x = a.radius;
      a.vx = -a.vx;
    } else if (a.x + a.radius >= endX) {
      a.x = endX - a.radius;
      a.vx = -a.vx;
    }

    if (a.y - a.radius < 0) {
      a.y = a.radius;
      a.vy = -a.vy;
    } else if (a.y + a.radius >= endY) {
      a.y = endY - a.radius;
      a.vy = -a.vy;
    }
  }
}
