import bagel.util.Point;

/**
 * Represents the end flag for a passenger's trip.
 */
public class TripEndFlag extends GameEntity {
    private boolean isVisible;

    /**
     * Constructs a new TripEndFlag at the specified position.
     *
     * @param x The x-coordinate of the flag's position
     * @param y The y-coordinate of the flag's position
     * @param imagePath The file path to the flag's image
     * @param radius The collision radius of the flag
     */
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