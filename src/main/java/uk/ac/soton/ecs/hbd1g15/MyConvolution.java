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
		
		// Get the middle of the template distance
		int middleColIndex = Math.floorDiv(this.kernel[1].length, 2);
		int middleRowIndex = Math.floorDiv(this.kernel[0].length, 2);
		
		// Loop through each pixel in the image ignoring the border
		for(int y=middleColIndex+1; y<image.getHeight()-middleColIndex; y++) {
			for(int x=middleRowIndex+1; x<image.getWidth()-middleRowIndex; x++) {
				float sum = 0;
				
				// Loop through each coefficient in the template
				for(int templateX=0; templateX<this.kernel[0].length; templateX++) {
					for(int templateY=0; templateY< this.kernel[1].length; templateY++) {
						// Times each pixel value by the template value and add them all together
						sum = sum + image.getPixelNative(x-middleRowIndex+templateX, y-middleColIndex+templateY) * this.kernel[templateX][templateY];
					}
				}
				temporaryBufferImage.setPixelNative(x, y, sum);
			}
		}
		
		// hint: use FImage#internalAssign(FImage) to set the contents
		// of your temporary buffer image to the image.
	}
}