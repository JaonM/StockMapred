package com.maqiang.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.maqiang.analyze.Analyze;
import com.maqiang.analyze.Predict;
import com.maqiang.model.Element;
import com.maqiang.model.Stock;

public class UI extends JFrame {

	private JPanel contentPane;
	private Configuration conf;
	
	private List<Stock> stockList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Configuration conf=new Configuration();
					UI frame = new UI(conf);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI(final Configuration conf) {
		this.conf=conf;
		this.setTitle("股票分析与预测");
		
		stockList=getStock(conf);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 793, 393);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		final JLabel mStatus = new JLabel("状态");
		mStatus.setBounds(639, 12, 59, 18);
		panel.add(mStatus);
		
		JButton btnAnalyze = new JButton("分析");
		btnAnalyze.setFont(new Font("Dialog", Font.BOLD, 16));
		btnAnalyze.setBounds(180, 7, 102, 28);
		panel.add(btnAnalyze);
		
		JButton btnPredict = new JButton("预测");
		btnPredict.setFont(new Font("Dialog", Font.BOLD, 16));
		btnPredict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPredict.setBounds(338, 7, 102, 28);
		panel.add(btnPredict);
		
		final JLabel lbAveError = new JLabel("平均误差");
		lbAveError.setBounds(639, 61, 59, 18);
		panel.add(lbAveError);
		
		final JLabel lbAveErrorResult = new JLabel("0.5");
		lbAveErrorResult.setBounds(639, 91, 59, 18);
		panel.add(lbAveErrorResult);
		
		JLabel lbPercentage = new JLabel("涨跌正确率");
		lbPercentage.setBounds(639, 134, 73, 18);
		panel.add(lbPercentage);
		
		final JLabel lbPercentageResult = new JLabel("80%");
		lbPercentageResult.setBounds(639, 182, 59, 18);
		panel.add(lbPercentageResult);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 7, 146, 334);
		panel.add(scrollPane);
		
		final JList<Stock> list = new JList<Stock>();
		scrollPane.setViewportView(list);
		list.setFont(new Font("Dialog", Font.BOLD, 16));
		
		list.setModel(new AbstractListModel() {
//			String[] values = new String[] {"sad,asdasdasd", "sdasd", "adasd", "asdad"};
			public int getSize() {
				return stockList.size();
			}
			public Object getElementAt(int index) {
//				return stockList.get(index).getCode()+" "+stockList.get(index).getName();
				return stockList.get(index);
			}
		});
		list.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(180, 47, 394, 285);
		panel.add(scrollPane_1);
		
		final JTextArea textPredict = new JTextArea();
		scrollPane_1.setViewportView(textPredict);
		
		//注册事件
		btnAnalyze.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mStatus.setText("分析中");
				boolean result=Analyze.analyze(list.getSelectedValue(), conf);
				if(result)
					mStatus.setText("分析完成");
				else
					mStatus.setText("分析失败");
			}
			
		});
		
		btnPredict.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				StringBuffer br=new StringBuffer();
				Predict.predict(list.getSelectedValue());
				for(Element elem:list.getSelectedValue().getPredictList()) {
					br.append(elem.toString()+"\n");
				}
				textPredict.setText(br.toString());
				lbAveErrorResult.setText(averError(list.getSelectedValue())+"");
				lbPercentageResult.setText(percentage(list.getSelectedValue())+"");
			}
			
		});
	}
	
	private List<Stock> getStock(Configuration conf) {
		String path="hdfs://master:9000/user/stock/data";
		List<Stock> stockList=new ArrayList<Stock>();
		try {
			FileSystem fs=FileSystem.get(URI.create(path),conf);
			FileStatus [] status=fs.listStatus(new Path(path));
			for(FileStatus file:status) {
				String v=file.getPath().toString().substring(35);
				String [] values=v.split(",");
				Stock stock=new Stock(values[0],values[1]);
				stockList.add(stock);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return stockList;
	}
	
	//求平均误差
	private double averError(Stock stock) {
		double result=0;
		for(Element elem:stock.getPredictList()) {
			result+=elem.getPredictClose()-elem.getClose();
		}
		return result/stock.getPredictList().size();
	}
	
	private double percentage(Stock stock) {
		double result=0;
		for(Element elem:stock.getPredictList()) {
			if((elem.getClose()>elem.getOpen()&&elem.getPredictClose()>elem.getOpen())||
					(elem.getClose()<elem.getOpen()&&elem.getPredictClose()<elem.getOpen())){
				result++;
			}
		}
		return result/stock.getPredictList().size();
	}
}
