import bagel.*;
import bagel.util.Point;

public class Car extends IndependentlyMovableEntity implements Damageable {
    private static final int COLLISION_TIMEOUT = 200;
    private double health;
    private int collisionTimeout;
    private static final double MIN_SPEED = 2.0;
    private static final double MAX_SPEED = 5.0;
    private int damage = 50;

    public Car(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius, 0, generateRandomSpeed());
        this.health = 100.0;
    }

    private static double generateRandomSpeed() {
        return MIN_SPEED + Math.random() * (MAX_SPEED - MIN_SPEED);
    }

    @Override
    public void moveIndependently() {
        position = new Point(position.x, position.y - speedY);
    }

    @Override
    public void update() {
        if (collisionTimeout > 0) collisionTimeout--;
        moveIndependently();
    }

    @Override
    public void draw() {
        image.draw(position.x, position.y);
    }

    @Override
    public void handleCollision(GameEntity other) {
        if (collisionTimeout > 0) return;

        if (other instanceof Damageable) {
            takeDamage(((Damageable) other).getDamage() * 1);
            collisionTimeout = COLLISION_TIMEOUT;
        }
    }

    @Override
    public void takeDamage(double amount) {
        health -= amount;
        if (health <= 0) {
            //handle car destruction with explosion and dead image
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
}