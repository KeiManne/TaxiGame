public class WeatherCondition {
    private WeatherType type;
    private int startFrame;
    private int endFrame;

    public enum WeatherType { SUNNY, RAINING }

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