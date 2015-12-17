
public class LossDelayFixed {
	private double loss;
	private int delay;
	
	public LossDelayFixed(double loss, int delay) {
		setLoss(loss);
		setDelay(delay);
	}
	
	public void setLoss(double loss) {
		this.loss = loss;
	}
	
	public double getLoss() {
		return this.loss;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delay;
		long temp;
		temp = Double.doubleToLongBits(loss);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		LossDelayFixed other = (LossDelayFixed) obj;
		if (delay != other.delay)
			return false;
		if (Double.doubleToLongBits(loss) != Double.doubleToLongBits(other.loss))
			return false;
		return true;
	}

}
