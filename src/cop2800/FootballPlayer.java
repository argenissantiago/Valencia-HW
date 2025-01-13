package cop2800;

public class FootballPlayer extends TeamAthlete {
	
	private String specialty;
    private int superBowlRings;

    public FootballPlayer(String name, int age, String team, String position, String specialty, int superBowlRings) {
        super(name, age, team, position);
        this.specialty = specialty;
        this.superBowlRings = superBowlRings;
    }

    public String toString() {
        return super.toString() + ", Specialty: " + specialty + ", Super Bowl Rings: " + superBowlRings;
    }
    
    public void Play() {
        System.out.println("Ran 10 yards");
    }

}