import bagel.Image;
import bagel.util.Point;

public abstract class GameEntity {
    protected Point position;
    protected Image image;
    protected double radius;

    public GameEntity(double x, double y, String imagePath, double radius) {
        this.position = new Point(x, y);
        this.image = new Image(imagePath);
        this.radius = radius;
    }

    public abstract void update();
    public abstract void draw();

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getRadius() {
        return radius;
    }

    public boolean collidesWith(GameEntity other) {
        double distance = position.distanceTo(other.getPosition());
        return distance < (this.radius + other.getRadius());
    }
}