import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExperiment {
	public static void main(String[] args) {
		String[] webPages = {"http://wings.cs.stonybrook.edu/courses/cse570/flipkart/", "http://wings.cs.stonybrook.edu/courses/cse570/cnn/"};
		
		/*
		 * experimentReults is:
		 * <
		 * 		<loss = ß1, delay = µ1, throughput = ∆1> ---> <
		 * 													 webPage1 --> <3123, 2343, 4392, ..., 2343>,
		 * 													 webPage2 --> <5434, 5432, 5464, ..., 5634>,
		 * 													 ...
		 * 													>,
		 * 		<loss = ß2, delay = µ2, throughput = ∆2> ---> <
		 * 													 webPage1 --> <6123, 2673, 4992, ..., 9343>,
		 * 													 webPage2 --> <7774, 7632, 5944, ..., 4534>,
		 * 													 ...
		 * 													>,
		 * 		...
		 * >
		 * */
		Map<Experiment, Map<String, List<Long>>> experimentResults = new HashMap<Experiment, Map<String, List<Long>>>();
		
		boolean firstTime = true;
		for (int delay = 10; delay <= 300; delay += 20)  // delay varies in the range [10, 300]ms +20 each step
			for (int loss = 6; loss <= 20; loss += 1) // loss varies in the range [0.1, 2.0]% +0.1 each step
				for (int throughput = 1250; throughput <= 10000; throughput += 250) { // throughput varies in the range [500 Kbps, 10Mbps = 10,000 Kbps] +250Kbps each step
					// Run the new experiment
					
					// Run the SSH commands first !!
					/*
					 * tc qdisc add dev eth0 root handle 1:0 netem delay XXms loss YY% 
					   tc qdisc add dev eth0 parent 1:1 handle 10: tbf rate XYZkbit buffer 16000 limit 30000 # use kbit or Mbit etc
					 * */
					if (firstTime) {
						firstTime = false;
						/*
						 * tc qdisc add dev eth0 root handle 1:0 netem delay XXms loss YY% 
					   	   tc qdisc add dev eth0 parent 1:1 handle 10: tbf rate XYZkbit buffer 16000 limit 30000 # use kbit or Mbit etc
						 * */
						// TODO: change the "host", "user" and "password" credentials
						SSH.run_sudo_command("130.245.145.88", "rafiki", "summer2015@hp", "sudo tc qdisc change dev wlan0 root handle 1:0 netem delay " + delay + "ms loss " + (((double) loss)/10) + "%");
						SSH.run_sudo_command("130.245.145.88", "rafiki", "summer2015@hp", "sudo tc qdisc change dev wlan0 parent 1:1 handle 10: tbf rate " + throughput + "kbit buffer 16000 limit 30000");
					} else {
						/*
						 * tc qdisc change dev eth0 root handle 1:0 netem delay XXms loss YY% 
					   	   tc qdisc change dev eth0 parent 1:1 handle 10: tbf rate XYZkbit buffer 16000 limit 30000 # use kbit or Mbit etc
						 * 
						 * */
						
						// TODO: change the "host", "user" and "password" credentials
						SSH.run_sudo_command("130.245.145.88", "rafiki", "summer2015@hp", "sudo tc qdisc change dev wlan0 root handle 1:0 netem delay " + delay + "ms loss " + (((double) loss)/10) + "%");
						SSH.run_sudo_command("130.245.145.88", "rafiki", "summer2015@hp", "sudo tc qdisc change dev wlan0 parent 1:1 handle 10: tbf rate " + throughput + "kbit buffer 16000 limit 30000");
						
					}
					
					// Try to experiment it !!
					Experiment newExperiment = new Experiment(delay, ((double)loss / 10), throughput);
					System.out.println("New experiment is being executed with delay: " + delay + ", loss: " + ((double)loss / 10) + ", throughput: " + throughput);
					Map<String, List<Long>> specificExperimentResults = new HashMap<String, List<Long>>();
					for (String webpage: webPages) {
						// for each web page, we iterate 10 times
						List<Long> specificWebPageResult = new ArrayList<Long>();
						for (int iteration = 0; iteration < 3; ++iteration) { 
							specificWebPageResult.add(PageLoadTime.getPageLoadTime(webpage));
						}
						specificExperimentResults.put(webpage, specificWebPageResult);
					}

					ObjectOutputStream oos = null;
					try {
						FileOutputStream fos = new FileOutputStream("/Users/FM/Desktop/experiments_fahimeh/Expdelay" + delay + "loss" + loss + "throughput" + throughput + ".ser");
				        oos = new ObjectOutputStream(fos);
				        oos.writeObject(specificExperimentResults);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						try {
							oos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					System.out.println("File with name Expdelay" + delay + "loss" + loss + "throughput" + throughput + ".ser has bene generated !!" );
					
					//experimentResults.put(newExperiment, specificExperimentResults);	
				}
		
		// Now, experimentResults contain the required information !!
		
		// First storing the result of the experiments on the disk
		
		
		
		// The links: 
		// http://www.java2s.com/Code/Java/File-Input-Output/OverridewriteObjectObjectOutputStreamoosandreadObjectObjectInputStreamois.htm
		// http://stackoverflow.com/questions/30544509/java-object-serialization-how-to-use-same-file-after-using-it-with-objectoutput
		/*
		 * FileInputStream fis = new FileInputStream("ExperimentResults.ser");
    	   ObjectInputStream  ois = new ObjectInputStream(fis);

    	   Map<Experiment, Map<String, List<Long>>> experimentResults = (Map<Experiment, Map<String, List<Long>>>) ois.readObject();
		 * 
		 * */
		
		// TODO using the following links to draw the graphs and charts:
		// http://www.jzy3d.org/ ==> for jar file: http://www.jzy3d.org/download-0.9.php
		// https://gist.github.com/timaschew/1078486
		// http://casmi.github.io/
		// https://processing.org/
		// https://code.google.com/p/surfaceplotter/
//		DrawPlot_1.makePlots(experimentResults, "/Users/FM/Desktop/ExpPlots");
	}
}
