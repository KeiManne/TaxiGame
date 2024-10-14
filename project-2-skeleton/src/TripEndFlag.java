import bagel.util.Point;

public class TripEndFlag extends GameEntity {
    private boolean isVisible;

    public TripEndFlag(double x, double y, String imagePath, double radius) {
        super(x, y, imagePath, radius);
        this.isVisible = false;
    }

    @Override
    public void update() {
        //add any per-frame updates
    }

    @Override
    public void draw() {
        if (isVisible) {
            image.draw(position.x, position.y);
        }
    }


    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + SCROLL_SPEED);
        }
    }

    //getters and setters
    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Point getPosition() {
        return position;
    }
}