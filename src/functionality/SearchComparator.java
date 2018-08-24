package functionality;

import java.util.Comparator;

public class SearchComparator implements Comparator<AdvancedFile>{
// the search algorithm // based on Smith-Waterman algorithm
	
	private String searchTerm = ""; // the string to search for
	private int match = 1; // score for match
	private int mismatch = -1; // penalty for mismatch
	private int gapInit = -1; // penalty for gap
//	private int gapExt = -2; // penalty for gap extension

	@Override
	public int compare(AdvancedFile a, AdvancedFile b) {
		char[] charA = a.getName().toLowerCase().toCharArray();
		char[] charB = b.getName().toLowerCase().toCharArray();
		char[] charSearch = searchTerm.toLowerCase().toCharArray();
		int scoreA = this.calculateScore(charA, charSearch);
		int scoreB = this.calculateScore(charB, charSearch);
//		System.out.println("Search string: " + searchTerm);
//		System.out.println(a.toString() + " score: " + scoreA);
//		System.out.println(b.toString() + " score: " + scoreB);
//		System.out.println(b.toString() + "Diff score: " + (scoreA - scoreB));
		if (scoreA != scoreB) {
			return scoreB - scoreA;
		} else { // sort alphabetically in the second instance
			return a.compareTo(b);
		}
	}
	
	private int calculateScore(char[] a, char[] b) {
		if (a != null && b != null && a.length > 0 && b.length > 0) {
			int[][][] matrix = new int[a.length][b.length][4]; // matrix of the first and the second string // 3rd dimension: 0: value / 1: i traceback / 2: n traceback / 3: gap(0), mismatch(1), match(2) 
			int[] values = new int[4]; // the calculated values per matrix field based on the Smith-Waterman algorithm
			values[0] = 0; // zero as smallest possible number for local alignment instead of global
			int maxPosition = 0; // the traceback position
			int[] startPoint = {0, 0}; // start point for trace back
			for (int i = 0; i < a.length; i++) {
				for (int n = 0; n < b.length; n++) {
					if ((i - 1) >= 0) {
						values[1] = matrix[i-1][n][0] + this.gapInit;
					} else {
						values[1] = 0;
					}
					if ((n - 1) >= 0) {
						values[2] = matrix[i][n-1][0] + this.gapInit;
					} else {
						values[2] = 0;
					}
					if ((i - 1) >= 0 && (n - 1) >= 0) {
						if (a[i] != b[n]) {
							values[3] = matrix[i-1][n-1][0] + this.mismatch;
						} else {
							values[3] = matrix[i-1][n-1][0] + this.match;
						}
					} else {
						if (a[i] != b[n]) {
							values[3] = this.mismatch;
						} else {
							values[3] = this.match;
						}
					}
					matrix[i][n][0] = getMaximum(values); // set the value of the matrix field
					maxPosition = getMaximumIndex(values, true); // determine traceback field
					if (maxPosition == 1) {
						matrix[i][n][1] = i - 1;
						matrix[i][n][2] = n;
						matrix[i][n][3] = 0; // gap
					} else if (maxPosition == 2) {
						matrix[i][n][1] = i;
						matrix[i][n][2] = n - 1;
						matrix[i][n][3] = 0; // gap
					} else {
						matrix[i][n][1] = i - 1;
						matrix[i][n][2] = n - 1;
						if (a[i] != b[n]) {
							matrix[i][n][3] = 1; // mismatch
						} else {
							matrix[i][n][3] = 2; // match
						}
					}
					if (matrix[i][n][0] >= matrix[startPoint[0]][startPoint[1]][0]) { // set new start point on matrix maximum if detected
						startPoint[0] = i;
						startPoint[1] = n;
					}
				}
			}
			// trace back scoring
			int score = 0; // the quality score
			int startI = 0; // helper variable for start point switching
			int startN = 0; // helper variable for start point switching
			while (startPoint[0] >= 0 && startPoint[1] >= 0) {
				score += this.getFieldScore(matrix, startPoint);
				startI = matrix[startPoint[0]][startPoint[1]][1];
				startN = matrix[startPoint[0]][startPoint[1]][2];
				startPoint[0] = startI;
				startPoint[1] = startN;
			}
			return score;
		} else {
			return 0;
		}
	}
	
	private int getFieldScore(int[][][] matrix, int[] point) {
		if (matrix != null && point != null) {
			if (matrix[point[0]][point[1]][3] == 0) {
				return this.gapInit;
			} else if (matrix[point[0]][point[1]][3] == 1) {
				return this.mismatch;
			} else {
				return this.match;
			}
		} else {
			return 0;
		}
		
	}
	
	// returns the maximum of an integer array
	private static int getMaximum(int[] array) {
		if (array != null) {
			int max = 0;
			for (int i = 0; i < array.length; i++) {
				if (i != 0) {
					if (array[i] > max) {
						max = array[i];
					}
				} else { // first pass
					max = array[i];
				}
			}
			return max;
		} else {
			return 0;
		}
	}
	
	// returns the index of the maximum of an integer array
	private static int getMaximumIndex(int[] array, boolean skipFirst) {
		if (array != null) {
			int maxIndex = 0;
			if (!skipFirst) {
				for (int i = 0; i < array.length; i++) {
					if (array[i] > array[maxIndex]) {
						maxIndex = i;
					}
				}
			} else if (array.length > 1){
				for (int i = 1; i < array.length; i++) {
					if (array[i] > array[maxIndex]) {
						maxIndex = i;
					}
				}
			}
			return maxIndex;
		} else {
			return 0;
		}
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
}
