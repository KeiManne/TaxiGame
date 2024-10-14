import bagel.*;
import bagel.util.Point;

public class Passenger extends MovableEntity implements Damageable, Collidable {
    private static final double PRIORITY_TEXT_OFFSET_X = 30;
    private static final double EARNINGS_TEXT_OFFSET_X = 100;
    private static final int COLLISION_TIMEOUT = 200;
    private static final double STARTING_HEALTH = 100.0;
    private static final int SEPARATION_FRAMES = 10;

    private int priority;
    private final double endX;
    private final double yDistance;
    private final Font font;
    private final double ratePerY;
    private final double[] priorityRates;
    private final int damage;
    private boolean isPickedUp;
    private boolean isDroppedOff;
    private boolean isWalking;
    private Point targetPosition;
    private boolean priorityIncreased;
    private boolean hasUmbrella;
    private double health;
    private int collisionTimeout;
    private int separationFramesLeft;
    private Point separationDirection;
    private boolean isFollowingDriver;


    public Passenger(double x, double y, int priority, double endX, double yDistance, String imagePath,
                     String fontPath, int fontSize, double radius, double ratePerY,
                     double priorityRate1, double priorityRate2, double priorityRate3,
                     double speedX, double speedY, boolean hasUmbrella) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.priority = priority;
        this.endX = endX;
        this.yDistance = yDistance;
        this.font = new Font(fontPath, fontSize);
        this.ratePerY = ratePerY;
        this.priorityRates = new double[]{priorityRate1, priorityRate2, priorityRate3};
        this.isPickedUp = false;
        this.isDroppedOff = false;
        this.isWalking = false;
        this.priorityIncreased = false;
        this.hasUmbrella = hasUmbrella;
        this.health = STARTING_HEALTH;
        this.damage = 0;
    }

    /*
    method to enable walking mechanics - taken from my project 1
     */
    public void moveTowards(Point target) {
        if (isWalking && targetPosition != null) {
            double dx = targetPosition.x - position.x;
            double dy = targetPosition.y - position.y;

            if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                position = targetPosition;
                isWalking = false;
            } else {
                double moveX = Math.signum(dx) * Math.min(Math.abs(dx), 1);
                double moveY = Math.signum(dy) * Math.min(Math.abs(dy), 1);
                position = new Point(position.x + moveX, position.y + moveY);
            }
        }
    }

    @Override
    public void moveVertically(boolean moveDown) {
        if (!isPickedUp && moveDown) {
            position = new Point(position.x, position.y + SCROLL_SPEED);
        }
    }

    @Override
    public void update() {
        if (collisionTimeout > 0) {
            collisionTimeout--;

            if (separationFramesLeft > 0) {
                position = new Point(
                        position.x + separationDirection.x,
                        position.y + separationDirection.y
                );
                separationFramesLeft--;
            }
        }
    }

    @Override
    public void draw() {
        if (!isDroppedOff() && !isPickedUp) {
            image.draw(position.x, position.y);
            if (!isPickedUp() && !isFollowingDriver()) {
                font.drawString(String.valueOf(priority), position.x - PRIORITY_TEXT_OFFSET_X, position.y);
                font.drawString(String.format("%.1f", calculateExpectedEarnings()),
                        position.x - EARNINGS_TEXT_OFFSET_X, position.y);
            }
        }
        if (isFollowingDriver()) {
            image.draw(position.x, position.y);
        }
    }

    public double calculateExpectedEarnings() {
        return yDistance * ratePerY + priorityRates[priority - 1];
    }

    public void increasePriority() {
        if (priority > 1 && !priorityIncreased) {
            priority--;
            priorityIncreased = true;
        }
    }

    @Override
    public void takeDamage(double amount) {
        health -= amount;
        if (health <= 0) {
            //handle passenger death, or in checkEndGame in shadowTaxi
        }
    }

    @Override
    public void handleCollision(GameEntity other) {
        if (collisionTimeout > 0) return;

        if (other instanceof Car || other instanceof EnemyCar || other instanceof Fireball) {
            takeDamage(((Damageable) other).getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            separationFramesLeft = SEPARATION_FRAMES;

            //determine separation direction
            separationDirection = new Point(
                    position.x < other.getPosition().x ? -2 : 2,
                    position.y < other.getPosition().y ? -2 : 2
            );
        }
    }


    public void updatePriority(WeatherCondition.WeatherType currentWeather) {
        if (currentWeather == WeatherCondition.WeatherType.RAINING && !hasUmbrella) {
            priority = 1;
        }
    }

    public void followDriver(Point driverPosition) {
        if (isFollowingDriver) {
            moveTowards(driverPosition);
        }
    }

    //getters and setters
    @Override
    public double getHealth() {
        return health;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point newPosition) {
        super.setPosition(newPosition);
    }


    public int getPriority() {
        return priority;
    }

    public boolean isPickedUp() {
        return isPickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.isPickedUp = pickedUp;
        if (pickedUp) {
            this.isWalking = false;
        }
    }

    public boolean isDroppedOff() {
        return isDroppedOff;
    }

    public void setDroppedOff(boolean droppedOff) {
        isDroppedOff = droppedOff;
    }

    public void setTargetPosition(Point target) {
        this.targetPosition = target;
    }

    public Point getTargetPosition() {
        return targetPosition;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean walking) {
        this.isWalking = walking;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    public void setFollowingDriver(boolean followingDriver) {
        this.isFollowingDriver = followingDriver;
    }
    public boolean isFollowingDriver() {
        return isFollowingDriver;
    }

}