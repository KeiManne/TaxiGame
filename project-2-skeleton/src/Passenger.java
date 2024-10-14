import bagel.*;
import bagel.util.Point;

/**
 * Represents a passenger that can be picked up and transported by taxis.
 */
public class Passenger extends MovableEntity implements Damageable, Collidable {
    private static final double PRIORITY_TEXT_OFFSET_X = 30;
    private static final double EARNINGS_TEXT_OFFSET_X = 100;
    private static final int COLLISION_TIMEOUT = 200;
    private static final double STARTING_HEALTH = 100.0;
    private static final int SEPARATION_FRAMES = 10;

    private int priority;
    private final double endX;
    private final double yDistance;
    private final Font font;
    private final double ratePerY;
    private final double[] priorityRates;
    private final int damage;
    private boolean isPickedUp;
    private boolean isDroppedOff;
    private boolean isWalking;
    private Point targetPosition;
    private boolean priorityIncreased;
    private boolean hasUmbrella;
    private double health;
    private int collisionTimeout;
    private int separationFramesLeft;
    private Point separationDirection;
    private boolean isFollowingDriver;

    /**
     * Constructs a new Passenger with specified attributes.
     *
     * @param x The x-coordinate of the passenger's initial position
     * @param y The y-coordinate of the passenger's initial position
     * @param priority The priority level of the passenger
     * @param endX The x-coordinate of the passenger's destination
     * @param yDistance The vertical distance to the passenger's destination
     * @param imagePath The file path to the passenger's image
     * @param fontPath The file path to the font used for passenger information
     * @param fontSize The font size for passenger information
     * @param radius The collision radius of the passenger
     * @param ratePerY The rate per vertical distance unit for fare calculation
     * @param priorityRate1 The fare rate for priority level 1
     * @param priorityRate2 The fare rate for priority level 2
     * @param priorityRate3 The fare rate for priority level 3
     * @param speedX The horizontal walking speed of the passenger
     * @param speedY The vertical walking speed of the passenger
     * @param hasUmbrella Whether the passenger has an umbrella
     */
    public Passenger(double x, double y, int priority, double endX, double yDistance, String imagePath,
                     String fontPath, int fontSize, double radius, double ratePerY,
                     double priorityRate1, double priorityRate2, double priorityRate3,
                     double speedX, double speedY, boolean hasUmbrella) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.priority = priority;
        this.endX = endX;
        this.yDistance = yDistance;
        this.font = new Font(fontPath, fontSize);
        this.ratePerY = ratePerY;
        this.priorityRates = new double[]{priorityRate1, priorityRate2, priorityRate3};
        this.isPickedUp = false;
        this.isDroppedOff = false;
        this.isWalking = false;
        this.priorityIncreased = false;
        this.hasUmbrella = hasUmbrella;
        this.health = STARTING_HEALTH;
        this.damage = 0;
    }

    /**
     * Moves the passenger towards a target position.
     *
     * @param target The target position to move towards
     */
    public void moveTowards(Point target) {
        if (isWalking && targetPosition != null) {
            double dx = targetPosition.x - position.x;
            double dy = targetPosition.y - position.y;

            if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                position = targetPosition;
                isWalking = false;
            } else {
                double moveX = Math.signum(dx) * Math.min(Math.abs(dx), 1);
                double moveY = Math.signum(dy) * Math.min(Math.abs(dy), 1);
                position = new Point(position.x + moveX, position.y + moveY);
            }
        }
    }

    /**
     * Moves the passenger vertically based on game world scrolling.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    @Override
    public void moveVertically(boolean moveDown) {
        if (!isPickedUp && moveDown) {
            position = new Point(position.x, position.y + SCROLL_SPEED);
        }
    }

    /**
     * Updates the passenger's state, including collision effects.
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
    }

    /**
     * Draws the passenger and their information on the game screen.
     */
    @Override
    public void draw() {
        if (!isDroppedOff() && !isPickedUp) {
            image.draw(position.x, position.y);
            if (!isPickedUp() && !isFollowingDriver()) {
                font.drawString(String.valueOf(priority), position.x - PRIORITY_TEXT_OFFSET_X, position.y);
                font.drawString(String.format("%.1f", calculateExpectedEarnings()),
                        position.x - EARNINGS_TEXT_OFFSET_X, position.y);
            }
        }
        if (isFollowingDriver()) {
            image.draw(position.x, position.y);
        }
    }

    /**
     * Calculates the expected earnings for transporting this passenger.
     *
     * @return The expected earnings
     */
    public double calculateExpectedEarnings() {
        return yDistance * ratePerY + priorityRates[priority - 1];
    }

    /**
     * Increases the priority of the passenger if possible.
     */
    public void increasePriority() {
        if (priority > 1 && !priorityIncreased) {
            priority--;
            priorityIncreased = true;
        }
    }

    /**
     * Applies damage to the passenger.
     *
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(double amount) {
        health -= amount;
        if (health <= 0) {
            //handle passenger death, or in checkEndGame in shadowTaxi
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

        if (other instanceof Car || other instanceof EnemyCar || other instanceof Fireball) {
            takeDamage(((Damageable) other).getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            separationFramesLeft = SEPARATION_FRAMES;

            //determine separation direction
            separationDirection = new Point(
                    position.x < other.getPosition().x ? -2 : 2,
                    position.y < other.getPosition().y ? -2 : 2
            );
        }
    }

    /**
     * Updates the passenger's priority based on weather conditions.
     *
     * @param currentWeather The current weather condition
     */
    public void updatePriority(WeatherCondition.WeatherType currentWeather) {
        if (currentWeather == WeatherCondition.WeatherType.RAINING && !hasUmbrella) {
            priority = 1;
        }
    }

    /**
     * Makes the passenger follow the driver's position.
     *
     * @param driverPosition The current position of the driver
     */
    public void followDriver(Point driverPosition) {
        if (isFollowingDriver) {
            moveTowards(driverPosition);
        }
    }

    //getters and setters
    /**
     * Gets the current health of the passenger.
     *
     * @return The current health value
     */
    @Override
    public double getHealth() {
        return health;
    }

    /**
     * Gets the current position of the passenger.
     *
     * @return The Point representing the passenger's position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the position of the passenger.
     *
     * @param newPosition The new position for the passenger
     */
    public void setPosition(Point newPosition) {
        super.setPosition(newPosition);
    }

    /**
     * Gets the priority of the passenger.
     *
     * @return The priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Checks if the passenger is currently picked up.
     *
     * @return true if the passenger is picked up, false otherwise
     */
    public boolean isPickedUp() {
        return isPickedUp;
    }

    /**
     * Sets the picked up state of the passenger.
     *
     * @param pickedUp The new picked up state
     */
    public void setPickedUp(boolean pickedUp) {
        this.isPickedUp = pickedUp;
        if (pickedUp) {
            this.isWalking = false;
        }
    }

    /**
     * Checks if the passenger has been dropped off.
     *
     * @return true if the passenger is dropped off, false otherwise
     */
    public boolean isDroppedOff() {
        return isDroppedOff;
    }

    /**
     * Sets the dropped off state of the passenger.
     *
     * @param droppedOff The new dropped off state
     */
    public void setDroppedOff(boolean droppedOff) {
        isDroppedOff = droppedOff;
    }

    /**
     * Sets the target position for the passenger to move towards.
     *
     * @param target The target position
     */
    public void setTargetPosition(Point target) {
        this.targetPosition = target;
    }

    /**
     * Gets the current target position of the passenger.
     *
     * @return The Point representing the target position
     */
    public Point getTargetPosition() {
        return targetPosition;
    }

    /**
     * Checks if the passenger is currently walking.
     *
     * @return true if the passenger is walking, false otherwise
     */
    public boolean isWalking() {
        return isWalking;
    }

    /**
     * Sets the walking state of the passenger.
     *
     * @param walking The new walking state
     */
    public void setWalking(boolean walking) {
        this.isWalking = walking;
    }

    /**
     * Gets the damage this passenger can inflict on other entities.
     *
     * @return The damage value
     */
    @Override
    public int getDamage() {
        return damage;
    }

    /**
     * Sets whether the passenger is following the driver.
     *
     * @param followingDriver The new following driver state
     */
    public void setFollowingDriver(boolean followingDriver) {
        this.isFollowingDriver = followingDriver;
    }

    /**
     * Checks if the passenger is currently following the driver.
     *
     * @return true if the passenger is following the driver, false otherwise
     */
    public boolean isFollowingDriver() {
        return isFollowingDriver;
    }
}