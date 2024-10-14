import bagel.util.Point;

/**
 * Represents a visual effect that appears when entities collide.
 */
public class CollisionEffect extends GameEntity {
    private static final int DISPLAY_FRAMES = 20;
    private int remainingFrames;
    private EffectType type;

    /**
     * Enumeration of possible collision effect types.
     */
    public enum EffectType { SMOKE, FIRE, BLOOD }

    /**
     * Constructs a new CollisionEffect at the specified position.
     *
     * @param x The x-coordinate of the effect's position
     * @param y The y-coordinate of the effect's position
     * @param imagePath The file path to the effect's image
     * @param type The type of collision effect
     */
    public CollisionEffect(double x, double y, String imagePath, EffectType type) {
        //radius not relevant for effects
        super(x, y, imagePath, 0);
        this.type = type;
        this.remainingFrames = DISPLAY_FRAMES;
    }

    /**
     * Updates the collision effect's state, decreasing its remaining display time.
     */
    @Override
    public void update() {
        remainingFrames--;
    }

    /**
     * Draws the collision effect on the game screen if it's still active.
     */
    @Override
    public void draw() {
        if (remainingFrames > 0) {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Moves the collision effect vertically based on game world scrolling.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + SCROLL_SPEED);
        }
    }

    //getters and setters
    /**
     * Checks if the collision effect is still active (visible).
     *
     * @return true if the effect is still active, false otherwise
     */
    public boolean isActive() {
        return remainingFrames > 0;
    }
}