package experiment.small;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.math3.stat.regression.MillerUpdatingRegression;
import org.apache.commons.math3.stat.regression.MillerUpdatingRegression_bug1;
import org.apache.commons.math3.stat.regression.MillerUpdatingRegression_bug2;
import org.apache.commons.math3.stat.regression.RegressionResults;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

public class Short_MillerUpdatingRegressionMyTest {
	public static Random r = new Random(177756);
	
	/**
	 * @param args
	 */
	public static int testOut(double[] truth, double[] result) {
		double tolerance = 1E-12;

		if (truth.length != result.length) {
//			System.out
//					.println("two inputs' dimension is mismatch, the result length is: "
//							+ result.length+" the truth length is"+truth.length);
			return 1;
		}

		double diff = 0;
		for (int i = 0; i < truth.length; i++) {

			diff = Math.abs(truth[i] - result[i]);
			
			if (diff > tolerance) {
				return 1;
			}

		}
		return 0;
	}
	public static double createSmallNumber() {
		double a = r.nextDouble();
		double b = r.nextDouble();
		double c=r.nextDouble();
		double number= (a - 0.5)*Math.pow(10,c*5) / Math.pow(10, b * 20);
		//System.out.println(number);
		return number;
		//return a;
	}
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			int max_len = 10;
			//int iter = 1000;

			Profiler.visitNewTest(-1);
			// TODO Auto-generated method stub

			// int count = 0;
			// double result = 0, truth = 0;
			int yout = 0;
			int count2 = 0;
			String filedir = "TestMillerUpdatingRegression/out";
			String filename = filedir + "/" + "out.txt";
			BufferedWriter out;
			out = new BufferedWriter(new FileWriter(filename, false));
			int count = 0;
			double diff=0;
			for (int i = 0; i < 1500; i++) {
				Profiler.visitNewTest(i);
				int varnum=1+r.nextInt(max_len-1);
				int flag=r.nextInt(2);
				flag=0;
			//	System.out.println("flag "+flag);
				yout=Short_MillerUpdatingRegressionMyTest.testReg(varnum,flag);
//				yout=testOut(diff);
				if (yout==1){count++;}
				out.write(i+" "+yout);
				out.write("\n");
				out.flush();
				//transformer_bug.log.setTestId(i);ffffff
				count2++;
			}
			Profiler.stopProfiling();
			System.out.println("count is :" + count + "  count2 is" + count2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
		
		
		
		
		//Short_MillerUpdatingRegressionMyTest.testRegRepeat();

	}
	
//	public static void testRegRepeat(){
//		int repeat=1000;
//		int max_len=10;
//		
//		 // number of variable no less than one
//		
//		for(int iter=0;iter<repeat;iter++){
//		//	System.out.print(iter+"");
//		//	int varnum=1+(int)(Math.random()*(max_len-1));
//			
//		}
//	}
	
	public static int testReg(int varnum,int flag){
		double[] truth,result;
		//Random rand=new Random();
		MillerUpdatingRegression reg=new MillerUpdatingRegression(varnum,true);
		
		MillerUpdatingRegression_bug2 reg_bug= new MillerUpdatingRegression_bug2(varnum,true);
		
		int samplenum=varnum+20+(int)(r.nextDouble()*(varnum+1000-1)); // number of observations
		double[] coef=new double[varnum]; // randomly generate regression coefficients
		for(int i=0;i<varnum;i++){
			coef[i]=r.nextDouble();
		}
		

		double[][] x=new double[samplenum-1][];
		double[] y=new double[samplenum-1];
		for(int i=0;i<samplenum-1;i++){
			x[i]=new double[varnum];
			double xsum=0;
			for(int j=0;j<varnum;j++){
				x[i][j]=r.nextDouble(); // randomly generate data
				xsum=xsum+coef[j]*x[i][j];
			}
			y[i]=xsum+r.nextDouble(); // generate labels using data, coefficients and random noise
		}
	

		///////////////function 1: add data/////////////////////////////
		
		reg.addObservations(x, y);
		reg_bug.addObservations(x, y);
//		///////////////function 2: add data/////////////////////////////
//		reg.addObservation(xAdd, yAdd);
		
		//////////////function 3: regression////////////////////////////
	//	RegressionResults res=reg.regress(); // regression
		
		int regssornum=1+(int)(r.nextDouble()*(varnum-1));
		//////////////function 4: regression with partial variables//////////////
		RegressionResults res1=reg.regress(regssornum); // regression with the first few variables
		RegressionResults res1_bug=reg_bug.regress(regssornum);
		
		int[] regssors=new int[regssornum];
		for(int i=0;i<regssornum;i++){
			regssors[i]=i;
		}
		/////////////function 5: regression with partial variables///////////////
		RegressionResults res2=reg.regress(regssors); // regression with chosen variables
		RegressionResults res2_bug=reg_bug.regress(regssors);
		if(flag==1){
		truth=res1.getParameterEstimates();
		result=res1_bug.getParameterEstimates();

	
		}
		else{
			truth=res2.getParameterEstimates();
			result=res2_bug.getParameterEstimates();
			if (truth.length != result.length) {
				System.out
						.println("two inputs' dimension is mismatch, the result length is: "
								+ result.length+" the truth length is"+truth.length);
				
			}
		}
		/////////////function 6: get partial correlation/////////////////////////
		double[] corr=reg.getPartialCorrelations(regssornum); // get partial correlations
		
		/////////////function 7: get diagonals of hat matrix//////////////////////
//		for(int i=0;i<x.length;i++){
//			double hat=reg.getDiagonalOfHatMatrix(x[i]);
//		}
		
		
//		
//		System.out.println("obs: "+reg.getN()+"; paras: "+varnum+", "+res1.getNumberOfParameters()
//				+", "+res2.getNumberOfParameters()+"; corr length: "+corr.length+"; ");
		
		int diff=testOut(truth,result);
		recordTruth(truth);
		recordResult(result);
		return diff;
	}
	public static void recordTruth(double []truth){
		String filedir = "TestMillerUpdatingRegression/out";
		String filename = filedir + "/" + "truth.txt";
		try {
			BufferedWriter writer= new BufferedWriter(new FileWriter(filename, true));
			for (int i=0;i<truth.length;i++){
				writer.write(truth[i]+" ");
				writer.flush();
			}
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void recordResult(double []result){
		
		String filedir = "TestMillerUpdatingRegression/out";
		String filename = filedir + "/" + "result.txt";
		try {
			BufferedWriter writer= new BufferedWriter(new FileWriter(filename, true));
			for (int i=0;i<result.length;i++){
				writer.write(result[i]+" ");
				writer.flush();
			}
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("exception");
			e.printStackTrace();
		}
		
	}
}
	//public void generateData(int varnum){}
