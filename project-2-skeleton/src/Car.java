import bagel.util.Point;

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

    public Car(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius, 0, generateRandomSpeed());
        this.health = CAR_HEALTH;
        this.isDamaged = false;
        this.damage = DAMAGE_POINTS;
    }

    private static double generateRandomSpeed() {
        return MIN_SPEED + Math.random() * (MAX_SPEED - MIN_SPEED);
    }

    @Override
    public void moveIndependently() {
        if (!isColliding) {
            position = new Point(position.x, position.y - speedY);
        }
    }

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

    @Override
    public void draw() {
        image.draw(position.x, position.y);
    }

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


    @Override
    public void takeDamage(double amount) {
        health -= amount;
        if (health <= 0) {
            isDamaged = true;
        }
    }

    //getters and setters
    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    public boolean isInCollisionTimeout() {
        return collisionTimeout > 0;
    }

    public boolean isDamaged() {
        return isDamaged;
    }
}