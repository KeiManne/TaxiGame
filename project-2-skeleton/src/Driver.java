import bagel.*;
import bagel.util.Point;

/**
 * Represents the driver character that can control taxis and walk independently.
 */
public class Driver extends MovableEntity implements Damageable, Collidable {
    private static final double INITIAL_HEALTH = 100.0;
    private static final int INVINCIBILITY_DURATION = 1000;
    private static final int COLLISION_TIMEOUT = 200;
    private static final int SEPARATION_FRAMES = 10;

    private double health;
    private Taxi currentTaxi;
    private int invincibilityFrames;
    private int collisionTimeout;
    private int damage;
    private int separationFramesLeft;
    private Point separationDirection;

    /**
     * Constructs a new Driver at the specified position.
     *
     * @param x The x-coordinate of the driver's initial position
     * @param y The y-coordinate of the driver's initial position
     * @param imagePath The file path to the driver's image
     * @param radius The collision radius of the driver
     * @param speedX The horizontal walking speed of the driver
     * @param speedY The vertical walking speed of the driver
     */
    public Driver(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.health = INITIAL_HEALTH;
        this.invincibilityFrames = 0;
        this.collisionTimeout = 0;
        this.damage = 0;
    }

    /**
     * Moves the driver based on user input when not in a taxi.
     *
     * @param input The current keyboard input
     */
    @Override
    public void move(Input input) {
        if (currentTaxi == null) {
            if (input.isDown(Keys.LEFT)) {
                position = new Point(Math.max(0, position.x - speedX), position.y);
            }
            if (input.isDown(Keys.RIGHT)) {
                position = new Point(Math.min(Window.getWidth(), position.x + speedX), position.y);
            }
            if (input.isDown(Keys.UP)) {
                position = new Point(position.x, Math.max(0, position.y - speedY));
            }
            if (input.isDown(Keys.DOWN)) {
                position = new Point(position.x, Math.min(Window.getHeight(), position.y + speedY));
            }
        }
    }

    /**
     * Updates the driver's state, including position and power-up durations.
     */
    @Override
    public void update() {
        if (currentTaxi != null) {
            position = currentTaxi.getPosition();
        }
        if (invincibilityFrames > 0) {
            invincibilityFrames--;
        }
        if (collisionTimeout > 0) {
            collisionTimeout--;

            if (separationFramesLeft > 0) {
                position = new Point(
                        position.x + separationDirection.x,
                        position.y + separationDirection.y
                );
                separationFramesLeft--;
            }
        }
    }

    /**
     * Updates the driver's state, including position and power-up durations.
     */
    @Override
    public void draw() {
        //only draw if taxi was destroyed
        if (currentTaxi == null) {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Applies damage to the driver if not invincible.
     *
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(double amount) {
        if (invincibilityFrames == 0) {
            health -= amount;
            collisionTimeout = COLLISION_TIMEOUT;
        }
    }

    /**
     * Handles collision with another game entity.
     *
     * @param other The other entity involved in the collision
     */
    @Override
    public void handleCollision(GameEntity other) {
        if (collisionTimeout > 0) return;

        if (other instanceof Damageable) {
            takeDamage(((Damageable) other).getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            separationFramesLeft = SEPARATION_FRAMES;

            //determine separation direction
            separationDirection = new Point(
                    position.x < other.getPosition().x ? -2 : 2,
                    position.y < other.getPosition().y ? -2 : 2
            );
        } else if (other instanceof PowerUp) {
            ((PowerUp) other).applyEffect(this);
        }
    }

    /**
     * Attempts to make the driver enter a taxi.
     *
     * @param taxi The taxi to enter
     * @return true if the driver successfully entered the taxi, false otherwise
     */
    public boolean enterTaxi(Taxi taxi) {
        if (taxi != null && !taxi.isDamaged() && position.distanceTo(taxi.getPosition()) <= 10) {
            currentTaxi = taxi;
            taxi.setHasDriver(true);
            return true;
        }
        return false;
    }

    /**
     * Makes the driver exit the current taxi.
     */
    public void exitTaxi() {
        if (currentTaxi != null) {
            currentTaxi.setHasDriver(false);
            currentTaxi = null;
        }
    }

    /**
     * Activates invincibility for the driver.
     */
    public void activateInvincibility() {
        invincibilityFrames = INVINCIBILITY_DURATION;
    }

    //getters and setters
    /**
     * Gets the current health of the driver.
     *
     * @return The current health value
     */
    @Override
    public double getHealth() {
        return health;
    }

    /**
     * Checks if the driver is currently in a taxi.
     *
     * @return true if the driver is in a taxi, false otherwise
     */
    public boolean isInTaxi() {
        return currentTaxi != null;
    }

    /**
     * Gets the damage this driver can inflict on other entities.
     *
     * @return The damage value
     */
    @Override
    public int getDamage() {
        return damage;
    }

    /**
     * Sets the collision timeout for the driver.
     *
     * @param amount The duration of the collision timeout in frames
     */
    public void setCollisionTimeout(int amount) {
        this.collisionTimeout = amount;

    }
}