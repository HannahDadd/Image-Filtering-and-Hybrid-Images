package uk.ac.soton.ecs.hbd1g15;

import java.io.File;
import java.io.IOException;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processing.resize.ResizeProcessor;

/**
 * OpenIMAJ Producing hybrid images from 2 similar images
 *
 */
public class App {
    public static void main( String[] args ) {
		try {
	    	App app = new App();
	    	
	    	// Hybrid of each pair of images
			MBFImage catImage = ImageUtilities.readMBF(new File("data/cat.bmp"));
			MBFImage dogImage = ImageUtilities.readMBF(new File("data/dog.bmp"));
			app.displayHybridImage(dogImage, 5, catImage, 7, app);

			MBFImage einsteinImage = ImageUtilities.readMBF(new File("data/einstein.bmp"));
			MBFImage marilynImage = ImageUtilities.readMBF(new File("data/marilyn.bmp"));
			app.displayHybridImage(einsteinImage, 7, marilynImage, 5, app);
			
			MBFImage fishImage = ImageUtilities.readMBF(new File("data/fish.bmp"));
			MBFImage submarineImage = ImageUtilities.readMBF(new File("data/submarine.bmp"));
			app.displayHybridImage(submarineImage, 8, fishImage, 5, app);
			
			MBFImage bicycleImage = ImageUtilities.readMBF(new File("data/bicycle.bmp"));
			MBFImage motorcycleImage = ImageUtilities.readMBF(new File("data/motorcycle.bmp"));
			app.displayHybridImage(bicycleImage, 8, motorcycleImage, 8, app);	
			
			MBFImage planeImage = ImageUtilities.readMBF(new File("data/plane.bmp"));
			MBFImage birdImage = ImageUtilities.readMBF(new File("data/bird.bmp"));
			app.displayHybridImage(planeImage, 8, birdImage, 6, app);
			
			MBFImage tongueExprImage = ImageUtilities.readMBF(new File("data/exp1.jpg"));
			MBFImage eyesExprImage = ImageUtilities.readMBF(new File("data/expr2.jpg"));
			app.displayHybridImage(tongueExprImage, 2, eyesExprImage, 3, app);		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Make and display a hybrid image
     */
    private void displayHybridImage(MBFImage lowPassFilterImage, float lowPassFilterSigma, 
    		MBFImage highPassFilterImage, float highPassFilterSigma, App app) {
    	// Clone original image for high pass filter
		MBFImage originalHighPassImage = highPassFilterImage.clone();
		
		// Sigma is the cut off frequency defined for each filter when amplitude gain is 1/2.
		lowPassFilterImage = app.createLowPassFilter(lowPassFilterImage, lowPassFilterSigma);
		highPassFilterImage = app.createLowPassFilter(highPassFilterImage, highPassFilterSigma);
		
		// High pass image made by subtracting low pass image from original
		highPassFilterImage = originalHighPassImage.subtract(highPassFilterImage);
		
		// Resize processor used to clearly show the change in image
		float[] sizeFactors = {0.5f, 0.25f, 0.12f, 0.06f};
		double distanceBetweenImages = 0.5;
		MBFImage hybridImage = highPassFilterImage.add(lowPassFilterImage);
		
		// Make a large black image to print the images on
		int width = hybridImage.getWidth();
		for(float sizeFactor : sizeFactors) {
			width = (int) (width + Math.ceil(hybridImage.getWidth()*sizeFactor) + distanceBetweenImages);
		}
		MBFImage displayHybridImage = new MBFImage(width, hybridImage.getHeight());

		// Finally display the hybrid images
		int widthPassed = hybridImage.getWidth();
		displayHybridImage.drawImage(hybridImage, hybridImage.getWidth(), 0);
		
		// Loop through each scale and add the scaled image to the display
		for(float sizeFactor : sizeFactors) {
			MBFImage imageToAdd = hybridImage.process(new ResizeProcessor(sizeFactor));
			widthPassed = (int) (imageToAdd.getWidth() + distanceBetweenImages);
			displayHybridImage.drawImage(imageToAdd, widthPassed, 0);
		}
		DisplayUtilities.display(displayHybridImage);
    }
    
    /**
     * Create a low pass version of an image using a Guassian filter
     */
    private MBFImage createLowPassFilter(MBFImage image, float sigma) {
		// Sigma= standard deviation of Gaussian, controls the cut off frequency
    	int size = (int) (8.0f * sigma + 1.0f); // (this implies the window is +/- 4 sigmas from the centre of the Gaussian)
    	if (size % 2 == 0) size++; // size must be odd
		FImage guassianImage = Gaussian2D.createKernelImage(size, sigma);
		float[][] guassianKernal = guassianImage.pixels;
    	
    	// Apply low pass filter to each and in the image
		MyConvolution myConvolution = new MyConvolution(guassianKernal);
		return image.process(myConvolution);
    }
}
