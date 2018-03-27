import MikowskiUtility.*;
import BottomLeftAlgorithms.BottomLeftPolygon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import CollectingDataUtility.CollectData;

public class OriSFLA 
{
	
	public static String ALLDATA_FILE_PATH = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\ALL_DATA\\Polygon";
	public static CollectData collectData = new CollectData();
	
	// ===== Properties =====
	public double ERROR_CHANGE_GOAL;
	public int MAX_GENERATION;
	public double FITNESS_VALUE[];
	public ResultObject resultObject;
	public MikowSkiUtility utilRotatedMikow;
	
	public static FrameConstraint frameConstObject; 
	public static JNFP hellObj;
	public static DecisionStepFitness CalObj;
	
	// ######## Constants configuration ########
	public static int SEQUENCE_SIZE = 28;
	public static int[] REPEATED_POLYGON = new int[] {4,3,3,3,3,3,3,3,3};
	public static int FRAME_WIDTH = 20;
	public static int FRAME_HEIGHT = 100;
	
	public List<Double> objectiveValue = new ArrayList<Double>();
	public List<Double> objectiveTime = new ArrayList<Double>();
	public List<SimplePolygon> rawPolygon = new ArrayList<SimplePolygon>();
	public List<File> POLYGON_FILES = new ArrayList<File>();
	
	// ===== Constructor =====
	public OriSFLA (int maxGen, double errorGoal, int M, int N, List<File> polyFiles) 
	{
		resultObject = new ResultObject(4,100);
		ERROR_CHANGE_GOAL = errorGoal;
		MAX_GENERATION = maxGen;
		FITNESS_VALUE = new double[M*N];
		POLYGON_FILES.addAll(polyFiles);
		hellObj = new JNFP();
		
		for (int i=0; i<POLYGON_FILES.size(); i++) 
		{
			rawPolygon.add(new SimplePolygon(POLYGON_FILES.get(i)));
			rawPolygon.get(i).setSeqNumber(i);
		}
	}
	
	// ===== Methods =====
	public void CreateOriginalSFLAObject(int m, int n, String funcType) throws IOException 
	{
		// ***** Constant for this Algorithms *****
		String odinary_file_path = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\PolyTmp.txt";
		String rotate_file_path = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\PolyRotateTmp";
		
		int TotalSampleSize = m*n,IDX = 1, DATA_COLLECT = 2, NUM_COORDINATE = 2, NUM_DEGREE_CHECK = 4;
		int[][] degreeCode = new int[TotalSampleSize][SEQUENCE_SIZE];
		double[][] X = new double[TotalSampleSize][DATA_COLLECT];
		double[][] sortedFitness = new double[NUM_COORDINATE][FITNESS_VALUE.length];
		
		NoFitPolygon outputDemo = null;
		List<List<Edge>>[] outputRotatedDemo = new List[NUM_DEGREE_CHECK];
		List<SimplePolygon> sortedPolygon = new ArrayList<>();
		FitnessSequence best_out = null;
		
		// ***** Step 1:: Generate a virtual population.
		List<Integer> sequenceList = new ArrayList<>();
		int[][] allSequenceList = new int[TotalSampleSize][TotalSampleSize];
		FitnessSequence[] X1 = new FitnessSequence[TotalSampleSize];
		FitnessSequence[][] PXI = new FitnessSequence[MAX_GENERATION][MAX_GENERATION];
		FitnessSequence Px = null;
		ArrayList<Long> time_for_start = new ArrayList<>();
		
		for (int t=0; t<TotalSampleSize; t++) 
		{
			for (int s=0; s<TotalSampleSize; s++)
				sequenceList.add(s);
			Collections.shuffle(sequenceList);
			
			for (int s=0; s<TotalSampleSize; s++)
				allSequenceList[t][s] = sequenceList.get(s);
			sequenceList.clear();
		}
		
		// ########### Create initial sequence with random process ###########
		int temporaryRandomIndex = 0;
		int[] checkRepeatedArray = new int[SEQUENCE_SIZE];
		int[][] TEST_SEQUENCE = new int[TotalSampleSize][SEQUENCE_SIZE];
		
		for (int i=0; i<TotalSampleSize; i++) {
			for (int j=0; j<SEQUENCE_SIZE; j++) {
				do {
					temporaryRandomIndex = 0 + (int)(Math.random() * (((REPEATED_POLYGON.length-1) - 0) + 1));
				}
				while(checkRepeatedArray[temporaryRandomIndex] == REPEATED_POLYGON[temporaryRandomIndex]);
				TEST_SEQUENCE[i][j] = temporaryRandomIndex;
				checkRepeatedArray[temporaryRandomIndex] = 1;
			}
			checkRepeatedArray = new int[SEQUENCE_SIZE];
		}
		long startTime, endTime;
		Minkowski.enablePrintData(false);
		for (int t=0; t<TotalSampleSize; t++) 
		{
			double[] fitnessValueStep = new double[NUM_DEGREE_CHECK];
			SimplePolygon tmpPolygon = null;
			utilRotatedMikow = new MikowSkiUtility(rotate_file_path);
			
			startTime = System.nanoTime();
			utilRotatedMikow.CreateRotatedPolygon(rawPolygon.get(TEST_SEQUENCE[t][0]), rawPolygon.get(TEST_SEQUENCE[t][1]));
			endTime = System.nanoTime();
			time_for_start.add((endTime-startTime));
			
			outputRotatedDemo = utilRotatedMikow.getOutputRotatedPolygon().clone();
			CalObj = new DecisionStepFitness(outputRotatedDemo);
			fitnessValueStep  = CalObj.fitnessValue.clone();
			
			int selectedIndex = FindMinimumFitness(fitnessValueStep);
	        GenerateTemporaryFile(outputRotatedDemo[selectedIndex], odinary_file_path);
			tmpPolygon = new SimplePolygon(new File(odinary_file_path));
			degreeCode[t][0] = selectedIndex;
	        
			for (int s=2; s<TEST_SEQUENCE[t].length; s++) 
			{
	        	SimplePolygon veryTmpPolygon = rawPolygon.get(TEST_SEQUENCE[t][s]);
	        	startTime = System.nanoTime();
	        	utilRotatedMikow.CreateRotatedPolygon(tmpPolygon, veryTmpPolygon);
	        	endTime = System.nanoTime();
	        	time_for_start.add(endTime-startTime);
	        	outputRotatedDemo = utilRotatedMikow.getOutputRotatedPolygon();
	        	CalObj = new DecisionStepFitness(outputRotatedDemo);
				fitnessValueStep = CalObj.fitnessValue.clone();
	        	selectedIndex = FindMinimumFitness(fitnessValueStep);
	        	degreeCode[t][s-1] = selectedIndex;
	        	
	        	if (outputRotatedDemo[selectedIndex] != null)
	        	{
					GenerateTemporaryFile(outputRotatedDemo[selectedIndex], odinary_file_path);
					tmpPolygon = new SimplePolygon(new File(odinary_file_path));
	        	}
	        	else
	        		continue;
	        }
			
			// -- Insert collect data [1] for collect initial sequence.
			FITNESS_VALUE[t] = CalculateFitnessValue_2(tmpPolygon, 0, funcType);
			collectData.CollectTestSequenceData(outputRotatedDemo[selectedIndex], degreeCode[t], TEST_SEQUENCE[t], FITNESS_VALUE[t], "test");
			if (FITNESS_VALUE[t] < 1.0) { FITNESS_VALUE[t] = Double.POSITIVE_INFINITY; }
		}
		
		BufferedWriter output = null;
		String writeText = "Time NFP: ";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String file_path = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\ALL_DATA\\TimeCollector_"+timestamp.getTime()+".txt";
		File temporaryFilePolygon = new File(file_path);
		
		if (time_for_start.size() > 0) {
			for (long timeElement: time_for_start)
				writeText += timeElement + ", ";
			
			try {
				output = new BufferedWriter(new FileWriter(temporaryFilePolygon, false));
				output.write(writeText);
				if ( output != null ) { output.close(); }
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		
		// ***** Step 2:: Rank frogs.
		sortedFitness = QuickSort(FITNESS_VALUE);
		for (int k=0; k<TotalSampleSize; k++) 
		{
		    X[k][0] = FITNESS_VALUE[(int)sortedFitness[IDX][k]];
		    X[k][1] = k;
		    X1[k] = new FitnessSequence(TEST_SEQUENCE[(int)sortedFitness[IDX][k]], FITNESS_VALUE[(int)sortedFitness[IDX][k]], k);
		}
		
		Px = X1[0];
		resultObject.initialPosition.add(sortedPolygon);
		resultObject.fitnessValue.add(Px.fitnessValue);
		resultObject.degreeCodeForBestFitness[0] = degreeCode[(int)sortedFitness[IDX][0]];
		FitnessSequence[][] Y1 = new FitnessSequence[m][n];
		int[] leapingDegreeCode = new int[SEQUENCE_SIZE];
		
		
		
		// ************** Real Loop for each Generation ****************** //
		for (int iTer=0; iTer<MAX_GENERATION; iTer++) {
			ArrayList<Long> time_for_collect = new ArrayList<>();
			long start_Time = System.nanoTime();
			System.out.print("Best iteration " + (iTer+1) + " is " + Px.fitnessValue + " | Degree = [");
			
			for (int degShow: resultObject.degreeCodeForBestFitness[iTer])
			{
				switch(degShow) 
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
			System.out.println("]");

			// === Reshape Zone ===
			// Change into from
			// [X[0]]-[X[3]]-[X[6]] <-- X[0] is best fitness and X[6] is worse fitness
			// [X[1]]-[X[4]]-[X[7]]
			// [X[2]]-[X[5]]-[X[8]]
			Y1 = ReshapeMatrices1(m, n, X1.clone());
			int N = n;
			
			for (int im=0; im<m; im++) 
			{
				for (int iN=0; iN<N; iN++) 
				{
					// step3 a construct submemplex
					int Zidx=0;
					FitnessSequence[] Z = null;
					FitnessSequence Pb=null, Pw=null;
			        
					if (n > 2)
					{
			            int sizeZidx = 1;
			            while(sizeZidx == 1)
			            {
			            	Zidx = n;
			                Z = new FitnessSequence[Zidx];
			                for (int t = 0; t<Zidx; t++)
			                {
			                	Z[t] = Y1[im][t];
			                }
			                Pb = Z[0];
			                Pw = Z[Zidx-1];
			                sizeZidx = Zidx;
			            }
			        }
			        
			        // ===== Step 4 improve the worst frog's position
			        double Smax = 0.45;
			        double Rd = 0;
			        FitnessSequence PbNet = null;
			        FitnessSequence PxNet = null;
			        FitnessSequence PwNet = null;
			        
			        double[] stepS = new double[NUM_COORDINATE];
			        double[] newPos = new double[NUM_COORDINATE];
			        double fitness = Double.POSITIVE_INFINITY;
			        int ZLengthIdx = Z.length - 1;
			        String randtype = "uniformdist";
			        Random randomGenerator = new Random(System.currentTimeMillis());
			        
			        Rd = randomGenerator.nextDouble();
			        PbNet = Pb;
			        PwNet = Pw;
			        
			        // ----- Leaping = Pw(new)= Pw + min(||rand().*(Pb-Pw)||, Smax); -----
			        int[] LeapingSequence = new int[Pw.sequenceNumber.length];
			        //LeapingSequence = CreateLeapingSequence(testObj, Pb, Pw, Rd, Smax);
			        LeapingSequence = CreateLeapingSequenceNormal(Pb, Pw, Rd, Smax);
			        
			        SimplePolygon[] leapRotatedPolygon = new SimplePolygon[NUM_DEGREE_CHECK];
			        SimplePolygon leapTmpPolygon = null;
					float leapDegreeCalculate = 0.0f;
			        
			        for (int rIdx=0; rIdx < NUM_DEGREE_CHECK; rIdx++) 
			        {
						SimplePolygon staticPolygon = new SimplePolygon(rawPolygon.get(LeapingSequence[0]).directoryPolygon);
						leapRotatedPolygon[rIdx] = RotateCoordinate(staticPolygon, leapDegreeCalculate);
						leapDegreeCalculate += 90.0;
						GenerateTemporaryRotateFile(leapRotatedPolygon[rIdx].xCoordinateList, leapRotatedPolygon[rIdx].yCoordinateList, rotate_file_path + Integer.toString(rIdx) + ".txt");
						
						startTime = System.nanoTime();
						outputRotatedDemo[rIdx] = hellObj.GenerateMinkowskiNFP(rawPolygon.get(LeapingSequence[0]).directoryPolygon, new File(rotate_file_path + Integer.toString(rIdx) + ".txt"));
						endTime = System.nanoTime();
						time_for_collect.add(endTime - startTime);
					}

			        double[] leapingFitnessValueStep = new double[NUM_DEGREE_CHECK];
			        CalObj = new DecisionStepFitness(outputRotatedDemo);
					leapingFitnessValueStep = CalObj.fitnessValue.clone();
					
					int selectedIndex = FindMinimumFitness(leapingFitnessValueStep);
					leapingDegreeCode[0] = selectedIndex;
			        GenerateTemporaryFile(outputRotatedDemo[selectedIndex], odinary_file_path);
					SimplePolygon leapingTmpPolygon = new SimplePolygon(new File(odinary_file_path));
			        
			        for (int s=2; s<LeapingSequence.length; s++)
			        {
			        	SimplePolygon veryTmpPolygon = rawPolygon.get(LeapingSequence[s]);
			        	leapDegreeCalculate = 0.0f;
			        	
			        	for (int rIdx=0; rIdx<NUM_DEGREE_CHECK; rIdx++)
			        	{
			        		SimplePolygon staticPolygon = new SimplePolygon(rawPolygon.get(LeapingSequence[s]).directoryPolygon);
			        		leapRotatedPolygon[rIdx] = RotateCoordinate(staticPolygon, leapDegreeCalculate);
			        		leapDegreeCalculate += 90.0;
							GenerateTemporaryRotateFile(leapRotatedPolygon[rIdx].xCoordinateList, leapRotatedPolygon[rIdx].yCoordinateList, rotate_file_path + Integer.toString(rIdx) + ".txt");
							
							startTime = System.nanoTime();
							outputRotatedDemo[rIdx] = hellObj.GenerateMinkowskiNFP(leapingTmpPolygon.directoryPolygon, new File(rotate_file_path + Integer.toString(rIdx) + ".txt"));
							endTime = System.nanoTime();
							time_for_collect.add(endTime - startTime);
			        	}
			        	CalObj = new DecisionStepFitness(outputRotatedDemo);
			        	leapingFitnessValueStep = CalObj.fitnessValue.clone();
			        	selectedIndex = FindMinimumFitness(leapingFitnessValueStep);
			        	leapingDegreeCode[s-1] = selectedIndex;
			        	GenerateTemporaryFile(outputRotatedDemo[selectedIndex], odinary_file_path);
						leapingTmpPolygon = new SimplePolygon(new File(odinary_file_path));
			        }
			        
			        frameConstObject = new FrameConstraint(FRAME_WIDTH, FRAME_HEIGHT,outputRotatedDemo, selectedIndex);
//			        BottomLeftPolygon debugTest;
		        	if (frameConstObject.IsInFrame())
		        	{
		        		// ---- Insert Collect data [2].
						fitness = CalculateFitnessValue_2(leapingTmpPolygon, 0, funcType);
//						debugTest = new BottomLeftPolygon(LeapingSequence, leapingDegreeCode, outputRotatedDemo[selectedIndex]);
						collectData.CollectTestSequenceData(outputRotatedDemo[selectedIndex], leapingDegreeCode, LeapingSequence, fitness, "leaping");
						leapingTmpPolygon.setFitnessValue(fitness);
		        	}
			        
			        if (fitness < Pw.fitnessValue && fitness != 0) 
			        {
			            Z[ZLengthIdx].fitnessValue = fitness;
			            Z[ZLengthIdx].sequenceNumber = LeapingSequence.clone();
			        } 
			        else
			        {
			        	// === Step 5 :: if step 4 can not product a better result ===
			        	switch (randtype) 
			        	{
			        		case "uniformdist":
			        			// ramdomize with uniform distribution
			        			Rd = randomGenerator.nextInt(n);
			        			break;
			                case "truncatedist":
			                	Rd = randomGenerator.nextInt(n);
			                    break;
			        	}
			        	PxNet = Px;
			            PwNet = Pw;
				        
			            int[] LeapingPxSequence = new int[Px.sequenceNumber.length];
			            LeapingPxSequence = CreateLeapingSequenceNormal(Px, Pw, Rd, Smax);
			            
			            startTime = System.nanoTime();
			            SimplePolygon elseTempPolygon = CreateMikowskiPolygon(rawPolygon.get(LeapingPxSequence[0]).directoryPolygon, rawPolygon.get(LeapingPxSequence[1]).directoryPolygon, LeapingPxSequence, odinary_file_path);
			            endTime = System.nanoTime();
			            time_for_collect.add(endTime-startTime);
				        
			            /* ###############################################
		            	// ############# My mistake addition ############# //
		            	   ############################################### */
			            
//		            	String mistakeFilePath = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\Polytmp2.txt";			            	
		            	String mistakeFilePath = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\Polytmp";
		            	List<List<Edge>>[] mistakeNFP = null;
		            	SimplePolygon mistakSP = null, mistakeTmpPolygon = null;
		            	int[] mistakeDegreeCode = new int[SEQUENCE_SIZE];
		            	utilRotatedMikow = new MikowSkiUtility(mistakeFilePath);
		    			utilRotatedMikow.CreateRotatedPolygon(rawPolygon.get(LeapingPxSequence[0]), rawPolygon.get(LeapingPxSequence[1]));
		    			mistakeNFP = utilRotatedMikow.getOutputRotatedPolygon().clone();
		    			CalObj = new DecisionStepFitness(mistakeNFP);
		    			double[] mistakeFitnessValueStep  = CalObj.fitnessValue.clone();
		    			
		    			int mistakeSelectedIndex = FindMinimumFitness(mistakeFitnessValueStep);
		    	        GenerateTemporaryFile(mistakeNFP[mistakeSelectedIndex], mistakeFilePath+".txt");
		    	        mistakSP = new SimplePolygon(new File(mistakeFilePath+".txt"));
		    			mistakeDegreeCode[0] = mistakeSelectedIndex;
		    			// ---------------------------------------------------------------------------- //
			            
			            for (int s=2; s<LeapingPxSequence.length; s++) 
				        {
				        	SimplePolygon veryTmpPolygon = rawPolygon.get(LeapingPxSequence[s]);
				        	
				        	startTime = System.nanoTime();
				        	elseTempPolygon = CreateMikowskiPolygon(elseTempPolygon.directoryPolygon, veryTmpPolygon.directoryPolygon,LeapingPxSequence, odinary_file_path);
				        	endTime = System.nanoTime();
				        	time_for_collect.add(endTime-start_Time);
				        	
				        	mistakeTmpPolygon = rawPolygon.get(LeapingPxSequence[s]);
				        	utilRotatedMikow.CreateRotatedPolygon(mistakSP, mistakeTmpPolygon);
				        	mistakeNFP = utilRotatedMikow.getOutputRotatedPolygon();
				        	CalObj = new DecisionStepFitness(mistakeNFP);
				        	mistakeFitnessValueStep = CalObj.fitnessValue.clone();
				        	mistakeSelectedIndex = FindMinimumFitness(mistakeFitnessValueStep);
				        	mistakeDegreeCode[s-1] = mistakeSelectedIndex;
				        	
				        	if (mistakeNFP[mistakeSelectedIndex] != null)
				        	{
								GenerateTemporaryFile(mistakeNFP[selectedIndex], mistakeFilePath+".txt");
								mistakeTmpPolygon = new SimplePolygon(new File(mistakeFilePath+".txt"));
				        	}
				        }
			        	frameConstObject = new FrameConstraint(FRAME_WIDTH, FRAME_HEIGHT, mistakeNFP, selectedIndex);
						double mistakeFitness = 0.0;
			        	if (frameConstObject.IsInFrame())
						{
							fitness = CalculateFitnessValue_2(elseTempPolygon, 0, funcType);
							mistakeFitness = CalculateFitnessValue_2(mistakeTmpPolygon, 0, funcType);
							elseTempPolygon.setFitnessValue(mistakeFitness);
							collectData.CollectTestSequenceData(mistakeNFP[mistakeSelectedIndex], mistakeDegreeCode, LeapingPxSequence, mistakeFitness, "leaping");
						}
			        	
			        	if (mistakeFitness < Pw.fitnessValue && mistakeFitness != 0)
						{
				            Z[ZLengthIdx].fitnessValue = mistakeFitness;
				            Z[ZLengthIdx].sequenceNumber = LeapingPxSequence.clone();
				        }
			            else 
			            {
			            	// === Step 6 Censorship ===
			            	// ----- Random all 0 to (n-1) to get one of input sequence.
			            	int idxRandom = randomGenerator.nextInt(n);
			            	int[] randomSequence = TEST_SEQUENCE[idxRandom].clone();
			            	
			            	startTime = System.nanoTime();
			            	SimplePolygon randomTmpPolygon = CreateMikowskiPolygon(rawPolygon.get(randomSequence[0]).directoryPolygon, rawPolygon.get(randomSequence[1]).directoryPolygon, randomSequence, odinary_file_path);
			            	endTime = System.nanoTime();
			            	time_for_collect.add(endTime-startTime);
			            	
			            	/* ###############################################
			            	// ############# My mistake addition ############# //
			            	   ############################################### */
			            	
			            	utilRotatedMikow = new MikowSkiUtility(mistakeFilePath);
			    			utilRotatedMikow.CreateRotatedPolygon(rawPolygon.get(randomSequence[0]), rawPolygon.get(randomSequence[1]));
			    			mistakeNFP = utilRotatedMikow.getOutputRotatedPolygon().clone();
			    			CalObj = new DecisionStepFitness(mistakeNFP);
			    			mistakeFitnessValueStep  = CalObj.fitnessValue.clone();
			    			
			    			mistakeSelectedIndex = FindMinimumFitness(mistakeFitnessValueStep);
			    	        GenerateTemporaryFile(mistakeNFP[mistakeSelectedIndex], mistakeFilePath+".txt");
			    	        mistakSP = new SimplePolygon(new File(mistakeFilePath+".txt"));
			    			mistakeDegreeCode[0] = mistakeSelectedIndex;
			    			// ---------------------------------------------------------------------------- //
					        
			    			for (int s=2; s<randomSequence.length; s++) 
					        {
					        	SimplePolygon veryTmpPolygon = rawPolygon.get(randomSequence[s]);
					        	
					        	startTime = System.nanoTime();
					        	randomTmpPolygon = CreateMikowskiPolygon(randomTmpPolygon.directoryPolygon, veryTmpPolygon.directoryPolygon, randomSequence, odinary_file_path);
					        	endTime = System.nanoTime();
					        	time_for_collect.add(endTime-startTime);
					        	
					        	mistakeTmpPolygon = rawPolygon.get(randomSequence[s]);
					        	utilRotatedMikow.CreateRotatedPolygon(mistakSP, mistakeTmpPolygon);
					        	mistakeNFP = utilRotatedMikow.getOutputRotatedPolygon();
					        	CalObj = new DecisionStepFitness(mistakeNFP);
					        	mistakeFitnessValueStep = CalObj.fitnessValue.clone();
					        	mistakeSelectedIndex = FindMinimumFitness(mistakeFitnessValueStep);
					        	mistakeDegreeCode[s-1] = mistakeSelectedIndex;
					        	
					        	if (mistakeNFP[mistakeSelectedIndex] != null)
					        	{
									GenerateTemporaryFile(mistakeNFP[selectedIndex], mistakeFilePath+".txt");
									mistakeTmpPolygon = new SimplePolygon(new File(mistakeFilePath+".txt"));
					        	}
					        }
			    			
			    			frameConstObject = new FrameConstraint(FRAME_WIDTH, FRAME_HEIGHT,mistakeNFP, selectedIndex);
							if (frameConstObject.IsInFrame())
							{
								// --- Insert collectdata[3].
								fitness = CalculateFitnessValue_2(randomTmpPolygon, 0, funcType);
								mistakeFitness = CalculateFitnessValue_2(mistakeTmpPolygon, 0, funcType);
								randomTmpPolygon.setFitnessValue(mistakeFitness);
								collectData.CollectTestSequenceData(mistakeNFP[mistakeSelectedIndex], mistakeDegreeCode, randomSequence, mistakeFitness, "leaping");
				                
				                if (mistakeFitness < Z[ZLengthIdx].fitnessValue && mistakeFitness != 0) 
				                { 
				                    Z[ZLengthIdx].fitnessValue = mistakeFitness;
				                    Z[ZLengthIdx].sequenceNumber = randomSequence.clone();
				                }
							}
			            }
			        }

		            // === Step 7 upgrade the memeplex and sort them ===
		            double[] fitn = new double[n];
			        double[][] SortedFitAgain = new double[2][n];

			        for (int z=0; z<Zidx; z++)
		            	Y1[im][z] = Z[z];
			        for (int k=0; k<n; k++)
			            fitn[k] = Y1[im][k].fitnessValue;
			        
			        SortedFitAgain = QuickSort(fitn);
			        FitnessSequence[] forTemporaryFitness = new FitnessSequence[Y1[im].length];
			        for (int k=0; k<SortedFitAgain[1].length; k++) 
			        {
			        	forTemporaryFitness[k] = Y1[im][(int) SortedFitAgain[1][k]];
			        }
			        Y1[im] = forTemporaryFitness;
				}
			}
			
			// ************* Global Search ************* //
			// === Step 5:: Shuffle memeplexes ===
			int CounterIdx = 0;
			FitnessSequence nPx = null;
			for (int i=0; i<m; i++) 
			{
				for (int j=0; j<n; j++)
					X1[CounterIdx++] = Y1[j][i];
			}
			
			double[] fitn = new double[TotalSampleSize];
			for (int k=0; k<TotalSampleSize; k++)
				fitn[k] = X1[k].fitnessValue;
			
			double[][] SortedFitAgain = new double[2][TotalSampleSize];
	        SortedFitAgain = QuickSort(fitn);
	        FitnessSequence[] lastSortedFitness = new FitnessSequence[SortedFitAgain[0].length];
	        for (int k=0; k<SortedFitAgain[1].length; k++) 
	        {
	        	lastSortedFitness[k] = X1[(int) SortedFitAgain[1][k]];
	        }
	        X1 = lastSortedFitness;
	
			nPx = X1[0];
			if (nPx.fitnessValue < Px.fitnessValue  && nPx.fitnessValue != 0)
				Px = nPx;
			
			resultObject.fitnessValue.add(Px.fitnessValue);
			best_out = Px;
			long end_Time = System.nanoTime();
			resultObject.generationTime.add((end_Time - start_Time)/1000.0);
		
			FitnessSequence[] pxi = new FitnessSequence[X1.length];
			for (int xi=0; xi<X1.length; xi++) 
			{
				pxi[xi] = X1[xi];
			}
			PXI[iTer] = pxi;
			resultObject.degreeCodeForBestFitness[iTer] = leapingDegreeCode.clone();
			
			output = null;
			writeText = "Time NFP: ";
			timestamp = new Timestamp(System.currentTimeMillis());
			file_path = "C:\\Users\\Nutthakorn Maneewan\\workspace\\Original_SFLA\\resources\\ALL_DATA\\TimeCollector_"+timestamp.getTime()+".txt";
			temporaryFilePolygon = new File(file_path);
			
			if (time_for_collect.size() > 0) {
				for (long timeElement: time_for_collect)
					writeText += timeElement + ", ";
				
				try {
					output = new BufferedWriter(new FileWriter(temporaryFilePolygon, false));
					output.write(writeText);
					if ( output != null ) { output.close(); }
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
		resultObject.bestFitnessValue = best_out;
		resultObject.X = X1;
		resultObject.allPosition = PXI.clone();
	}
	
	public double[] fix(double[] PxNet, double[] PwNet, double Rd) 
	{
		double[] stepS = new double[2];
		stepS[0]  = (PxNet[0] - PwNet[0]) *Rd;
        stepS[1] = (PxNet[1] - PwNet[1])*Rd;
        
		if (stepS[0] < 0)
			stepS[0] = Math.ceil(stepS[0]);
		else
			stepS[0] = Math.floor(stepS[0]);
		if (stepS[1] < 0)
			stepS[1] = Math.ceil(stepS[1]);
		else
			stepS[1] = Math.floor(stepS[1]);
		
		return stepS;
	}
	public double[] CalculateFitnessValue(int loopSize,String funcType) 
	{
		double[] fitnessValue = new double[loopSize];
		for (int k=0; k<loopSize; k++) 
		{
			fitnessValue[k] = SummationFormala(rawPolygon.get(k), funcType);
		}
		return fitnessValue;
	}
	
	public double CalculateFitnessValue_2(SimplePolygon inputNumber, int loopSize,String funcType) 
	{
		return SummationFormala(inputNumber, funcType); 
	}
	
	public double[][][] ReshapeMatrices(int m, int n,double[][] A)
	{
		double[][][] Y = new double[m][n][2];
		for (int i=0; i<m; i++)
		{
			for (int j=0; j<n; j++)
			{
				Y[j][i][0] = A[m*i+j][0];
				Y[j][i][1] = A[m*i+j][1];
			}
		}
		return Y;
	}
	
	public FitnessSequence[][] ReshapeMatrices1(int m, int n,FitnessSequence[] A)
	{
		FitnessSequence[][] Y = new FitnessSequence[m][n];
		for (int i=0; i<m; i++) 
		{
			for (int j=0; j<n; j++)
			{
				Y[j][i] = A[m*i+j];
			}
		}
		return Y;
	}
	
	public double[] SimpleBound(double[] s, double LowerBound, double UpperBound)
	{
		boolean[] checkLowerBound = new boolean[2];
		boolean[] checkUpperBound = new boolean[2];
		checkLowerBound[0] = s[0] < LowerBound; checkLowerBound[1] = s[1] < LowerBound;
		checkUpperBound[0] = s[0] > UpperBound; checkUpperBound[1] = s[1] > UpperBound;
		
		if (checkLowerBound[0] && checkLowerBound[1])
			//System.out.println("Solution is out of Lower bound!");
		if (checkUpperBound[0] && checkUpperBound[1])
			//System.out.println("Solution is out of Upper bound!");
			
		for (int c=0; c<checkLowerBound.length; c++) 
		{
			if (checkLowerBound[c])
				s[c] = LowerBound;
			if (checkUpperBound[c])
				s[c] = UpperBound;
		}
		return s;
	}

	// ===== Misc =====
	public double SummationFormala(SimplePolygon inputPolygon, String funcType) {
		double maxHeight = Double.NEGATIVE_INFINITY, minHeight = Double.POSITIVE_INFINITY;
		
		if (funcType.toLowerCase().equals("min-height")) 
		{
			for (int i=0; i<inputPolygon.numberOfCoordinate; i++) 
			{
				if (inputPolygon.getYCoordinate(i) < minHeight )
					minHeight = inputPolygon.getYCoordinate(i);
				if (inputPolygon.getYCoordinate(i) > maxHeight)
					maxHeight = inputPolygon.getYCoordinate(i);
			}
		}
		return maxHeight-minHeight; 
	}
	
	public double[][] QuickSort(double[] fitnessValue) 
	{
		int IDX=1, VALUE=0;
		double[][] tmpSorted = new double[2][fitnessValue.length];
		tmpSorted[VALUE] = fitnessValue.clone();
		for (int i=0; i<fitnessValue.length; i++)
			tmpSorted[IDX][i] = i;
		tmpSorted = quickSort(tmpSorted, 0, tmpSorted[VALUE].length-1);
		return tmpSorted;
	}
	
	public SimplePolygon CreateMikowskiPolygon(File polygonA, File polygonB, int[] inputSequence,String odinary_file_path) 
	{
		List<List<Edge>> outputDemo = null;
		SimplePolygon returnPolygon = null;
		List<List<Edge>> nfpActiveList = new ArrayList<>();
		int[] dummyArray = new int[4];
		try 
		{
			outputDemo = hellObj.GenerateMinkowskiNFP(polygonA, polygonB);
			GenerateTemporaryFile(outputDemo, odinary_file_path);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (NullPointerException e) 
		{
			if (outputDemo == null)
				System.out.println("");
		}
		returnPolygon = new SimplePolygon(new File(odinary_file_path));
		return returnPolygon;
	}
	
	public int[] CreateLeapingSequenceNormal (FitnessSequence PbInput, FitnessSequence PwInput, double Rd, double Smax) 
	{
		int[] outputSequence = new int [PbInput.sequenceNumber.length];
		
		for (int idxLeap=0; idxLeap<PbInput.sequenceNumber.length; idxLeap++) 
		{
			double tmpTerm = Rd * (PbInput.sequenceNumber[idxLeap]-PwInput.sequenceNumber[idxLeap]);
		    int tmpAgain   = (int) Math.round(PwInput.sequenceNumber[idxLeap] + Math.min(tmpTerm, Smax));
		    
		    if (tmpAgain < 0) { tmpAgain = 0; }
		    outputSequence[idxLeap] = tmpAgain; 
		}
		return outputSequence;
	}
	
	public int[] CreateLeapingSequence(LeapingMachine testObj, FitnessSequence PbInput, FitnessSequence PwInput, double Rd, double Smax) 
	{	
		int[] outputSequence = new int[PbInput.sequenceNumber.length];
        for (int idxLeap=0; idxLeap<PbInput.sequenceNumber.length; idxLeap++) 
        {
        	double tmpTerm = Rd * (PbInput.sequenceNumber[idxLeap]-PwInput.sequenceNumber[idxLeap]);
        	int tmpAgain   = (int) Math.round(PwInput.sequenceNumber[idxLeap] + Math.min(tmpTerm, Smax));
        	if (tmpAgain < 0) { tmpAgain = 0; }
        	outputSequence[idxLeap] = tmpAgain; 
        }
        
        testObj = new LeapingMachine(outputSequence.clone(), PbInput.sequenceNumber.clone(), PwInput.sequenceNumber.clone());
        testObj.SetZero();
        testObj.CheckRepeated();
        
        if (testObj.amountOfRepeated > 0) 
        {
        	testObj.CreatePermutationList();
        	testObj.permute(testObj.permuteList, 0);
        	testObj.ConvertStringPermToInteger();
        	testObj.FindEuclideanDistance();
        	outputSequence = testObj.checkedSequence.clone();
        }
		return outputSequence;
	}
	
	// ********************* For QuickSort part, Don't worry ******************//
	public double[][] quickSort(double arr[][], int left, int right) 
	{
		double[][] tmpArr = arr;
		int index = partition(tmpArr, left, right);
		
		if (left < index - 1) { quickSort(tmpArr, left, index - 1); }
		if (index < right) { quickSort(tmpArr, index, right); }
		return tmpArr;
	}
	
	public int partition(double arr[][], int left, int right) 
	{
		int i = left, j = right, IDX=1, VALUE=0;
		double tmpValue, tmpIdx;
		double pivot = arr[VALUE][(left + right) / 2];
		while (i <= j) 
		{
			while (arr[VALUE][i] < pivot) { i++; }
			while (arr[VALUE][j] > pivot) { j--; }
			
			if (i <= j)
			{
				tmpValue = arr[VALUE][i];
				tmpIdx = arr[IDX][i];
				arr[VALUE][i] = arr[VALUE][j];
				arr[IDX][i] = arr[IDX][j];
				arr[VALUE][j] = tmpValue;
				arr[IDX][j] = tmpIdx;
				i++;
				j--;
			}
		}
		return i;
	}
	// ************************************************************************//
	
	public void GenerateTemporaryFile(List<List<Edge>> outputDemo, String file_path) throws IOException 
	{
		int smallestNumberAllowed = 1;
		int sizeToCheck = (int)Double.NEGATIVE_INFINITY, indexToCheck=0;
		BufferedWriter output = null;
		String writeText = "";
		File temporaryFilePolygon = new File(file_path);
		
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
			writeText += ("0\n")+(sizeToCheck)+ "\n" + XCoordinate + " " + YCoordinate + "\n";
				
			for (int j=0; j<sizeToCheck; j++) 
			{
				XCoordinate = outputDemo.get(indexToCheck).get(j).getEndPoint().getxCoord();
				YCoordinate = outputDemo.get(indexToCheck).get(j).getEndPoint().getyCoord();
				writeText += (XCoordinate) + " " + (YCoordinate) + "\n";
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
	
	public void GenerateTemporaryRotateFile(List<Double> XCoordinate,List<Double> YCoordinate, String file_path) throws IOException 
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
	
	public SimplePolygon RotateCoordinate(SimplePolygon staticPolygons, float degree) {
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
	
	public int FindMinimumFitness (double[] fitnessValueStep) 
	{
		int minimumIndex = 9999;
		double minimumValue = Double.POSITIVE_INFINITY;
		for (int mIdx=0; mIdx<fitnessValueStep.length; mIdx++) 
		{
			if (fitnessValueStep[mIdx] <= minimumValue && fitnessValueStep[mIdx]!=0) 
			{
				minimumValue = fitnessValueStep[mIdx];
				minimumIndex = mIdx;
			}
		}
		return minimumIndex;
	}
}