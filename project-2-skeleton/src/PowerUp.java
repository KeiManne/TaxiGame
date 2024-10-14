/**
 * Defines the behavior for power-up entities in the game.
 */
public interface PowerUp {
    /**
     * Applies the power-up effect to the given game entity.
     *
     * @param entity The entity to apply the power-up effect to
     */
    void applyEffect(GameEntity entity);

    /**
     * Sets the active state of the power-up.
     *
     * @param active The new active state
     */
    void setActive(boolean active);
}