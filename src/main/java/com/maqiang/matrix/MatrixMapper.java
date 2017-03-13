package com.maqiang.matrix;
/*
 * matrix multi mapper
 */
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class MatrixMapper extends Mapper<LongWritable,Text,Text,Text>{

	private final String SPLIT="\t";
	private final String FILE_A="input_a";
	private final String FILE_B="input_b";
	
	private int m;			//A matrix row
	private int l;			//B matrix column
	
	@Override
	public void setup(Context context) throws IOException ,InterruptedException {
		Configuration conf=context.getConfiguration();
		m=conf.getInt("m", 0);
		l=conf.getInt("l", 0);
	}
	
	protected void map(LongWritable key,Text value,Context context) throws IOException,InterruptedException {
		String fileName=((FileSplit)context.getInputSplit()).getPath().getName();
		//System.out.println(fileName);
		if(fileName.equals(FILE_A)) {		//input source judge from file name
			String [] values=value.toString().split(SPLIT);
			if(values.length<3) return;
			
			String i=values[0];
			String j=values[1];
			String v=values[2];
			
			for(int k=1;k<=l;k++){
				//System.out.println(new Text(i+","+k).toString()+" "+ new Text("a,"+j+","+v).toString());
				context.write(new Text(i+","+k), new Text("a,"+j+","+v));
			}
		}else if(fileName.equals(FILE_B)) {
			String [] values=value.toString().split(SPLIT);
			if(values.length<3) return;
			
			String i=values[0];
			String j=values[1];
			String v=values[2];
			
			for(int k=1;k<=m;k++){
				//System.out.println(new Text(i+","+k).toString()+" "+ new Text("b,"+j+","+v).toString());
				context.write(new Text(k+","+j),new Text("b,"+i+","+v));
			}
		}
	}
}
