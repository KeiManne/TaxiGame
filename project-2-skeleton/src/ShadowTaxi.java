import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 2, 2024
 * @author Keith Menezes
 */
public class ShadowTaxi extends AbstractGame {

    //constants
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    private static final double PASSENGER_PICKUP_DISTANCE = 100.0;
    private static final double PASSENGER_ENTER_DISTANCE = 5.0;

    //properties
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    //game state variables
    private GameState currentState;
    private String playerName;
    private int currentFrame;
    private double totalScore;
    private boolean isWin;
    private String lastTripInfo;


    //images
    private final Image BACKGROUND_IMAGE_SUNNY;
    private final Image BACKGROUND_IMAGE_RAINING;
    private final Image BACKGROUND_PLAYER_INFO;
    private final Image BACKGROUND_HOME;
    private final Image BACKGROUND_GAME_END;

    //fonts
    private final Font FONT_TITLE;
    private final Font FONT_INSTRUCTION;
    private final Font FONT_PLAYER_INFO;
    private final Font FONT_GAMEPLAY;
    private final Font FONT_END_STATUS;
    private final Font FONT_END_SCORES;

    //game objects
    private Taxi taxi;
    private Driver driver;
    private List<Passenger> passengers;
    private List<Car> cars;
    private List<EnemyCar> enemyCars;
    private List<Coin> coins;
    private List<InvinciblePower> invinciblePowers;
    private List<TripEndFlag> tripEndFlags;
    private List<Fireball> fireballs;

    //weather
    private List<WeatherCondition> weatherConditions;
    private WeatherCondition.WeatherType currentWeather;

    //background scrolling
    private double backgroundY1;
    private double backgroundY2;


    //enum for game states
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

        //images
        BACKGROUND_IMAGE_SUNNY = new Image(GAME_PROPS.getProperty("backgroundImage.sunny"));
        BACKGROUND_IMAGE_RAINING = new Image(GAME_PROPS.getProperty("backgroundImage.raining"));
        BACKGROUND_PLAYER_INFO = new Image(GAME_PROPS.getProperty("backgroundImage.playerInfo"));
        BACKGROUND_HOME = new Image(GAME_PROPS.getProperty("backgroundImage.home"));
        BACKGROUND_GAME_END = new Image(GAME_PROPS.getProperty("backgroundImage.gameEnd"));

        //fonts
        FONT_TITLE = new Font(GAME_PROPS.getProperty("font"), Integer.parseInt(GAME_PROPS.getProperty("home.title.fontSize")));
        FONT_INSTRUCTION = new Font(GAME_PROPS.getProperty("font"), Integer.parseInt(GAME_PROPS.getProperty("home.instruction.fontSize")));
        FONT_PLAYER_INFO = new Font(GAME_PROPS.getProperty("font"), Integer.parseInt(GAME_PROPS.getProperty("playerInfo.fontSize")));
        FONT_GAMEPLAY = new Font(GAME_PROPS.getProperty("font"), Integer.parseInt(GAME_PROPS.getProperty("gamePlay.info.fontSize")));
        FONT_END_STATUS = new Font(GAME_PROPS.getProperty("font"), Integer.parseInt(GAME_PROPS.getProperty("gameEnd.status.fontSize")));
        FONT_END_SCORES = new Font(GAME_PROPS.getProperty("font"), Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.fontSize")));

        //game state
        currentState = GameState.HOME;
        resetGame();
    }

    /*
    method to reset game variables when continue is chosen on gameEnd screen
     */
    private void resetGame() {
        playerName = "";
        currentFrame = 0;
        totalScore = 0;
        isWin = false;

        backgroundY1 = WINDOW_HEIGHT / 2.0;
        backgroundY2 = -WINDOW_HEIGHT / 2.0;

        //initialize game objects
        readGameObjects();
        readWeatherConditions();

        currentWeather = WeatherCondition.WeatherType.SUNNY;
    }

    /*
    method to read in game objects
     */
    private void readGameObjects() {
        String[][] objects = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gamePlay.objectsFile"));

        passengers = new ArrayList<>();
        cars = new ArrayList<>();
        enemyCars = new ArrayList<>();
        coins = new ArrayList<>();
        invinciblePowers = new ArrayList<>();
        tripEndFlags = new ArrayList<>();
        fireballs = new ArrayList<>();

        for (String[] object : objects) {
            switch (object[0]) {
                case "TAXI":
                    taxi = new Taxi(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            GAME_PROPS.getProperty("gameObjects.taxi.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.radius")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedX")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedY")));
                    break;
                case "DRIVER":
                    driver = new Driver(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            GAME_PROPS.getProperty("gameObjects.driver.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.driver.radius")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.driver.walkSpeedX")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.driver.walkSpeedY")));
                    break;
                case "PASSENGER":
                    Passenger passenger = new Passenger(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            Integer.parseInt(object[3]), Double.parseDouble(object[4]), Double.parseDouble(object[5]),
                            GAME_PROPS.getProperty("gameObjects.passenger.image"),
                            GAME_PROPS.getProperty("font"),
                            Integer.parseInt(GAME_PROPS.getProperty("gameObjects.passenger.fontSize")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.radius")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.perY")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority1")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority2")),
                            Double.parseDouble(GAME_PROPS.getProperty("trip.rate.priority3")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.walkSpeedX")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.walkSpeedY")),
                            object[6].equals("1"));
                    passengers.add(passenger);

                    TripEndFlag flag = new TripEndFlag(Double.parseDouble(object[4]),
                            Double.parseDouble(object[2]) - Double.parseDouble(object[5]),
                            GAME_PROPS.getProperty("gameObjects.tripEndFlag.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.tripEndFlag.radius")));
                    tripEndFlags.add(flag);
                    break;
                case "COIN":
                    coins.add(new Coin(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            GAME_PROPS.getProperty("gameObjects.coin.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.coin.radius"))));
                    break;
                case "INVINCIBLE_POWER":
                    invinciblePowers.add(new InvinciblePower(Double.parseDouble(object[1]), Double.parseDouble(object[2]),
                            GAME_PROPS.getProperty("gameObjects.invinciblePower.image"),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.invinciblePower.radius")),
                            Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedY"))));
                    break;
            }
        }
    }

    /*
    method to read in weather conditions
     */
    private void readWeatherConditions() {
        String[][] weatherData = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gamePlay.weatherFile"));
        weatherConditions = new ArrayList<>();

        for (String[] condition : weatherData) {
            WeatherCondition.WeatherType type = WeatherCondition.WeatherType.valueOf(condition[0]);
            int startFrame = Integer.parseInt(condition[1]);
            int endFrame = Integer.parseInt(condition[2]);
            weatherConditions.add(new WeatherCondition(type, startFrame, endFrame));
        }
    }

    /**
     * Render the relevant screens and game objects based on the keyboard input
     * given by the user and the status of the game play.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        //switch statement to manage game state - taken from project 1
        switch (currentState) {
            case HOME:
                updateHomeScreen(input);
                break;
            case PLAYER_INFO:
                updatePlayerInfoScreen(input);
                break;
            case GAME_PLAY:
                updateGamePlayScreen(input);
                break;
            case GAME_END:
                updateGameEndScreen(input);
                break;
        }
    }

    private void updateHomeScreen(Input input) {
        BACKGROUND_HOME.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

        String title = MESSAGE_PROPS.getProperty("home.title");
        double titleWidth = FONT_TITLE.getWidth(title);
        double titleX = (WINDOW_WIDTH - titleWidth) / 2;
        double titleY = Double.parseDouble(GAME_PROPS.getProperty("home.title.y"));
        FONT_TITLE.drawString(title, titleX, titleY);

        String instruction = MESSAGE_PROPS.getProperty("home.instruction");
        double instructionWidth = FONT_INSTRUCTION.getWidth(instruction);
        double instructionX = (WINDOW_WIDTH - instructionWidth) / 2;
        double instructionY = Double.parseDouble(GAME_PROPS.getProperty("home.instruction.y"));
        FONT_INSTRUCTION.drawString(instruction, instructionX, instructionY);

        if (input.wasPressed(Keys.ENTER)) {
            currentState = GameState.PLAYER_INFO;
        }
    }

    private void updatePlayerInfoScreen(Input input) {
        BACKGROUND_PLAYER_INFO.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

        String enterName = MESSAGE_PROPS.getProperty("playerInfo.playerName");
        double enterNameWidth = FONT_PLAYER_INFO.getWidth(enterName);
        double enterNameX = (WINDOW_WIDTH - enterNameWidth) / 2;
        double enterNameY = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerName.y"));
        FONT_PLAYER_INFO.drawString(enterName, enterNameX, enterNameY);

        double nameInputY = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.playerNameInput.y"));
        double nameWidth = FONT_PLAYER_INFO.getWidth(playerName);
        double nameInputX = (WINDOW_WIDTH - nameWidth) / 2;

        DrawOptions blackText = new DrawOptions().setBlendColour(Colour.BLACK);
        FONT_PLAYER_INFO.drawString(playerName, nameInputX, nameInputY, blackText);

        if (input.wasPressed(Keys.BACKSPACE) && !playerName.isEmpty()) {
            playerName = playerName.substring(0, playerName.length() - 1);
        } else {
            String key = MiscUtils.getKeyPress(input);
            if (key != null) {
                playerName += key;
            }
        }

        String startInstructions = MESSAGE_PROPS.getProperty("playerInfo.start");
        String[] lines = startInstructions.split("\n");
        double startY = Double.parseDouble(GAME_PROPS.getProperty("playerInfo.start.y"));
        for (int i = 0; i < lines.length; i++) {
            double lineWidth = FONT_PLAYER_INFO.getWidth(lines[i]);
            double lineX = (WINDOW_WIDTH - lineWidth) / 2;
            FONT_PLAYER_INFO.drawString(lines[i], lineX, startY + i * 30);
        }

        if (input.wasPressed(Keys.ENTER) && !playerName.isEmpty()) {
            currentState = GameState.GAME_PLAY;
        }
    }

    private void updateGamePlayScreen(Input input) {
        currentFrame++;
        updateWeather();
        updateBackgrounds(input);
        updateGameObjects(input);
        handlePassengerPickup();
        drawGameObjects();
        drawGameInfo();
        checkGameEndConditions();
        handlePassengerDropOff();
    }

    private void updateWeather() {
        for (WeatherCondition condition : weatherConditions) {
            if (condition.isActive(currentFrame)) {
                currentWeather = condition.getType();
                break;
            }
        }
    }

    /*
    method to create background scrolling effect based on user input
     */
    private void updateBackgrounds(Input input) {
        if (input.isDown(Keys.UP)) {
            backgroundY1 += Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedY"));
            backgroundY2 += Double.parseDouble(GAME_PROPS.getProperty("gameObjects.taxi.speedY"));

            if (backgroundY1 >= WINDOW_HEIGHT * 1.5) {
                backgroundY1 = backgroundY2 - WINDOW_HEIGHT;
            }
            if (backgroundY2 >= WINDOW_HEIGHT * 1.5) {
                backgroundY2 = backgroundY1 - WINDOW_HEIGHT;
            }
        }

        Image currentBackground = (currentWeather == WeatherCondition.WeatherType.SUNNY) ?
                BACKGROUND_IMAGE_SUNNY : BACKGROUND_IMAGE_RAINING;
        currentBackground.draw(WINDOW_WIDTH / 2.0, backgroundY1);
        currentBackground.draw(WINDOW_WIDTH / 2.0, backgroundY2);
    }

    /*
    method to apply per-frame updates to game entities
     */
    private void updateGameObjects(Input input) {
        boolean moveDown = input.isDown(Keys.UP);

        taxi.move(input);
        driver.move(input);

        for (Passenger passenger : passengers) {
            passenger.update();
            if (!passenger.isPickedUp() && !passenger.isDroppedOff()) {
                passenger.moveVertically(moveDown);
            }
            passenger.updatePriority(currentWeather);
        }

        for (Car car : cars) {
            car.update();
            car.moveIndependently();
        }

        for (EnemyCar enemyCar : enemyCars) {
            enemyCar.update();
            enemyCar.moveIndependently();
            //1 in 300 chance to spawn fireball
            if (MiscUtils.canSpawn(300)) {
                fireballs.add(enemyCar.shootFireball());
            }
        }

        for (Coin coin : coins) {
            coin.moveVertically(moveDown);
        }

        for (InvinciblePower power : invinciblePowers) {
            power.moveVertically(moveDown);
        }

        for (TripEndFlag flag : tripEndFlags) {
            flag.moveVertically(moveDown);
        }

        for (Fireball fireball : fireballs) {
            fireball.moveIndependently();
        }

        //spawn new cars
        if (MiscUtils.canSpawn(200)) { //1 in 200 chance to spawn car
            spawnCar();
        }

        if (MiscUtils.canSpawn(400)) { //1 in 400 chance to spawn enemy car
            spawnEnemyCar();
        }

    }

    private void drawGameObjects() {
        taxi.draw();

        for (Passenger passenger : passengers) {
            passenger.draw();
        }

        for (Car car : cars) {
            car.draw();
        }

        for (EnemyCar enemyCar : enemyCars) {
            enemyCar.draw();
        }

        for (Coin coin : coins) {
            coin.draw();
        }

        for (InvinciblePower power : invinciblePowers) {
            power.draw();
        }

        for (TripEndFlag flag : tripEndFlags) {
            flag.draw();
        }

        for (Fireball fireball : fireballs) {
            fireball.draw();
        }
    }

    private void drawGameInfo() {
        String payText = MESSAGE_PROPS.getProperty("gamePlay.earnings") + String.format("%.2f", totalScore);
        FONT_GAMEPLAY.drawString(payText,
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.earnings.x")),
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.earnings.y")));

        String targetText = MESSAGE_PROPS.getProperty("gamePlay.target") +
                GAME_PROPS.getProperty("gamePlay.target");
        FONT_GAMEPLAY.drawString(targetText,
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.target.x")),
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.target.y")));

        String framesText = MESSAGE_PROPS.getProperty("gamePlay.remFrames") +
                (Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames")) - currentFrame);
        FONT_GAMEPLAY.drawString(framesText,
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.maxFrames.x")),
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.maxFrames.y")));

        String taxiHealthText = MESSAGE_PROPS.getProperty("gamePlay.taxiHealth") +
                String.format("%.0f", taxi.getHealth());
        FONT_GAMEPLAY.drawString(taxiHealthText,
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.taxiHealth.x")),
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.taxiHealth.y")));

        String driverHealthText = MESSAGE_PROPS.getProperty("gamePlay.driverHealth") +
                String.format("%.0f", driver.getHealth());
        FONT_GAMEPLAY.drawString(driverHealthText,
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.driverHealth.x")),
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.driverHealth.y")));

        String passengerHealthText = MESSAGE_PROPS.getProperty("gamePlay.passengerHealth") +
                String.format("%.0f", getMinPassengerHealth());
        FONT_GAMEPLAY.drawString(passengerHealthText,
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.passengerHealth.x")),
                Double.parseDouble(GAME_PROPS.getProperty("gamePlay.passengerHealth.y")));

        if (taxi.hasCoinPower()) {
            FONT_GAMEPLAY.drawString(String.valueOf(taxi.getCoinPowerFrames()),
                    Double.parseDouble(GAME_PROPS.getProperty("gameplay.coin.x")),
                    Double.parseDouble(GAME_PROPS.getProperty("gameplay.coin.y")));
        }

        drawTripInfo();
    }

    /*
    method to ensure to print accurate minimum passenger health
     */
    private double getMinPassengerHealth() {
        double minHealth = 100.0;
        for (Passenger passenger : passengers) {
            if (passenger.getHealth() < minHealth) {
                minHealth = passenger.getHealth();
            }
        }
        return minHealth;
    }

    private void drawTripInfo() {
        double tripInfoX = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.tripInfo.x"));
        double tripInfoY = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.tripInfo.y"));

        if (taxi.getCurrentPassenger() != null) {
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.onGoingTrip.title"),
                    tripInfoX, tripInfoY);
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning") +
                            String.format("%.1f", taxi.getCurrentPassenger().calculateExpectedEarnings()),
                    tripInfoX, tripInfoY + 30);
            FONT_GAMEPLAY.drawString(MESSAGE_PROPS.getProperty("gamePlay.trip.priority") +
                    taxi.getCurrentPassenger().getPriority(), tripInfoX, tripInfoY + 60);
        } else if (lastTripInfo != null) {
            String[] lines = lastTripInfo.split("\n");
            for (int i = 0; i < lines.length; i++) {
                FONT_GAMEPLAY.drawString(lines[i], tripInfoX, tripInfoY + i * 30);
            }
        }
    }

    private void spawnCar() {
        double first = MiscUtils.selectAValue(
                Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter1")),
                Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter3"))
        );
        double x = MiscUtils.selectAValue((int) first, Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter2")));
        double y = MiscUtils.selectAValue(-50, WINDOW_HEIGHT);

        Car newCar = new Car(x, y,
                String.format(GAME_PROPS.getProperty("gameObjects.otherCar.image"),
                        MiscUtils.getRandomInt(1, Integer.parseInt(GAME_PROPS.getProperty("gameObjects.otherCar.types")) + 1)),
                Double.parseDouble(GAME_PROPS.getProperty("gameObjects.otherCar.radius")));
        cars.add(newCar);
    }

    private void spawnEnemyCar() {
        double first = MiscUtils.selectAValue(
                Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter2")),
                Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter3"))
        );
        double x = MiscUtils.selectAValue((int) first, Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter1")));
        double y = MiscUtils.selectAValue(-50, WINDOW_HEIGHT);

        EnemyCar newEnemyCar = new EnemyCar(x, y,
                GAME_PROPS.getProperty("gameObjects.enemyCar.image"),
                Double.parseDouble(GAME_PROPS.getProperty("gameObjects.enemyCar.radius")));
        enemyCars.add(newEnemyCar);
    }

    private void handlePassengerPickup() {
        if (taxi.getCurrentPassenger() == null && !taxi.isMoving()) {
            for (Passenger passenger : passengers) {
                if (!passenger.isPickedUp() && !passenger.isDroppedOff()) {
                    double distance = taxi.getPosition().distanceTo(passenger.getPosition());
                    if (distance <= PASSENGER_PICKUP_DISTANCE) {
                        if (!passenger.isWalking()) {
                            passenger.setWalking(true);
                            passenger.setTargetPosition(taxi.getPosition());
                        } else if (distance <= PASSENGER_ENTER_DISTANCE) {
                            if (taxi.pickupPassenger(passenger)) {
                                passenger.setWalking(false);
                                setTripEndFlagVisible(passenger, true);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Passenger passenger : passengers) {
            if (passenger.isWalking() && !passenger.isPickedUp()) {
                passenger.moveTowards(taxi.getPosition());
            }
        }
    }

    private void handlePassengerDropOff() {
        Passenger currentPassenger = taxi.getCurrentPassenger();
        if (currentPassenger != null && currentPassenger.isPickedUp() && !taxi.isMoving()) {
            TripEndFlag flag = getTripEndFlagForPassenger(currentPassenger);
            if (flag != null && taxi.canDropOffPassenger(flag)) {
                currentPassenger = taxi.dropOffPassenger();
                currentPassenger.setPickedUp(false);
                currentPassenger.setWalking(true);
                currentPassenger.setTargetPosition(flag.getPosition());
            }
        }

        for (Passenger passenger : passengers) {
            if (passenger.isWalking() && !passenger.isPickedUp() && !passenger.isDroppedOff()) {
                TripEndFlag flag = getTripEndFlagForPassenger(passenger);
                if (flag != null) {
                    passenger.moveTowards(flag.getPosition());
                    if (passenger.getPosition().distanceTo(flag.getPosition()) < 1) {
                        passenger.setDroppedOff(true);
                        passenger.setWalking(false);
                        passenger.setPosition(flag.getPosition());
                        flag.setVisible(false);
                        completeTripAndUpdateInfo(passenger, flag);
                    }
                }
            }
        }
    }

    private void completeTripAndUpdateInfo(Passenger passenger, TripEndFlag flag) {
        double tripEarnings = calculateTripEarnings(passenger, flag);
        totalScore += tripEarnings;
        updateLastTripInfo(passenger, tripEarnings);
        flag.setVisible(false);
        System.out.println("Passenger dropped off, earnings: " + tripEarnings);
    }

    private TripEndFlag getTripEndFlagForPassenger(Passenger passenger) {
        int index = passengers.indexOf(passenger);
        return (index != -1 && index < tripEndFlags.size()) ? tripEndFlags.get(index) : null;
    }

    private double calculateTripEarnings(Passenger passenger, TripEndFlag flag) {
        double expectedEarnings = passenger.calculateExpectedEarnings();
        double penalty = calculateTripPenalty(passenger, flag);
        return Math.max(0, expectedEarnings - penalty);
    }

    private double calculateTripPenalty(Passenger passenger, TripEndFlag flag) {
        if (taxi.getPosition().y < flag.getPosition().y) {
            return (flag.getPosition().y - taxi.getPosition().y) *
                    Double.parseDouble(GAME_PROPS.getProperty("trip.penalty.perY"));
        }
        return 0;
    }

    private void updateLastTripInfo(Passenger passenger, double actualEarnings) {
        lastTripInfo = String.format("%s\n%s %.1f\n%s %d\n%s %.2f",
                MESSAGE_PROPS.getProperty("gamePlay.completedTrip.title"),
                MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning"),
                passenger.calculateExpectedEarnings(),
                MESSAGE_PROPS.getProperty("gamePlay.trip.priority"),
                passenger.getPriority(),
                MESSAGE_PROPS.getProperty("gamePlay.trip.penalty"),
                passenger.calculateExpectedEarnings() - actualEarnings);
    }

    private void setTripEndFlagVisible(Passenger passenger, boolean visible) {
        int passengerIndex = passengers.indexOf(passenger);
        if (passengerIndex != -1 && passengerIndex < tripEndFlags.size()) {
            tripEndFlags.get(passengerIndex).setVisible(visible);
            System.out.println("Trip end flag visibility set to: " + visible);
        }
    }

    /*
    method to check end game conditions
     */
    private void checkGameEndConditions() {
        double targetScore = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.target"));
        int maxFrames = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames"));

        if (totalScore >= targetScore) {
            isWin = true;
            endGame();
        } else if (currentFrame >= maxFrames || driver.getHealth() <= 0 || getMinPassengerHealth() <= 0) {
            isWin = false;
            endGame();
        }
    }

    private void endGame() {
        writeScore();
        currentState = GameState.GAME_END;
    }

    private void writeScore() {
        String scoreEntry = playerName + "," + String.format("%.2f", totalScore);
        IOUtils.writeScoreToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"), scoreEntry);
    }

    private void updateGameEndScreen(Input input) {
        BACKGROUND_GAME_END.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

        String endMessage = isWin ?
                MESSAGE_PROPS.getProperty("gameEnd.won") :
                MESSAGE_PROPS.getProperty("gameEnd.lost");
        String[] endMessageLines = endMessage.split("\n");
        double messageY = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.status.y"));
        for (int i = 0; i < endMessageLines.length; i++) {
            double lineWidth = FONT_END_STATUS.getWidth(endMessageLines[i]);
            double lineX = (WINDOW_WIDTH - lineWidth) / 2;
            FONT_END_STATUS.drawString(endMessageLines[i], lineX, messageY + i * 30);
        }

        String[] topScores = getTopScores();
        double scoresY = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.scores.y"));
        String topScoresTitle = MESSAGE_PROPS.getProperty("gameEnd.highestScores");
        double titleWidth = FONT_END_SCORES.getWidth(topScoresTitle);
        double titleX = (WINDOW_WIDTH - titleWidth) / 2;
        FONT_END_SCORES.drawString(topScoresTitle, titleX, scoresY);

        for (int i = 0; i < topScores.length; i++) {
            if (topScores[i] != null) {
                double scoreWidth = FONT_END_SCORES.getWidth(topScores[i]);
                double scoreX = (WINDOW_WIDTH - scoreWidth) / 2;
                FONT_END_SCORES.drawString(topScores[i], scoreX, scoresY + (i + 1) * 40);
            }
        }

        if (input.wasPressed(Keys.SPACE)) {
            resetGame();
            currentState = GameState.HOME;
        }
    }

    private String[] getTopScores() {
        String[][] scores = IOUtils.readCommaSeparatedFile(GAME_PROPS.getProperty("gameEnd.scoresFile"));
        List<ScoreEntry> scoreList = new ArrayList<>();

        for (String[] score : scores) {
            if (score.length == 2) {
                scoreList.add(new ScoreEntry(score[0], Double.parseDouble(score[1])));
            }
        }

        scoreList.sort((a, b) -> Double.compare(b.score, a.score));

        String[] topScores = new String[5];
        for (int i = 0; i < Math.min(5, scoreList.size()); i++) {
            ScoreEntry entry = scoreList.get(i);
            topScores[i] = String.format("%s - %.2f", entry.name, entry.score);
        }

        return topScores;
    }

    /*
    class to streamline score entry
     */
    private static class ScoreEntry {
        String name;
        double score;

        ScoreEntry(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }


    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}