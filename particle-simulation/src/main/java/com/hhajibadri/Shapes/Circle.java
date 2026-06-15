package com.hhajibadri.Shapes;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Circle {

  private static final double DAMPING = 0.999;
  private static final double SPRING_STIFFNESS = 50.0;
  private static final double SPRING_DAMPING = 10.0;
  private static final double DEFAULT_RADIUS = 10.0;
  private static final double GRAVITY = 100.0;

  private static final Random random = new Random();

  private double x, y;
  private double vx, vy;
  private double radius;

  public Circle(double x, double y) {
    this(x, y, DEFAULT_RADIUS);
  }

  public Circle(double x, double y, double radius) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    vx = vy = 0.0;
  }

  public void update(double dt) {
    vy += GRAVITY * dt;
    x += vx * dt;
    y += vy * dt;
    vx *= DAMPING;
    vy *= DAMPING;
  }

  public static void resolveCircleCollisions(Circle a, Circle b) {

    double dx = b.x - a.x;
    double dy = b.y - a.y;
    double currentDistanceSquared = euclideanDistanceSquared(b.x, b.y, a.x, a.y);
    double minimumCollisionDistance = a.radius + b.radius;

    if (currentDistanceSquared > minimumCollisionDistance * minimumCollisionDistance) {
      return;
    } else if (currentDistanceSquared < 1e-8) {
      dx = 1.0;
      dy = 0.0;
      currentDistanceSquared = 1.0;
    }
    
    double currentDistance = Math.sqrt(currentDistanceSquared);

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

  public void resolveWallCollision(double width, double height) {
    if (x - radius < 0) {
      x = radius;
      vx = -vx;
    } else if (x + radius >= width) {
      x = width - radius;
      vx = -vx;
    }

    if (y - radius < 0) {
      y = radius;
      vy = -vy;
    } else if (y + radius >= height) {
      y = height - radius;
      vy = -vy;
    }
  }

  public static Optional<Circle> getClosestCircle(List<Circle> CIRCLES, double sourceX, double sourceY) {

    if (CIRCLES.isEmpty()) {
      return Optional.empty();
    }

    Circle closestCircle = CIRCLES.get(0);
    double minDistanceSquared = euclideanDistanceSquared(closestCircle.x, closestCircle.y, sourceX, sourceY);

    for (int i = 1; i < CIRCLES.size(); ++i) {
      Circle currentCircle = CIRCLES.get(i);
      double currDistanceSquared = euclideanDistanceSquared(currentCircle.x, currentCircle.y, sourceX, sourceY);

      if (currDistanceSquared < minDistanceSquared) {
        closestCircle = currentCircle;
        minDistanceSquared = currDistanceSquared;
      }

    }

    return Optional.of(closestCircle);

  }

  public void applySpringForceFromSource(double dt, double sourceX, double sourceY) {
    double dx = sourceX - x;
    double dy = sourceY - y;

    double ax = dx * SPRING_STIFFNESS - vx * SPRING_DAMPING;
    double ay = dy * SPRING_STIFFNESS - vy * SPRING_DAMPING;

    vx += ax * dt;
    vy += ay * dt;
  }

  public static Circle generateRandomCircle(double width, double height, boolean randomRadius) {

    double radius;

    if (randomRadius) {
      radius = random.nextDouble(1.0, Math.min(width, height));
    } else {
      radius = DEFAULT_RADIUS;
    }

    return new Circle(
        random.nextDouble(radius, width - radius),
        random.nextDouble(radius, height - radius),
        radius);

  }

  public static double euclideanDistance(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    return Math.sqrt(dx * dx + dy * dy);
  }

  public static double euclideanDistanceSquared(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    return dx * dx + dy * dy;
  }

  public boolean contains(double sourceX, double sourceY) {
    double dx = x - sourceX;
    double dy = y - sourceY;
    return dx * dx + dy * dy <= radius * radius;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getVx() {
    return vx;
  }

  public void setVx(double vx) {
    this.vx = vx;
  }

  public double getVy() {
    return vy;
  }

  public void setVy(double vy) {
    this.vy = vy;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

}
