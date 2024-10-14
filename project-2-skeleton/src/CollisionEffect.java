import bagel.util.Point;

public class CollisionEffect extends GameEntity {
    private static final int DISPLAY_FRAMES = 20;
    private int remainingFrames;
    private EffectType type;

    public enum EffectType { SMOKE, FIRE, BLOOD }

    /*
    constructor to create the relevant collision effect
     */
    public CollisionEffect(double x, double y, String imagePath, EffectType type) {
        //radius not relevant for effects
        super(x, y, imagePath, 0);
        this.type = type;
        this.remainingFrames = DISPLAY_FRAMES;
    }

    @Override
    public void update() {
        remainingFrames--;
    }

    @Override
    public void draw() {
        if (remainingFrames > 0) {
            image.draw(position.x, position.y);
        }
    }

    public void moveVertically(boolean moveDown) {
        if (moveDown) {
            position = new Point(position.x, position.y + 5);
        }
    }

    //getters and setters
    public boolean isActive() {
        return remainingFrames > 0;
    }
}