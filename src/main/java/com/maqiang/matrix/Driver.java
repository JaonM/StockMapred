package com.maqiang.matrix;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Driver {


	public static boolean matrixMulti(double [][]x1,double [][]x2,Configuration conf)throws IOException,ClassNotFoundException,
		InterruptedException{
		
		String path="hdfs://master:9000/user/stock/output";
		FileSystem fs=FileSystem.get(URI.create(path),conf);
		if(fs.exists(new Path(path))) {
			fs.delete(new Path(path), true);
		}
		
		conf.setInt("m", Integer.valueOf(x1.length));
		conf.setInt("n", Integer.valueOf(x2.length));
		conf.setInt("l", Integer.valueOf(x2[0].length));
		
		Job job=new Job(conf,"matrix_multi");
		
		job.setJarByClass(Driver.class);
		job.setMapperClass(MatrixMapper.class);
		job.setReducerClass(MatrixReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/user/stock/input"));
		FileOutputFormat.setOutputPath(job,new Path("hdfs://master:9000/user/stock/output"));
		
		return job.waitForCompletion(true);
	}
}
