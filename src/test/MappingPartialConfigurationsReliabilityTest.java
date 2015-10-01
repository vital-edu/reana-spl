package test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import FeatureFamilyBasedAnalysisTool.Evaluation;
import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.Feature;
import FeatureFamilyBasedAnalysisTool.FeatureModel;
import FeatureFamilyBasedAnalysisTool.PartialConfiguration;
import FeatureFamilyBasedAnalysisTool.RDGNode;

public class MappingPartialConfigurationsReliabilityTest {
	
	FeatureModel fmBSN; 
	HashSet<Feature> features; 
	
	RDGNode rdgSqlite, 
	 		rdgMemory,
	 		rdgFile, 
	 		rdgOxygenation, 
	 		rdgTemperature, 
	 		rdgPulseRate,
	 		rdgPosition, 
	 		rdgFall, 
	 		rdgSituation; 
	
	Feature fSqlite, 
			fMemory, 
			fFile, 
			fOxygenation, 
			fTemperature, 
			fPulseRate, 
			fPosition, 
			fFall, 
			fSituation; 
	
	FDTMC 	fdtmcSqlite, 
			fdtmcMemory, 
			fdtmcFile, 
			fdmcOxygenation, 
			fdtmcTemperature, 
			fdtmcPulseRate, 
			fdtmcPosition, 
			fdtmcFall, 
			fdtmcSituation;

	@Before
	public void setUp() throws Exception {
		
		features = new HashSet<Feature>();
		features.add(Feature.getFeatureByName("Sqlite"));
		features.add(Feature.getFeatureByName("Memory"));
		features.add(Feature.getFeatureByName("File"));
		features.add(Feature.getFeatureByName("Oxygenation"));
		features.add(Feature.getFeatureByName("Temperature"));
		features.add(Feature.getFeatureByName("PulseRate"));
		features.add(Feature.getFeatureByName("Position"));
		features.add(Feature.getFeatureByName("Fall"));
		features.add(Feature.getFeatureByName("Situation"));
		fmBSN = new FeatureModel();
		fmBSN.addFeatures(features);
	  
		//File setup
		fFile = Feature.getFeatureByName("File"); 
		rdgFile = new RDGNode();
		rdgFile.setFeature(fFile);
		
		//Memory setup
		fMemory = Feature.getFeatureByName("Memory");
		rdgMemory = new RDGNode(); 
		rdgMemory.setFeature(fMemory);
		
		//Sqlite setup
		fSqlite = Feature.getFeatureByName("Sqlite"); 
		rdgSqlite = new RDGNode(); 
		rdgSqlite.setFeature(fSqlite);
		
		//Oxygenation setup
		fOxygenation = Feature.getFeatureByName("Oxygenation"); 
		rdgOxygenation = new RDGNode(); 
		rdgOxygenation.addFeatureDependency(fSqlite); 
		rdgOxygenation.addFeatureDependency(fMemory);
		rdgOxygenation.addFeatureDependency(fFile);
		rdgOxygenation.setFeature(fOxygenation);
	}

	@Test
	public void testFile() {
		rdgFile.firstStep();
		Assert.assertEquals(fFile, rdgFile.getFeature());
		Assert.assertNotNull(rdgFile.getFDTMC());
		
		rdgFile.secondStep(fmBSN);
		Assert.assertNotNull(rdgFile.getBDD());
		Assert.assertTrue(rdgFile.getFeatureDependencies().isEmpty());
		Assert.assertNotNull(rdgFile.getBDD().getProjection());
		Assert.assertTrue(rdgFile.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertTrue(rdgFile.getEvaluations().isEmpty());
		
		rdgFile.thirdStep();
		
		rdgFile.fourthStep();
		Evaluation expected = new Evaluation(new HashSet<Feature>(), 0.997002999);
		
		Assert.assertEquals(1, rdgFile.getEvaluations().size());
		Assert.assertTrue(areEvaluationsEquals(expected, rdgFile.getEvaluations()));		
	}
	
	
	@Test
	public void testMemory() {
		rdgMemory.firstStep();
		Assert.assertEquals(fMemory, rdgMemory.getFeature());
		Assert.assertNotNull(rdgMemory.getFDTMC());
		
		rdgMemory.secondStep(fmBSN); 
		Assert.assertNotNull(rdgMemory.getBDD());
		Assert.assertTrue(rdgMemory.getFeatureDependencies().isEmpty());
		Assert.assertNotNull(rdgMemory.getBDD().getProjection());
		Assert.assertTrue(rdgMemory.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertTrue(rdgMemory.getEvaluations().isEmpty());
		
		rdgMemory.thirdStep();
		
		rdgMemory.fourthStep();
		Evaluation expected = new Evaluation(new HashSet<Feature>(), 0.998001);
		
		Assert.assertEquals(1, rdgMemory.getEvaluations().size());
		Assert.assertTrue(areEvaluationsEquals(expected, rdgMemory.getEvaluations()));
	}
	
	
	@Test
	public void testSqlite() {
		rdgSqlite.firstStep();
		Assert.assertEquals(fSqlite, rdgSqlite.getFeature());
		Assert.assertNotNull(rdgSqlite.getFDTMC());
		
		rdgSqlite.secondStep(fmBSN);
		Assert.assertNotNull(rdgSqlite.getBDD());
		Assert.assertTrue(rdgSqlite.getFeatureDependencies().isEmpty());
		Assert.assertNotNull(rdgSqlite.getBDD().getProjection());
		Assert.assertTrue(rdgSqlite.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertTrue(rdgSqlite.getEvaluations().isEmpty());
		
		rdgSqlite.thirdStep();
		
		rdgSqlite.fourthStep();
		Evaluation expected = new Evaluation(new HashSet<Feature>(), 0.998001);
		
		Assert.assertEquals(1, rdgSqlite.getEvaluations().size());
		Assert.assertTrue(areEvaluationsEquals(expected, rdgSqlite.getEvaluations()));
	}

	
	@Test
	public void testOxygenation() {
		//The evaluation set for Oxygenation is given by the elements: 
		//{ Sqlite } : 0.9890587955100504
		//{ File } : 0.9890548353295385
		//{ Memory } : 0.9890587955100504
		Evaluation evSqlite = new Evaluation(), 
				   evFile   = new Evaluation(),
				   evMemory = new Evaluation(); 
		evSqlite.addFeature(Feature.getFeatureByName("Sqlite"));
		evSqlite.setRealiability(0.9890587955100504);
		evFile.addFeature(Feature.getFeatureByName("File")); 
		evFile.setRealiability(0.9890548353295385);
		evMemory.addFeature(Feature.getFeatureByName("Memory"));
		evMemory.setRealiability(0.9890587955100504);

		
		//Sqlite, mem and file setup
		rdgSqlite.firstStep(); rdgSqlite.secondStep(fmBSN); rdgSqlite.thirdStep(); rdgSqlite.fourthStep();
		rdgMemory.firstStep(); rdgMemory.secondStep(fmBSN); rdgMemory.thirdStep(); rdgMemory.fourthStep();
		rdgFile.firstStep(); rdgFile.secondStep(fmBSN); rdgFile.thirdStep(); rdgFile.fourthStep();
		
		rdgOxygenation.firstStep(); 
		Assert.assertEquals(fOxygenation, rdgOxygenation.getFeature());
		Assert.assertNotNull(rdgOxygenation.getFDTMC());
		System.out.println(rdgOxygenation.getFDTMC());
		
		rdgOxygenation.secondStep(fmBSN);
		Assert.assertNotNull(rdgOxygenation.getBDD());
		Assert.assertFalse(rdgOxygenation.getFeatureDependencies().isEmpty());
		Assert.assertEquals(3, rdgOxygenation.getFeatureDependencies().size());
		Assert.assertNotNull(rdgOxygenation.getBDD().getProjection()); 
		Assert.assertFalse(rdgOxygenation.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertEquals(3, rdgOxygenation.getBDD().getProjection().getFeatures().size());
		
		rdgOxygenation.thirdStep();
		
		rdgOxygenation.fourthStep();
		
		//Ensure the number of elements at the set of evaluations is equal to the number of partial configurations and
		//ensure each evaluation (i.e. each mapping PartialConfiguration -> double) is present.
		Assert.assertEquals(3, rdgOxygenation.getEvaluations().size());
		Assert.assertTrue(rdgOxygenation.contains(evSqlite));
		Assert.assertTrue(rdgOxygenation.contains(evMemory));
		Assert.assertTrue(rdgOxygenation.contains(evFile));				
	}
	

	
	private boolean areEvaluationsEquals(Evaluation expected,
			Collection<Evaluation> evaluations) {
		
		Iterator <Evaluation> it = evaluations.iterator();
		while (it.hasNext()) {
			Evaluation e = it.next();
			if (e.getFeatures().equals(expected.getFeatures()) && 
					(e.getReliability() == expected.getReliability())) {
				return true;
			}
		}
		
		
		return false;
	}

}
