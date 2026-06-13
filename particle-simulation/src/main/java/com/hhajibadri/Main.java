package com.hhajibadri;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hhajibadri.Shapes.Circle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private long lastTime = 0;
    
    private double mouseX = 0.0;
    private double mouseY = 0.0;
    private boolean mouseClicked = false;
    
    private final double gravity = 100.0;

    private final List<Circle> CIRCLES = new ArrayList<>();
    private final List<Color> COLORS = new ArrayList<>();

    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private Canvas canvas;
    private GraphicsContext gc;

    private Random random;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        random = new Random();

        primaryStage.setTitle("2D Particle Simulation");

        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnMouseClicked((mouseEvent) -> {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
            mouseClicked = true;
        });

        lastTime = System.nanoTime();

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                double deltaTime = (now - lastTime) / 1e9;
                lastTime = now;

                if (mouseClicked) {
                    CIRCLES.add(new Circle(mouseX, mouseY, 10.0));
                    COLORS.add(Color.rgb(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256), 1.0));
                    mouseClicked = false;
                }

                for (Circle circle : CIRCLES) {
                    circle.update(deltaTime, gravity);
                }

                for (int i = 0; i < CIRCLES.size(); ++i) {
                    for (int j = i + 1; j < CIRCLES.size(); ++j) {
                        Circle.resolveCircleCollisions(CIRCLES.get(i), CIRCLES.get(j));
                    }
                }

                for (Circle circle : CIRCLES) {
                    Circle.resolveWallCollision(circle, WIDTH, HEIGHT);
                }

                render();

            }
        }.start();
    }

    private void render() {

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < CIRCLES.size(); ++i) {
            Circle circle = CIRCLES.get(i);
            Color color = COLORS.get(i);
            gc.setFill(color);
            gc.fillOval(circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2.0, circle.radius * 2.0);
        }

    }
}