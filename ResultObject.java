import java.util.ArrayList;
import java.util.List;

public class ResultObject {
	
	public FitnessSequence bestFitnessValue;
	public List<List<SimplePolygon>> initialPosition;
	public List<Double> fitnessValue;
	public List<Double> generationTime;
	public FitnessSequence[][] allPosition;
	public FitnessSequence[] X;
	
	public int[][] degreeCodeForBestFitness;
	public int[] bestDegreeCode;
	private int sequenceSize, genSize;
	
	public ResultObject(int sizeOfSequence, int sizeOfGenerations) {
		X = null;
		sequenceSize = sizeOfSequence;
		genSize = sizeOfGenerations;
		degreeCodeForBestFitness = new int[genSize][sequenceSize];
		bestDegreeCode = new int[sequenceSize];
		allPosition = null;
		bestFitnessValue = null;
		initialPosition = new ArrayList<>();
		fitnessValue = new ArrayList<>();
		generationTime = new ArrayList<>();
	}
	
	public void showBestFitnessValue() 
	{
		System.out.println("Best fitness value:");
		System.out.println("Value="+bestFitnessValue.fitnessValue); 
		
		System.out.print("Position(");
		for (int seqElement: bestFitnessValue.sequenceNumber) 
		{
			System.out.print(seqElement + " ");
		}
		System.out.println(")");
		System.out.print("Rotation(");
		for (int seqElement: degreeCodeForBestFitness[genSize-1]) 
		{
			switch(seqElement) 
			{
				case 1:
					System.out.print("90 ");
					break;
				case 2:
					System.out.print("180 ");
					break;
				case 3:
					System.out.print("270 ");
					break;
				default:
					System.out.print("0 ");
					break;
			}
		}
		System.out.println(")");
	}
	
	public void showGenerationTime() 
	{
		for (int i=0; i<generationTime.size(); i++) 
		{
			System.out.println("Generation Time #" + (i+1) + ": " + generationTime.get(i));
		}
	}
	
	public void showAllFitnessValue() 
	{
		for (int i=0; i<fitnessValue.size(); i++) 
		{
			System.out.println("Fitness value #" + (i+1) + ": " + fitnessValue.get(i));
		}
	}
}
