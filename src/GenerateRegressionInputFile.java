import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


// By inputting the file "/Users/FM/Desktop/QoS.txt" to the online regression calculator (link: http://www.xuru.org/rt/ExpR.asp), we got the value a = 7.474315058 and b = 1.415523842·10^-2
public class GenerateRegressionInputFile {
	public static void main(String[] args) {
		
		String regressionInputFile = "";
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/Users/FM/Desktop/experiment_data.txt"));
			String line = br.readLine();

		    while (line != null) {
		        String[] lineComponents = line.split(" ");
		        double throughput = Double.parseDouble(lineComponents[0]);
		        double delay = Double.parseDouble(lineComponents[1]);
		        double loss = Double.parseDouble(lineComponents[2]);
		        
		       
		        double plt = Double.parseDouble(lineComponents[3]);
		       
		        regressionInputFile += (throughput / delay) + " " + plt + "\n";
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
		
		
		try {
		    File outputFile = new File("/Users/FM/Desktop/QoS.txt");

		    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		    writer.write (regressionInputFile);

		    //Close writer
		    writer.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
		
	}
}
