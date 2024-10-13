import bagel.util.Point;

public class Fireball extends IndependentlyMovableEntity implements Collidable {
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
        moveIndependently();
    }

    @Override
    public void draw() {
        image.draw(position.x, position.y);
    }
}