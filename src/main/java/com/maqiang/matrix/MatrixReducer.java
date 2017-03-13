package com.maqiang.matrix;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MatrixReducer extends Reducer<Text,Text,Text,Text> {

	private final String SPLIT=",";
	
	@Override
	public void reduce(Text key,Iterable<Text> values,Context context)throws 
		IOException,InterruptedException {
		Configuration conf=context.getConfiguration();
		int n=conf.getInt("n", 0);
		
		double [] a=new double[n];
		double [] b=new double[n];
		
		while(values.iterator().hasNext()) {
			String value=values.iterator().next().toString();
			String [] valueA=value.split(SPLIT);
			String source=valueA[0];
			if(source.equals("a")) {
				a[Integer.valueOf(valueA[1])-1]=Double.valueOf(valueA[2]);
			}else if(source.equals("b")) {
				//String [] k=key.toString().split(SPLIT);
				b[Integer.valueOf(valueA[1])-1]=Double.valueOf((valueA[2]));
			}
			
		}
//		for(int i=0;i<a.length;i++) {
//			System.out.print("a["+i+"]: "+a[i]+" b["+i+"]: "+b[i]);
//		}
		//vertex a b do dot-product
		double result=0;
		for(int i=0;i<n;i++) {
			result+=a[i]*b[i];
		}
		String [] ks=key.toString().split(SPLIT);
		String k=null;
		if(ks.length==2) {
			k=ks[0]+"\t"+ks[1];
		}
		if(result!=0)
			context.write(new Text(k), new Text(String.valueOf(result)));
	}
}
