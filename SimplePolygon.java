import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimplePolygon {
	public int numberOfCoordinate;
	public List<Double> xCoordinateList;
	public List<Double> yCoordinateList;
	public double fitnessValue;
	public int seqNumber;
	public File directoryPolygon;
	
	// ***** Constructor *****
	SimplePolygon (File inputPolygonFile) {
		xCoordinateList = new ArrayList<Double>();
		yCoordinateList = new ArrayList<Double>();
		fitnessValue = 0.0;
		seqNumber = 0;
		directoryPolygon = inputPolygonFile;
		ReadPolygonDataFromTextFile(inputPolygonFile);
	}
	
	// ***** Methods *****
	
	public double getXCoordinate(int seqNo) {
		return this.xCoordinateList.get(seqNo);
	}
	public double getYCoordinate(int seqNo) {
		return this.yCoordinateList.get(seqNo);
	}
	
	public void setFitnessValue(double fitness) {
		this.fitnessValue = fitness;
	}
	public void setSeqNumber(int k) {
		this.seqNumber = k;
	}
	
	public void ReadPolygonDataFromTextFile(File inputFile){
		int colCounter=0;
		Scanner scan;
	    try {
	    	scan = new Scanner(inputFile);
	    	scan.nextLine();
	    	this.numberOfCoordinate = scan.nextInt();
	    	
	        while(scan.hasNextDouble()) {
	        	if (colCounter == 0)
	        		xCoordinateList.add(scan.nextDouble());
		        else 
		        	yCoordinateList.add(scan.nextDouble());
		        colCounter = 1 - colCounter;
		    }
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    }
	}
}
