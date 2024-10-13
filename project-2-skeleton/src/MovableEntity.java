import bagel.Input;
import bagel.util.Point;

public abstract class MovableEntity extends GameEntity {
    protected double speedX;
    protected double speedY;

    public MovableEntity(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius);
        this.speedX = speedX;
        this.speedY = speedY;
    }

    //default implementation left blank for custom implementation
    public void move(Input input) {
    }

    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + speedY);
        }
    }
}
