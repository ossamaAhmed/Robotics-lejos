import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

public class MaxValueFilter extends AbstractFilter {
	float[] sample;
	private int max;

	public MaxValueFilter(SampleProvider source, int max) {
		super(source);
		this.max = max;
	}

	public void fetchSample(float sample[], int offset) {
		super.fetchSample(sample, offset);
		for (int i = 0; i < sampleSize(); i++) {
			if (sample[offset + i] > max) {
				sample[offset+i] = max;
			}
		}
	}
}
