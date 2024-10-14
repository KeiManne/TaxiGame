/**
 * Defines the behavior for game entities that can take damage.
 */
public interface Damageable {
    /**
     * Applies damage to the entity.
     *
     * @param amount The amount of damage to apply
     */
    void takeDamage(double amount);

    /**
     * Gets the current health of the entity.
     *
     * @return The current health value
     */
    double getHealth();

    /**
     * Gets the damage this entity can inflict on others.
     *
     * @return The damage value
     */
    int getDamage();
}

