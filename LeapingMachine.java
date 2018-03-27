import java.util.ArrayList;
import java.util.List;

public class LeapingMachine {
	public int[] pbSequence;
	public int[] pwSequence;
	public int[][] boundaryLookingTable;
	public int[] uncheckedSequence;
	public int[] checkedSequence;
	public int[] countingArray;
	public int amountOfRepeated, amountOfMissingValue;
	
	public List<Integer> permuteList;
	public List<List<Integer>> indexCheckedSequence;
	public List<List<Integer>> allPermutationList;
	public List<String> allPermutationString;
	
	public LeapingMachine(int[] inputArray, int[] Pb, int[] Pw) {
		pbSequence = Pb.clone();
		pwSequence = Pw.clone();
		amountOfRepeated     = 0;
		amountOfMissingValue = 0;
		permuteList = new ArrayList<>();
		uncheckedSequence    = inputArray.clone();
		checkedSequence      = new int[uncheckedSequence.length];
		allPermutationString = new ArrayList<>();
		boundaryLookingTable = new int[2][uncheckedSequence.length];
		countingArray        = new int[uncheckedSequence.length];
		allPermutationList   = new ArrayList<>();
		indexCheckedSequence = new ArrayList<List<Integer>>(uncheckedSequence.length);
		for (int i=0; i<uncheckedSequence.length; i++)
			indexCheckedSequence.add(new ArrayList<>());
	}
	
	public void CheckRepeated() {
		for (int i=0; i<uncheckedSequence.length; i++) {
			countingArray[uncheckedSequence[i]]++;
			indexCheckedSequence.get(uncheckedSequence[i]).add(i);
			if (countingArray[uncheckedSequence[i]] > 1) {
				amountOfRepeated++;
			}
		}
		for (int i=0; i<countingArray.length; i++) {
			if (countingArray[i]==0) {
				amountOfMissingValue++;
			}
		}
	}
	
	public void CreatePermutationList() {
		permuteList = new ArrayList<>();
		for (int i=0; i<indexCheckedSequence.size(); i++) {
			if (pbSequence[i] >= pwSequence[i]) {
				boundaryLookingTable[0][i] = pbSequence[i];
				boundaryLookingTable[1][i] = pwSequence[i];
			}
			else if(pbSequence[i] < pwSequence[i]) {
				boundaryLookingTable[0][i] = pwSequence[i];
				boundaryLookingTable[1][i] = pbSequence[i];
			}
			
			if (indexCheckedSequence.get(i).size() != 0) {
				if (indexCheckedSequence.get(i).size() > 1) {
					permuteList.add(i);
				}
			}
			else {
				permuteList.add(i);
			}
		}
	}
	
	public void ConvertStringPermToInteger() {
		String temporaryString = null;
		for (int i=0; i<allPermutationString.size(); i++) {
			temporaryString = allPermutationString.get(i).replaceAll("\\D+","");
			List<Integer> temporaryList = new ArrayList<>();
			int counter=0;
			for (int j=0; j<temporaryString.length(); j++) {
				temporaryList.add(Character.getNumericValue(temporaryString.charAt(counter++)));
			}
			allPermutationList.add(temporaryList);
		}
	}
	
	public void FindEuclideanDistance() {
		int[][] allPossibleLeaping = new int[allPermutationList.size()][uncheckedSequence.length];
		double[] allDistance = new double[allPermutationList.size()];
		double sum = 0.0;
		int counter = 0;
		int minimumIdx = 0;
		double minimumDistanceValue = 10000;
		
		for (int i=0; i<allPermutationList.size(); i++) {
			allPossibleLeaping[i] = uncheckedSequence.clone();
			
			for (int j=0; j<indexCheckedSequence.size(); j++) {
				if (indexCheckedSequence.get(j).size() > 1) {
					for (int k=0; k<indexCheckedSequence.get(j).size(); k++) {
						allPossibleLeaping[i][indexCheckedSequence.get(j).get(k)] = allPermutationList.get(i).get(counter++);
					}
				}
			}
			counter=0;
		}
		
		if (allPossibleLeaping.length > 0) {
			for (int j=0; j<allPossibleLeaping.length; j++) {
				for (int k=0; k<allPossibleLeaping[j].length; k++) {
					sum += Math.pow((allPossibleLeaping[j][k]-uncheckedSequence[k]), 2.0); 
				}
				allDistance[j] = Math.sqrt(sum);
				if (allDistance[j] <= minimumDistanceValue) {
					minimumDistanceValue = allDistance[j];
					minimumIdx = j;
				}
			}
			checkedSequence = allPossibleLeaping[minimumIdx].clone();
			for (int b=0; b<checkedSequence.length; b++)
				System.out.print(checkedSequence[b] + " ");
				System.out.println();
		}
		else {
			checkedSequence = pwSequence.clone();
			for (int b=0; b<checkedSequence.length; b++)
				System.out.print(checkedSequence[b] + " ");
				System.out.println();
		}
	}
	
	public void permute(List<Integer> arr, int k){
        for(int i = k; i < arr.size(); i++){
            java.util.Collections.swap(arr, i, k);
            permute(arr, k+1);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size()-1) {
        	int counter=0, sum=0;
        	String resultString = null;
        	for (int j=0; j<indexCheckedSequence.size(); j++) {
        		
        		if (indexCheckedSequence.get(j).size() > 1) {
        			// --- This if statement trigged when that index is a repeated number ---
        			for (int t=0; t<indexCheckedSequence.get(j).size(); t++) {
        				if (arr.get(counter) >= boundaryLookingTable[1][indexCheckedSequence.get(j).get(t)] && arr.get(counter) <= boundaryLookingTable[0][indexCheckedSequence.get(j).get(t)]) {
        					sum++;
        				}
		        		counter++;
        			}
    			}
        	}
        	if (sum >= arr.size()) {
        		sum = 0; counter = 0;
        		resultString = arr.toString();
        		allPermutationString.add(resultString);
        	}
        }
    }
	// ----- Presentation's code part -----
	public void showPermutationList() {
		System.out.println("Permutation list : ");
		System.out.print("[ ");
		for (int i=0; i<permuteList.size(); i++) {
			System.out.print(permuteList.get(i) + " ");
		}
		System.out.println("] ");
	}
	public void showIndexSequence() {
		System.out.println("Index checked sequence : ");
		for (int i=0; i<indexCheckedSequence.size(); i++) {
			System.out.print("[ ");
			for (int j=0; j<indexCheckedSequence.get(i).size(); j++) {
				System.out.print(indexCheckedSequence.get(i).get(j)+" ");
			}
			System.out.print("] ");
		}
		System.out.println(" ");
	}
	public void SetZero() {
		for (int i=0; i<countingArray.length; i++) {
			countingArray[i] = 0;
		}
	}
}
