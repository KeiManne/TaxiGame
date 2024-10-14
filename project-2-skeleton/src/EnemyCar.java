/**
 * Represents an enemy car in the game that can shoot fireballs.
 */
public class EnemyCar extends Car {
    private static final double FIREBALL_SPEED = 7.0;
    private static final int FIREBALL_SPAWN_CHANCE = 300; //1 in 300 chance per frame

    /**
     * Constructs a new EnemyCar at the specified position.
     *
     * @param x The x-coordinate of the enemy car's initial position
     * @param y The y-coordinate of the enemy car's initial position
     * @param imagePath The file path to the enemy car's image
     * @param radius The collision radius of the enemy car
     */
    public EnemyCar(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius);
    }

    /**
     * Updates the enemy car's state and potentially shoots a fireball.
     */
    @Override
    public void update() {
        super.update();
        //shoot fireball based on 1 in 300 chance
        if (Math.random() * FIREBALL_SPAWN_CHANCE < 1) {
            shootFireball();
        }
    }

    /**
     * Creates and returns a new Fireball projectile.
     *
     * @return A new Fireball object
     */
    public Fireball shootFireball() {
        return new Fireball(position.x, position.y, "res/fireball.png", 10.0, 0, FIREBALL_SPEED);
    }

    /**
     * Handles collision with another game entity.
     *
     * @param other The other entity involved in the collision
     */
    @Override
    public void handleCollision(GameEntity other) {
        super.handleCollision(other);
    }

    /**
     * Applies damage to the enemy car.
     *
     * @param amount The amount of damage to apply
     */
    @Override
    public void takeDamage(double amount) {
        super.takeDamage(amount);
    }
}