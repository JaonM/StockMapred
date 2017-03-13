package com.maqiang.analyze;

import org.apache.hadoop.conf.Configuration;

import com.maqiang.matrix.Driver;
import com.maqiang.model.Element;
import com.maqiang.model.Stock;
import com.maqiang.preprocess.PreProcess;

public class Analyze {

	public static boolean analyze(Stock stock,Configuration conf) {

		PreProcess.fileToMatrix(stock, conf);
		boolean result=true;
		try {
			//第一次MapReduce
			double [][] x=stock.getAnalyzeX();
			double [][] y=PreProcess.matrixTranspose(x);
		
			System.out.println(x.length);
			PreProcess.matrixPreInput(y, "input_a", conf);
			PreProcess.matrixPreInput(x, "input_b", conf);
			boolean mapred1=Driver.matrixMulti(y, x, conf);
			if(!mapred1)
				return false;
			//第二次MapReduce
			x=PreProcess.reduceToMatrix(conf);
			x=PreProcess.matrixInverse(x);

//			PreProcess.matrixPreInput(y, "input_b", conf);
//			PreProcess.matrixPreInput(x, "input_a", conf);
			double [] t=stock.getAnalyzeY();
			System.out.println(t.length);
			PreProcess.matrixPreInput(y, "input_a", conf);
			PreProcess.matrixPreInput(t, "input_b", conf);
			double [][]t1=new double [t.length][1];
			for(int i=0;i<t1.length;i++) {
				t1[i][0]=t[i];
			}
			boolean mapred2=Driver.matrixMulti(y, t1, conf);
			if(!mapred2)
				return false;
			//第三次MapReduce
			t1=PreProcess.reduceToMatrix(conf);

			PreProcess.matrixPreInput(x, "input_a", conf);
			PreProcess.matrixPreInput(t1, "input_b", conf);

			boolean mapred3=Driver.matrixMulti(x, t1, conf);
			if(!mapred3)
				return false;
			double [][] arg=PreProcess.reduceToMatrix(conf);
			double [] args=new double[arg.length];

			for(int i=0;i<arg.length;i++) {
				args[i]=arg[i][0];
			}
			stock.setArgs(args);

		}catch(Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	public static void main(String [] args) {
		Stock stock=new Stock("000402","金融街");
		Configuration conf=new Configuration();
		System.out.println(analyze(stock,conf));
		for(int i=0;i<stock.getArgs().length;i++) {
			System.out.print(stock.getArgs()[i]+" ");
		}
		System.out.println();
		Predict.predict(stock);
		for(Element elem:stock.getPredictList()) {
			System.out.println(elem);
		}
		for(Element elem:stock.getAnalyzeList()) {
			System.out.println(elem);
		}
	}
}
