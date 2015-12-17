import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// running the main method inside this class, will draw the plots for each web page (based on the result you yourself got !!!!!)
public class DrawPlot {

	public static void main(String[] args) {
		DrawPlot.makePlots("/Users/FM/Desktop/experiments_fahimeh",
				"/Users/FM/Desktop/ExpPlots");
	}
	
	// DrawPlot.makePlots("/Users/FM/Desktop/ExperimentFiles", "/Users/FM/Desktop/ExpPlots");
	public static void makePlots(String inputFileDirectory, String outputFileDirectory) {
		Map<String, WebPageExperiments> experiments = new HashMap<String, WebPageExperiments>();
		
		//Expdelay10loss1throughput5250.ser
		
		File inputFiles = new File(inputFileDirectory);
		for(File inputFile: inputFiles.listFiles()) {
			String fileName = inputFile.getName();
			if (fileName.equals(".DS_Store"))
				continue;
			
			int delayValueStartIndex = fileName.indexOf("delay") + 5;
			int delayValueEndIndex = fileName.indexOf("loss");
			
			String delayValue = fileName.substring(delayValueStartIndex, delayValueEndIndex);
			int delay = Integer.parseInt(delayValue);
			
			
			int lossValueStartIndex = fileName.indexOf("loss") + 4;
			int lossValueEndIndex = fileName.indexOf("throughput");
			
			String lossValue = fileName.substring(lossValueStartIndex, lossValueEndIndex);
			double loss = (Double.parseDouble(lossValue))/10.0;
			
			int throughputValueStartIndex = fileName.indexOf("throughput") + 10;
			int throughputValueEndIndex = fileName.indexOf(".");
			
			String throughputValue = fileName.substring(throughputValueStartIndex, throughputValueEndIndex);
			int throughput = Integer.parseInt(throughputValue);
			
			System.out.println("Reading the file: " + fileName + "which has value loss: " + loss + ", throughput: " + throughput + ", delay: " + delay);
			
			FileInputStream fis = null;
			ObjectInputStream  ois = null;
			try {
				fis = new FileInputStream(inputFile.getAbsolutePath());
				ois = new ObjectInputStream(fis);
				Map<String, List<Long>> experiment = (Map<String, List<Long>>) ois.readObject();
				
				// TODO
				for(Map.Entry<String, List<Long>> entry: experiment.entrySet()) {
					String webPage = entry.getKey();
					List<Long> pageLoadTimes = entry.getValue();
					Collections.sort(pageLoadTimes);
					Long pageLoadTime = pageLoadTimes.get(1);
					
					// delay, loss, throughput, webpage, pageLoadTime
					ThroughputDelayFixed througputDelayFixed = new ThroughputDelayFixed(throughput, delay);
					ThroughputLossFixed throughputLossFixed = new ThroughputLossFixed(throughput, loss);
					LossDelayFixed lossDelayFixed = new LossDelayFixed(loss, delay);
					
					if (experiments.containsKey(webPage)) {
						// updating the corresponding lossData, delayData and throughputData
						WebPageExperiments relatedExperiment = experiments.get(webPage);
						Map<ThroughputDelayFixed, Map<Double, Long>> lossData = relatedExperiment.getLossData();
						Map<ThroughputLossFixed, Map<Integer, Long>> delayData = relatedExperiment.getDelayData();
						Map<LossDelayFixed, Map<Integer, Long>> throughputData = relatedExperiment.getThroughputData();
						
						if (lossData.containsKey(througputDelayFixed)) {
							Map<Double, Long> points = lossData.get(througputDelayFixed);
							points.put(loss, pageLoadTime);
						} else {
							Map<Double, Long> points = new HashMap<Double, Long>();
							points.put(loss, pageLoadTime);
							lossData.put(througputDelayFixed, points);
						}
						
						if (delayData.containsKey(throughputLossFixed)) {
							Map<Integer, Long> points = delayData.get(throughputLossFixed);
							points.put(delay, pageLoadTime);
						} else {
							Map<Integer, Long> points = new HashMap<Integer, Long>();
							points.put(delay, pageLoadTime);
							
							delayData.put(throughputLossFixed, points);
						}
						
						if (throughputData.containsKey(lossDelayFixed)) {
							Map<Integer, Long> points = throughputData.get(lossDelayFixed);
							points.put(throughput, pageLoadTime);
						} else {
							Map<Integer, Long> points = new HashMap<Integer, Long>();
							points.put(throughput, pageLoadTime);
							
							throughputData.put(lossDelayFixed, points);
						}
						
						
					} else {
						WebPageExperiments relatedExperiment = new WebPageExperiments();
						
						Map<ThroughputDelayFixed, Map<Double, Long>> lossData = new HashMap<ThroughputDelayFixed, Map<Double, Long>>();
						Map<Double, Long> points = new HashMap<Double, Long>();
						points.put(loss, pageLoadTime);
						lossData.put(througputDelayFixed, points);
						
						relatedExperiment.setLossData(lossData);
						
						Map<ThroughputLossFixed, Map<Integer, Long>> delayData = new HashMap<ThroughputLossFixed, Map<Integer, Long>>();
						Map<Integer, Long> points2 = new HashMap<Integer, Long>();
						points2.put(delay, pageLoadTime);
						delayData.put(throughputLossFixed, points2);
						
						relatedExperiment.setDelayData(delayData);
						
						Map<LossDelayFixed, Map<Integer, Long>> throughputData = new HashMap<LossDelayFixed, Map<Integer, Long>>();
						Map<Integer, Long> points3 = new HashMap<Integer, Long>();
						points3.put(throughput, pageLoadTime);
						throughputData.put(lossDelayFixed, points3);
						
						relatedExperiment.setThroughputData(throughputData);
						
						experiments.put(webPage, relatedExperiment);
						
					}
				}
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (ClassNotFoundException classNotFoundException) {
				classNotFoundException.printStackTrace();
			}
		}
		
		System.out.println("Finish reading the input files, now, it is time to output the plots !!");
		for (Map.Entry<String, WebPageExperiments> entry: experiments.entrySet()) {
			String webPage = entry.getKey();
			System.out.println("Webpage is " + webPage.replace("/", "-"));
			WebPageExperiments webPageExperiments = entry.getValue();
			File webPageFile = new File(outputFileDirectory + "/" + webPage.replace("/", "-"));
			webPageFile.mkdir();
			
			webPageExperiments.generatePlots(webPageFile.getAbsolutePath());
		}
		
	}
}
