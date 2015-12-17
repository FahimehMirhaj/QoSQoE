import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// http://www.codejava.net/java-se/graphics/using-jfreechart-to-draw-xy-line-chart-with-xydataset
// http://sourceforge.net/projects/jfreechart/files/
public class XYLineChartExample {
 
    public static XYDataset getDataSet() {
    	XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Object 1");
        XYSeries series2 = new XYSeries("Object 2");
        XYSeries series3 = new XYSeries("Object 3");
     
        series1.add(3.0, 2.5);
        series1.add(4.2, 6.0);
        series1.add(1.0, 2.0);
        series1.add(2.0, 3.0);
        series1.add(3.5, 2.8);
     
        series2.add(2.0, 1.0);
        series2.add(2.5, 2.4);
        series2.add(3.9, 2.8);
        series2.add(3.2, 1.2);
        series2.add(4.6, 3.0);
     
        series3.add(4.3, 3.8);
        series3.add(2.5, 4.4);
        series3.add(3.8, 4.2);
        series3.add(4.5, 4.0);
        series3.add(1.2, 4.0);
        
     
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
     
        return dataset;
    }
    
    public static JFreeChart getChart() {
    	String chartTitle = "Objects Movement Chart";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";
     
        XYDataset dataset = getDataSet();
     
       // ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, dataset, orientation, legend, tooltips, urls)
        JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
                xAxisLabel, yAxisLabel, dataset);
        
        return chart;
    }
    
    public static void main(String[] args) {
    	
    	JFreeChart chart = getChart();
    	File imageFile = new File("XYLineChart2.png");
    	int width = 640;
    	int height = 480;
    	 
    	try {
    	    ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
    	} catch (IOException ex) {
    	    System.err.println(ex);
    	}
    }
}