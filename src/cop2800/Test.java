package cop2800;

public class Test {
	
	public static void StartGame(TeamAthlete athlete) {
		
        System.out.println(athlete.toString());
        athlete.Play();
    }

    public static void main(String[] args) {
        BaseballPlayer baseballPlayer1 = new BaseballPlayer("Barbie", 25, "Pink Sox", "Pitcher", "Right-Handed", 300);
        BaseballPlayer baseballPlayer2 = new BaseballPlayer("007", 46, "ScientoloGiants", "Shortstop", "Left-Handed", 280);

        FootballPlayer footballPlayer1 = new FootballPlayer("Ken", 25, "Yankens", "Tight End", "Offense", 6);
        FootballPlayer footballPlayer2 = new FootballPlayer("Oppenheimer", 40, "Patriots", "Quarterback", "Defense", 1);

        StartGame(baseballPlayer1);
        StartGame(baseballPlayer2);
        StartGame(footballPlayer1);
        StartGame(footballPlayer2);
    }

}