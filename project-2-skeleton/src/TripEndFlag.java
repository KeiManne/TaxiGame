import bagel.*;
import bagel.util.Point;

public class TripEndFlag {
    private final Image image;
    private Point position;
    private final double radius;
    private boolean isVisible;
    private final double verticalScrollSpeed;

    public TripEndFlag(double x, double y, String imagePath, double radius, double verticalScrollSpeed) {
        this.image = new Image(imagePath);
        this.position = new Point(x, y);
        this.radius = radius;
        this.isVisible = false;
        this.verticalScrollSpeed = verticalScrollSpeed;
    }

    public void move(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + verticalScrollSpeed);
        }
    }

    public void draw() {
        if (isVisible) {
            image.draw(position.x, position.y);
        }
    }

    //getters and setters
    public Point getPosition() {
        return position;
    }
    public double getRadius() {
        return radius;
    }
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}