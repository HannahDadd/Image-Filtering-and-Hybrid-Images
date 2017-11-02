package uk.ac.soton.ecs.hbd1g15;

import java.io.File;
import java.io.IOException;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.FConvolution;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.convolution.Gaussian2D;

/**
 * OpenIMAJ Producing hybrid images from 2 similar images
 *
 */
public class App {
    public static void main( String[] args ) {
    	try {
        	// Get 2 similar images
    		MBFImage lowPassFilterImage = ImageUtilities.readMBF(new File("data/dog.bmp"));
    		MBFImage highPassFilterImage = ImageUtilities.readMBF(new File("data/cat.bmp"));
    		MBFImage originalHighPassImage = highPassFilterImage.clone();
    		
    		// Sigma is the cut off frequency defined for each filter when amplitude gain is 1/2.
    		lowPassFilterImage = createLowPassFilter(lowPassFilterImage, 16f);
    		highPassFilterImage = createLowPassFilter(highPassFilterImage, 2.4f);
    		
    		// High pass image made by subtracting low pass image from original
    		highPassFilterImage = originalHighPassImage.subtract(highPassFilterImage);

    		// Finally create and display the hybrid image
    		MBFImage hybridImage = highPassFilterImage.add(lowPassFilterImage);
    		DisplayUtilities.display(hybridImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Create a low pass version of an image using a Guassian filter
     */
    private static MBFImage createLowPassFilter(MBFImage image, float sigma) {
		// Sigma= standard deviation of Gaussian, controls the cut off frequency
		int size = (int) (8.0f * sigma + 1.0f);
		if (size % 2 == 0) size++;
		FImage guassianImage = Gaussian2D.createKernelImage(size, sigma);
		
		// Create kernal from pixels in image
		float[][] guassianKernal = new float[guassianImage.getHeight()][guassianImage.getWidth()];
		for (int y=0; y<guassianImage.getHeight(); y++) {
		    for (int x=0; x<guassianImage.getWidth(); x++) {
		    	guassianKernal[y][x] = guassianImage.getPixel(x, y);
		    }
		}
    	
    	// Apply low pass filter to each and in the image
		FGaussianConvolve myGConvolution = new FGaussianConvolve(sigma);
		FConvolution myConvolution = new FConvolution(guassianKernal);
		return image.process(myGConvolution);
    }
}
