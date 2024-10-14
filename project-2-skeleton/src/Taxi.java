import bagel.*;
import bagel.util.Point;

public class Taxi extends MovableEntity implements Collidable, Damageable {
    private static final String DAMAGED_IMAGE = "res/taxiDamaged.png";
    private static final int COLLISION_TIMEOUT = 200;
    private static final int INVINCIBILITY_FRAMES = 1000;
    private static final int MAX_COIN_POWER_FRAMES = 500;
    private static final int SEPARATION_FRAMES = 10;
    private static final int TAXI_HEALTH = 100;
    private static final int TAXI_DAMAGE = 25;
    //edited taxi damage to be 25, otherwise on collisions cars and enemy cars are immediately destroyed

    private double health;
    private boolean isDamaged;
    private int collisionTimeout;
    private int invincibilityFrames;
    private boolean coinPowerActive;
    private int coinPowerFrames;
    private Passenger currentPassenger;
    private boolean hasDriver;
    private boolean isMoving;
    private int damage;
    private Point separationDirection;
    private int separationFramesLeft;


    public Taxi(double x, double y, String imagePath, double radius, double speedX, double speedY) {
        super(x, y, imagePath, radius, speedX, speedY);
        this.health = TAXI_HEALTH;
        this.isDamaged = false;
        this.collisionTimeout = 0;
        this.invincibilityFrames = 0;
        this.coinPowerActive = false;
        this.coinPowerFrames = 0;
        this.hasDriver = false;
        this.currentPassenger = null;
        this.damage = TAXI_DAMAGE;
    }

    /*
    method for taxi movement when not damaged
     */
    @Override
    public void move(Input input) {
        isMoving = false;
        if (hasDriver) {
            if (input.isDown(Keys.LEFT)) {
                position = new Point(Math.max(0, position.x - speedX), position.y);
                isMoving = true;
            }
            if (input.isDown(Keys.RIGHT)) {
                position = new Point(Math.min(Window.getWidth(), position.x + speedX), position.y);
                isMoving = true;
            }
            if (input.isDown(Keys.UP)) {
                isMoving = true;
            }
        }
    }

    @Override
    public void moveVertically(boolean moveDown) {
        if (!hasDriver && moveDown) {
            super.moveVertically(moveDown);
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
        if (invincibilityFrames > 0) invincibilityFrames--;
        if (coinPowerActive) {
            coinPowerFrames++;
            if (coinPowerFrames >= MAX_COIN_POWER_FRAMES) {
                coinPowerActive = false;
                coinPowerFrames = 0;
            }
        }
        updatePassengerPosition();
    }

    private void updatePassengerPosition() {
        if (currentPassenger != null) {
            currentPassenger.setPosition(new Point(position.x, position.y));
        }
    }

    /*
    method to draw taxi in both damaged state and normal state
     */
    @Override
    public void draw() {
        if (isDamaged) {
            new Image(DAMAGED_IMAGE).draw(position.x, position.y);
        } else {
            image.draw(position.x, position.y);
        }
    }

    /*
    method for taking damage and applying power-ups to taxi
     */
    @Override
    public void handleCollision(GameEntity other) {
        //only collide if not active invincibility or recent collision
        if (collisionTimeout > 0) return;

        if (other instanceof Damageable) {
            takeDamage(((Damageable) other).getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            separationFramesLeft = SEPARATION_FRAMES;

            //determine separation direction
            if (this.position.y < other.getPosition().y) {
                separationDirection = new Point(0, -1); // Taxi moves up
            } else {
                separationDirection = new Point(0, 1); // Taxi moves down
            }
        } else if (other instanceof PowerUp) {
            ((PowerUp) other).applyEffect(this);
        }
    }

    @Override
    public void takeDamage(double amount) {
        if (invincibilityFrames == 0 && collisionTimeout == 0) {
            health -= amount;
            collisionTimeout = COLLISION_TIMEOUT;
            if (health <= 0) {
                isDamaged = true;
                ejectOccupants();
            }
        }
    }

    /*
    method to eject passenger if taxi is damaged
     */
    private void ejectOccupants() {
        if (currentPassenger != null) {
            currentPassenger.setPosition(new Point(position.x - 100, position.y));
            currentPassenger.setFollowingDriver(true);
            currentPassenger.setPickedUp(false);
        }
        hasDriver = false;
    }

    public void activateInvincibility() {
        invincibilityFrames = INVINCIBILITY_FRAMES;
    }

    public void activateCoinPower() {
        coinPowerActive = true;
        coinPowerFrames = 0;
    }

    public boolean pickupPassenger(Passenger passenger) {
        if (currentPassenger == null && !isMoving && hasDriver) {
            currentPassenger = passenger;
            passenger.setPickedUp(true);
            return true;
        }
        return false;
    }

    /*
    method to ensure drop off only occurs when taxi is not moving
     */
    public boolean canDropOffPassenger(TripEndFlag flag) {
        if (currentPassenger != null && !isMoving()) {
            return position.y <= flag.getPosition().y ||
                    position.distanceTo(flag.getPosition()) <= flag.getRadius();
        }
        return false;
    }

    public Passenger dropOffPassenger() {
        Passenger passenger = currentPassenger;
        setCurrentPassenger(null);
        return passenger;
    }


    //getters and setters
    @Override
    public double getHealth() {
        return health;
    }

    public boolean isDamaged() {
        return isDamaged;
    }

    public boolean hasCoinPower() {
        return coinPowerActive;
    }

    public int getCoinPowerFrames() {
        return coinPowerFrames;
    }

    public Passenger getCurrentPassenger() {
        return currentPassenger;
    }

    public void setCurrentPassenger(Passenger passenger) {
        this.currentPassenger = passenger;
    }

    public boolean hasDriver() {
        return hasDriver;
    }

    public void setHasDriver(boolean hasDriver) {
        this.hasDriver = hasDriver;
    }


    public boolean isMoving() {
        return isMoving;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}