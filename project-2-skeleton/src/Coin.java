import bagel.util.Point;

/**
 * Represents a coin power-up in the game that can be collected by the taxi or driver.
 */
public class Coin extends GameEntity implements PowerUp {
    private boolean isActive;

    /**
     * Constructs a new Coin at the specified position.
     *
     * @param x The x-coordinate of the coin's position
     * @param y The y-coordinate of the coin's position
     * @param imagePath The file path to the coin's image
     * @param radius The collision radius of the coin
     */
    public Coin(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius);
        this.isActive = true;
    }

    /**
     * Updates the coin's state with any per-frame updates.
     */
    @Override
    public void update() {
        //add any per frame updates
    }

    /**
     * Draws the coin on the game screen if it's active.
     */
    @Override
    public void draw() {
        if (isActive) {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Moves the coin vertically based on game world scrolling.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + SCROLL_SPEED);
        }
    }

    /**
     * Applies the coin's power-up effect to the given entity.
     *
     * @param entity The entity to apply the power-up effect to
     */
    @Override
    public void applyEffect(GameEntity entity) {
        if (entity instanceof Taxi) {
            ((Taxi) entity).activateCoinPower();
        } else if (entity instanceof Driver) {
            ((Driver) entity).activateInvincibility();
        }
        setActive(false);
    }

    //getters and setters
    /**
     * Sets the active state of the coin.
     *
     * @param active The new active state
     */
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}