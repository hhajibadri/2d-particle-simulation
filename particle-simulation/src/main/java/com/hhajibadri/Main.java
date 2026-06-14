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
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        gc = canvas.getGraphicsContext2D();

        primaryStage.setScene(scene);
        primaryStage.show();

        VBox controls = new VBox(10.0);
        Button addButton = new Button("Add objects");
        Button clearButton = new Button("Clear Objects");
        controls.getChildren().addAll(addButton, clearButton);

        root.getChildren().addAll(canvas, controls);

        controls.layoutXProperty().bind(
            root.widthProperty()
                .subtract(controls.widthProperty())
                .subtract(10.0)
        );
        controls.setLayoutY(10.0);

        addButton.setOnAction(event -> {
            CIRCLES.add(new Circle(
                random.nextDouble(10.0, canvas.getWidth() - 10.0),
                random.nextDouble(10.0, canvas.getHeight() - 10.0),
                10.0
            ));
            COLORS.add(Color.rgb(
                random.nextInt(0, 256),
                random.nextInt(0, 256),
                random.nextInt(0, 256),
                1.0)
            );
        });

        clearButton.setOnAction(event -> {
            CIRCLES.clear();
            COLORS.clear();
        });

        canvas.setOnMouseClicked((mouseEvent) -> {
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
                    Circle.resolveWallCollision(circle, canvas.getWidth(), canvas.getHeight());
                }

                render();

            }
        }.start();
    }

    private void render() {

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < CIRCLES.size(); ++i) {
            Circle circle = CIRCLES.get(i);
            Color color = COLORS.get(i);
            gc.setFill(color);
            gc.fillOval(circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2.0, circle.radius * 2.0);
        }

    }
}