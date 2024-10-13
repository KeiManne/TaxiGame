import bagel.*;
import bagel.util.Point;

public class Passenger extends MovableEntity implements Damageable, Collidable {
    private static final double PRIORITY_TEXT_OFFSET_X = 30;
    private static final double EARNINGS_TEXT_OFFSET_X = 100;
    private static final int SCROLL_SPEED = 5;

    private int priority;
    private final double yDistance;
    private final Font font;
    private final double ratePerY;
    private final double[] priorityRates;
    private boolean isPickedUp;
    private boolean isDroppedOff;
    private boolean isWalking;
    private Point targetPosition;
    private boolean priorityIncreased;
    private boolean hasUmbrella;
    private double health;


    public Passenger(double x, double y, int priority, double endX, double yDistance, String imagePath,
                     String fontPath, int fontSize, double radius, double ratePerY,
                     double priorityRate1, double priorityRate2, double priorityRate3,
                     double speedX, double speedY, boolean hasUmbrella) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.priority = priority;
        this.yDistance = yDistance;
        this.font = new Font(fontPath, fontSize);
        this.ratePerY = ratePerY;
        this.priorityRates = new double[]{priorityRate1, priorityRate2, priorityRate3};
        this.isPickedUp = false;
        this.isDroppedOff = false;
        this.isWalking = false;
        this.priorityIncreased = false;
        this.hasUmbrella = hasUmbrella;
        this.health = 100.0;
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
                System.out.println("Passenger reached target at " + position);
            } else {
                double moveX = Math.signum(dx) * Math.min(Math.abs(dx), 1);
                double moveY = Math.signum(dy) * Math.min(Math.abs(dy), 1);
                position = new Point(position.x + moveX, position.y + moveY);
                System.out.println("Passenger moved to " + position + ", Target: " + targetPosition);
            }
        } else {
            System.out.println("Passenger not walking or no target set. Walking: " + isWalking + ", Target: " + targetPosition);
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
    }

    @Override
    public void draw() {
        if (!isDroppedOff() && !isPickedUp) {
            image.draw(position.x, position.y);
            if (!isPickedUp()) {
                font.drawString(String.valueOf(priority), position.x - PRIORITY_TEXT_OFFSET_X, position.y);
                font.drawString(String.format("%.1f", calculateExpectedEarnings()),
                        position.x - EARNINGS_TEXT_OFFSET_X, position.y);
            }
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
    }

    public void updatePriority(WeatherCondition.WeatherType currentWeather) {
        if (currentWeather == WeatherCondition.WeatherType.RAINING && !hasUmbrella) {
            priority = 1;
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
        System.out.println("Passenger position set to: " + newPosition);
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
        System.out.println("Passenger picked up: " + pickedUp);
    }


    public boolean isDroppedOff() {
        return isDroppedOff;
    }

    public void setDroppedOff(boolean droppedOff) {
        isDroppedOff = droppedOff;
    }

    public void setTargetPosition(Point target) {
        this.targetPosition = target;
        System.out.println("Passenger target set to: " + target);
    }

    public Point getTargetPosition() {
        return targetPosition;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean walking) {
        this.isWalking = walking;
        System.out.println("Passenger walking set to: " + walking);
    }

    @Override
    public int getDamage() {
        return 0;
    }
}