import java.util.ArrayList;
import java.util.List;
import MikowskiUtility.Edge;

public class FrameConstraint 
{
	
	protected int frameWidth, frameHeight; 
	protected List<List<Edge>> checkingNFPolygon;
	protected List<List<Double>> interestNFP;
	protected boolean NFPnotNull = false;
	
	public FrameConstraint(int inputFrameWidth, int inputFrameHeight, List<List<Edge>> inputRotatedDemo[], int indexInterest) 
	{
		frameHeight = inputFrameHeight;
		frameWidth = inputFrameWidth;
		checkingNFPolygon = inputRotatedDemo[indexInterest];
		NFPnotNull = (checkingNFPolygon != null);
		interestNFP = new ArrayList<>();
	}
	
	public boolean IsInFrame ()
	{
		boolean isInFrame = false;
		if (NFPnotNull) {
			MoveNFPToOrigin(checkingNFPolygon);
			isInFrame = CheckNFPIsInFrame();
		}
		return isInFrame;
	}
	
	public void MoveNFPToOrigin(List<List<Edge>> inputNFP)
	{
		List<List<Edge>> movedNFP = inputNFP;
		int sizeToCheck = (int)Double.NEGATIVE_INFINITY;
		int indexToCheck = 0;
		
		if (NFPnotNull) {
			for (int i=0; i<movedNFP.size(); i++) {
				if (movedNFP.get(i).size() > sizeToCheck) {
					sizeToCheck = movedNFP.get(i).size();
					indexToCheck = i;
				}
			}
		}
		
		double[] xyMinimum = new double[2];
		double XCoordinate = movedNFP.get(indexToCheck).get(0).getStartPoint().getxCoord();
		double YCoordinate = movedNFP.get(indexToCheck).get(0).getStartPoint().getyCoord();
		
		xyMinimum[0] = XCoordinate; xyMinimum[1] = YCoordinate;
		
		for (int i=0; i<movedNFP.get(indexToCheck).size(); i++) {
			XCoordinate = movedNFP.get(indexToCheck).get(i).getEndPoint().getxCoord();
			YCoordinate = movedNFP.get(indexToCheck).get(i).getEndPoint().getyCoord();
			
			if (XCoordinate < xyMinimum[0]) 
				xyMinimum[0] = XCoordinate;
			if (YCoordinate < xyMinimum[1])
				xyMinimum[1] = YCoordinate;
		}
		
		for (Edge e: movedNFP.get(indexToCheck)) {
			List<Double> tmpForGetCoordinate = new ArrayList<>();
			tmpForGetCoordinate.add(e.getStartPoint().getxCoord() + (-1 * xyMinimum[0]));
			tmpForGetCoordinate.add(e.getStartPoint().getyCoord() + (-1 * xyMinimum[1]));
			interestNFP.add(tmpForGetCoordinate);
		}
		
	}
	
	public boolean CheckNFPIsInFrame ()
	{
		double[] minMaxHeight = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
		double[] minMaxWidth = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
		double minHeight, minWidth;
		
		for (List<Double> x: interestNFP) {
			if (x.get(1) < minMaxHeight[0])
				minMaxHeight[0] = x.get(1);
			if (x.get(1) > minMaxHeight[1])
				minMaxHeight[1] = x.get(1);
			if (x.get(0) < minMaxWidth[0])
				minMaxWidth[0] = x.get(0);
			if (x.get(0) > minMaxWidth[1])
				minMaxWidth[1] = x.get(0);
		}
		minHeight = minMaxHeight[1] - minMaxHeight[0];
		minWidth = minMaxWidth[1] - minMaxWidth[0];
		return (minHeight <= frameHeight && minWidth <= frameWidth);
	}
}
