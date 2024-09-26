import bagel.*;
import bagel.util.Point;

public class Taxi {
    private final Image image;
    private final double radius;
    private Point position;
    private final double speedX;
    private boolean isMoving;

    public Taxi(double x, double y, String imagePath, double radius, double speedX) {
        this.image = new Image(imagePath);
        this.position = new Point(x, y);
        this.radius = radius;
        this.speedX = speedX;
    }

    /*
    method to enable taxi movement by user
     */
    public void move(Input input) {
        isMoving = false;
        if (input.isDown(Keys.LEFT)) {
            position = new Point(Math.max(0, position.x - speedX), position.y);
            isMoving = true;
        }
        if (input.isDown(Keys.RIGHT)) {
            position = new Point(Math.min(Window.getWidth(), position.x + speedX), position.y);
            isMoving = true;
        }
        if (input.isDown(Keys.UP)) {
            isMoving = true;
        }
    }

    public void draw() {
        image.draw(position.x, position.y);
    }

    //getters and setters
    public Point getPosition() {
        return position;
    }
    public double getRadius() {
        return radius;
    }
    public boolean isMoving() {
        return isMoving;
    }
}