import bagel.*;
import bagel.util.Point;

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


    public Driver(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.health = INITIAL_HEALTH;
        this.invincibilityFrames = 0;
        this.collisionTimeout = 0;
        this.damage = 0;
    }

    /*
    move method for when driver has left taxi
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

    /*
    method for keeping position equal to taxi, and handle power-up cooldowns
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


    @Override
    public void draw() {
        //only draw if taxi was destroyed
        if (currentTaxi == null) {
            image.draw(position.x, position.y);
        }
    }

    /*
    method to inflict damage to driver
     */
    @Override
    public void takeDamage(double amount) {
        if (invincibilityFrames == 0) {
            health -= amount;
            collisionTimeout = COLLISION_TIMEOUT;
            if (health <= 0) {
                // Handle driver "death" or game over
            }
        }
    }

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


    public boolean enterTaxi(Taxi taxi) {
        if (taxi != null && !taxi.isDamaged() && position.distanceTo(taxi.getPosition()) <= 10) {
            currentTaxi = taxi;
            taxi.setHasDriver(true);
            return true;
        }
        return false;
    }

    public void exitTaxi() {
        if (currentTaxi != null) {
            currentTaxi.setHasDriver(false);
            currentTaxi = null;
        }
    }

    public void activateInvincibility() {
        invincibilityFrames = INVINCIBILITY_DURATION;
    }

    //getters and setters
    @Override
    public double getHealth() {
        return health;
    }

    public boolean isInTaxi() {
        return currentTaxi != null;
    }

    public Taxi getCurrentTaxi() {
        return currentTaxi;
    }

    public void setCurrentTaxi(Taxi taxi) {
        this.currentTaxi = taxi;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    public void setCollisionTimeout(int amount) {
        this.collisionTimeout = amount;

    }

}