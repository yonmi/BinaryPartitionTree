package metric.bricks;

import java.awt.image.BufferedImage;

import metric.bricks.Metric.TypeOfMetric;
import metric.color.NdviMetric;
import metric.color.NdwiMetric;
import metric.color.RadiometricAverage;
import metric.color.Ominmax;
import metric.color.Omse;
import metric.color.Owsdm;
import metric.combination.MmCompacity;
import metric.combination.MmFelNdvi;
import metric.combination.MmFelNdviNdwi;
import metric.combination.MmNdvi;
import metric.combination.MmNdviNdwi;
import metric.combination.MmNdwi;
import metric.combination.Ocolcont;
import metric.others.Orandom;
import metric.shape.Elongation;
import metric.shape.FastCompactness;
import metric.shape.FastSmoothness;
import metric.shape.Ocontour_;
import metric.shape.Smoothness;
import metric.vector.VectorialDistance;

/**
 * 
 * Factory building the right metric objects.
 * Each coded metric class should figure in this class as a choice.
 *
 */
public class MetricFactory {

	/**
	 * Chooses and builds the right metric and associates it to an image
	 * @param metricType; should not be null
	 * @param image; should not be null
	 * @return the right metric object
	 * 
	 * @throws NullPointerException if metricType or image is null
	 */
	public static Metric initMetric(TypeOfMetric metricType, BufferedImage image) {

		switch(metricType){
		
			case RADIOMETRIC_MIN_MAX: return new Ominmax(image);
			case RADIOMETRIC_AVERAGE: return new RadiometricAverage(image);
			case PRECISED_ELONGATION: return new Elongation(TypeOfMetric.PRECISED_ELONGATION, image);
			case SIMPLE_ELONGATION: return new Elongation(TypeOfMetric.SIMPLE_ELONGATION, image);
			case FAST_ELONGATION: return new Elongation(TypeOfMetric.FAST_ELONGATION, image);
			case SMOOTHNESS: return new Smoothness(image);
			case FAST_SMOOTHNESS: return new FastSmoothness(image);	
			case FAST_COMPACTNESS: return new FastCompactness(image);	
			case NDVI: return new NdviMetric(image);
			case NDWI: return new NdwiMetric(image);
			case CL_MM_NDVI: return new MmNdvi(image);
			case CL_MM_NDWI: return new MmNdwi(image);
			case CL_MM_COMPACTNESS: return new MmCompacity(image);
			case CL_MM_NDVI_NDWI:return new MmNdviNdwi(image);
			case CL_MM_FEL_NDVI: return new MmFelNdvi(image);
			case CL_MM_FEL_NDVI_NDWI: return new MmFelNdviNdwi(image);
			case VECTORIAL_DISTANCE: return new VectorialDistance(image);
			case ORANDOM: return new Orandom(image);
			case OMIN_MAX: return new Ominmax(image);
			case OMSE: return new Omse(image);
			case OWSDM: return new Owsdm(image);
			case OCONTOUR: return new Ocontour_(image);
			case OCOL_CONT_MIN_MAX: return new Ocolcont(image, TypeOfMetric.OMIN_MAX, 0.5);
			case OCOL_CONT_MSE: return new Ocolcont(image, TypeOfMetric.OMSE, 0.5);
			case OCOL_CONT_MSE_LAB: return new Ocolcont(image, TypeOfMetric.OMSE_LAB, 0.5);
			case OCOL_CONT_WSDM: return new Ocolcont(image, TypeOfMetric.OWSDM, 0.5);
			default: return new Ominmax(image);
		}
	}
}
