package com.maqiang.analyze;

import com.maqiang.model.Element;
import com.maqiang.model.Stock;

public class Predict {

	public static boolean predict(Stock stock) {
		if(stock.getArgs()==null||stock.getPredictList().size()==0)
			return false;
		for(Element elem:stock.getPredictList()) {
			double value=stock.getArgs()[0]+stock.getArgs()[1]*elem.getOpen()+
					stock.getArgs()[2]*elem.getHigh()+stock.getArgs()[3]*elem.getLow()+
					stock.getArgs()[4]*elem.getVolume();
			elem.setPredictClose(value);
		}
		return true;
	}
}
