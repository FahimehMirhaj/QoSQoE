import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class WebPageExperiments {
	private Map<ThroughputDelayFixed, Map<Double, Long>> lossData;
	private Map<ThroughputLossFixed, Map<Integer, Long>> delayData;
	private Map<LossDelayFixed, Map<Integer, Long>> throughputData;
	
	public Map<ThroughputDelayFixed, Map<Double, Long>> getLossData() {
		return lossData;
	}
	public void setLossData(Map<ThroughputDelayFixed, Map<Double, Long>> lossData) {
		this.lossData = lossData;
	}
	public Map<ThroughputLossFixed, Map<Integer, Long>> getDelayData() {
		return delayData;
	}
	public void setDelayData(Map<ThroughputLossFixed, Map<Integer, Long>> delayData) {
		this.delayData = delayData;
	}
	public Map<LossDelayFixed, Map<Integer, Long>> getThroughputData() {
		return throughputData;
	}
	public void setThroughputData(Map<LossDelayFixed, Map<Integer, Long>> throughputData) {
		this.throughputData = throughputData;
	}
	
	// For plotting loss-PLT
	private XYDataset getLossDataSet(Map<Double, Long> points, int throughput, int delay) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("throughput" + throughput + "_delay" + delay);
		for (Map.Entry<Double, Long> entry: points.entrySet()) {
			Double x = entry.getKey();
			Long y = entry.getValue();
			
			series1.add(x, y);
		}
		dataset.addSeries(series1);
		
		return dataset;
	}
	
	private void drawLossPLTPlot(String directory, int throughput, int delay, Map<Double, Long> points) {
		String fileName = "throughput" + throughput + "_delay" + delay + ".png";
		
		String chartTitle = "throughput" + throughput + "_delay" + delay;
        String xAxisLabel = "Loss";
        String yAxisLabel = "Page Load Time";
        
        XYDataset dataset = getLossDataSet(points, throughput, delay);
     
        JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
                xAxisLabel, yAxisLabel, dataset);
        
        System.out.println("Directory is " + directory);
        
        File imageFile = new File(directory + "/" + fileName);
    	int width = 640;
    	int height = 480;
    	 
    	try {
    	    ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
    	} catch (IOException ex) {
    	    System.err.println(ex);
    	}
	}
	// End for plotting loss-PLT
	
	// For plotting delay-PLT
	
	private XYDataset getDelayDataSet(Map<Integer, Long> points, int throughput, double loss) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("throughput" + throughput + "_loss" + loss);
		for(Map.Entry<Integer, Long> entry: points.entrySet()) {
			Integer x = entry.getKey();
			Long y = entry.getValue();
			
			series1.add(x, y);
		}
		
		dataset.addSeries(series1);
		return dataset;
	}
	
	private void drawDelayPLTPlots(String directory, int throughput, double loss, Map<Integer, Long> points) {
		String fileName = "throughput" + throughput + "_loss" + loss + ".png";
		
		String chartTitle = "throughput" + throughput + "_loss" + loss;
        String xAxisLabel = "Delay";
        String yAxisLabel = "Page Load Time";
        
        XYDataset dataset = getDelayDataSet(points, throughput, loss);
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
	// End for plotting delay-PLT
	
	// For plotting throughput-PLT
	private XYDataset getThroughputDataSet(Map<Integer, Long> points, int delay, double loss) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("delay" + delay + "_loss" + loss);
		for(Map.Entry<Integer, Long> entry: points.entrySet()) {
			Integer x = entry.getKey();
			Long y = entry.getValue();
			series1.add(x, y);
		}
		
		dataset.addSeries(series1);
		return dataset;
	}
	
	private void drawThroughputPLTPlots(String directory, int delay, double loss, Map<Integer, Long> points) {
		String fileName = "delay" + delay + "_loss" + loss + ".png";
		
		String chartTitle = "delay" + delay + "_loss" + loss;
        String xAxisLabel = "Throughput";
        String yAxisLabel = "Page Load Time";
        
        XYDataset dataset = getThroughputDataSet(points, delay, loss);
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
	
	
	// webPageFolder directory will contain three other directories called lossPLTPlots, delayPLTPlots, and throughputPLTPlots
	public void generatePlots(String webPageFolder) {
		// drawing lossPLTPlots
		System.out.println("drawing lossPLTPlots");
		File lossPLTPlots = new File(webPageFolder + "/lossPLTPlots");
		lossPLTPlots.mkdir();
		for (Map.Entry<ThroughputDelayFixed, Map<Double, Long>> entry: lossData.entrySet()) {
			ThroughputDelayFixed throughputDelayData = entry.getKey();
			int throughput = throughputDelayData.getThroughput();
			int delay = throughputDelayData.getDelay();
			
			Map<Double, Long> points = entry.getValue();
			drawLossPLTPlot(lossPLTPlots.getAbsolutePath(), throughput, delay, points);	
		}
		
		// drawing delayPLTPlots
		System.out.println("drawing delayPLTPlots");
		File delayPLTPlots = new File(webPageFolder + "/delayPLTPlots");
		delayPLTPlots.mkdir();
		for (Map.Entry<ThroughputLossFixed, Map<Integer, Long>> entry: delayData.entrySet()) {
			ThroughputLossFixed throughputLossData = entry.getKey();
			double loss = throughputLossData.getLoss();
			int throughput = throughputLossData.getThroughput();
			
			Map<Integer, Long> points = entry.getValue();
			drawDelayPLTPlots(delayPLTPlots.getAbsolutePath(), throughput, loss, points);
		}
		
		// drawing throughputPLTPlots
		System.out.println("drawing throughputPLTPlots");
		File throughputPLTPlots = new File(webPageFolder + "/throughputPLTPlots");
		throughputPLTPlots.mkdir();
		for (Map.Entry<LossDelayFixed, Map<Integer, Long>> entry: throughputData.entrySet()) {
			LossDelayFixed lossDelayData = entry.getKey();
			int delay = lossDelayData.getDelay();
			double loss = lossDelayData.getLoss();
			
			Map<Integer, Long> points = entry.getValue();
			drawThroughputPLTPlots(throughputPLTPlots.getAbsolutePath(), delay, loss, points);
		}
		
	}
	
}
