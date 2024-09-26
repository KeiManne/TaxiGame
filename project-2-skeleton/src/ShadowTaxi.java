import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import java.util.Arrays;
import java.util.Properties;


/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2024
 * Please enter your name below
 * @author Keith Menezes
 */
public class ShadowTaxi extends AbstractGame {

    //initialise properties files
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    //initialise images
    private final Image BACKGROUND_IMAGE;
    private final Image PLAYERINFO_IMAGE;
    private final Image GAMEPLAY_IMAGE;
    private final Image GAME_END_IMAGE;

    //initialise fonts and text elements
    private final Font FONT_TITLE;
    private final Font FONT_INSTRUCTION;
    private final Font FONT_PI;
    private final Font FONT_GAMEPLAY;
    private final int LINE_SPACING_PI;
    private final int LINE_SPACING_GAME_PLAY;
    private final Font FONT_END_STATUS;
    private final Font FONT_END_SCORES;
    private final double BACKGROUND_RESET_FACTOR;
    private final double LINE_SPACING_END_MESSAGE;
    private final double LINE_SPACING_TOP_SCORES;

    //initialise game objects
    private Taxi taxi;
    private Passenger[] passengers;
    private Coin[] coins;
    private boolean[] activeCoins;
    private TripEndFlag[] tripEndFlags;
    private Passenger currentPassenger;

    //initialise game play variables
    private static GameState currentState;
    private String playerName;
    private int currentFrame;
    private double totalScore;
    private double backgroundA;
    private double backgroundB;
    private boolean isWin;
    private final int MAX_TOP_SCORES;

    //initialise properties files variables
    private final int WINDOW_WIDTH;
    private final int WINDOW_HEIGHT;
    private final double VERTICAL_SCROLL_SPEED; //This is taxi speed y
    private final double PASSENGER_PICKUP_DISTANCE;
    private final double PASSENGER_DROPOFF_DISTANCE;
    private final int MAX_FRAMES;

    private int coinPowerFrames;
    private boolean coinPowerisActive;
    private final int MAX_COIN_POWER_FRAMES;

    private double lastTripExpectedEarnings;
    private double lastTripActualEarnings;
    private double lastTripPenalty;
    private int lastTripPriority;

    //enum object to manage the different states of the game for update method (clean up code)
    private enum GameState {
        HOME,
        PLAYER_INFO,
        GAME_PLAY,
        GAME_END
    }

    /*
    Constructor that creates a ShadowTaxi object with game and message properties files
     */
    public ShadowTaxi(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage.home"));
        PLAYERINFO_IMAGE = new Image(gameProps.getProperty("backgroundImage.playerInfo"));
        GAMEPLAY_IMAGE = new Image(gameProps.getProperty("backgroundImage.sunny"));
        GAME_END_IMAGE = new Image(gameProps.getProperty("backgroundImage.gameEnd"));
        BACKGROUND_RESET_FACTOR = 1.5;

        FONT_TITLE = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("home.title.fontSize")));
        FONT_INSTRUCTION = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("home.instruction.fontSize")));
        FONT_PI = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("playerInfo.fontSize")));
        FONT_GAMEPLAY = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("gamePlay.info.fontSize")));
        LINE_SPACING_PI = 30;
        LINE_SPACING_GAME_PLAY = 30;
        FONT_END_STATUS = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("gameEnd.status.fontSize")));
        FONT_END_SCORES = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.fontSize")));
        LINE_SPACING_END_MESSAGE = 30;
        LINE_SPACING_TOP_SCORES = 40;

        WINDOW_WIDTH = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        WINDOW_HEIGHT = Integer.parseInt(GAME_PROPS.getProperty("window.height"));
        VERTICAL_SCROLL_SPEED = Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedY"));
        PASSENGER_PICKUP_DISTANCE = Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.taxiDetectRadius")
        );
        PASSENGER_DROPOFF_DISTANCE = 0;
        MAX_FRAMES = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames"));
        MAX_COIN_POWER_FRAMES = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.coin.maxFrames"));
        MAX_TOP_SCORES = 5;

        playerName = "";
        currentFrame = 0;
        totalScore = 0;
        isWin = false;

        coinPowerFrames = 0;
        coinPowerisActive = false;
        coins = new Coin[0];
        activeCoins = new boolean[0];
        lastTripActualEarnings = 0;
        lastTripPenalty = 0;
        lastTripPriority = 0;

        backgroundA = Window.getHeight() / 2.0;
        backgroundB = -Window.getHeight() / 2.0;

        readGameObjects();

        //set currentState to start as HOME
        currentState = GameState.HOME;
    }

    /*
    method to read game objects from CSV, assumes objects are formatted perfectly
     */
    private void readGameObjects() {
        String[][] worldObjects = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gamePlay.objectsFile"));
        int passengerCount = 0;
        int coinCount = 0;

        for (String[] object : worldObjects) {
            if (object[0].equals("PASSENGER")) passengerCount++;
            if (object[0].equals("COIN")) coinCount++;
        }
        passengers = new Passenger[passengerCount];
        coins = new Coin[coinCount];
        activeCoins = new boolean[coinCount];
        tripEndFlags = new TripEndFlag[passengerCount];

        int passengerIndex = 0;
        int coinIndex = 0;

        //loop through csv lines and initialise objects for each item in gameObjects
        for (String[] object : worldObjects) {
            switch (object[0]) {
                case "TAXI":
                    taxi = new Taxi(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            GAME_PROPS.getProperty("gameObjects.taxi.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.radius")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedX")));
                    break;
                case "PASSENGER":
                    double passengerX = Double.parseDouble(object[1]);
                    double passengerY = Double.parseDouble(object[2]);
                    int priority = Integer.parseInt(object[3]);
                    double endX = Double.parseDouble(object[4]);
                    double yDistance = Double.parseDouble(object[5]);
                    passengers[passengerIndex] = new Passenger(
                            passengerX, passengerY, priority, endX, yDistance,
                            GAME_PROPS.getProperty("gameObjects.passenger.image"),
                            GAME_PROPS.getProperty("font"),
                            Integer.parseInt(GAME_PROPS.getProperty("gameObjects.passenger.fontSize")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.perY")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority1")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority2")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority3")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.walkSpeedX")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.walkSpeedY")),
                            VERTICAL_SCROLL_SPEED
                    );
                    tripEndFlags[passengerIndex] = new TripEndFlag(endX, passengerY - yDistance,
                            GAME_PROPS.getProperty("gameObjects.tripEndFlag.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.tripEndFlag.radius")),
                            VERTICAL_SCROLL_SPEED);
                    passengerIndex++;
                    break;
                case "COIN":
                    coins[coinIndex] = new Coin(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            GAME_PROPS.getProperty("gameObjects.coin.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.coin.radius")),
                            VERTICAL_SCROLL_SPEED);
                    activeCoins[coinIndex] = true;
                    coinIndex++;
                    break;
            }
        }
    }

    /*
    method to handle all operations related to passenger pickup
     */
    private void handlePassengerPickup() {
        //if no current passenger and within pickup range, set to walking
        if (currentPassenger == null) {
            for (Passenger passenger : passengers) {
                if (!passenger.isPickedUp() && !passenger.isDroppedOff() && !passenger.isWalking()) {
                    double distance = calculateDistance(taxi.getPosition(), passenger.getPosition());
                    if (distance <= PASSENGER_PICKUP_DISTANCE && !taxi.isMoving()) {
                        passenger.setWalking(true);
                        passenger.setTargetPosition(taxi.getPosition());
                        break;
                    }
                }
            }
        }
        //once walking, pickup passenger once it walks to car (reminder change dist to 0)
        for (Passenger passenger : passengers) {
            if (passenger.isWalking() && !passenger.isPickedUp()) {
                passenger.updatePosition();
                double distance = calculateDistance(taxi.getPosition(), passenger.getPosition());
                if (distance <= PASSENGER_DROPOFF_DISTANCE) {
                    currentPassenger = passenger;
                    currentPassenger.setPickedUp(true);
                    currentPassenger.setWalking(false);
                    tripEndFlags[Arrays.asList(passengers).indexOf(currentPassenger)].setVisible(true);
                }
                break;
            }
        }
        //update current passenger coordinates
        if (currentPassenger != null && currentPassenger.isPickedUp()) {
            currentPassenger.setPosition(taxi.getPosition());
        }
    }

    /*
    method handle all operations related to passenger dropoff
     */
    private void handlePassengerDropOff() {
        //if current passenger, display flag and calc distance to flag
        if (currentPassenger != null) {
            int passengerIndex = Arrays.asList(passengers).indexOf(currentPassenger);
            TripEndFlag flag = tripEndFlags[passengerIndex];
            double distance = calculateDistance(taxi.getPosition(), flag.getPosition());
            //dropoff passenger if conditions are met and reset variables
            if (!currentPassenger.isDroppedOff()) {
                if (!currentPassenger.isWalking() && !taxi.isMoving() &&
                        (taxi.getPosition().y <= flag.getPosition().y || distance <= flag.getRadius())) {
                    currentPassenger.setPickedUp(false);
                    currentPassenger.setWalking(true);
                    currentPassenger.setTargetPosition(flag.getPosition());
                }
                if (currentPassenger.isWalking()) {
                    currentPassenger.updatePosition();
                    if (calculateDistance(currentPassenger.getPosition(), flag.getPosition()) <=
                            PASSENGER_DROPOFF_DISTANCE) {
                        currentPassenger.setDroppedOff(true);
                        currentPassenger.setWalking(false);
                        flag.setVisible(false);

                        lastTripExpectedEarnings = calculateExpectedEarnings(currentPassenger);
                        lastTripActualEarnings = calculateTripEarnings(currentPassenger, distance);
                        lastTripPenalty = calculateTripPenalty(currentPassenger, distance);
                        lastTripPriority = currentPassenger.getPriority();

                        totalScore += lastTripActualEarnings;

                        currentPassenger = null;
                    }
                }
            } else if (currentPassenger.getPosition().y > WINDOW_HEIGHT) {
                currentPassenger = null;
            }
        }
    }

    /*
    method to calculate euclidean distance (can change distance calc later for extensibility)
     */
    private double calculateDistance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /*
    method to calculate trip penalty amount on dropOff
     */
    private double calculateTripPenalty(Passenger passenger, double dropOffDistance) {
        TripEndFlag flag = tripEndFlags[Arrays.asList(passengers).indexOf(passenger)];
        if (taxi.getPosition().y < flag.getPosition().y && dropOffDistance > flag.getRadius()) {
            return (flag.getPosition().y - taxi.getPosition().y) *
                    Double.parseDouble(GAME_PROPS.getProperty("trip.penalty.perY"));
        }
        return 0;
    }

    /*
    method to handle coin collisions for update method
     */
    private void handleCoinCollision() {
        //loop that goes through active coin array and determine if collision occurs
        for (int i = 0; i < coins.length; i++) {
            if (activeCoins[i]) {
                double distance = calculateDistance(taxi.getPosition(), coins[i].getPosition());
                double collisionRange = taxi.getRadius() + coins[i].getRadius();
                //if collision, coin power becomes active and priority increase
                if (distance <= collisionRange) {
                    coinPowerisActive = true;
                    coinPowerFrames = 0;
                    if (currentPassenger != null && !currentPassenger.isPriorityIncreased()) {
                        currentPassenger.increasePriority();
                    }
                    //remove the coin
                    activeCoins[i] = false;
                    break;
                }
            }
        }
        //ensure coinPowerFrames does not exceed MAX
        if (coinPowerisActive) {
            coinPowerFrames++;
            if (coinPowerFrames >= MAX_COIN_POWER_FRAMES) {
                coinPowerisActive = false;
                coinPowerFrames = 0;
            }
        }
    }

    /*
    method to update the displayed current and last trip information to user
     */
    private void updateTripInfo() {
        double tripInfoX = Double.parseDouble(GAME_PROPS.getProperty("gameplay.tripInfo.x"));
        double tripInfoY = Double.parseDouble(GAME_PROPS.getProperty("gameplay.tripInfo.y"));

        if (currentPassenger != null && currentPassenger.isPickedUp()) {
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.onGoingTrip.title"), tripInfoX, tripInfoY);
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning") +
                    String.format("%.1f", calculateExpectedEarnings(currentPassenger)), tripInfoX, tripInfoY +
                    LINE_SPACING_GAME_PLAY);
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.priority") +
                    currentPassenger.getPriority(), tripInfoX, tripInfoY + (2 * LINE_SPACING_GAME_PLAY));
        } else if (lastTripActualEarnings > 0) {
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.completedTrip.title"), tripInfoX, tripInfoY);
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning") +
                    String.format("%.1f", lastTripExpectedEarnings), tripInfoX, tripInfoY + LINE_SPACING_GAME_PLAY);
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.priority") +
                    lastTripPriority, tripInfoX, tripInfoY + (2 * LINE_SPACING_GAME_PLAY));
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.penalty") +
                    String.format("%.2f", lastTripPenalty), tripInfoX, tripInfoY + (3 * LINE_SPACING_GAME_PLAY));
        }
    }

    /*
    method to calculate expected earnings seperately from dropoff method
     */
    private double calculateExpectedEarnings(Passenger passenger) {
        double distanceFee = passenger.getYDistance() * Double.parseDouble(GAME_PROPS.getProperty("trip.rate.perY"));
        double priorityFee = Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority" + passenger.getPriority()));
        return distanceFee + priorityFee;
    }

    /*
    method to calculate trip earnings as per spec
     */
    private double calculateTripEarnings(Passenger passenger, double dropOffDistance) {
        double expectedEarnings = calculateExpectedEarnings(passenger);
        double penalty = calculateTripPenalty(passenger, dropOffDistance);
        return Math.max(0, expectedEarnings - penalty);
    }

    /*
    method to write score to scoresFile
     */
    private void writeScore() {
        String scoreEntry = playerName + "," + String.format("%.2f", totalScore);
        IOUtils.writeScoreToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"), scoreEntry);
    }

    /*
    method to access scoresFile and update with current score if it makes top 5
     */
    private String[] getTopScores() {
        String[][] allScores = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gameEnd.scoresFile"));
        String[] topScores = new String[MAX_TOP_SCORES];
        double[] topScoreValues = new double[MAX_TOP_SCORES];

        for (String[] score : allScores) {
            double currentScore = Double.parseDouble(score[1]);
            for (int i = 0; i < MAX_TOP_SCORES; i++) {
                if (topScores[i] == null || currentScore > topScoreValues[i]) {
                    //shift lower scores down
                    for (int j = MAX_TOP_SCORES - 1; j > i; j--) {
                        topScores[j] = topScores[j-1];
                        topScoreValues[j] = topScoreValues[j-1];
                    }
                    //insert new higher score
                    topScores[i] = score[0] + " - " + score[1];
                    topScoreValues[i] = currentScore;
                    break;
                }
            }
        }
        return topScores;
    }

    /*
    method to reset game state variables and objects for replay
     */
    private void resetGame() {
        //Check to see if you need to add any other resets
        //reset game state variables
        currentFrame = 0;
        totalScore = 0;
        isWin = false;
        playerName = "";
        currentState = GameState.HOME;

        //reset passengers, coins, and trip end flags
        readGameObjects();

        //reset current passenger
        currentPassenger = null;

        //reset coin power
        coinPowerFrames = 0;
        coinPowerisActive = false;

        //reset last trip info
        lastTripExpectedEarnings = 0;
        lastTripActualEarnings = 0;
        lastTripPenalty = 0;
        lastTripPriority = 0;

        backgroundA = Window.getHeight() / 2.0;
        backgroundB = -Window.getHeight() / 2.0;
    }

    /**
     * Render the relevant screens and game objects based on the keyboard input
     * given by the user and the status of the game play.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }
        /*
        Use switch statement to control/progress through different game states
         */
        switch (currentState) {
            case HOME:
                BACKGROUND_IMAGE.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

                //get real centre for title after factoring in font width
                String title = MESSAGE_PROPS.getProperty("home.title");
                double titleWidth = FONT_TITLE.getWidth(title);
                double titleX = (WINDOW_WIDTH - titleWidth) / 2;
                double titleY = Double.parseDouble(GAME_PROPS.getProperty("home.title.y"));
                FONT_TITLE.drawString(title, titleX, titleY);

                //get real centre for instruction after factoring in font width
                String instruction = MESSAGE_PROPS.getProperty("home.instruction");
                double instructionWidth = FONT_INSTRUCTION.getWidth(instruction);
                double instructionX = (WINDOW_WIDTH - instructionWidth) / 2;
                double instructionY = Double.parseDouble(GAME_PROPS.getProperty("home.instruction.y"));
                FONT_INSTRUCTION.drawString(instruction, instructionX, instructionY);

                if (input.wasPressed(Keys.ENTER)) {
                    currentState = GameState.PLAYER_INFO;
                }
                break;
            case PLAYER_INFO:
                PLAYERINFO_IMAGE.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

                //draw the Enter Name message to the screen
                String enterName = MESSAGE_PROPS.getProperty("playerInfo.playerName");
                double enterNameWidth = FONT_PI.getWidth(enterName);
                double enterNameX = (WINDOW_WIDTH - enterNameWidth) / 2;
                double enterNameY = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerName.y"));
                FONT_PI.drawString(enterName, enterNameX, enterNameY);

                //calculate the center position for the player's name, so it always stays centered
                double nameInputY = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerNameInput.y"));
                double nameWidth = FONT_PI.getWidth(playerName);
                double nameInputX = (WINDOW_WIDTH - nameWidth) / 2;

                //draw the player's name centered and in black
                DrawOptions blackText = new DrawOptions().setBlendColour(Colour.BLACK);
                FONT_PI.drawString(playerName, nameInputX, nameInputY, blackText);

                //handle player name input and backspace features
                if (input.wasPressed(Keys.BACKSPACE) && !playerName.isEmpty()) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                } else {
                    String key = MiscUtils.getKeyPress(input);
                    if (key != null) {
                        playerName += key;
                    }
                }

                //draw start instructions
                String startInstructions = MESSAGE_PROPS.getProperty("playerInfo.start");
                String[] lines = startInstructions.split("\n");
                double startY = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.start.y"));
                for (int i = 0; i < lines.length; i++) {
                    double lineWidth = FONT_PI.getWidth(lines[i]);
                    double lineX = (WINDOW_WIDTH - lineWidth) / 2;
                    FONT_PI.drawString(lines[i], lineX, startY + i * LINE_SPACING_PI);
                }

                //transition to GAME_PLAY state
                if (input.wasPressed(Keys.ENTER) && !playerName.isEmpty()) {
                    currentState = GameState.GAME_PLAY;
                }
                break;
            case GAME_PLAY:
                //update game screen objects
                currentFrame++;
                taxi.move(input);

                boolean moveDown = input.isDown(Keys.UP);

                //move background
                if (moveDown) {
                    backgroundA += VERTICAL_SCROLL_SPEED;
                    backgroundB += VERTICAL_SCROLL_SPEED;

                    if (backgroundA >= WINDOW_HEIGHT * BACKGROUND_RESET_FACTOR) {
                        backgroundA = backgroundB - WINDOW_HEIGHT;
                    }
                    if (backgroundB >= WINDOW_HEIGHT * BACKGROUND_RESET_FACTOR) {
                        backgroundB = backgroundA - WINDOW_HEIGHT;
                    }
                }

                //move game objects
                for (Passenger passenger : passengers) {
                    passenger.move(moveDown);
                }
                for (int i = 0; i < coins.length; i++) {
                    if (activeCoins[i]) {
                        coins[i].move(moveDown);
                    }
                }
                for (TripEndFlag flag : tripEndFlags) {
                    flag.move(moveDown);
                }

                //draw game objects
                GAMEPLAY_IMAGE.draw(WINDOW_WIDTH / 2.0, backgroundA);
                GAMEPLAY_IMAGE.draw(WINDOW_WIDTH / 2.0, backgroundB);
                taxi.draw();

                for (Passenger passenger : passengers) {
                    passenger.updatePosition();
                    passenger.draw();

                }
                for (int i = 0; i < coins.length; i++) {
                    if (activeCoins[i]) {
                        coins[i].draw();
                    }
                }
                for (TripEndFlag flag : tripEndFlags) {
                    flag.draw();
                }

                //handle coin collisions
                handleCoinCollision();

                //complete passenger pickup and drop-off
                handlePassengerDropOff();
                handlePassengerPickup();

                //update and draw current and last trip info text to screen
                updateTripInfo();

                //draw coinPowerFrames if coin collision
                if (coinPowerisActive) {
                    FONT_GAMEPLAY.drawString(String.valueOf(coinPowerFrames),
                            Double.parseDouble(GAME_PROPS.getProperty("gameplay.coin.x")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameplay.coin.y")));
                }

                //draw game info text
                String payText = MESSAGE_PROPS.getProperty("gamePlay.earnings") + String.format("%.2f", totalScore);
                FONT_GAMEPLAY.drawString(payText,
                        Double.parseDouble(GAME_PROPS.getProperty("gameplay.earnings.x")),
                        Double.parseDouble(GAME_PROPS.getProperty("gameplay.earnings.y")));

                String targetText = MESSAGE_PROPS.getProperty("gamePlay.target") +
                        GAME_PROPS.getProperty("gamePlay.target");
                FONT_GAMEPLAY.drawString(targetText,
                        Double.parseDouble(GAME_PROPS.getProperty("gameplay.target.x")),
                        Double.parseDouble(GAME_PROPS.getProperty("gameplay.target.y")));

                String framesText = MESSAGE_PROPS.getProperty("gamePlay.remFrames") +
                        (MAX_FRAMES - currentFrame);
                FONT_GAMEPLAY.drawString(framesText,
                        Double.parseDouble(GAME_PROPS.getProperty("gameplay.maxFrames.x")),
                        Double.parseDouble(GAME_PROPS.getProperty("gameplay.maxFrames.y")));


                //check for game loss/win condition and write score to csv
                double targetScore = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.target"));
                if (totalScore >= targetScore) {
                    isWin = true;
                    writeScore();
                    currentState = GameState.GAME_END;
                } else if (currentFrame >= MAX_FRAMES) {
                    isWin = false;
                    writeScore();
                    currentState = GameState.GAME_END;
                }
                break;

            case GAME_END:
                GAME_END_IMAGE.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

                //draw win/loss message
                String endMessage;
                if (isWin) {
                    endMessage = MESSAGE_PROPS.getProperty("gameEnd.won");
                } else {
                    endMessage = MESSAGE_PROPS.getProperty("gameEnd.lost");
                }
                String[] endMessageLines = endMessage.split("\n");
                double messageY = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.status.y"));
                for (int i = 0; i < endMessageLines.length; i++) {
                    double lineWidth = FONT_END_STATUS.getWidth(endMessageLines[i]);
                    double lineX = (WINDOW_WIDTH - lineWidth) / 2;
                    FONT_END_STATUS.drawString(endMessageLines[i], lineX, messageY + i * LINE_SPACING_END_MESSAGE);
                }

                //draw top scores
                String[] topScores = getTopScores();
                double scoresY_GE = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.scores.y"));
                String topScoresTitle = MESSAGE_PROPS.getProperty("gameEnd.highestScores");
                double titleWidth_GE = FONT_END_SCORES.getWidth(topScoresTitle);
                double titleX_GE = (WINDOW_WIDTH - titleWidth_GE) / 2;
                FONT_END_SCORES.drawString(topScoresTitle, titleX_GE, scoresY_GE);

                for (int i = 0; i < topScores.length; i++) {
                    if (topScores[i] != null) {
                        double scoreWidth = FONT_END_SCORES.getWidth(topScores[i]);
                        double scoreX = (WINDOW_WIDTH - scoreWidth) / 2;
                        FONT_END_SCORES.drawString(topScores[i], scoreX, scoresY_GE + (i + 1) *
                                LINE_SPACING_TOP_SCORES);
                    }
                }

                //reset game if space is pressed
                if (input.wasPressed(Keys.SPACE)) {
                    currentState = GameState.HOME;
                    resetGame();
                }
                break;
        }
    }

    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}