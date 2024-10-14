import bagel.Input;
import bagel.util.Point;

/**
 * Represents a game entity that can move based on user input or game logic.
 */
public abstract class MovableEntity extends GameEntity {
    protected double speedX;
    protected double speedY;

    /**
     * Constructs a new MovableEntity at the specified position.
     *
     * @param x The x-coordinate of the entity's initial position
     * @param y The y-coordinate of the entity's initial position
     * @param imagePath The file path to the entity's image
     * @param radius The collision radius of the entity
     * @param speedX The horizontal speed of the entity
     * @param speedY The vertical speed of the entity
     */
    public MovableEntity(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius);
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * Skeleton Method to move the entity based on user input.
     *
     * @param input The current keyboard input
     */
    public void move(Input input) {
    }

    /**
     * Moves the entity vertically based on game world scrolling speed.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + speedY);
        }
    }
}
