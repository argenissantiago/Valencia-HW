package cop2800;

public class BaseballPlayer extends TeamAthlete {
	
	private String handedness;
    private int battingAverage;

    public BaseballPlayer(String name, int age, String team, String position, String handedness, int battingAverage) {
        super(name, age, team, position);
        this.handedness = handedness;
        this.battingAverage = battingAverage;
    }

    public String toString() {
        return super.toString() + ", Handedness: " + handedness + ", Batting Average: " + battingAverage;
    }

    public void Play() {
        System.out.println("Hit the ball");
    }

}