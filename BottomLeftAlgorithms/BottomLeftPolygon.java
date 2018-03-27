package BottomLeftAlgorithms;

import MikowskiUtility.Edge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BottomLeftPolygon 
{
	public static String POLYGON_FILE_PATH = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\Poly_0";
	
	double minHeight;
	int countRound;
	boolean isNoPacking;
	
	int[] POLYGON_SEQ;
	int[] DEGREE_CODE;
	
	List<Integer> COUNT_ROUND;
	List<Rect> BOUNDING_BOX;
	List<Rect> POLYGON_LIST;
	List<Double> HEIGHT_FITNESS;
		
	public BottomLeftPolygon (int[] polygonSequence, int[] degreeCode, List<List<Edge>> trackLineTrip)
	{
		minHeight = 0;
		countRound = 0;
		isNoPacking = true;
		COUNT_ROUND = new ArrayList<>();
		HEIGHT_FITNESS = new ArrayList<>();			// Collect min-height per round.
		BOUNDING_BOX = new ArrayList<>();
		BOUNDING_BOX.add(new Rect(trackLineTrip, null));
		
		POLYGON_SEQ = polygonSequence.clone();
		DEGREE_CODE = degreeCode.clone();
		POLYGON_LIST = new ArrayList();
		CreatePolygonListFromTextFile();
		BLF();
	}
	
	protected void CreatePolygonListFromTextFile() 
	{
		for (int polygon_idx=0; polygon_idx < POLYGON_SEQ.length; polygon_idx++) 
		{
			File polygonfile = new File((POLYGON_FILE_PATH + (POLYGON_SEQ[polygon_idx]+1) + ".txt"));
			
			try {
				Scanner fileScanner = new Scanner(polygonfile);
				int countLineDummy = 0;
				boolean swapBetweenXandY = true;
				
				double[] temporaryXYBeforeInsertToList = new double[2];
				List<double[]> thePolygonBlock = new ArrayList<>();
				
				while (fileScanner.hasNextLine()) {
					// index 0 = x-coordinate
					// index 1 = y-coordinate
					double numberForStorage = fileScanner.nextDouble();
					
					if (swapBetweenXandY) {
						temporaryXYBeforeInsertToList[0] = numberForStorage;
						swapBetweenXandY = !swapBetweenXandY;
					}
					else {
						temporaryXYBeforeInsertToList[1] = numberForStorage;
						thePolygonBlock.add(temporaryXYBeforeInsertToList.clone());
						swapBetweenXandY = !swapBetweenXandY;
					}
				}
				POLYGON_LIST.add(new Rect(null, thePolygonBlock));
			} catch (FileNotFoundException e) {
				System.err.println("File \""+ ((polygon_idx+1) + ".txt") +"\" not Found");
			}
		}
	}

	
	protected void BLF()
	{
		// --- Save initial height of bounding box ---
		HEIGHT_FITNESS.add(BOUNDING_BOX.get(BOUNDING_BOX.size()-1).Height());
		Rect newGap;
		
		while (isNoPacking && countRound < 2) {
			for (int polygon_idx=0; polygon_idx<POLYGON_LIST.size(); polygon_idx++) {
				
				int boundingBoxSize = BOUNDING_BOX.size();
				Rect temporaryGaps = null;
				
				for (Rect boundingBox: BOUNDING_BOX) {
					if (boundingBox.Height() > HEIGHT_FITNESS.get(HEIGHT_FITNESS.size()-1)) {
						HEIGHT_FITNESS.add(boundingBox.Height());
						COUNT_ROUND.add(polygon_idx);
					}
				}
				
				for (int gh_idx=0; gh_idx<BOUNDING_BOX.size(); gh_idx++) {
					temporaryGaps = BOUNDING_BOX.get(gh_idx);
	                if(temporaryGaps.Width() >= POLYGON_LIST.get(polygon_idx).Width() && temporaryGaps.Height() >= POLYGON_LIST.get(polygon_idx).Height()) {
	                    break;
					}
	                if(gh_idx == BOUNDING_BOX.size()-1) {
	                    temporaryGaps = new Rect(-1,-1,-1,-1);
	                }
				}
				
				if(temporaryGaps.X() != -1 && temporaryGaps.Y() != -1) {
					int ghCheckIdx = 0;
					
					POLYGON_LIST.get(polygon_idx).setX(temporaryGaps.X());
					POLYGON_LIST.get(polygon_idx).setY(temporaryGaps.Y());
					
					while ( ghCheckIdx <= boundingBoxSize ) {
						if ( !(POLYGON_LIST.get(polygon_idx).Right() < temporaryGaps.X() 
								|| POLYGON_LIST.get(polygon_idx).Top() < temporaryGaps.Y() 
								|| (POLYGON_LIST.get(polygon_idx).X() > temporaryGaps.Right()) 
								|| (POLYGON_LIST.get(polygon_idx).Y() > temporaryGaps.Top())) ) {
							
							if(temporaryGaps.X() < POLYGON_LIST.get(polygon_idx).X()) {
                                newGap = new Rect(temporaryGaps.X(), temporaryGaps.Y(), POLYGON_LIST.get(polygon_idx).X() - temporaryGaps.X(), temporaryGaps.Height());
                                if(!IsEqual(temporaryGaps, newGap))
                                	BOUNDING_BOX.add(newGap);
							}
							
							// --- Add a gap on top of the new rectangle if possible
                            if(temporaryGaps.Y() < POLYGON_LIST.get(polygon_idx).Y()) {
                                newGap = new Rect(temporaryGaps.X(), temporaryGaps.Y(), temporaryGaps.Width(), POLYGON_LIST.get(polygon_idx).Y() - temporaryGaps.Y());
                                if(!IsEqual(temporaryGaps, newGap))
                                	BOUNDING_BOX.add(newGap);
							}
                            
                            // --- Add a gap to the right of the new rectangle if possible
                            if (temporaryGaps.Right() > POLYGON_LIST.get(polygon_idx).Right()) {
                                newGap = new Rect(POLYGON_LIST.get(polygon_idx).Right(), temporaryGaps.Y(), temporaryGaps.Right() - POLYGON_LIST.get(polygon_idx).Right(), temporaryGaps.Height());
                                if(!IsEqual(temporaryGaps, newGap))
                                	BOUNDING_BOX.add(newGap);
                                
                            }

                            // --- Add a gap below the new rectangle if possible
                            if (temporaryGaps.Top() > POLYGON_LIST.get(polygon_idx).Top()) {
                                newGap = new Rect(temporaryGaps.X(), POLYGON_LIST.get(polygon_idx).Top(), temporaryGaps.Width(), temporaryGaps.Top() - POLYGON_LIST.get(polygon_idx).Top());
                                if(!IsEqual(temporaryGaps, newGap))
                                	BOUNDING_BOX.add(newGap);
							}
                            
                            FindAndRemove(temporaryGaps);
                            ghCheckIdx--;
                            boundingBoxSize--;
						}
						ghCheckIdx++;
					}
//					// Sorting GAPS
                    SortAndComparePosition();
				}
			}
			
			for (int checkYIdx = 0; checkYIdx < POLYGON_LIST.size(); checkYIdx++) {
	            //heightCollector(check_y_idx) = rects(check_y_idx).y + rects(check_y_idx).height;
	            if(POLYGON_LIST.get(checkYIdx).X() == -1) {
	                isNoPacking = false;
	                countRound++;
	            }
			}
		    
	        
	        if(isNoPacking)
	            isNoPacking = false;
	        else
	        	isNoPacking = true;
		}
		
//		HEIGHT_FITNESS.add(heightCollector);
//	    allFitness.height  = HEIGHT_FITNESS;
//	    allFitness.seq     = sequenceNumber;
//	    allFitness.round   = roundFitness;
//	    allFitness.rect    = POLYGON_LIST;
	}
	
	public void FindAndRemove(Rect temporaryGaps) {
		
		int gapCheckCounter = BOUNDING_BOX.size();
	    
		if(gapCheckCounter > 0) {
	        int runningCounter = 0;
	        
	    	while(runningCounter < gapCheckCounter) {
	            
	            if(BOUNDING_BOX.get(runningCounter).X() == temporaryGaps.X() 
	            		&& BOUNDING_BOX.get(runningCounter).Y() == temporaryGaps.Y() 
	            		&& BOUNDING_BOX.get(runningCounter).Width() == temporaryGaps.Width() 
	            		&& BOUNDING_BOX.get(runningCounter).Height() == temporaryGaps.Height()) {
	            	BOUNDING_BOX.remove(runningCounter);
	                gapCheckCounter--;
	            }
	            else {
	                runningCounter++;
	            }
	            
	    	}
	    }
	}
	
	public void SortAndComparePosition() {
		// before sorting must compare to find every value is unique    
		int runningIndex = 1;
		int gapSize = BOUNDING_BOX.size();
		
		while ( runningIndex < gapSize) {
			int e = runningIndex+1;
			while(e < gapSize) {
				
				if(IsEqual(BOUNDING_BOX.get(runningIndex), BOUNDING_BOX.get(e))) { 
		        	BOUNDING_BOX.remove(e);
		        	gapSize--;
		    	}
				else {
					e++;
				}
		    }
		    runningIndex++;
		}
	
		int xIndex = BOUNDING_BOX.size();
		
	    if(xIndex > 1) {
	        int n = xIndex;
	        while(n > 0) {
	            int nnew = 0;
	            for (int rIndex = 1; rIndex<n; rIndex++) {
	                // Swap elements in wrong order
	                if (PositionComparator(BOUNDING_BOX.get(rIndex), BOUNDING_BOX.get(rIndex-1))) {
	                    // swap runningIndex and runningIndex-1
	                    Rect temp = BOUNDING_BOX.get(rIndex);
	                    BOUNDING_BOX.set(rIndex, BOUNDING_BOX.get(rIndex-1));
	                    BOUNDING_BOX.set(rIndex-1, temp);
	                    nnew = rIndex;
	        		}
	            }
	            n = nnew;
	        }
		}
	}
	
	public boolean PositionComparator(Rect currentBoundingBox, Rect previousBoundingBox) {
	    return ((currentBoundingBox.Y() < previousBoundingBox.Y()) || ((currentBoundingBox.Y() == previousBoundingBox.Y()) && ((currentBoundingBox.X() < previousBoundingBox.X()) || ((currentBoundingBox.X() == previousBoundingBox.X()) && ((currentBoundingBox.Height() > previousBoundingBox.Height()) || ((currentBoundingBox.Height() == previousBoundingBox.Height()) && (currentBoundingBox.Width() > previousBoundingBox.Width())))))));
	}
	
	public boolean IsEqual(Rect gap, Rect newGap) {
		return ( gap.X()==newGap.X() && gap.Y()==newGap.Y() && gap.Width()==newGap.Width() && gap.Height()==newGap.Height() );
	}
}
