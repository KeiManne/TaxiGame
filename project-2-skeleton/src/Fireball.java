import bagel.util.Point;

/**
 * Represents a fireball projectile shot by enemy cars.
 */
public class Fireball extends IndependentlyMovableEntity implements Collidable, Damageable {
    private static final double DAMAGE = 20.0;

    /**
     * Constructs a new Fireball at the specified position with given speed.
     *
     * @param x The x-coordinate of the fireball's initial position
     * @param y The y-coordinate of the fireball's initial position
     * @param imagePath The file path to the fireball's image
     * @param radius The collision radius of the fireball
     * @param speedX The horizontal speed of the fireball
     * @param speedY The vertical speed of the fireball
     */
    public Fireball(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
    }

    /**
     * Moves the fireball independently based on vertical speed.
     */
    @Override
    public void moveIndependently() {
        position = new Point(position.x, position.y - speedY);
    }

    /**
     * Updates the fireball's state with any per-frame updates.
     */
    @Override
    public void update() {
    }

    /**
     * Draws the fireball on the game screen.
     */
    @Override
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Handles collision with another game entity.
     *
     * @param other The other entity involved in the collision
     */
    @Override
    public void handleCollision(GameEntity other) {
        if (other instanceof Damageable) {
            ((Damageable) other).takeDamage(DAMAGE);
        }
    }

    /**
     * Applies damage to the fireball (not applicable).
     *
     * @param amount The amount of damage to apply (ignored)
     */
    @Override
    public void takeDamage(double amount) {
        //damage is not relevant for fireball
    }

    /**
     * Gets the current health of the fireball (always 0).
     *
     * @return The health value (always 0)
     */
    @Override
    public double getHealth() {
        //health is not relevant for fireball
        return 0;
    }

    /**
     * Gets the damage this fireball can inflict on other entities.
     *
     * @return The damage value
     */
    @Override
    public int getDamage() {
        return (int) DAMAGE;
    }
}