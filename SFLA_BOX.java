import java.util.ArrayList;

public class SFLA_BOX {
	public int m=2,n=10;
	public double Goal = 1e-5;
	public int maxGen = 5;
	public int total_sample_size = 0;
	
	ArrayList<Integer> fitness_value = new ArrayList<Integer>();
	ArrayList<Double> generation_time = new ArrayList<Double>();
	
	// ***** Constructor *****
	SFLA_BOX(int usr_m, int usr_n) {
		m = usr_m;
		n = usr_n;
		total_sample_size = m*n;
	}
	
	SFLA_BOX(int usr_m, int usr_n, double usr_Goal, int usr_maxGen) {
		m      = usr_m;
		n      = usr_n;
		Goal   = usr_Goal;
		maxGen = usr_maxGen;
		total_sample_size = m*n;
	}
}
