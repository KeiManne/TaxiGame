import bagel.Image;
import bagel.util.Point;

/**
 * Represents a basic game entity with position, image, and collision properties.
 */
public abstract class GameEntity implements Collidable {
    /**
     * The speed at which the game world scrolls vertically.
     */
    protected final int SCROLL_SPEED = 5;
    protected Point position;
    protected Image image;
    protected double radius;

    /**
     * Gives template code to construct a new GameEntity at the specified position.
     *
     * @param x The x-coordinate of the entity's initial position
     * @param y The y-coordinate of the entity's initial position
     * @param imagePath The file path to the entity's image
     * @param radius The collision radius of the entity
     */
    public GameEntity(double x, double y, String imagePath, double radius) {
        this.position = new Point(x, y);
        this.image = new Image(imagePath);
        this.radius = radius;
    }

    /**
     * Updates the entity's state.
     */
    public abstract void update();

    /**
     * Draws the entity on the game screen.
     */
    public abstract void draw();

    /**
     * Checks if this entity collides with another entity.
     *
     * @param other The other entity to check collision with
     * @return true if the entities collide, false otherwise
     */
    public boolean collidesWith(GameEntity other) {
        double distance = position.distanceTo(other.getPosition());
        return distance < (this.radius + other.getRadius());
    }

    /**
     * Handles collision with another game entity.
     *
     * @param other The other entity involved in the collision
     */
    @Override
    public void handleCollision(GameEntity other) {
    }

    //getters and setters
    /**
     * Gets the current position of the entity.
     *
     * @return The Point representing the entity's position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the position of the entity.
     *
     * @param position The new position for the entity
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Gets the collision radius of the entity.
     *
     * @return The collision radius
     */
    public double getRadius() {
        return radius;
    }
}
