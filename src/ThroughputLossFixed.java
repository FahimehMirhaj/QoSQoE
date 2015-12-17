
public class ThroughputLossFixed {
	private int throughput;
	private double loss;
	
	public ThroughputLossFixed(int throughput, double loss) {
		setThroughput(throughput);
		setLoss(loss);
	}
	
	public int getThroughput() {
		return throughput;
	}
	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}
	public double getLoss() {
		return loss;
	}
	public void setLoss(double loss) {
		this.loss = loss;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(loss);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + throughput;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThroughputLossFixed other = (ThroughputLossFixed) obj;
		if (Double.doubleToLongBits(loss) != Double.doubleToLongBits(other.loss))
			return false;
		if (throughput != other.throughput)
			return false;
		return true;
	}
	
	
	
}
