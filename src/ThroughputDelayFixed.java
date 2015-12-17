
public class ThroughputDelayFixed {
	private int throughput;
	private int delay;
	
	public ThroughputDelayFixed(int throughput, int delay) {
		setThroughput(throughput);
		setDelay(delay);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delay;
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
		ThroughputDelayFixed other = (ThroughputDelayFixed) obj;
		if (delay != other.delay)
			return false;
		if (throughput != other.throughput)
			return false;
		return true;
	}
	public int getThroughput() {
		return throughput;
	}
	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	
}
