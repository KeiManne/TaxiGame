public class EnemyCar extends Car {
    private static final double FIREBALL_SPEED = 7.0;
    private static final int FIREBALL_SPAWN_CHANCE = 300; //1 in 300 chance per frame

    public EnemyCar(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius);
    }

    @Override
    public void update() {
        super.update();
        //shoot fireball based on 1 in 300 chance
        if (Math.random() * FIREBALL_SPAWN_CHANCE < 1) {
            shootFireball();
        }
    }

    public Fireball shootFireball() {
        return new Fireball(position.x, position.y, "res/fireball.png", 10.0, 0, FIREBALL_SPEED);
    }

    @Override
    public void handleCollision(GameEntity other) {
        super.handleCollision(other);
    }

    @Override
    public void takeDamage(double amount) {
        super.takeDamage(amount);
    }
}