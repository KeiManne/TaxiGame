import bagel.util.Point;

/**
 * Represents an invincibility power-up in the game.
 */
public class InvinciblePower extends GameEntity implements PowerUp {
    private boolean isActive;
    private double speedY;

    /**
     * Constructs a new InvinciblePower at the specified position.
     *
     * @param x The x-coordinate of the power-up's position
     * @param y The y-coordinate of the power-up's position
     * @param imagePath The file path to the power-up's image
     * @param radius The collision radius of the power-up
     * @param speedY The vertical speed of the power-up
     */
    public InvinciblePower(double x, double y, String imagePath, double radius, double speedY) {
        super(x, y, imagePath, radius);
        this.isActive = true;
        this.speedY = speedY;
    }

    /**
     * Updates the power-up's state per frame.
     */
    @Override
    public void update() {
        //add any per-frame updates
    }

    /**
     * Draws the power-up on the game screen if it's active.
     */
    @Override
    public void draw() {
        if (isActive) {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Moves the power-up vertically based on game world scrolling.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + speedY);
        }
    }

    /**
     * Applies the invincibility effect to the given entity.
     *
     * @param entity The entity to apply the invincibility effect to
     */
    @Override
    public void applyEffect(GameEntity entity) {
        if (entity instanceof Taxi) {
            ((Taxi) entity).activateInvincibility();
        } else if (entity instanceof Driver) {
            ((Driver) entity).activateInvincibility();
        }
        setActive(false);
    }

    //getters and setters
    /**
     * Sets the active state of the power-up.
     *
     * @param active The new active state
     */
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }


}