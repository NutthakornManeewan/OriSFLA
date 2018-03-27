package BottomLeftAlgorithms;

import java.util.List;
import MikowskiUtility.Edge;

public class Rect 
{
	protected double x,y,width,height;
	
	public Rect (List<List<Edge>> coordinate, List<double[]> coordinate2)
	{
		CreateBoundingBox(coordinate, coordinate2);
	}
	
	public Rect (double inX, double inY, double inWidth, double inHeight)
	{
		this.x = inX;
		this.y = inY;
		this.width = inWidth;
		this.height = inHeight;
	}
	
	public void CreateBoundingBox (List<List<Edge>> outputDemo, List<double[]> outputDemo2)
	{
		int sizeToCheck = (int)Double.NEGATIVE_INFINITY, indexToCheck=0;
		
		if (outputDemo != null && outputDemo2 == null) {
			for (int i=0; i<outputDemo.size(); i++) {
				if (outputDemo.get(i).size() >= sizeToCheck) {
					sizeToCheck  = outputDemo.get(i).size();
					indexToCheck = i;
				}
			}
			
			double XCoordinateMax = outputDemo.get(indexToCheck).get(0).getStartPoint().getxCoord();;
			double XCoordinateMin = outputDemo.get(indexToCheck).get(0).getStartPoint().getxCoord();;
			double YCoordinateMax = outputDemo.get(indexToCheck).get(0).getStartPoint().getyCoord();
			double YCoordinateMin = outputDemo.get(indexToCheck).get(0).getStartPoint().getyCoord();
			
			for (int j=0; j<sizeToCheck; j++) {
				
				double checkingY_PerRound = outputDemo.get(indexToCheck).get(j).getEndPoint().getyCoord();
				double checkingX_PerRound = outputDemo.get(indexToCheck).get(j).getEndPoint().getxCoord();
				
				if (checkingX_PerRound >= XCoordinateMax)
					XCoordinateMax = checkingX_PerRound;
				if (checkingX_PerRound <= XCoordinateMin)
					XCoordinateMin = checkingX_PerRound;
				
				if (checkingY_PerRound >= YCoordinateMax)
					YCoordinateMax = checkingY_PerRound;
				if (checkingY_PerRound <= YCoordinateMin)
					YCoordinateMin = checkingY_PerRound;
			}
			x = XCoordinateMin;
			y = YCoordinateMin;
			width = XCoordinateMax-x;
			height = YCoordinateMax-y;
		}
		else {
			
			double XCoordinateMax = outputDemo2.get(1)[0];
			double XCoordinateMin = outputDemo2.get(1)[0];
			double YCoordinateMax = outputDemo2.get(1)[1];
			double YCoordinateMin = outputDemo2.get(1)[1];
			
			for (int opdm_idx = 2; opdm_idx < outputDemo2.size(); opdm_idx++) {
				double checkingY_PerRound = outputDemo2.get(opdm_idx)[1];
				double checkingX_PerRound = outputDemo2.get(opdm_idx)[0];
					
				if (checkingX_PerRound >= XCoordinateMax)
					XCoordinateMax = checkingX_PerRound;
				if (checkingX_PerRound <= XCoordinateMin)
					XCoordinateMin = checkingX_PerRound;
				
				if (checkingY_PerRound >= YCoordinateMax)
					YCoordinateMax = checkingY_PerRound;
				if (checkingY_PerRound <= YCoordinateMin)
					YCoordinateMin = checkingY_PerRound;
			}
			x = XCoordinateMin;
			y = YCoordinateMin;
			width = XCoordinateMax-x;
			height = YCoordinateMax-y;
		}
	}
	
	// ##### Getter #####
	public double X() {
		return x;
	}
	public double Y() {
		return y;
	}
	public double Top() {
		return (y+height);
	}
	public double Right() {
		return (x+width);
	}
	public double Height() {
		return height;
	}
	public double Width() {
		return width;
	}
	
	// ###### Setter #####
	public void setX(double inputX) {
		this.x = inputX;
	}
	public void setY(double inputY) {
		this.y = inputY;
	}
}
