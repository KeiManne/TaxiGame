import bagel.*;
import bagel.util.Point;
/**
 * Represents a taxi in the game that can be driven and pick up passengers.
 */
public class Taxi extends MovableEntity implements Collidable, Damageable {
    private static final String DAMAGED_IMAGE = "res/taxiDamaged.png";
    private static final int COLLISION_TIMEOUT = 200;
    private static final int INVINCIBILITY_FRAMES = 1000;
    private static final int MAX_COIN_POWER_FRAMES = 500;
    private static final int SEPARATION_FRAMES = 10;
    private static final int TAXI_HEALTH = 100;
    private static final int TAXI_DAMAGE = 100;
    //edited taxi damage to be 25, otherwise on collisions cars and enemy cars are immediately destroyed

    private double health;
    private boolean isDamaged;
    private int collisionTimeout;
    private int invincibilityFrames;
    private boolean coinPowerActive;
    private int coinPowerFrames;
    private Passenger currentPassenger;
    private boolean hasDriver;
    private boolean isMoving;
    private int damage;
    private Point separationDirection;
    private int separationFramesLeft;

    /**
     * Constructs a new Taxi at the specified position.
     *
     * @param x The x-coordinate of the taxi's initial position
     * @param y The y-coordinate of the taxi's initial position
     * @param imagePath The file path to the taxi's image
     * @param radius The collision radius of the taxi
     * @param speedX The horizontal speed of the taxi
     * @param speedY The vertical speed of the taxi
     */
    public Taxi(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.health = TAXI_HEALTH;
        this.isDamaged = false;
        this.collisionTimeout = 0;
        this.invincibilityFrames = 0;
        this.coinPowerActive = false;
        this.coinPowerFrames = 0;
        this.hasDriver = false;
        this.currentPassenger = null;
        this.damage = TAXI_DAMAGE;
    }

    /**
     * Moves the taxi based on user input.
     *
     * @param input The current keyboard input
     */
    @Override
    public void move(Input input) {
        isMoving = false;
        if (hasDriver) {
            if (input.isDown(Keys.LEFT)) {
                position = new Point(Math.max(0, position.x - speedX), position.y);
                isMoving = true;
            }
            if (input.isDown(Keys.RIGHT)) {
                position = new Point(Math.min(Window.getWidth(), position.x + speedX), position.y);
                isMoving = true;
            }
            if (input.isDown(Keys.UP)) {
                isMoving = true;
            }
        }
    }

    /**
     * Moves the taxi vertically based on game world scrolling.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    @Override
    public void moveVertically(boolean moveDown) {
        if (!hasDriver && moveDown) {
            super.moveVertically(moveDown);
        }
    }

    /**
     * Updates the taxi's state, including collision timeouts and power-ups.
     */
    @Override
    public void update() {
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
        if (invincibilityFrames > 0) invincibilityFrames--;
        if (coinPowerActive) {
            coinPowerFrames++;
            if (coinPowerFrames >= MAX_COIN_POWER_FRAMES) {
                coinPowerActive = false;
                coinPowerFrames = 0;
            }
        }
        updatePassengerPosition();
    }

    private void updatePassengerPosition() {
        if (currentPassenger != null) {
            currentPassenger.setPosition(new Point(position.x, position.y));
        }
    }

    /**
     * Draws the taxi on the game screen.
     */
    @Override
    public void draw() {
        if (isDamaged) {
            new Image(DAMAGED_IMAGE).draw(position.x, position.y);
        } else {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Handles collision with another game entity.
     *
     * @param other The other entity involved in the collision
     */
    @Override
    public void handleCollision(GameEntity other) {
        //only collide if not active invincibility or recent collision
        if (collisionTimeout > 0) return;

        if (other instanceof Damageable) {
            takeDamage(((Damageable) other).getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            separationFramesLeft = SEPARATION_FRAMES;

            //determine separation direction
            if (this.position.y < other.getPosition().y) {
                separationDirection = new Point(0, -1); // Taxi moves up
            } else {
                separationDirection = new Point(0, 1); // Taxi moves down
            }
        } else if (other instanceof PowerUp) {
            ((PowerUp) other).applyEffect(this);
        }
    }

    /**
     * Applies damage to the taxi.
     *
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(double amount) {
        if (invincibilityFrames == 0 && collisionTimeout == 0) {
            health -= amount;
            collisionTimeout = COLLISION_TIMEOUT;
            if (health <= 0) {
                isDamaged = true;
                ejectOccupants();
            }
        }
    }

    /*
    method to eject passenger if taxi is damaged
     */
    private void ejectOccupants() {
        if (currentPassenger != null) {
            currentPassenger.setPosition(new Point(position.x - 100, position.y));
            currentPassenger.setFollowingDriver(true);
            currentPassenger.setPickedUp(false);
        }
        hasDriver = false;
    }

    /**
     * Activates invincibility for the taxi.
     */
    public void activateInvincibility() {
        invincibilityFrames = INVINCIBILITY_FRAMES;
    }

    /**
     * Activates the coin power for the taxi.
     */
    public void activateCoinPower() {
        coinPowerActive = true;
        coinPowerFrames = 0;
    }

    /**
     * Attempts to pick up a passenger.
     *
     * @param passenger The passenger to pick up
     * @return true if the passenger was successfully picked up, false otherwise
     */
    public boolean pickupPassenger(Passenger passenger) {
        if (currentPassenger == null && !isMoving && hasDriver) {
            currentPassenger = passenger;
            passenger.setPickedUp(true);
            return true;
        }
        return false;
    }

    /**
     * Checks if the taxi can drop off its current passenger at the given flag.
     *
     * @param flag The trip end flag to check against
     * @return true if the taxi can drop off the passenger, false otherwise
     */
    public boolean canDropOffPassenger(TripEndFlag flag) {
        if (currentPassenger != null && !isMoving()) {
            return position.y <= flag.getPosition().y ||
                    position.distanceTo(flag.getPosition()) <= flag.getRadius();
        }
        return false;
    }

    /**
     * Drops off the current passenger.
     *
     * @return The passenger that was dropped off
     */
    public Passenger dropOffPassenger() {
        Passenger passenger = currentPassenger;
        setCurrentPassenger(null);
        return passenger;
    }

    //getters and setters
    /**
     * Gets the current health of the taxi.
     *
     * @return The current health value
     */
    @Override
    public double getHealth() {
        return health;
    }

    /**
     * Checks if the taxi is damaged.
     *
     * @return true if the taxi is damaged, false otherwise
     */
    public boolean isDamaged() {
        return isDamaged;
    }

    /**
     * Checks if the taxi has an active coin power.
     *
     * @return true if the coin power is active, false otherwise
     */
    public boolean hasCoinPower() {
        return coinPowerActive;
    }

    /**
     * Gets the number of frames the coin power has been active.
     *
     * @return The number of active coin power frames
     */
    public int getCoinPowerFrames() {
        return coinPowerFrames;
    }

    /**
     * Gets the current passenger in the taxi.
     *
     * @return The current passenger, or null if there is none
     */
    public Passenger getCurrentPassenger() {
        return currentPassenger;
    }

    /**
     * Sets the current passenger in the taxi.
     *
     * @param passenger The passenger to set as current
     */
    public void setCurrentPassenger(Passenger passenger) {
        this.currentPassenger = passenger;
    }

    /**
     * Checks if the taxi has a driver.
     *
     * @return true if the taxi has a driver, false otherwise
     */
    public boolean hasDriver() {
        return hasDriver;
    }

    /**
     * Sets whether the taxi has a driver.
     *
     * @param hasDriver The new driver state
     */
    public void setHasDriver(boolean hasDriver) {
        this.hasDriver = hasDriver;
    }

    /**
     * Checks if the taxi is currently moving.
     *
     * @return true if the taxi is moving, false otherwise
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Gets the damage this taxi can inflict on other entities.
     *
     * @return The damage value
     */
    @Override
    public int getDamage() {
        return damage;
    }
}