/**
 * Represents a weather condition in the game with a specific type and duration.
 */
public class WeatherCondition {
    private WeatherType type;
    private int startFrame;
    private int endFrame;

    public enum WeatherType { SUNNY, RAINING }

    /**
     * Constructs a new WeatherCondition.
     *
     * @param type The type of weather condition
     * @param startFrame The frame at which this weather condition starts
     * @param endFrame The frame at which this weather condition ends
     */
    public WeatherCondition(WeatherType type, int startFrame, int endFrame) {
        this.type = type;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    //getters and setters
    public WeatherType getType() {
        return type;
    }

    public boolean isActive(int currentFrame) {
        return currentFrame >= startFrame && currentFrame <= endFrame;
    }
}