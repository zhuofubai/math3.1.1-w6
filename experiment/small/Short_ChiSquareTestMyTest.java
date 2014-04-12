package experiment.small;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.ChiSquareTest_bug2;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

public class Short_ChiSquareTestMyTest {
	public static Random r = new Random(177756);
	public static int upper = 999;
	static double tolerance=1E-12;

	/**
	 * @param args
	 */
	public static int testOut(double[] truth, double[] result) {
		double tolerance = 1E-12;

		if (truth.length != result.length) {
			System.out
					.println("two inputs' dimension is mismatch, the result length is: "
							+ result.length);
			return 1;
		}

		double diff = 0;
		for (int i = 0; i < truth.length; i++) {

			diff = Math.abs(truth[i] - result[i]);
			if (diff > tolerance) {
				
				System.out.println(i);
				return 1;
			}

		}
		return 0;
	}
    public static double getDiff(double[] truth, double[] result){
    	double diff=0;
    	
    	for(int i=0;i<truth.length;i++){
    		diff = Math.abs(truth[i] - result[i]);
    		if(diff>tolerance){
    			return diff;
    		}
    	}
    	return 0;
    }
    public static double createSmallNumber() {
		double a = r.nextDouble();
		double b = r.nextDouble();
		double c=r.nextDouble();
		double number= (a - 0.5)*Math.pow(10,c*5) / Math.pow(10, b * 5);
		//System.out.println(number);
		return number;
		//return a;
	}
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			int max_len = 100;
			// int iter = 1000;

			Profiler.visitNewTest(-1);
			// TODO Auto-generated method stub

			int yout = 0;
			int count2 = 0;
			String filedir = "TestChiSquare/out";
			String filename = filedir + "/" + "out.txt";
			String truthFile = filedir + "/" + "truth.txt";
			String resultFile= filedir + "/" + "result.txt";
			String diffFile=filedir + "/" + "result.txt";
			BufferedWriter out, Twriter, Rwriter,Dwriter;

			out = new BufferedWriter(new FileWriter(filename, false));
			Twriter = new BufferedWriter(new FileWriter(truthFile, false));
			Rwriter =  new BufferedWriter(new FileWriter(resultFile, false));
			Dwriter =  new BufferedWriter(new FileWriter(diffFile, false));
			int count = 0;

			for (int i = 0; i < 100; i++) {
				Profiler.visitNewTest(i);
				int length = 50 + r.nextInt(max_len - 1);

				
				double[] exp = generateExp(length);
				long[] obs = generateLongArray(exp);
				long[] obs2 = generateLongArray(exp);
				long[][] counts = generateCounts(length);

				double[] truth = testChiSquare(obs, exp, obs2, counts);
				double[] result = testChiSquare_bug(obs, exp, obs2, counts);
				yout = testOut(truth, result);
				
				double diff=getDiff(truth,result);
				if (yout == 1) {
					count++;
				}
				out.write(i + " " + yout);
				out.write("\n");
				out.flush();
				
				Twriter.write(truth + " ");
				Twriter.write("\n");
				Twriter.flush();
				
				Rwriter.write(result + " ");
				Rwriter.write("\n");
				Rwriter.flush();
				
				out.write(i + " " + yout);
				out.write("\n");
				out.flush();
				
				Dwriter.write(diff +" ");
				Dwriter.write("\n");
				out.flush();
				// transformer_bug.log.setTestId(i);ffffff
				count2++;
			}
			Profiler.stopProfiling();
			System.out.println("count is :" + count + "  count2 is" + count2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}

	public static double[] testChiSquare(long[] obs, double[] exp, long[] obs2,
			long[][] counts) {
		ChiSquareTest chi = new ChiSquareTest();

		double pval1 = chi.chiSquareTest(exp, obs);
//		boolean rej1 = chi.chiSquareTest(exp, obs, 0.5);
		double pval2 = chi.chiSquareTest(counts);
//		boolean rej2 = chi.chiSquareTest(counts, 0.5);
		double pval3 = chi.chiSquareTestDataSetsComparison(obs, obs2);
	//	boolean rej3 = chi.chiSquareTestDataSetsComparison(obs, obs2, 0.5);
		double[] truth = { pval1, pval2, pval3 };

		// System.out.println(pval1);
		return truth;
	}

	public static double[] testChiSquare_bug(long[] obs, double[] exp,
			long[] obs2, long[][] counts) {
		ChiSquareTest_bug2 chi = new ChiSquareTest_bug2();

		double pval1 = chi.chiSquareTest(exp, obs);
	//	boolean rej1 = chi.chiSquareTest(exp, obs, 0.5);
		double pval2 = chi.chiSquareTest(counts);
		//boolean rej2 = chi.chiSquareTest(counts, 0.5);
		double pval3 = chi.chiSquareTestDataSetsComparison(obs, obs2);
		//boolean rej3 = chi.chiSquareTestDataSetsComparison(obs, obs2, 0.5);
		double[] result = { pval1, pval2, pval3 };

		// System.out.println(pval1);
		return result;
	}

	public static double[] generateExp(int n) {
		double[] a = new double[n];
		for (int i = 0; i < n; i++) {
			a[i] = Math.abs(r.nextGaussian()) * (upper - 1) + 1;
		}
		return a;
	}

	public static long[] generateLongArray(int n) {
		long[] a = new long[n];
		for (int i = 0; i < n; i++) {
			double b=r.nextDouble();
			long c=(long)b;
			a[i] = (long) (Math.abs(r.nextGaussian()) * (upper - 1) + 1);

		}
		return a;
	}
	public static long[] generateLongArray(double[]e){
		int m=r.nextInt(50);
		long[] a=new long[e.length];
		for(int i=0;i<e.length;i++){
			a[i]=(long)(e[i]+(r.nextDouble()*m));
		}
		return a;
	}
	public static long[][] generateCounts(int length) {
		long[][] counts = new long[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				counts[i][j] = (long) (Math.abs(r.nextGaussian()) * (upper - 1) + 1);
			}
		}
		return counts;
	}
}
