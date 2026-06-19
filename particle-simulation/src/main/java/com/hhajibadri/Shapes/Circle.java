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
  private static final double RESTITUTION = 0.8;

  private static final Random random = new Random();

  private double x, y;
  private double vx, vy;
  private double radius;
  private double mass;

  public Circle(double x, double y) {
    this(x, y, DEFAULT_RADIUS);
  }

  public Circle(double x, double y, double radius) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    vx = vy = 0.0;
    mass = radius * radius;
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

    double invMassA = 1.0 / a.mass;
    double invMassB = 1.0 / b.mass;

    double totalInvMass = invMassA + invMassB;

    a.x -= nx * overlap * (invMassA / totalInvMass);
    a.y -= ny * overlap * (invMassA / totalInvMass);

    b.x += nx * overlap * (invMassB / totalInvMass);
    b.y += ny * overlap * (invMassB / totalInvMass);

    double rvx = b.vx - a.vx;
    double rvy = b.vy - a.vy;
    double velAlongNormal = rvx * nx + rvy * ny;

    if (velAlongNormal > 0) {
      return;
    }

    double j = -(1.0 + RESTITUTION) * velAlongNormal;
    j /= totalInvMass;
    double impulseX = j * nx;
    double impulseY = j * ny;

    a.vx -= impulseX * invMassA;
    a.vy -= impulseY * invMassA;
    b.vx += impulseX * invMassB;
    b.vy += impulseY * invMassB;

  }

  public void resolveWallCollision(double width, double height) {
    if (x - radius < 0) {
      x = radius;
      vx *= -RESTITUTION;
    } else if (x + radius >= width) {
      x = width - radius;
      vx *= -RESTITUTION;
    }

    if (y - radius < 0) {
      y = radius;
      vy *= -RESTITUTION;
    } else if (y + radius >= height) {
      y = height - radius;
      vy *= -RESTITUTION;
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

    double ax = (dx * SPRING_STIFFNESS - vx * SPRING_DAMPING);
    double ay = (dy * SPRING_STIFFNESS - vy * SPRING_DAMPING);

    vx += ax * dt;
    vy += ay * dt;
  }

  public static Optional<Circle> generateRandomCircle(double width, double height, boolean randomRadius) {

    double minDimension = Math.min(width, height);
    double radius = DEFAULT_RADIUS;

    if (randomRadius) {
      double maxRadius = Math.max(DEFAULT_RADIUS + Double.MIN_NORMAL, minDimension * 0.125);
      radius = random.nextDouble(DEFAULT_RADIUS, maxRadius);
    }

    if (minDimension <= 2.0 * radius) {
      return Optional.empty();
    }
    
    return Optional.of(
      new Circle(
        random.nextDouble(radius, width - radius),
        random.nextDouble(radius, height - radius),
        radius
      )
    );
  }

  public static double euclideanDistance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(euclideanDistanceSquared(x1, y1, x2, y2));
  }

  public static double euclideanDistanceSquared(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    return dx * dx + dy * dy;
  }

  public boolean contains(double sourceX, double sourceY) {
    return euclideanDistanceSquared(x, y, sourceX, sourceY) <= radius * radius;
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
    mass = radius * radius;
  }

}
