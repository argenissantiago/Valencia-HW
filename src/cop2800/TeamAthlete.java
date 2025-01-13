package cop2800;

public class TeamAthlete extends Person {
	
	private String team;
    private String position;

    public TeamAthlete(String name, int age, String team, String position) {
        super(name, age);
        this.team = team;
        this.position = position;
    }

    public String toString() {
        return super.toString() + ", Team: " + team + ", Position: " + position;
    }

    public void Play() {
        System.out.println("Played their sport");
    }

}

