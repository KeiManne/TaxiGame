/**
 * Represents a game entity that can move independently of user input.
 */
public abstract class IndependentlyMovableEntity extends MovableEntity {
    /**
     * Skeleton code to construct a new IndependentlyMovableEntity at the specified position.
     *
     * @param x The x-coordinate of the entity's initial position
     * @param y The y-coordinate of the entity's initial position
     * @param imagePath The file path to the entity's image
     * @param radius The collision radius of the entity
     * @param speedX The horizontal speed of the entity
     * @param speedY The vertical speed of the entity
     */
    public IndependentlyMovableEntity(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
    }

    /**
     * Moves the entity independently of user input.
     */
    public abstract void moveIndependently();
}