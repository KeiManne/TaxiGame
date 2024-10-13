public abstract class IndependentlyMovableEntity extends MovableEntity {
    public IndependentlyMovableEntity(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
    }

    public abstract void moveIndependently();
}