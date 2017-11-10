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
		float[][] temporaryImage = new float[image.getHeight()][image.getWidth()];
		for (int y=0; y<image.getHeight(); y++) {
		    for (int x=0; x<image.getWidth(); x++) {
		    	temporaryImage[y][x] = 0;
		    }
		}
		FImage temporaryBufferImage = new FImage(temporaryImage);
		
		// Get the middle of the template distance
		int middleRowIndex = Math.floorDiv(this.kernel[0].length, 2);
		int middleColIndex = Math.floorDiv(this.kernel[1].length, 2);
		
		// Loop through each pixel in the image ignoring the border
		for(int x=0; x<image.getWidth(); x++) {
			for(int y=0; y<image.getHeight(); y++) {
				float sum = 0;
				
				// Loop through each coefficient in the template
				for(int templateX=0; templateX<this.kernel[0].length; templateX++) {
					for(int templateY=0; templateY< this.kernel[1].length; templateY++) {
						int pixelX = 0;
						int pixelY = 0;
						
						// When at the part of the template that cannot be filled loop round to the top
						if(x-middleRowIndex+templateX<0) {
							pixelX = image.getWidth()-Math.abs(x-middleRowIndex+templateX);
						} else if(x-middleRowIndex+templateX > image.getWidth()-1) {
							pixelX = 0 + x-middleRowIndex+templateX - image.getWidth();
						} else {
							pixelX = x-middleRowIndex+templateX;
						}
						if(y-middleColIndex+templateY<0) {
							pixelY= image.getHeight()-Math.abs(y-middleColIndex+templateY);
						} else if(y-middleColIndex+templateY > image.getHeight()-1) {
							pixelY = 0 + y-middleColIndex+templateY - image.getHeight();
						} else {
							pixelY = y-middleColIndex+templateY;
						}
						// Times each pixel value by the template value and add them all together
						sum = sum + image.getPixelNative(pixelX, pixelY) * this.kernel[templateX][templateY];
					}
				}
				temporaryBufferImage.setPixelNative(x, y, sum);
			}
		}
		
		
		
		// Set the contents of temporary buffer image to the image.
		image.internalAssign(temporaryBufferImage);
	}
}