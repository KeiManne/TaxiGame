/**
 * Acts to signal game entities that can collide with each other.
 */
public interface Collidable {
    /**
     * Skeleton method to handle the collision between this entity and another game entity.
     *
     * @param other The other entity involved in the collision
     */
    void handleCollision(GameEntity other);
}