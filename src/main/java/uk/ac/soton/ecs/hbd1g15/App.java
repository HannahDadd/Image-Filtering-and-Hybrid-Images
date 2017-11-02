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
	    	App app = new App();
	    	
	    	// Hybrid of each pair of images
			MBFImage catImage = ImageUtilities.readMBF(new File("data/dog.bmp"));
			MBFImage dogImage = ImageUtilities.readMBF(new File("data/cat.bmp"));
			app.displayHybridImage(catImage, dogImage, app);

			MBFImage einsteinImage = ImageUtilities.readMBF(new File("data/einstein.bmp"));
			MBFImage marilynImage = ImageUtilities.readMBF(new File("data/marilyn.bmp"));
			app.displayHybridImage(einsteinImage, marilynImage, app);
			
			MBFImage fishImage = ImageUtilities.readMBF(new File("data/fish.bmp"));
			MBFImage submarineImage = ImageUtilities.readMBF(new File("data/submarine.bmp"));
			app.displayHybridImage(fishImage, submarineImage, app);
			
			MBFImage bicycleImage = ImageUtilities.readMBF(new File("data/bicycle.bmp"));
			MBFImage motorcycleImage = ImageUtilities.readMBF(new File("data/motorcycle.bmp"));
			app.displayHybridImage(bicycleImage, motorcycleImage, app);			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Make and display a hybrid image
     */
    private void displayHybridImage(MBFImage lowPassFilterImage, MBFImage highPassFilterImage, App app) {
    	// Clone original image for high pass filter
		MBFImage originalHighPassImage = highPassFilterImage.clone();
		
		// Sigma is the cut off frequency defined for each filter when amplitude gain is 1/2.
		lowPassFilterImage = app.createLowPassFilter(lowPassFilterImage, 16f);
		highPassFilterImage = app.createLowPassFilter(highPassFilterImage, 24f);
		
		// High pass image made by subtracting low pass image from original
		highPassFilterImage = originalHighPassImage.subtract(highPassFilterImage);

		// Finally create and display the hybrid image
		MBFImage hybridImage = highPassFilterImage.add(lowPassFilterImage);
		DisplayUtilities.display(hybridImage);
    }
    
    /**
     * Create a low pass version of an image using a Guassian filter
     */
    private MBFImage createLowPassFilter(MBFImage image, float sigma) {
		// Sigma= standard deviation of Gaussian, controls the cut off frequency
		int size = (int) (8.0f * sigma + 1.0f);
		if (size % 2 == 0) size++;
		FImage guassianImage = Gaussian2D.createKernelImage(size, sigma);
		float[][] guassianKernal = guassianImage.pixels;
//		
//		// Create kernal from pixels in image
//		float[][] guassianKernal = new float[guassianImage.getWidth()][guassianImage.getHeight()];
//		for (int y=0; y<guassianImage.getHeight(); y++) {
//		    for (int x=0; x<guassianImage.getWidth(); x++) {
//		    	guassianKernal[x][y] = guassianImage.getPixel(x, y);
//		    }
//		}
    	
    	// Apply low pass filter to each and in the image
		FGaussianConvolve myGConvolution = new FGaussianConvolve(sigma);
		FConvolution myConvolution = new FConvolution(guassianKernal);
		return image.process(myConvolution);
    }
}
