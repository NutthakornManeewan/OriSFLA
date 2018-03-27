import MikowskiUtility.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * 		--- Class definition ---
 * 			[-] This class 'Rotate' polygon into 4 degree angles (0, 90, 180, 270).
 * 			[-] This class create text file of 'Rotated polygon' in variable -> "rotatedFilePath".
 * 			[-] This class create NFP each of rotated polygon.
 * 
 */

public class MikowSkiUtility {
	
	protected int DEGREE_ROTATE_NUMBER = 4;
	protected int[] sequencePolygon;
	protected List<SimplePolygon> rawPolygon;
	protected SimplePolygon[] leapRotatedPolygon;
	protected JNFP minkowObject;
	protected String rotatedFilePath;
	
	protected List<List<Edge>>[] outputRotatedDemo;
	
	public MikowSkiUtility(String inputFilePath) 
	{
		minkowObject = new JNFP();
		leapRotatedPolygon = new SimplePolygon[DEGREE_ROTATE_NUMBER];
		rotatedFilePath = inputFilePath;
	}
	
	public List<List<Edge>>[] getOutputRotatedPolygon() 
	{
		return outputRotatedDemo.clone();
	}
	
	public void CreateRotatedPolygon (SimplePolygon simplePolygonA, SimplePolygon simplePolygonB) 
	{
		outputRotatedDemo = new List[DEGREE_ROTATE_NUMBER];
		float degreeChangeForCalculate = 0.0f;
		for (int rotateIdx=0; rotateIdx<DEGREE_ROTATE_NUMBER; rotateIdx++) 
		{
			SimplePolygon staticPolygon = simplePolygonB;
    		leapRotatedPolygon[rotateIdx] = RotateCoordinate(staticPolygon, degreeChangeForCalculate);
    		degreeChangeForCalculate += 90.0;
			
    		try 
    		{
				GenerateTemporaryRotateFile(leapRotatedPolygon[rotateIdx].xCoordinateList, leapRotatedPolygon[rotateIdx].yCoordinateList, rotatedFilePath + Integer.toString(rotateIdx) + ".txt");
				outputRotatedDemo[rotateIdx] = minkowObject.GenerateMinkowskiNFP(simplePolygonA.directoryPolygon, new File(rotatedFilePath + Integer.toString(rotateIdx) + ".txt"));
    		} catch (IOException e) {
				System.err.println("Error in line 55 - MikowSkiUtility.java class!");
			}
		}
	}
	
	protected SimplePolygon RotateCoordinate(SimplePolygon staticPolygons, float degree) 
	{
		/* Rotation matrices
		 * R = [ cos(x) -sin(x) ]
		 *     [ sin(x)  cos(x) ] */
		 
		List<Double> xCoordinateList = new ArrayList<>();
		List<Double> yCoordinateList = new ArrayList<>();
		int coordinateSize = staticPolygons.numberOfCoordinate;
		
		for (int cIdx=0; cIdx < coordinateSize; cIdx++) 
		{
			double tmpX = staticPolygons.getXCoordinate(cIdx)*Math.cos(Math.toRadians(degree)) - staticPolygons.getYCoordinate(cIdx)*Math.sin(Math.toRadians(degree));
			double tmpY = staticPolygons.getXCoordinate(cIdx)*Math.sin(Math.toRadians(degree)) + staticPolygons.getYCoordinate(cIdx)*Math.cos(Math.toRadians(degree));
			xCoordinateList.add(tmpX);
			yCoordinateList.add(tmpY);
		}
		staticPolygons.xCoordinateList.clear(); 
		staticPolygons.yCoordinateList.clear();
		staticPolygons.xCoordinateList.addAll(xCoordinateList);
		staticPolygons.yCoordinateList.addAll(yCoordinateList);
		
		return staticPolygons;
	}
	
	protected void GenerateTemporaryRotateFile (List<Double> XCoordinate,List<Double> YCoordinate, String file_path) throws IOException 
	{
		BufferedWriter output = null;
		String writeText = "";
		File temporaryFilePolygon = new File(file_path);
		
		writeText += "0\n" + XCoordinate.size() + "\n";
		
		for (int j=0; j<XCoordinate.size(); j++) 
		{
			double Xtmp = XCoordinate.get(j);
			double Ytmp = YCoordinate.get(j);
			writeText += (Double.toString(Xtmp)) + " " + (Double.toString(Ytmp)) + "\n";
		}
		
		output = new BufferedWriter(new FileWriter(temporaryFilePolygon, false));
		output.write(writeText);
		if ( output != null ) { output.close(); }
	}
}
