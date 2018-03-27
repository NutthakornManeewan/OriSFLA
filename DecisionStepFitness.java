
import java.util.List;

import MikowskiUtility.Coordinate;
import MikowskiUtility.Edge;
import MikowskiUtility.NoFitPolygon;

public class DecisionStepFitness {

	public int AMT_OF_FITNESS_VALUE;
	public double[] fitnessValue = new double[4];
	
	public List<List<Edge>>[] rotatedResult;
	
	public DecisionStepFitness(List<List<Edge>>[] outputRotatedDemo) {
		rotatedResult = outputRotatedDemo;
		CalculateFitness();
	}
	
	public void CalculateFitness() {
		
		List<List<Edge>> nfpActiveList1 = null;
		List<List<Edge>> nfpActiveList2 = null;
		List<List<Edge>> nfpActiveList3 = null;
		List<List<Edge>> nfpActiveList4 = null;
		
		if (rotatedResult[0] != null)
			nfpActiveList2 = rotatedResult[0];
		if (rotatedResult[1] != null)
			nfpActiveList3 = rotatedResult[1];
		if (rotatedResult[2] != null)
			nfpActiveList4 = rotatedResult[2];
		if (rotatedResult[3] != null)
			nfpActiveList1 = rotatedResult[3];
		
		fitnessValue[0] = SummationFormala(nfpActiveList1);
		fitnessValue[1] = SummationFormala(nfpActiveList2);
		fitnessValue[2] = SummationFormala(nfpActiveList3);
		fitnessValue[3] = SummationFormala(nfpActiveList4);
	}
	
	public double SummationFormala(List<List<Edge>> inputPolygon) {
		double maxHeight = Double.NEGATIVE_INFINITY, minHeight = Double.POSITIVE_INFINITY;
		double yCood=0, MINH=0;
		int indexToCheck=0, sizeToCheck=(int)Double.NEGATIVE_INFINITY;
		
		if (inputPolygon != null) {
			for (int i=0; i<inputPolygon.size(); i++) 
			{
				int tmpSize = inputPolygon.get(i).size();
				if (tmpSize > 2 && tmpSize >= sizeToCheck) {
					sizeToCheck  = tmpSize;
					indexToCheck = i;
				}
			}
			// ----- Find minimum height of all polygon. -----
			yCood = inputPolygon.get(indexToCheck).get(0).getStartPoint().getyCoord();
			if (yCood < minHeight) minHeight = yCood;
			if (yCood > maxHeight) maxHeight = yCood;
			
			for (int i=0; i<inputPolygon.get(indexToCheck).size(); i++) 
			{
				yCood = inputPolygon.get(indexToCheck).get(i).getEndPoint().getyCoord();
				if (yCood < minHeight ) { minHeight = yCood; }
				if (yCood > maxHeight) { maxHeight = yCood; }
			}
			MINH = maxHeight-minHeight;
		}
		if (MINH > 0) { return MINH; }
		else { return 99999; }
	}
}