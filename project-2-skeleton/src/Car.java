import bagel.util.Point;

/**
 * Represents a car in the game that moves independently and can collide with other entities.
 */
public class Car extends IndependentlyMovableEntity implements Damageable, Collidable {
    private static final int COLLISION_TIMEOUT = 200;
    private static final int SEPARATION_FRAMES = 10;
    private static final double MIN_SPEED = 2.0;
    private static final double MAX_SPEED = 5.0;
    private static final int DAMAGE_POINTS = 50;
    private static final double CAR_HEALTH = 100;

    private double health;
    private int collisionTimeout;
    private int damage;
    private boolean isDamaged;
    private int separationFramesLeft;
    private Point separationDirection;
    private boolean isColliding;

    /**
     * Constructs a new Car at the specified position.
     *
     * @param x The x-coordinate of the car's initial position
     * @param y The y-coordinate of the car's initial position
     * @param imagePath The file path to the car's image
     * @param radius The collision radius of the car
     */
    public Car(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius, 0, generateRandomSpeed());
        this.health = CAR_HEALTH;
        this.isDamaged = false;
        this.damage = DAMAGE_POINTS;
    }

    private static double generateRandomSpeed() {
        return MIN_SPEED + Math.random() * (MAX_SPEED - MIN_SPEED);
    }

    /**
     * Moves the car independently based on its current state and speed.
     */
    @Override
    public void moveIndependently() {
        if (!isColliding) {
            position = new Point(position.x, position.y - speedY);
        }
    }

    /**
     * Updates the car's state, including collision timeouts and movement.
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
            } else if (collisionTimeout == 0) {
                //collision timeout ended, choose new random speed
                speedY = generateRandomSpeed();
                isColliding = false;
            }
        }
        if (!isColliding) {
            moveIndependently();
        }
    }

    /**
     * Updates the car's state, considering vertical scrolling of the game world.
     *
     * @param moveDown true if the game world is scrolling down, false otherwise
     */
    public void update(boolean moveDown) {
        if (collisionTimeout > 0) {
            collisionTimeout--;

            if (separationFramesLeft > 0) {
                position = new Point(
                        position.x + separationDirection.x,
                        position.y + separationDirection.y
                );
                separationFramesLeft--;
            } else if (collisionTimeout == 0) {
                //collision timeout ended, choose new random speed
                speedY = generateRandomSpeed();
                isColliding = false;
            }
        }

        if (isColliding) {
            if (moveDown) {
                position = new Point(position.x, position.y + SCROLL_SPEED);
            }
        }

        if (!isColliding) {
            moveIndependently();
        }
    }

    /**
     * Renders the car on the game screen.
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
        if (collisionTimeout > 0) return;

        if (other instanceof Damageable) {
            takeDamage(((Damageable) other).getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            separationFramesLeft = SEPARATION_FRAMES;
            isColliding = true;

            //determine separation direction
            if (this.position.y < other.getPosition().y) {
                separationDirection = new Point(0, 1);
            } else {
                separationDirection = new Point(0, -1);
            }
        }
    }

    /**
     * Applies damage to the car.
     *
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(double amount) {
        health -= amount;
        if (health <= 0) {
            isDamaged = true;
        }
    }

    //getters and setters
    /**
     * Gets the current health of the car.
     *
     * @return The current health value
     */
    @Override
    public double getHealth() {
        return health;
    }

    /**
     * Gets the current health of the car.
     *
     * @return The current health value
     */
    @Override
    public int getDamage() {
        return damage;
    }

    /**
     * Checks if the car is currently in a collision timeout period.
     *
     * @return true if the car is in collision timeout, false otherwise
     */
    public boolean isInCollisionTimeout() {
        return collisionTimeout > 0;
    }

    /**
     * Checks if the car is damaged (health <= 0).
     *
     * @return true if the car is damaged, false otherwise
     */
    public boolean isDamaged() {
        return isDamaged;
    }
}