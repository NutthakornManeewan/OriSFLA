import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mainClass {
	public static void main(String[] args) throws FileNotFoundException{
		List<File> olderPolygonFile = new ArrayList<File>();
		String olderPolygonFilePath = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\Poly_0";
		
		int POLYGON_SIZE = 12;
		int REPEATED_NUMBER = 9;
		int REPEATED_POLYGON[] = {1,1,1,1,2,1,1,2,2};
		
		for (int i=0; i<REPEATED_NUMBER; i++) 
		{
			for (int j=0; j<REPEATED_POLYGON[i]; j++) 
			{
				String tmpFilePath = olderPolygonFilePath + Integer.toString(i+1) + ".txt";
				olderPolygonFile.add(new File(tmpFilePath));
			}
		}
		
		int M = 3;
		int N = 3;
		
		OriSFLA originalSFLAObject = new OriSFLA(100, 0.00005, M, N ,olderPolygonFile);
		try {
			originalSFLAObject.CreateOriginalSFLAObject(M, N, "min-height");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("=== Generation Time ===");
		originalSFLAObject.resultObject.showGenerationTime();
		
		System.out.println("=== Best ===");
		originalSFLAObject.resultObject.showBestFitnessValue();
	}
}