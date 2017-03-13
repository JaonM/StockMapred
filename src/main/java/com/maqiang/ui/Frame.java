package com.maqiang.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class Frame {

	private JFrame mFrame;
	private JList mList;
	
	public Frame() {
		init();
	}
	
	private void init() {
		mFrame=new JFrame("股票分析预测 MapReduce");
		mFrame.setSize(800, 480);
		JPanel mPanel=new JPanel();
		mPanel.setLayout(new BorderLayout());
		mFrame.getContentPane().add(mPanel);
		//设置列表框 
		mList=new JList(new String[]{"1","2","3"});
		mList.setAlignmentX(20);
		mList.setAlignmentY(20);
		mPanel.add("West",mList);

		JPanel mRightPanel=new JPanel();
		mRightPanel.setBackground(Color.RED);
		mPanel.add("Right",mRightPanel);
		mFrame.setVisible(true);
	}
	
	public static void main(String [] args) {
		Frame f=new Frame();

	}
}
