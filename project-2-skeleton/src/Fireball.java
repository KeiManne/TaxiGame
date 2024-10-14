import bagel.util.Point;

public class Fireball extends IndependentlyMovableEntity implements Collidable, Damageable {
    private static final double DAMAGE = 20.0;

    public Fireball(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
    }

    @Override
    public void moveIndependently() {
        position = new Point(position.x, position.y - speedY);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw() {
        image.draw(position.x, position.y);
    }

    @Override
    public void handleCollision(GameEntity other) {
        if (other instanceof Damageable) {
            ((Damageable) other).takeDamage(DAMAGE);
        }
    }

    @Override
    public void takeDamage(double amount) {
        //damage is not relevant for fireball
    }

    @Override
    public double getHealth() {
        //health is not relevant for fireball
        return 0;
    }

    @Override
    public int getDamage() {
        return (int) DAMAGE;
    }
}