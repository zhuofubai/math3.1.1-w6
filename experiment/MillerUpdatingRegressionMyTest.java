package experiment;

import java.util.Random;

import org.apache.commons.math3.stat.regression.MillerUpdatingRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;

public class MillerUpdatingRegressionMyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MillerUpdatingRegressionMyTest.testRegRepeat();

	}
	
	public static void testRegRepeat(){
		int repeat=1000;
		int max_len=200;
		
		 // number of variable no less than one
		
		for(int iter=0;iter<repeat;iter++){
			System.out.print(iter+"");
			int varnum=1+(int)(Math.random()*(max_len-1));
			MillerUpdatingRegressionMyTest.testReg(varnum);
		}
	}
	
	public static void testReg(int varnum){
		Random rand=new Random();
		int samplenum=varnum+20+(int)(Math.random()*(varnum+1000-1)); // number of observations
		double[] coef=new double[varnum]; // randomly generate regression coefficients
		for(int i=0;i<varnum;i++){
			coef[i]=rand.nextDouble();
		}
		
		MillerUpdatingRegression reg=new MillerUpdatingRegression(varnum,true);
		double[][] x=new double[samplenum-1][];
		double[] y=new double[samplenum-1];
		for(int i=0;i<samplenum-1;i++){
			x[i]=new double[varnum];
			double xsum=0;
			for(int j=0;j<varnum;j++){
				x[i][j]=rand.nextDouble(); // randomly generate data
				xsum=xsum+coef[j]*x[i][j];
			}
			y[i]=xsum+Math.random(); // generate labels using data, coefficients and random noise
		}
		///////////////function 1: add data/////////////////////////////
		reg.addObservations(x, y);
		
//		double[] xAdd=new double[varnum];
//		double yAdd=0;
//		for(int j=0;j<varnum;j++){
//			xAdd[j]=rand.nextDouble();
//			yAdd=yAdd+coef[j]*xAdd[j];
//		}
//		yAdd=yAdd+Math.random();
//		///////////////function 2: add data/////////////////////////////
//		reg.addObservation(xAdd, yAdd);
		
		//////////////function 3: regression////////////////////////////
		//RegressionResults res=reg.regress(); // regression
		
		int regssornum=1+(int)(Math.random()*(varnum-1));
		//////////////function 4: regression with partial variables//////////////
		RegressionResults res1=reg.regress(regssornum); // regression with the first few variables
		
		int[] regssors=new int[3];
		for(int i=0;i<regssornum;i++){
			regssors[i]=1+(int)(Math.random()*(regssornum-1));
		}
		/////////////function 5: regression with partial variables///////////////
		RegressionResults res2=reg.regress(regssors); // regression with chosen variables
		
		/////////////function 6: get partial correlation/////////////////////////
		double[] corr=reg.getPartialCorrelations(regssornum); // get partial correlations
		
		/////////////function 7: get diagonals of hat matrix//////////////////////
		for(int i=0;i<x.length;i++){
			double hat=reg.getDiagonalOfHatMatrix(x[i]);
		}
		
		
		
		System.out.println("obs: "+reg.getN()+"; paras: "+varnum+", "+res1.getNumberOfParameters()
				+", "+res2.getNumberOfParameters()+"; corr length: "+corr.length+"; ");
		
		
	}

}
