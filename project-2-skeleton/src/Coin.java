import bagel.*;
import bagel.util.Point;

public class Coin {
    private final Image image;
    private Point position;
    private final double radius;
    private final double verticalScrollSpeed;

    public Coin(double x, double y, String imagePath, double radius, double verticalScrollSpeed) {
        this.image = new Image(imagePath);
        this.position = new Point(x, y);
        this.radius = radius;
        this.verticalScrollSpeed = verticalScrollSpeed;
    }

    /*
    method to create vertical movement for coins
     */
    public void move(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + verticalScrollSpeed);
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
}