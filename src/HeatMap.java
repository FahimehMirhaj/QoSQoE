import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HeatMap {
	
	public static void main(String[] args) {
		String data = "/Users/FM/Desktop/experiment_data.txt";
		Map<Integer, Map<Integer, List<Double>>> lines = new TreeMap<Integer, Map<Integer, List<Double>>>();
		
		try {
			FileReader fReader = new FileReader(data);
			BufferedReader bf = new BufferedReader(fReader);
			
			String line = "";
			while ((line = bf.readLine()) != null) {
				String[] lineComponents = line.split(" ");
				int throughput = ((int)(Double.parseDouble(lineComponents[0]) / 20)) * 20;
				int delay = ((int)(Double.parseDouble(lineComponents[1])/10)) * 10;
				double loss = Double.parseDouble(lineComponents[2]);
				double plt = Double.parseDouble(lineComponents[3]);
				
				
				if (lines.containsKey(delay)) {
					Map<Integer, List<Double>> delayData = lines.get(delay);
					if (delayData.containsKey(throughput)) {
						List<Double> data2 = delayData.get(throughput);
						data2.add(plt);
					} else {
						List<Double> data2 = new ArrayList<Double>();
						data2.add(plt);
						delayData.put(throughput, data2);
						
					}
				} else {
					Map<Integer, List<Double>> delayData = new TreeMap<Integer, List<Double>>();
					List<Double> values = new ArrayList<Double>();
					values.add(plt);
					
					delayData.put(throughput, values);
					lines.put(delay, delayData);
				}
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String excelFile = "";
				
		for(Map.Entry<Integer, Map<Integer, List<Double>>> entry: lines.entrySet()) {
			double throughput = entry.getKey();
			Map<Integer, List<Double>> delayData = entry.getValue();
			System.out.println("delay is " + throughput + ", and througputInformation is " + delayData);
		}
		
		
		String lastLine = " ";
		for (int throughputIndex = 100; throughputIndex <= 1330; throughputIndex += 20) {
			lastLine += "," + throughputIndex;
		}
		
		excelFile += lastLine;
		
		System.out.println(lastLine);
		for (int delayIndex = 10; delayIndex <= 260; delayIndex += 10) {
			String line = Integer.toString(delayIndex);
			
			if (lines.containsKey(delayIndex)) {
				Map<Integer, List<Double>> theInformation = lines.get(delayIndex);
				for (int throughputIndex = 100; throughputIndex <= 1330; throughputIndex += 20) {
					if (theInformation.containsKey(throughputIndex)) {
						List<Double> thelist = theInformation.get(throughputIndex);
						Collections.sort(thelist);
						line += "," + thelist.get(thelist.size() / 2);
					} else {
						line += ", ";
					}
				}
			}
			
			excelFile = line + "\n" + excelFile;
		}
		
		System.out.println("The excel file will be: ");
		System.out.println(excelFile);
		
		try
		{
		    FileWriter writer = new FileWriter("/Users/FM/Desktop/matrix.csv");
			writer.append(excelFile);
				
		    //generate whatever data you want
				
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		}
		
	}
}

//  