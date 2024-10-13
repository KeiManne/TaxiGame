import bagel.*;
import bagel.util.Point;

public class Driver extends MovableEntity implements Damageable, Collidable {
    private static final double INITIAL_HEALTH = 100.0;
    private static final int INVINCIBILITY_DURATION = 1000;

    private double health;
    private Taxi currentTaxi;
    private int invincibilityFrames;
    private int damage;

    public Driver(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.health = INITIAL_HEALTH;
        this.invincibilityFrames = 0;
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
    }


    @Override
    public void draw() {
    }

    @Override
    public void takeDamage(double amount) {
    }

    @Override
    public void handleCollision(GameEntity other) {
    }


    public void activateInvincibility() {
        invincibilityFrames = INVINCIBILITY_DURATION;
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

}