package test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import FeatureFamilyBasedAnalysisTool.DependantFeatureEvaluation;
import FeatureFamilyBasedAnalysisTool.Feature;
import FeatureFamilyBasedAnalysisTool.PartialConfiguration;

public class DependantFeatureEvaluationTest {

	Feature sqlite, mem, file, oxygenation;
	DependantFeatureEvaluation eSqlite, eMem, eFile, eOxygenation, eEmpty; 
	
	PartialConfiguration pc1, pc2, pc3;
//	Evaluation ev1, ev2, ev3; 
	
	@Before
	public void setUp() throws Exception {
		sqlite = Feature.getFeatureByName("Sqlite");
		mem = Feature.getFeatureByName("Memory"); 
		file = Feature.getFeatureByName("File");
		
		pc1 = new PartialConfiguration(); 
		pc1.addFeature(sqlite); 
		
		pc2 = new PartialConfiguration(); 
		pc2.addFeature(mem);
		
		pc3 = new PartialConfiguration(); 
		pc3.addFeature(file);
	}

	
	/**
	 * Test for creating empty dependant feature evaluation
	 */
	@Test
	public void testEmptyFeatureEvaluation() {
		eEmpty = new DependantFeatureEvaluation();
		
		Assert.assertNotNull(eEmpty.getPartialConfiguration());
		Assert.assertEquals(0, eEmpty.getPartialConfiguration().size());
		Assert.assertNotNull(eEmpty.getFeatureReliability());
		Assert.assertEquals(0, eEmpty.getFeatureReliability().size());
	}
	
	
	/**
	 * Test for creating dependant feature Evaluation with empty partial configuration and only one reliability value (for using with basic RDG nodes)
	 */
	@Test
	public void  testEmptyPartialConfiguration() {
		HashMap<Feature, Double> featureReliability = new HashMap<Feature, Double>();
		featureReliability.put(sqlite, 0.998001);
		
		eSqlite = new DependantFeatureEvaluation(null, featureReliability);
		Assert.assertNull(eSqlite.getPartialConfiguration());
		Assert.assertNotNull(eSqlite.getFeatureReliability());
		Assert.assertEquals(1, eSqlite.getFeatureReliability().size());
		Assert.assertEquals(0.998001, eSqlite.getFeatureReliability().get(sqlite).doubleValue(), 0);
		
		featureReliability = new HashMap<Feature, Double>();
		featureReliability.put(mem, 0.998001); 
		eMem = new DependantFeatureEvaluation(null, featureReliability);
		Assert.assertNull(eMem.getPartialConfiguration());
		Assert.assertNotNull(eMem.getFeatureReliability());
		Assert.assertEquals(1, eMem.getFeatureReliability().size()); 
		Assert.assertEquals(0.998001, eMem.getFeatureReliability().get(mem).doubleValue(), 0);
		
		featureReliability = new HashMap<Feature, Double>(); 
		featureReliability.put(file, 0.997002999);
		eFile = new DependantFeatureEvaluation(null, featureReliability);
		Assert.assertNull(eFile.getPartialConfiguration()); 
		Assert.assertNotNull(eFile.getFeatureReliability());
		Assert.assertEquals(1, eFile.getFeatureReliability().size());
		Assert.assertEquals(0.997002999, eFile.getFeatureReliability().get(file).doubleValue(), 0);		
	}

	
	/**
	 * Test for creating dependant feature Evaluation with initial partial configuration and features reliabilities set
	 */
	@Test
	public void testFeatureEvaluationInitiallyDefined() {
		Collection<Feature> pcOxygenation = new HashSet<Feature>();
		pcOxygenation.add(sqlite); 
		HashMap<Feature, Double> featureReliability = new HashMap<Feature, Double>(); 
		featureReliability.put(sqlite, 0.998001);
		
		eOxygenation = new DependantFeatureEvaluation(pcOxygenation, featureReliability);
		Assert.assertNotNull(eOxygenation);
		Assert.assertNotNull(eOxygenation.getPartialConfiguration()); 
		Assert.assertEquals(1, eOxygenation.getPartialConfiguration().size());
		Assert.assertNotNull(eOxygenation.getFeatureReliability());
		Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(sqlite).doubleValue(), 0);
		
		
		
		pcOxygenation = new HashSet<Feature>(); 
		pcOxygenation.add(mem); 
		featureReliability = new HashMap<Feature, Double>();
		featureReliability.put(mem, 0.998001); 
		
		eOxygenation = new DependantFeatureEvaluation(pcOxygenation, featureReliability);
		Assert.assertNotNull(eOxygenation);
		Assert.assertNotNull(eOxygenation.getPartialConfiguration()); 
		Assert.assertEquals(1, eOxygenation.getPartialConfiguration().size());
		Assert.assertNotNull(eOxygenation.getFeatureReliability());
		Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(mem).doubleValue(), 0);
		
		
		pcOxygenation = new HashSet<Feature>(); 
		pcOxygenation.add(file); 
		featureReliability = new HashMap<Feature, Double>(); 
		featureReliability.put(file, 0.997002999);
		
		eOxygenation = new DependantFeatureEvaluation(pcOxygenation, featureReliability);
		Assert.assertNotNull(eOxygenation);
		Assert.assertNotNull(eOxygenation.getPartialConfiguration()); 
		Assert.assertEquals(1, eOxygenation.getPartialConfiguration().size());
		Assert.assertNotNull(eOxygenation.getFeatureReliability());
		Assert.assertEquals(0.997002999, eOxygenation.getFeatureReliability().get(file).doubleValue(), 0);
		
		//This partial Configuration is not valid, but it is being used to stress the capability of storing the reliability values for 
		//different features
		pcOxygenation = new HashSet<Feature>(); 
		pcOxygenation.add(sqlite); 
		pcOxygenation.add(mem); 
		pcOxygenation.add(file); 
		featureReliability = new HashMap<Feature, Double>(); 
		featureReliability.put(sqlite, 0.998001); 
		featureReliability.put(mem, 0.998001); 
		featureReliability.put(file, 0.997002999);
		
		eOxygenation = new DependantFeatureEvaluation(pcOxygenation, featureReliability); 
		Assert.assertNotNull(eOxygenation);
		Assert.assertNotNull(eOxygenation.getPartialConfiguration());
		Assert.assertEquals(3, eOxygenation.getPartialConfiguration().size());
		Assert.assertNotNull(eOxygenation.getFeatureReliability());
		Assert.assertEquals(3, eOxygenation.getFeatureReliability().size());
		Assert.assertTrue(eOxygenation.getFeatureReliability().containsKey(sqlite));
		Assert.assertTrue(eOxygenation.getFeatureReliability().containsKey(mem));
		Assert.assertTrue(eOxygenation.getFeatureReliability().containsKey(file));
		Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(sqlite).doubleValue(), 0);
		Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(mem).doubleValue(), 0);
		Assert.assertEquals(0.997002999, eOxygenation.getFeatureReliability().get(file).doubleValue(), 0); 
	}
	
	
	/**
	 * Test for adding one feature to partial configuration
	 */
	@Test
	public void testAddingOneFeatureToPartialConfiguration() {
		eOxygenation = new DependantFeatureEvaluation(); 
		Assert.assertTrue(eOxygenation.addFeature(sqlite));
		Assert.assertTrue(eOxygenation.addFeature(mem));
		Assert.assertTrue(eOxygenation.addFeature(file)); 
		
		Assert.assertFalse(eOxygenation.addFeature(sqlite));
		Assert.assertFalse(eOxygenation.addFeature(mem));
		Assert.assertFalse(eOxygenation.addFeature(file));
	}
	
	
	/**
	 * Test for adding more than one feature to partial configuration
	 */
	@Test
	public void testAddingMultipleFeaturesToPartialConfiguration() {
		Collection<Feature> pc = new HashSet<Feature>(); 
		pc.add(sqlite); 
		pc.add(mem); 
		pc.add(file); 
		
		eOxygenation = new DependantFeatureEvaluation(); 
		Assert.assertTrue(eOxygenation.addFeature(pc));
		Assert.assertTrue(eOxygenation.getPartialConfiguration().contains(sqlite));
		Assert.assertTrue(eOxygenation.getPartialConfiguration().contains(mem));
		Assert.assertTrue(eOxygenation.getPartialConfiguration().contains(file));
	}
	
	
	/**
	 * Test for adding one feature reliability evaluation
	 */
	@Test
	public void testAddingOneFeatureReliabilityEvaluation() {
		Collection<Feature> pc = new HashSet<Feature>();
		pc.add(sqlite); 
		
		eOxygenation = new DependantFeatureEvaluation(); 
		eOxygenation.addFeature(pc); 
		Double answer = eOxygenation.addFeatureReliabilityValue(sqlite, 0.998001);
		
		Assert.assertNotNull(eOxygenation);
		Assert.assertTrue(eOxygenation.getFeatureReliability().containsKey(sqlite));
		Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(sqlite).doubleValue(), 0);
	}
	
	
	/**
	 * Add more than one feature reliability evaluation 
	 */
	 @Test
	 public void testAddingMultipleReliabilityEvaluations() {
		 Collection<Feature> pc = new HashSet<Feature>();		 
		 pc.add(sqlite); 
		 pc.add(mem);

		 HashMap<Feature, Double> featureEvaluation = new HashMap<Feature, Double>(); 
		 featureEvaluation.put(sqlite, 0.998001); 
		 featureEvaluation.put(mem, 0.998001);
		 
		 eOxygenation = new DependantFeatureEvaluation(); 
		 eOxygenation.addFeatureReliabilityValue(featureEvaluation);
		 
		 Assert.assertNotNull(eOxygenation);
		 Assert.assertNotNull(eOxygenation.getFeatureReliability()); 
		 Assert.assertEquals(2, eOxygenation.getFeatureReliability().size());
		 Assert.assertTrue(eOxygenation.getFeatureReliability().containsKey(sqlite));
		 Assert.assertTrue(eOxygenation.getFeatureReliability().containsKey(mem));
		 Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(sqlite).doubleValue(), 0);
		 Assert.assertEquals(0.998001, eOxygenation.getFeatureReliability().get(mem).doubleValue(), 0);
		 
	 }
}
