package com.snake2d;

import java.awt.geom.Ellipse2D;
import java.util.Random;

public class Pemburu {

    private Ellipse2D.Double object;
    private boolean visible;

    public Pemburu() {
        visible = false;
        generateObject();
    }

    public void generateObject() {
        Random random = new Random();
        int x, y;
        do {
            x = (int) (random.nextInt(39));
            y = (int) (random.nextInt(30));
        } while (x == 0 || y == 0 || x == 38 || y == 29);

        x = x * 16 + 227;
        y = y * 16 + 127;

        object = new Ellipse2D.Double(x, y, 16, 16);
        visible = true;
    }

    public void moveTowardsSnake(Snake snake) {
        Ellipse2D.Double snakeHead = snake.getHead();

        double currentX = object.getCenterX();
        double currentY = object.getCenterY();
        double snakeX = snakeHead.getCenterX();
        double snakeY = snakeHead.getCenterY();

        int speed = 4; 

        double diffX = snakeX - currentX;
        double diffY = snakeY - currentY;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (diffX > 0) {
                currentX += speed;
            } else {
                currentX -= speed;
            }
        } else {
            if (diffY > 0) {
                currentY += speed;
            } else {
                currentY -= speed;
            }
        }

        object.setFrame(currentX - 8, currentY - 8, 16, 16);
    }


    public void setVisible(boolean isVisible) {
        visible = isVisible;
    }

    public boolean isVisible() {
        return visible;
    }

    public Ellipse2D.Double getObject() {
        return object;
    }
}