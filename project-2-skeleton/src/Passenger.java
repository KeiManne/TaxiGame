import bagel.*;
import bagel.util.Point;

public class Passenger {
    private final Image image;
    private Point position;
    private final Point startPosition;
    private int priority;
    private final double endX;
    private final double walkSpeedX;
    private final double walkSpeedY;
    private final double yDistance;
    private final Font font;
    private final double ratePerY;
    private final double[] priorityRates;
    private boolean isPickedUp;
    private boolean isDroppedOff;
    private boolean isWalking;
    private Point targetPosition;
    private final double verticalScrollSpeed;
    private boolean priorityIncreased;
    private static final double PRIORITY_TEXT_OFFSET_X = 30;
    private static final double EARNINGS_TEXT_OFFSET_X = 100;

    public Passenger(double x, double y, int priority, double endX, double yDistance, String imagePath,
                     String fontPath, int fontSize, double ratePerY, double priorityRate1, double priorityRate2, double priorityRate3,
                     double walkSpeedX, double walkSpeedY, double verticalScrollSpeed) {
        this.image = new Image(imagePath);
        this.walkSpeedX = walkSpeedX;
        this.walkSpeedY = walkSpeedY;
        this.position = new Point(x, y);
        this.startPosition = new Point(x, y);
        this.priority = priority;
        this.endX = endX;
        this.yDistance = yDistance;
        this.font = new Font(fontPath, fontSize);
        this.ratePerY = ratePerY;
        this.priorityRates = new double[]{priorityRate1, priorityRate2, priorityRate3};
        this.isPickedUp = false;
        this.isDroppedOff = false;
        this.isWalking = false;
        this.verticalScrollSpeed = verticalScrollSpeed;
        this.priorityIncreased = false;
    }

    /*
    method to create vertical movement for passengers
     */
    public void move(boolean moveDown) {
        if (moveDown && !isPickedUp) {
            position = new Point(position.x, position.y + verticalScrollSpeed);
        }
    }

    /*
    method to create 'walking' mechanics for passengers
     */
    public void updatePosition() {
        if (isWalking && targetPosition != null) {
            double dx = targetPosition.x - position.x;
            double dy = targetPosition.y - position.y;

            //determine the movement for the frame
            double moveX = 0;
            double moveY = 0;

            if (Math.abs(dx) >= Math.abs(dy)) {
                //move in x
                moveX = Math.min(Math.abs(dx), walkSpeedX) * Math.signum(dx);
            } else {
                //move in y
                moveY = Math.min(Math.abs(dy), walkSpeedY) * Math.signum(dy);
            }

            //update position and check if at target
            position = new Point(position.x + moveX, position.y + moveY);
            if (Math.abs(targetPosition.x - position.x) < walkSpeedX && Math.abs(targetPosition.y - position.y) < walkSpeedY) {
                position = targetPosition;
                isWalking = false;
            }
        }
    }

    public void draw() {
        if (!isPickedUp) {
            image.draw(position.x, position.y);
            if (!isDroppedOff) {
                font.drawString(String.valueOf(priority), position.x - PRIORITY_TEXT_OFFSET_X, position.y);
                font.drawString(String.format("%.1f", calculateExpectedEarnings()), position.x -
                        EARNINGS_TEXT_OFFSET_X, position.y);
            }
        }
    }

    private double calculateExpectedEarnings() {
        return yDistance * ratePerY + priorityRates[priority - 1];
    }

    public void increasePriority() {
        if (priority > 1 && !priorityIncreased) {
            priority--;
            priorityIncreased = true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Passenger passenger = (Passenger) obj;
        return position.equals(passenger.position);
    }


    //getters and setters
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }
    public int getPriority() {
        return priority;
    }
    public boolean isPickedUp() {
        return isPickedUp;
    }
    public void setPickedUp(boolean pickedUp) {
        isPickedUp = pickedUp;
    }
    public boolean isDroppedOff() {
        return isDroppedOff;
    }
    public void setDroppedOff(boolean droppedOff) {
        isDroppedOff = droppedOff;
    }
    public double getEndX() {
        return endX;
    }
    public double getYDistance() {
        return yDistance;
    }
    public Point getStartPosition() {
        return startPosition;
    }
    public void setTargetPosition(Point targetPosition) {
        this.targetPosition = targetPosition;
    }
    public boolean isWalking() {
        return isWalking;
    }
    public void setWalking(boolean walking) {
        isWalking = walking;
    }
    public boolean isPriorityIncreased() {
        return priorityIncreased;
    }
}