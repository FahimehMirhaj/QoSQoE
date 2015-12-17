// The code which takes the experiment result of Ayon and produces the plots: ThroughtpuPLTPlot, delayPLTPlot and QoSPLTPlot

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.general.DefaultHeatMapDataset;
import org.jfree.data.general.HeatMapDataset;
import org.jfree.data.general.HeatMapUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PlotData {
	public static void main(String[] args) {
		Map<Experiment_Double, Double> experiments = new HashMap<Experiment_Double, Double>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/Users/FM/Desktop/experiment_data.txt"));
			String line = br.readLine();

		    while (line != null) {
		        String[] lineComponents = line.split(" ");
		        double throughput = Double.parseDouble(lineComponents[0]);
		        double delay = Double.parseDouble(lineComponents[1]);
		        double loss = Double.parseDouble(lineComponents[2]);
		        
		        Experiment_Double experiment_double = new Experiment_Double();
		        experiment_double.setDelay(delay);
		        experiment_double.setLoss(loss);
		        experiment_double.setThroughput(throughput);
		       
		        double plt = Double.parseDouble(lineComponents[3]);
		        experiments.put(experiment_double, plt);
		        
		        line = br.readLine();
		    }
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		finally {
		    try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String directory = "/Users/FM/Desktop/ExpPlots/AyonsData";
		
		drawThroughtpuPLTPlot(directory, experiments);
		drawDelayPLTPlot(directory, experiments);
		drawQoSPLTPlot(directory, experiments);
		
//		drawHeatMap(directory, experiments);
	}
	
	// END DRAWING HEAT MAP
	
	// DRAWING QOS PLOT
	public static XYDataset getQoSDataSet(Map<Experiment_Double, Double> experiments) {
	
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("experiment1");
		for (Map.Entry<Experiment_Double, Double> entry: experiments.entrySet()) {
			Experiment_Double experimentValues = entry.getKey();
			double x = experimentValues.getThroughput() / experimentValues.getDelay();
			double y = entry.getValue();
			
			series1.add(x, y);
		}
		dataset.addSeries(series1);
		return dataset;
	}
	


public static void drawQoSPLTPlot(String directory, Map<Experiment_Double, Double> experiments) {
	String fileName = "HeatMap.png";
 
	XYDataset dataset = getQoSDataSet(experiments);
	
    JFreeChart chart = ChartFactory.createXYStepAreaChart("HeatMap",
            "X", "Y", dataset, PlotOrientation.VERTICAL, true, false,
            false);
   
    File imageFile = new File(directory + "/" + fileName);
	int width = 640;
	int height = 480;
	 
	try {
	    ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
	} catch (IOException ex) {
	    System.err.println(ex);
	}
}
	
	
	
	// DRAWING DELAY PLOT
	public static XYDataset getDelayDataSet(Map<Experiment_Double, Double> experiments) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("experiment1");
		for (Map.Entry<Experiment_Double, Double> entry: experiments.entrySet()) {
			Experiment_Double experimentValues = entry.getKey();
			double x = experimentValues.getDelay();
			double y = entry.getValue();
			
			series1.add(x, y);
		}
		dataset.addSeries(series1);
		return dataset;
	}
	


public static void drawDelayPLTPlot(String directory, Map<Experiment_Double, Double> experiments) {
	String fileName = "delayPLTPlot.png";
	
	String chartTitle = "delayPLTPlot";
    String xAxisLabel = "delay";
    String yAxisLabel = "Page Load Time";
    
    XYDataset dataset = getDelayDataSet(experiments);
 
    JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
            xAxisLabel, yAxisLabel, dataset);
    
    
    File imageFile = new File(directory + "/" + fileName);
	int width = 640;
	int height = 480;
	 
	try {
	    ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
	} catch (IOException ex) {
	    System.err.println(ex);
	}
}
	
	
	
	// DRAWING THROUGPUT PLOT
		public static XYDataset getThroughputDataSet(Map<Experiment_Double, Double> experiments) {
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries series1 = new XYSeries("experiment1");
			for (Map.Entry<Experiment_Double, Double> entry: experiments.entrySet()) {
				Experiment_Double experimentValues = entry.getKey();
				double x = experimentValues.getThroughput();
				double y = entry.getValue();
				
				series1.add(x, y);
			}
			dataset.addSeries(series1);
			return dataset;
		}
		
	
	
	public static void drawThroughtpuPLTPlot(String directory, Map<Experiment_Double, Double> experiments) {
		String fileName = "throughputPLTPlot.png";
		
		String chartTitle = "throughputPLTPlot";
        String xAxisLabel = "Throughput";
        String yAxisLabel = "Page Load Time";
        
        XYDataset dataset = getThroughputDataSet(experiments);
     
        JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
                xAxisLabel, yAxisLabel, dataset);
        
        
        File imageFile = new File(directory + "/" + fileName);
    	int width = 640;
    	int height = 480;
    	 
    	try {
    	    ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
    	} catch (IOException ex) {
    	    System.err.println(ex);
    	}
	}
}
