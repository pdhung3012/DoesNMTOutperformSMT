package studentScorePrediction;

import java.util.Arrays;

public class CS319HW5 {

	public static double median(double[] d) {
		double median = Double.NaN;
		if (d != null & d.length > 0) {
			System.out.println(d.length);
			if (d.length == 1) {
				median = d[0];
			} else {
				Arrays.sort(d); // sorted ascending
				int mid = d.length / 2;
				if (d.length % 2 != 0) {
					median = d[mid];
				} else {
					median = (d[mid - 1] + d[mid]) / 2;
				}
			}
		}
		return median;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] d=null;
		System.out.println(median(d));
	}

}
