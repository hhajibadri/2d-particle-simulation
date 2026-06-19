package com.hhajibadri;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.hhajibadri.Shapes.Circle;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static final List<Circle> CIRCLES = new ArrayList<>();
    private static final List<Color> COLORS = new ArrayList<>();

    private long lastTime = 0;

    private double mouseX = 0.0;
    private double mouseY = 0.0;

    private boolean mousePrimaryClicked = false;
    private boolean mouseSecondaryDown = false;

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

        Scene scene = new Scene(root, 800, 600);

        canvas = new Canvas(800, 600);
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
                        .subtract(10.0));
        controls.setLayoutY(10.0);

        addButton.setOnAction(event -> {
            Optional<Circle> randomCircle = Circle.generateRandomCircle(canvas.getWidth(), canvas.getHeight(), true);
            if (randomCircle.isPresent()) {
                Circle circle = randomCircle.get();
                CIRCLES.add(circle);
                COLORS.add(Color.rgb(
                        random.nextInt(0, 256),
                        random.nextInt(0, 256),
                        random.nextInt(0, 256),
                        1.0));
            }
        });

        clearButton.setOnAction(event -> {
            CIRCLES.clear();
            COLORS.clear();
        });

        canvas.setOnMousePressed(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            if (event.getButton() == MouseButton.PRIMARY) {
                mousePrimaryClicked = true;
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                mouseSecondaryDown = true;
            }

        });

        canvas.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                mouseSecondaryDown = false;
            }
        });

        canvas.setOnMouseDragged(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        lastTime = System.nanoTime();

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                double deltaTime = (now - lastTime) / 1e9;
                lastTime = now;

                if (mousePrimaryClicked) {
                    CIRCLES.add(new Circle(mouseX, mouseY));
                    COLORS.add(Color.rgb(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256), 1.0));
                    mousePrimaryClicked = false;
                }

                if (mouseSecondaryDown) {
                    for (Circle circle : CIRCLES) {
                        circle.applySpringForceFromSource(deltaTime, mouseX, mouseY);
                    }
                }

                for (Circle circle : CIRCLES) {
                    circle.update(deltaTime);
                }

                for (int i = 0; i < CIRCLES.size(); ++i) {
                    for (int j = i + 1; j < CIRCLES.size(); ++j) {
                        Circle.resolveCircleCollisions(CIRCLES.get(i), CIRCLES.get(j));
                    }
                }

                for (Circle circle : CIRCLES) {
                    circle.resolveWallCollision(canvas.getWidth(), canvas.getHeight());
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
            gc.fillOval(
                circle.getX() - circle.getRadius(),
                circle.getY() - circle.getRadius(),
                circle.getRadius() * 2.0,
                circle.getRadius() * 2.0);
        }

    }
}