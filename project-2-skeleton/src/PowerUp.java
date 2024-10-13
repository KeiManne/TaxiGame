public interface PowerUp {
    void applyEffect(GameEntity entity);
    boolean isActive();
    void setActive(boolean active);
}