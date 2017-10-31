package uk.ac.soton.ecs.hbd1g15;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

/**
 * Operator to perform a Convolution
 *
 */
public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {
	private float[][] kernel;

	public MyConvolution(float[][] kernel) {
		this.kernel = kernel;
	}

	@Override
	public void processImage(FImage image) {
		// Check that both dimensions are odd
		if(this.kernel[0].length%2 == 0) {
			throw new IllegalArgumentException(this.kernel[0].length + " is even. Both dimensions of template must be odd.");
		}
		if(this.kernel[1].length%2 == 0) {
			throw new IllegalArgumentException(this.kernel[1].length + " is even. Both dimensions of template must be odd.");
		}
		
		// Create a temporary, completely black image
		float[][] temporaryImage = new float[image.width][image.height];
		for (int y=0; y<image.getHeight(); y++) {
		    for (int x=0; x<image.getWidth(); x++) {
		    	temporaryImage[x][y] = 0;
		    }
		}
		FImage temporaryBufferImage = new FImage(temporaryImage);
		
		//
		// hint: use FImage#internalAssign(FImage) to set the contents
		// of your temporary buffer image to the image.
	}
}