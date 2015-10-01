package test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import FeatureFamilyBasedAnalysisTool.Evaluation;
import FeatureFamilyBasedAnalysisTool.Feature;
import FeatureFamilyBasedAnalysisTool.FeatureModel;
import FeatureFamilyBasedAnalysisTool.RDGNode;

public class ReliabilityEvaluationTest {

	RDGNode node;
	
	FeatureModel fmBSN; 
	
	
	Feature sqlite = Feature.getFeatureByName("Sqlite"), 
			mem = Feature.getFeatureByName("Memory"), 
			file = Feature.getFeatureByName("File"), 
			sensorECG = Feature.getFeatureByName("ECG"), 
			sensorSPO2 = Feature.getFeatureByName("SPO2"), 
			sensorTemperature = Feature.getFeatureByName("Temp"), 
			sensorACC = Feature.getFeatureByName("ACC"), 
			fall = Feature.getFeatureByName("Fall"), 
			oxygenation = Feature.getFeatureByName("Oxygenation"), 
			position = Feature.getFeatureByName("Position"), 
			pulseRate = Feature.getFeatureByName("PulseRate"), 
			temperature = Feature.getFeatureByName("Temperature");
	
	private String fdtmcSqlite = "sSqlite=0(init) --- persist / 0.999 ---> sSqlite=3" + '\n' + 
			             "sSqlite=0(init) --- persist / 0.001 ---> sSqlite=2(fail)" + '\n' + 
			             "sSqlite=1(success) ---  / 1.0 ---> sSqlite=1(success)" + '\n' + 
			             "sSqlite=2(fail) ---  / 1.0 ---> sSqlite=2(fail)" + '\n' + 
			             "sSqlite=3 --- persist_return / 0.999 ---> sSqlite=1(success)" + '\n' + 
			             "sSqlite=3 --- persist_return / 0.001 ---> sSqlite=2(fail)" + '\n';

	private String fdtmcFile = "sFile=0(init) --- persist / 0.999 ---> sFile=3" + '\n' + 
			"sFile=0(init) --- persist / 0.001 ---> sFile=2(fail)" + '\n' + 
			"sFile=1(success) ---  / 1.0 ---> sFile=1(success)" + '\n' + 
			"sFile=2(fail) ---  / 1.0 ---> sFile=2(fail)" + '\n' + 
			"sFile=3 --- write / 0.999 ---> sFile=4" + '\n' + 
			"sFile=3 --- write / 0.001 ---> sFile=2(fail)" + '\n' + 
			"sFile=4 --- persistReturn / 0.999 ---> sFile=1(success)" + '\n' + 
			"sFile=4 --- persistReturn / 0.001 ---> sFile=2(fail)" + '\n';

	private String fdtmcOxygenation = "sOxygenation=0(init) --- register / 0.999 ---> sOxygenation=3" + '\n' +
			"sOxygenation=0(init) --- register / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=1(success) ---  / 1.0 ---> sOxygenation=1(success)" + '\n' + 
			"sOxygenation=2(fail) ---  / 1.0 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=3 --- register_return / 0.999 ---> sOxygenation=4" + '\n' + 
			"sOxygenation=3 --- register_return / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=4 --- sendSituation_SPO2 / 0.999 ---> sOxygenation=5" + '\n' + 
			"sOxygenation=4 --- sendSituation_SPO2 / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=5 --- persist / 0.999 ---> sOxygenation=6" + '\n' + 
			"sOxygenation=5 --- persist / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=6 --- sqliteSelection / fSqlite ---> sOxygenation=7" + '\n' + 
			"sOxygenation=6 --- sqliteSelection / 1-fSqlite ---> sOxygenation=8" + '\n' + 
			"sOxygenation=7 ---  / rSqlite ---> sOxygenation=9" + '\n' + 
			"sOxygenation=7 ---  / 1-rSqlite ---> sOxygenation=10" + '\n' + 
			"sOxygenation=8 --- memorySelection / fMemory ---> sOxygenation=11" + '\n' + 
			"sOxygenation=8 --- memorySelection / 1-fMemory ---> sOxygenation=12" + '\n' + 
			"sOxygenation=9 ---  / 1.0 ---> sOxygenation=8" + '\n' + 
			"sOxygenation=10 ---  / 1.0 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=11 ---  / rMemory ---> sOxygenation=13" + '\n' + 
			"sOxygenation=11 ---  / 1-rMemory ---> sOxygenation=14" + '\n' + 
			"sOxygenation=12 --- persistReturn / 0.999 ---> sOxygenation=15" + '\n' + 
			"sOxygenation=12 --- persistReturn / 0.001 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=13 ---  / 1.0 ---> sOxygenation=12" + '\n' + 
			"sOxygenation=14 ---  / 1.0 ---> sOxygenation=2(fail)" + '\n' + 
			"sOxygenation=15 --- sendSituation_Oxygenation / 0.999 ---> sOxygenation=1(success)" + '\n' + 
			"sOxygenation=15 --- sendSituation_Oxygenation / 0.001 ---> sOxygenation=2(fail)" + '\n'; 

	private String fdtmcPosition = "sPosition=0(init) --- register / 0.999 ---> sPosition=3" + '\n' + 
			"sPosition=0(init) --- register / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=1(success) ---  / 1.0 ---> sPosition=1(success)" + '\n' + 
			"sPosition=2(fail) ---  / 1.0 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=3 --- register_return / 0.999 ---> sPosition=4" + '\n' + 
			"sPosition=3 --- register_return / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=4 --- sendSituation_POS / 0.999 ---> sPosition=5" + '\n' + 
			"sPosition=4 --- sendSituation_POS / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=5 --- persist / 0.999 ---> sPosition=6" + '\n' + 
			"sPosition=5 --- persist / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=6 --- sqliteSelection / fSqlite ---> sPosition=7" + '\n' + 
			"sPosition=6 --- sqliteSelection / 1-fSqlite ---> sPosition=8" + '\n' + 
			"sPosition=7 ---  / rSqlite ---> sPosition=9" + '\n' + 
			"sPosition=7 ---  / 1-rSqlite ---> sPosition=10" + '\n' + 
			"sPosition=8 --- memorySelection / fMemory ---> sPosition=11" + '\n' + 
			"sPosition=8 --- memorySelection / 1-fMemory ---> sPosition=12" + '\n' + 
			"sPosition=9 ---  / 1.0 ---> sPosition=8" + '\n' + 
			"sPosition=10 ---  / 1.0 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=11 ---  / rMemory ---> sPosition=13" + '\n' + 
			"sPosition=11 ---  / 1-rMemory ---> sPosition=14" + '\n' + 
			"sPosition=12 --- persistReturn / 0.999 ---> sPosition=15" + '\n' + 
			"sPosition=12 --- persistReturn / 0.001 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=13 ---  / 1.0 ---> sPosition=12" + '\n' + 
			"sPosition=14 ---  / 1.0 ---> sPosition=2(fail)" + '\n' + 
			"sPosition=15 --- sendSituation_Position / 0.999 ---> sPosition=1(success)" + '\n' + 
			"sPosition=15 --- sendSituation_Position / 0.001 ---> sPosition=2(fail)" + '\n';
	

	@Before
	public void setUp() {
		
		//Criacao do feature Model. 
		HashSet <Feature> featuresBSN = new HashSet<Feature>();
		featuresBSN.add(sqlite); 
		featuresBSN.add(mem);
		featuresBSN.add(file);
		featuresBSN.add(sensorECG);
		featuresBSN.add(sensorSPO2);
		featuresBSN.add(sensorTemperature);
		featuresBSN.add(sensorACC);
		featuresBSN.add(fall);
		featuresBSN.add(oxygenation);
		featuresBSN.add(position);
		featuresBSN.add(pulseRate);
		featuresBSN.add(temperature);
		
		fmBSN = new FeatureModel(); 
		fmBSN.addFeatures(featuresBSN);
		
	}
	
	
	/**
	 * These tests should be able to assert if dependencies among different RDG nodes are being created successfully.
	 */
	@Test
	public void testCreateRDGNodeWithoutDependenciesSqlite() {
		/*
		 * This test must ensure that a basic node (without dependencies):
		 * OK feature associated with the RDGnode is the feature under evaluation;
		 * OK featureDependencies is equal to null, because the feature under evaluation is a basic feature;
		 * - bdd is equal to the constant bdd 1;
		 * - RDGdepencies set is empty because it's a basic feature.
		 * OK fdtmc is equal to the FDTMC of the feature under evaluation, without variability;   
		 * OK evaluations set has only one element, i.e., has only one evaluation value.
		 */

		node = new RDGNode();
		node.setFeature(sqlite);
		Assert.assertEquals(node.getFeature(), sqlite);
		Assert.assertSame(node.getFeature(), sqlite);
		
		
		//1st step - build the FDTMC for the RDGNode.
		node.firstStep();
		Assert.assertNotNull(node.getFDTMC());
		Assert.assertEquals(fdtmcSqlite, node.getFDTMC().toString());

		//2nd step - discover the feature dependencies and compute the BDD for the RDG node
		node.secondStep(fmBSN); 
		Assert.assertTrue(node.getFeatureDependencies().isEmpty());
		Assert.assertNotNull(node.getBDD());
		Assert.assertTrue(node.getBDD().getDependantFeatures().isEmpty());
		Assert.assertTrue(node.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertTrue(node.getBDD().getValidPartialConfigurations().isEmpty());

		//3rd step - analyse sharing possibilities for the FDTMC
		node.thirdStep();
		Assert.assertEquals(fdtmcSqlite, node.getFDTMC().toString());
		
		//4th step - call the PARAM to obtain the formula and evaluates it according to the partial configuration in 
		//order to get evaluations
		node.fourthStep(); 
		//Ensure the evaluation set has size equals to one.
		Assert.assertEquals(1, node.getEvaluations().size());
		//Ensure the only evaluation item has no features for its partial configuration
		//Ensure the reliability value is equal to SQLite reliability (0.998001)
		Collection<Evaluation> evaluations = node.getEvaluations(); 
		Iterator<Evaluation> it = evaluations.iterator(); 
		while (it.hasNext()) {
			Evaluation e = it.next();
			Assert.assertTrue(e.getFeatures().isEmpty());
			Assert.assertEquals(0.998001, e.getReliability(), 0);
		}
		
	}
	
	
	@Test
	public void testeCreateRGDNodeWithoutDependenciesFile() {
		node = new RDGNode(); 
		node.setFeature(file);
		
		Assert.assertEquals(file, node.getFeature());
		Assert.assertSame(file, node.getFeature());
		
		//1st step - build the fdtmc for the RDGNode. 
		node.firstStep(); 
		Assert.assertNotNull(node.getFDTMC());
		Assert.assertEquals(fdtmcFile, node.getFDTMC().toString());
		
		//2nd step - discover the features dependencies and compute the BDD for the RDG node
		node.secondStep(fmBSN);
		Assert.assertTrue(node.getFeatureDependencies().isEmpty());
		Assert.assertTrue(node.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertTrue(node.getBDD().getValidPartialConfigurations().isEmpty());
		
		//3rd step - analyse sharing possibilities for the FDTMC
		node.thirdStep();
		Assert.assertEquals(fdtmcFile, node.getFDTMC().toString());
		
		//4th step - call the PARAM to obtain the formula and evaluates it according to the partial configurations in
		//order to get evaluations
		node.fourthStep();
		//Ensure the evaluation set has size equals to one
		Assert.assertEquals(1, node.getEvaluations().size());
		//Ensure the only evaluation item has no features for its partial configuration
		//Ensure the reliability value is equal to File Reliability (0,997002999)
		Collection<Evaluation> evaluations = node.getEvaluations();
		Iterator<Evaluation> it = evaluations.iterator();
		while(it.hasNext()) {
			Evaluation e = it.next();
			Assert.assertTrue(e.getFeatures().isEmpty());
			Assert.assertEquals(0.997002999, e.getReliability(), 0);
		}
	}
	
	
	
	@Test
	public void testCreateRDGNodeWithDependenciesPosition() {
		/**
		 * Evaluation set for fabe function
		 * { Memory } : 0.9890587955100504
		 * { File } : 0.9890548353295385
		 * { Sqlite } : 0.9890587955100504
		 */
		Evaluation evSqlite = new Evaluation(), 
				   evMemory = new Evaluation(), 
				   evFile   = new Evaluation(); 
		evSqlite.addFeature(Feature.getFeatureByName("Sqlite"));
		evSqlite.setRealiability(0.9890587955100504);
		evMemory.addFeature(Feature.getFeatureByName("Memory"));
		evMemory.setRealiability(0.9890587955100504);
		evFile.addFeature(Feature.getFeatureByName("File"));
		evFile.setRealiability(0.9890548353295385);
		
		
		/*
		 * Setup for this RDG node. It depends on features Sqlite, Mem and File. 
		 */
		RDGNode sqliteNode = new RDGNode(), 
				memNode = new RDGNode(), 
				fileNode = new RDGNode();
		sqliteNode.setFeature(sqlite);
		memNode.setFeature(mem);
		fileNode.setFeature(file);
		
		sqliteNode.firstStep(); sqliteNode.secondStep(fmBSN); sqliteNode.thirdStep(); sqliteNode.fourthStep();
		memNode.firstStep(); memNode.secondStep(fmBSN); memNode.thirdStep(); memNode.fourthStep();
		fileNode.firstStep(); fileNode.secondStep(fmBSN); fileNode.thirdStep(); fileNode.fourthStep();
		
		/*
		 * This test must ensure that a variable node (with dependencies to other RDG nodes): 
		 * - bdd express the FM rules comprising the dependant features
		 * - fdtmc is equal to the FDTMC of the feature under evaluation, with variability. 
		 * 	  (REMARK: it should be good if there's a way to track feature interfaces of the FDTMC.
		 * - feature associated with the RDGnode is the feature under evaluation; 
		 * - featureDependencies is equal to the set of features the feature under evaluation depends on
		 * - evaluation set has the number of evaluations items equals to the number of valid partial configurations
		 * - RDG dependencies set has dependencies to RDGnodes related to the features the nodes depends on. So the cardinality of 
		 * RDG Dependency set is equal to the cardinality of featureDependencies set.    
		 */
		node = new RDGNode(); 
		node.setFeature(position);
		node.addFeatureDependency(sqlite);
		node.addFeatureDependency(mem);
		node.addFeatureDependency(file);
		
		//1st step: build the FDTMC for the RDG node
		node.firstStep();
		Assert.assertNotNull(node.getFDTMC());
		System.out.println(node.getFDTMC());
		Assert.assertEquals(fdtmcPosition, node.getFDTMC().toString());

		//2nd step - Discover the features dependencies and compute the BDD for the RDG node
		node.secondStep(fmBSN);
		Assert.assertFalse(node.getFeatureDependencies().isEmpty());
		Assert.assertFalse(node.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertFalse(node.getBDD().getValidPartialConfigurations().isEmpty());
		Assert.assertEquals(3, node.getBDD().getValidPartialConfigurations().size());
		
		//3rd step - analyse sharing possibilities for the FDTMC
		node.thirdStep();
		
		//4th step - call the PARAM to obtain the formula and evaluates it according to the partial configurations in
		//order to get evaluations
				
		node.fourthStep();
		System.out.println(node.getEvaluations().size());
		Assert.assertEquals(3, node.getEvaluations().size());
				
		Collection<Evaluation> evaluationsPosition = node.getEvaluations();
		assertTrue(nodeContainsEvaluation(evaluationsPosition, evSqlite));
		assertTrue(nodeContainsEvaluation(evaluationsPosition, evMemory));
		assertTrue(nodeContainsEvaluation(evaluationsPosition, evFile));
	}
	
	
	@Test
	public void testCreateRDGNodeWithDependenciesOxygenation() {
		/**
		 * Evaluation set for fabe function
		 * { Memory } : 0.9890587955100504
		 * { File } : 0.9890548353295385
		 * { Sqlite } : 0.9890587955100504
		 */
		Evaluation evSqlite = new Evaluation(), 
				   evMemory = new Evaluation(), 
				   evFile   = new Evaluation(); 
		evSqlite.addFeature(Feature.getFeatureByName("Sqlite"));
		evSqlite.setRealiability(0.9890587955100504);
		evMemory.addFeature(Feature.getFeatureByName("Memory"));
		evMemory.setRealiability(0.9890587955100504);
		evFile.addFeature(Feature.getFeatureByName("File"));
		evFile.setRealiability(0.9890548353295385);
		
		
		/*
		 * Setup for this RDG node. It depends on features Sqlite, Mem and File. 
		 */
		RDGNode sqliteNode = new RDGNode(), 
				memNode = new RDGNode(), 
				fileNode = new RDGNode();
		sqliteNode.setFeature(sqlite);
		memNode.setFeature(mem);
		fileNode.setFeature(file);
		
		sqliteNode.firstStep(); sqliteNode.secondStep(fmBSN); sqliteNode.thirdStep(); sqliteNode.fourthStep();
		memNode.firstStep(); memNode.secondStep(fmBSN); memNode.thirdStep(); memNode.fourthStep();
		fileNode.firstStep(); fileNode.secondStep(fmBSN); fileNode.thirdStep(); fileNode.fourthStep();
		
		/*
		 * This test must ensure that a variable node (with dependencies to other RDG nodes): 
		 * - bdd express the FM rules comprising the dependant features
		 * - fdtmc is equal to the FDTMC of the feature under evaluation, with variability. 
		 * 	  (REMARK: it should be good if there's a way to track feature interfaces of the FDTMC.
		 * - feature associated with the RDGnode is the feature under evaluation; 
		 * - featureDependencies is equal to the set of features the feature under evaluation depends on
		 * - evaluation set has the number of evaluations items equals to the number of valid partial configurations
		 * - RDG dependencies set has dependencies to RDGnodes related to the features the nodes depends on. So the cardinality of 
		 * RDG Dependency set is equal to the cardinality of featureDependencies set.    
		 */
		node = new RDGNode(); 
		node.setFeature(oxygenation);
		node.addFeatureDependency(sqlite);
		node.addFeatureDependency(mem);
		node.addFeatureDependency(file);
		
		//1st step: build the FDTMC for the RDG node
		node.firstStep();
		Assert.assertNotNull(node.getFDTMC());
		Assert.assertEquals(fdtmcOxygenation, node.getFDTMC().toString());
		
		//2nd step - Discover the features dependencies and compute the BDD for the RDG node
		node.secondStep(fmBSN);
		Assert.assertFalse(node.getFeatureDependencies().isEmpty());
		Assert.assertFalse(node.getBDD().getProjection().getFeatures().isEmpty());
		Assert.assertFalse(node.getBDD().getValidPartialConfigurations().isEmpty());
		Assert.assertEquals(3, node.getBDD().getValidPartialConfigurations().size());

		//3rd step - analyse sharing possibilities for the FDTMC
		node.thirdStep();
		
		//4th step - call the PARAM to obtain the formula and evaluates it according to the partial configurations in
		//order to get evaluations
		
		node.fourthStep();
		System.out.println(node.getEvaluations().size());
		Assert.assertEquals(3, node.getEvaluations().size());
		
		Collection<Evaluation> evaluationsOxygenation = node.getEvaluations();
		assertTrue(nodeContainsEvaluation(evaluationsOxygenation, evSqlite));
		assertTrue(nodeContainsEvaluation(evaluationsOxygenation, evMemory));
		assertTrue(nodeContainsEvaluation(evaluationsOxygenation, evFile));
	}
	
	
	private boolean nodeContainsEvaluation(
			Collection<Evaluation> evaluationsOxygenation, Evaluation evSqlite) {
		boolean found = false; 
		Iterator<Evaluation> it = evaluationsOxygenation.iterator(); 
		while (it.hasNext() && !found) {
			Evaluation e = it.next(); 
			if (evSqlite.getFeatures().equals(e.getFeatures()) && (e.getReliability() == evSqlite.getReliability()))
				found = true; 
		}
		return found;
	}


	/**
	 * This test should be able to assert if a feature-based evaluation is being executed accordingly at a RDG node
	 * A feature-based evaluation must be able to create an FDTMC for a specific node. 
	 * FEBE: RDGNode --> FDTMC
	 */
	@Test
	public void testFebeEvaluation() {
		fail("Not yet implemented"); 
	}
	
	
	/**
	 * This test should be able to assert if an FDTMC is being created accordingly for a specific RDG node.
	 * MODELLING: RDGNode --> FDTMC 
	 */
	@Test
	public void testModelling() {
		fail ("Not yet implemented");
	}
	
	
	/**
	 * This test should be able to assert if an FDTMC interface is being created accordingly for a specific RDG node. 
	 * It must receive a set o evaluations as input and return an FDTMC interface as output. 
	 * EXTRACTINTERFACE: RDGNode --> FDTMC 
	 */
	@Test
	public void testExtractInterface() {
		fail("Not yet implemented");
	}
	
	
	/**
	 * This test should be able to assert if a family-based evaluation for a RDG node is being performed accordingly.
	 * The family-based evaluation must be performed over a RDG node and return a set of evaluations as answer. 
	 * It must also discard the invalid partial configurations, i.e., the evaluations which violates some FM rules.
	 * FABE: RDGNode --> [evaluation]
	 */
	@Test
	public void testFabeEvaluation() {
		fail("Not yet implemented");
	}
	

}
