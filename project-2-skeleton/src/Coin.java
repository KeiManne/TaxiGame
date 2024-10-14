import bagel.util.Point;

public class Coin extends GameEntity implements PowerUp {
    private boolean isActive;

    public Coin(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius);
        this.isActive = true;
    }

    @Override
    public void update() {
        //add any per frame updates
    }

    @Override
    public void draw() {
        if (isActive) {
            image.draw(position.x, position.y);
        }
    }

    /*
    move coin down as background scrolls
     */
    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + SCROLL_SPEED);
        }
    }

    /*
    method to apply invincible power and coin power
     */
    @Override
    public void applyEffect(GameEntity entity) {
        if (entity instanceof Taxi) {
            ((Taxi) entity).activateCoinPower();
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