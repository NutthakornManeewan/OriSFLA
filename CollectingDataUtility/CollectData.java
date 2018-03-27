package CollectingDataUtility;

import MikowskiUtility.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CollectData 
{
	public static int testSeqCount = 0;
	public static int leapingSeqCount = 0;
	public String FILE_PATH = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\ALL_DATA\\Data";
	
	
	public CollectData()
	{
		
	}
	
	public void CollectTestSequenceData (List<List<Edge>> saveInput, int[] degreeCode, int[] seqList, double fitnessValue, String mode) 
	{
		String filePathTemp = "";
		if (mode.toLowerCase().equals("test"))
			filePathTemp = FILE_PATH + "_Fitnessval_" + (int)fitnessValue + "_Ts_" + "_";
		else
			filePathTemp = FILE_PATH + "_Fitnessval_" + (int)fitnessValue +"_Lp_" + "_";	
		
		for (int seqElement: seqList)
		{
			filePathTemp += seqElement;
		}
		filePathTemp += ".txt";
		
		try {
			GenerateTemporaryFile(saveInput, degreeCode, filePathTemp, seqList, fitnessValue);
		} catch (IOException e) {
			System.err.println("Error in collect test data process!");
		}
	}
	
	public void GenerateTemporaryFile(List<List<Edge>> outputDemo, int[] degreeCode, String file_path, int[] seqList, double fitnessValue) throws IOException 
	{
		int sizeToCheck = (int)Double.NEGATIVE_INFINITY, indexToCheck=0;
		BufferedWriter output = null;
		String writeText = "Sequence list: ";
		File temporaryFilePolygon = new File(file_path);
		
		for (int seqElement: seqList)
		{
			writeText += seqElement + " ";
		}
		writeText += "\nDegree code: ";
		for (int degreeElement: degreeCode)
		{
			writeText += degreeElement + " ";
		}
		writeText += "\nFitness value: " + fitnessValue + "\n---Track line trip list---\n";
		
		if (outputDemo != null) 
		{
			for (int i=0; i<outputDemo.size(); i++) 
			{
				if (outputDemo.get(i).size() >= sizeToCheck) 
				{
					sizeToCheck  = outputDemo.get(i).size();
					indexToCheck = i;
				}
			}
			
			double XCoordinate = outputDemo.get(indexToCheck).get(0).getStartPoint().getxCoord();
			double YCoordinate = outputDemo.get(indexToCheck).get(0).getStartPoint().getyCoord();
			writeText += ("0\n")+(sizeToCheck)+ "\n" + Math.round(XCoordinate) + " " + Math.round(YCoordinate) + "\n";
				
			for (int j=0; j<sizeToCheck; j++) 
			{
				XCoordinate = outputDemo.get(indexToCheck).get(j).getEndPoint().getxCoord();
				YCoordinate = outputDemo.get(indexToCheck).get(j).getEndPoint().getyCoord();
				writeText += (Math.round(XCoordinate)) + " " + (Math.round(YCoordinate)) + "\n";
			}
			output = new BufferedWriter(new FileWriter(temporaryFilePolygon, false));
			output.write(writeText);
			if ( output != null ) { output.close(); }
		} 
		else 
		{
			return;
		}
	}
}
