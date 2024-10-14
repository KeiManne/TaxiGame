import bagel.util.Point;

public class InvinciblePower extends GameEntity implements PowerUp {
    private boolean isActive;
    private double speedY;

    public InvinciblePower(double x, double y, String imagePath, double radius, double speedY) {
        super(x, y, imagePath, radius);
        this.isActive = true;
        this.speedY = speedY;
    }

    @Override
    public void update() {
        //add any per-frame updates
    }

    @Override
    public void draw() {
        if (isActive) {
            image.draw(position.x, position.y);
        }
    }

    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + speedY);
        }
    }

    @Override
    public void applyEffect(GameEntity entity) {
        if (entity instanceof Taxi) {
            ((Taxi) entity).activateInvincibility();
        } else if (entity instanceof Driver) {
            ((Driver) entity).activateInvincibility();
        }
        setActive(false);
    }

    //getters and setters
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }


}