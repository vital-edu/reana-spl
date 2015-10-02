package test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fdtmc.BDD;
import fdtmc.Evaluation;
import fdtmc.Feature;
import fdtmc.FeatureModel;
import fdtmc.PartialConfiguration;


public class BDDTest {

	BDD bdd1, bdd2;
	
	FeatureModel fm;
	Feature oxygenation, pulseRate, temperature, position, fall, sensorInformation, 
	        sSpo2, sEcg, sTemp, sAcc, sensor, monitoring, sqlite, memory, file, storage,
	        root;

	private HashSet<PartialConfiguration> partialConfigurationsSensorInformation;
	
	@Before
	public void  setUp() {
		bdd1 = new BDD();
		bdd2 = new BDD();
		
		oxygenation = Feature.getFeatureByName("Oxygenation");
		pulseRate = Feature.getFeatureByName("PulseRate");
		temperature = Feature.getFeatureByName("Temperature");
		position = Feature.getFeatureByName("Position");
		fall = Feature.getFeatureByName("Fall");
		sensorInformation = Feature.getFeatureByName("SensorInformation");
		sSpo2 = Feature.getFeatureByName("SPO2");
		sEcg = Feature.getFeatureByName("ECG");
		sTemp = Feature.getFeatureByName("Temp");
		sAcc = Feature.getFeatureByName("ACC");
		sensor = Feature.getFeatureByName("Sensor");
		monitoring = Feature.getFeatureByName("Monitoring");
		sqlite = Feature.getFeatureByName("Sqlite");
		memory = Feature.getFeatureByName("Memory");
		file = Feature.getFeatureByName("File"); 
		storage = Feature.getFeatureByName("Storage");
		root = Feature.getFeatureByName("Root");
		
		/**
		 * Feature Model creation: the constructor method receives a set of features
		 * as input for the FM.  
		 */
		fm = new FeatureModel(); 
		HashSet<Feature> tempPartialCfg = new HashSet<Feature>();
		
		tempPartialCfg.add(oxygenation);
		tempPartialCfg.add(pulseRate);
		tempPartialCfg.add(temperature);
		tempPartialCfg.add(position);
		tempPartialCfg.add(fall);
		tempPartialCfg.add(sensorInformation);
		tempPartialCfg.add(sSpo2);
		tempPartialCfg.add(sEcg);
		tempPartialCfg.add(sTemp);
		tempPartialCfg.add(sAcc);
		tempPartialCfg.add(sensor); 
		tempPartialCfg.add(monitoring);
		tempPartialCfg.add(sqlite);
		tempPartialCfg.add(memory);
		tempPartialCfg.add(file);
		tempPartialCfg.add(storage);
		tempPartialCfg.add(root);
		fm.addFeatures(tempPartialCfg);		
		bdd1.setFeatureModel(fm);        //Each BDD has a Feature Model associated with for projection purposes!
		
		/** 
		 * Set of partial configurations created for validation of BDD Class. 
		 * The BDD class must return a set of partial configuration equals to 
		 * (i.e. with the same elements and cardinality) of partial configura-
		 * tions described below.  
		 */
		PartialConfiguration p0, p1, p2, p3, p4, p5,
		                     p6, p7, p8, p9, p10, p11, 
		                     p12, p13, p14, p15, p16, 
		                     p17, p18, p19, p20, p21, 
		                     p22, p23, p24, p25, p26, 
		                     p27, p28, p29, p30, p31;
		
		partialConfigurationsSensorInformation = new HashSet<PartialConfiguration>();
		//{}
		p0 = new PartialConfiguration(new HashSet<Feature>()); 
		partialConfigurationsSensorInformation.add(p0); 

		
		//{fall}
		p1 = new PartialConfiguration(); 
		p1.addFeature(fall);
		partialConfigurationsSensorInformation.add(p1);
	
		//{position}	
		p2 = new PartialConfiguration(); 
		p2.addFeature(position);
		partialConfigurationsSensorInformation.add(p2); 

		//{position, fall}
		p3 = new PartialConfiguration();
		p3.addFeature(position);
		p3.addFeature(fall);
		partialConfigurationsSensorInformation.add(p3);

		//{temperature}		
		p4 = new PartialConfiguration(); 
		p4.addFeature(temperature);
		partialConfigurationsSensorInformation.add(p4);
		
		//{temperature, fall}
		p5 = new PartialConfiguration();
		p5.addFeature(temperature);
		p5.addFeature(fall);
		partialConfigurationsSensorInformation.add(p5);

		//{temperature, position}	
		p6 = new PartialConfiguration();
		p6.addFeature(temperature);
		p6.addFeature(position);
		partialConfigurationsSensorInformation.add(p6);
		
		//{temperature, position, fall}
		p7 = new PartialConfiguration(); 
		p7.addFeature(temperature);
		p7.addFeature(position);
		p7.addFeature(fall);
		partialConfigurationsSensorInformation.add(p7);
		
		//{pulseRate}			
		p8 = new PartialConfiguration(); 
		p8.addFeature(pulseRate);
		partialConfigurationsSensorInformation.add(p8);
		
		//{pulseRate, fall}
		p9 = new PartialConfiguration();
		p9.addFeature(pulseRate);
		p9.addFeature(fall);
		partialConfigurationsSensorInformation.add(p9);

		//{pulseRate, position}	
		p10 = new PartialConfiguration();
		p10.addFeature(pulseRate);
		p10.addFeature(position);
		partialConfigurationsSensorInformation.add(p10);
		
		//{pulseRate, position, fall}
		p11 = new PartialConfiguration(); 
		p11.addFeature(pulseRate);
		p11.addFeature(position);
		p11.addFeature(fall);
		partialConfigurationsSensorInformation.add(p11);
		
		//{pulseRate, temperature}		
		p12 = new PartialConfiguration(); 
		p12.addFeature(pulseRate);
		p12.addFeature(temperature);
		partialConfigurationsSensorInformation.add(p12);
		
		//{pulseRate, temperature, fall}
		p13 = new PartialConfiguration(); 
		p13.addFeature(pulseRate);
		p13.addFeature(temperature);
		p13.addFeature(fall);
		partialConfigurationsSensorInformation.add(p13); 
		
		//{pulseRate, temperature, position}	
		p14 = new PartialConfiguration(); 
		p14.addFeature(pulseRate);
		p14.addFeature(temperature);
		p14.addFeature(position);
		partialConfigurationsSensorInformation.add(p14);
		
		//{pulseRate, temperature, position, fall}
		p15 = new PartialConfiguration(); 
		p15.addFeature(pulseRate);
		p15.addFeature(temperature);
		p15.addFeature(position);
		p15.addFeature(fall);
		partialConfigurationsSensorInformation.add(p15);
		
		//{oxygenation}				
		p16 = new PartialConfiguration(); 
		p16.addFeature(oxygenation);
		partialConfigurationsSensorInformation.add(p16);
		
		//{oxygenation, fall}
		p17 = new PartialConfiguration(); 
		p17.addFeature(oxygenation);
		p17.addFeature(fall);
		partialConfigurationsSensorInformation.add(p17);
		
		//{oxygenation, position}	
		p18 = new PartialConfiguration(); 
		p18.addFeature(oxygenation);
		p18.addFeature(position);
		partialConfigurationsSensorInformation.add(p18);
		
		//oxygenation, position, fall}
		p19 = new PartialConfiguration(); 
		p19.addFeature(oxygenation);
		p19.addFeature(position);
		p19.addFeature(fall);
		partialConfigurationsSensorInformation.add(p19);
		
		//{oxygenation, temperature}		
		p20 = new PartialConfiguration(); 
		p20.addFeature(oxygenation);
		p20.addFeature(temperature);
		partialConfigurationsSensorInformation.add(p20);

		//{oxygenation, temperature, fall}
		p21 = new PartialConfiguration(); 
		p21.addFeature(oxygenation);
		p21.addFeature(temperature);
		p21.addFeature(fall);
		partialConfigurationsSensorInformation.add(p21);
		
		//{oxygenation, temperature, position}
		p22 = new PartialConfiguration();
		p22.addFeature(oxygenation);
		p22.addFeature(temperature);
		p22.addFeature(position);
		partialConfigurationsSensorInformation.add(p22);

		//{oxygenation, temperature, position, fall}
		p23 = new PartialConfiguration(); 
		p23.addFeature(oxygenation);
		p23.addFeature(temperature);
		p23.addFeature(position);
		p23.addFeature(fall);
		partialConfigurationsSensorInformation.add(p23);
		
		//{oxygenation, pulseRate}
		p24 = new PartialConfiguration(); 
		p24.addFeature(oxygenation);
		p24.addFeature(pulseRate);
		partialConfigurationsSensorInformation.add(p24);
		
		//{oxygenation, pulseRate, fall}
		p25 = new PartialConfiguration(); 
		p25.addFeature(oxygenation);
		p25.addFeature(pulseRate);
		p25.addFeature(fall);
		partialConfigurationsSensorInformation.add(p25);
		
		//{oxygenation, pulseRate, position}
		p26 = new PartialConfiguration(); 
		p26.addFeature(oxygenation);
		p26.addFeature(pulseRate);
		p26.addFeature(position);
		partialConfigurationsSensorInformation.add(p26);
		
		//{oxygenation, pulseRate, position, fall}
		p27 = new PartialConfiguration(); 
		p27.addFeature(oxygenation);
		p27.addFeature(pulseRate);
		p27.addFeature(position);
		p27.addFeature(fall);
		partialConfigurationsSensorInformation.add(p27);
		
		//{oxygenation, pulseRate, temperature}
		p28 = new PartialConfiguration(); 
		p28.addFeature(oxygenation);
		p28.addFeature(pulseRate);
		p28.addFeature(temperature);
		partialConfigurationsSensorInformation.add(p28);
		
		//{oxygenation, pulseRate, temperature, fall}
		p29 = new PartialConfiguration(); 
		p29.addFeature(oxygenation);
		p29.addFeature(pulseRate);
		p29.addFeature(temperature);
		p29.addFeature(fall);
		partialConfigurationsSensorInformation.add(p29);
		
		//{oxygenation, pulseRate, temperature, position}
		p30 = new PartialConfiguration(); 
		p30.addFeature(oxygenation);
		p30.addFeature(pulseRate);
		p30.addFeature(temperature);
		p30.addFeature(position);
		partialConfigurationsSensorInformation.add(p30);
		
		//{oxygenation, pulseRate, temperature, position, fall}
		p31 = new PartialConfiguration(); 
		p31.addFeature(oxygenation);
		p31.addFeature(pulseRate);
		p31.addFeature(temperature);
		p31.addFeature(position);
		p31.addFeature(fall);
		partialConfigurationsSensorInformation.add(p31);
		
	}
	
	
	/**
	 * This test case is to ensure whenever a BDD object is created, there will be 
	 * an FeatureModel associated with it. If two or more objects are created from this 
	 * class they must share the same FeatureModel, because the FM is unique for the SPL.
	 * 
	 *  TODO Review this test, because it seems to be useless. It creates the Feature model
	 *  for two BDD objects and test if they are the same. It should test what happens when
	 *  no feature model is associated with the BDD class. Probably an exception should be 
	 *  launched, because it does not make sense to create a BDD object without rules to be
	 *  expressed. 
	 *  
	 */
	@Test 
	public void testEmptyBDDCreation() {
		Assert.assertNotNull(bdd1.getFeatureModel());
		Assert.assertNotNull(bdd2.getFeatureModel());
		
		Assert.assertEquals(bdd1.getFeatureModel(), bdd2.getFeatureModel());
		Assert.assertSame(bdd1.getFeatureModel(), bdd2.getFeatureModel());
	}
	
	
	/**
	 * This test case is to ensure whenever a new BDD object is created with an initial
	 * Feature Model, the feature model will be shared among all the BDD objects created
	 * for the SPL. 
	 */
	@Test 
	public void testNonEmptyBDDCreation() {
		bdd1 = new BDD(new FeatureModel());
		bdd2 = new BDD(new FeatureModel()); 
		
		Assert.assertNotNull(bdd1.getFeatureModel());
		Assert.assertNotNull(bdd2.getFeatureModel());
		
		Assert.assertEquals(bdd1.getFeatureModel(), bdd2.getFeatureModel());
		Assert.assertSame(bdd1.getFeatureModel(), bdd2.getFeatureModel());
	}
	
	
	
	/**
	 * The test cases below are to ensure a BDD object can receive a set of dependant features, 
	 * from which the valid partial configurations will be computed. 
	 */
	@Test
	public void testReceiveEmptySetOfDependantFeatures() { 
		bdd1.addSetOfDependantFeatures(null);
		bdd2.addSetOfDependantFeatures(null);
		
		HashSet<Feature> depFeatBdd1, depFeatBdd2; 
		depFeatBdd1 = bdd1.getDependantFeatures();
		depFeatBdd2 = bdd2.getDependantFeatures();
		
		Assert.assertNull(depFeatBdd1);
		Assert.assertNull(depFeatBdd2);
	}
	
	
	@Test
	public void testeReceiveNonEmptySetOfDependantFeatures() {
		
		HashSet<Feature> inputSqlite = new HashSet<Feature>(), outputSqlite,  
				         inputMem = new HashSet<Feature>(), outputMem, 
				         inputFile = new HashSet<Feature>(), outputFile;
		Iterator<Feature> it; 
		Feature f = null; 
		
		inputSqlite.add(sqlite);
		inputMem.add(memory);
		inputFile.add(Feature.getFeatureByName("File"));
		
		
		//Sqlite input for test
		bdd1.addSetOfDependantFeatures(inputSqlite);
		outputSqlite = bdd1.getDependantFeatures(); 
		Assert.assertNotNull(outputSqlite);
		Assert.assertTrue(bdd1.getDependantFeatures().contains(sqlite));
		it = bdd1.getDependantFeatures().iterator(); 
		while (it.hasNext()) {
			f = it.next(); 
			if (f.equals(sqlite))
				break;
		}
		Assert.assertSame(sqlite, f);
		
		
		//Memory input for test
		bdd1.addSetOfDependantFeatures(inputMem);
		outputMem = bdd1.getDependantFeatures();
		Assert.assertNotNull(outputMem);
		Assert.assertTrue(bdd1.getDependantFeatures().contains(memory));
		it = bdd1.getDependantFeatures().iterator();
		while(it.hasNext()) {
			f = it.next(); 
			if (f.equals(memory))
				break; 
		}
		Assert.assertSame(memory, f);
		
		
		//File input for test
		bdd1.addSetOfDependantFeatures(inputFile);
		outputFile = bdd1.getDependantFeatures(); 
		Assert.assertNotNull(outputFile);
		Assert.assertTrue(bdd1.getDependantFeatures().contains(file));
		it = bdd1.getDependantFeatures().iterator(); 
		while (it.hasNext()) {
			f = it.next(); 
			if (f.equals(file)) 
				break; 
		}
		Assert.assertSame(file, f);
	}
	
	
	
	/**
	 * The test cases below ensure a set of dependant features can be projected over
	 * the feature model to get the relations between them.
	 * Projecting a set of features over a FM will return a FM comprised only with features 
	 * and cross-tree constraints which contains the features projected. So, if a cross-tree 
	 * constraint does not have a single feature in the feature set it must not be part of
	 * the resulting feature model (i.e. all features of a cross-tree constraint must be 
	 * inside the set of features).   
	 * 
	 * TODO change this test to ensure the FM rules are represented after the projection. 
	 * Currently the projection is comprised only of features projected, but the rules are
	 * missing.
	 */
	@Test
	
	public void testCreateProjectionEmptySet() {
		//TODO ensure the "set of rules" is also empty! 
		//The resulting projection for an empty set of dependant features must be null
		HashSet<Feature> dependantFeatures = new HashSet<Feature>();
		
		FeatureModel projection = bdd1.createProjection(dependantFeatures);
		HashSet<Feature> temp = projection.getFeatures();
		
		Assert.assertNotNull(temp);
		Assert.assertEquals(0, temp.size());
		
		fail("Finish this implementation: it is necessary to assert if the FM rules "
				+ "returned by the projection are valid. It will be possible to test "
				+ "when the implementation of FeatureModel class is finished! In this"
				+ "case it is necessary to ensure no FM rule will be returned (cardinality"
				+ "=0)");
	}
	
	
	@Test
	public void testCreateProjectionOneFeature() {
		//TODO ensure the cardinality of the "set of rules" is equal to zero! 
		
		//Sqlite feature
		HashSet<Feature> sqliteDependantFeatures = new HashSet<Feature>();
		sqliteDependantFeatures.add(sqlite);
		FeatureModel sqliteProjection = bdd1.createProjection(sqliteDependantFeatures);
		Assert.assertTrue(sqliteProjection.getFeatures().contains(sqlite));
		Assert.assertEquals(1, sqliteProjection.getFeatures().size());
		
		//Mem feature
		HashSet<Feature> memDependantFeatures = new HashSet<Feature>();
		memDependantFeatures.add(memory); 
		FeatureModel memProjection = bdd1.createProjection(memDependantFeatures);
		Assert.assertTrue(memProjection.getFeatures().contains(memory));
		Assert.assertEquals(1,memProjection.getFeatures().size());
		
		//File feature
		HashSet<Feature> fileDependantFeatures = new HashSet<Feature>();
		fileDependantFeatures.add(file);
		FeatureModel fileProjection = bdd1.createProjection(fileDependantFeatures); 
		Assert.assertTrue(fileProjection.getFeatures().contains(file));
		Assert.assertEquals(1, fileProjection.getFeatures().size());
		
		
		fail("Finish this implementation: it is necessary to assert if the FM rules "
				+ "returned by the projection are valid. It will be possible to test "
				+ "when the implementation of FeatureModel class is finished. In this"
				+ "case it is necessary to ensure no FM rule will be returned (cardinality"
				+ "=0)");
	}
	
	
	@Test
	public void testCreateProjectionWithMoreThanOneFeature() {
		//TODO ensure the set of rules for this projection comprises the FM rules described
		//in terms of the features. So the cardinality of the set of rules must be equals 
		//to the number of rules comprising only the features in the dependant features set.
		
		
		//Storage features
		HashSet<Feature> storageDependantFeatures = new HashSet<Feature>();
		storageDependantFeatures.add(storage); 
		storageDependantFeatures.add(sqlite);
		storageDependantFeatures.add(memory); 
		storageDependantFeatures.add(file); 	
		FeatureModel storageProjection = bdd1.createProjection(storageDependantFeatures);  
		Assert.assertTrue(storageProjection.getFeatures().contains(storage));
		Assert.assertTrue(storageProjection.getFeatures().contains(sqlite));
		Assert.assertTrue(storageProjection.getFeatures().contains(memory));
		Assert.assertTrue(storageProjection.getFeatures().contains(file));
		Assert.assertEquals(4, storageProjection.getFeatures().size());
		
		fail("Finish this implementation: it is necessary ensure the FM rules of the projection "
				+ "are valid. #FM rules = 4");
		
		//SensorFeatures
		HashSet<Feature> sensorDependantFeatures = new HashSet<Feature>();
		sensorDependantFeatures = new HashSet<Feature>();
		sensorDependantFeatures.add(sSpo2);
		sensorDependantFeatures.add(sEcg);
		sensorDependantFeatures.add(sTemp);
		sensorDependantFeatures.add(sAcc);
		FeatureModel sensorProjection = bdd1.createProjection(sensorDependantFeatures);
		Assert.assertTrue(sensorProjection.getFeatures().contains(sSpo2));
		Assert.assertTrue(sensorProjection.getFeatures().contains(sEcg));
		Assert.assertTrue(sensorProjection.getFeatures().contains(sTemp));
		Assert.assertTrue(sensorProjection.getFeatures().contains(sAcc));
		Assert.assertEquals(4, sensorProjection.getFeatures().size());
		
		fail("Finish this implementation: it is necessary ensure the FM rules of the projection "
				+ "are valid. #FM rules = 4");

		//SensorInformation
		HashSet<Feature> sensorInformationDependantFeatures = new HashSet<Feature>();
		sensorInformationDependantFeatures = new HashSet<Feature>();
		sensorInformationDependantFeatures.add(oxygenation);
		sensorInformationDependantFeatures.add(pulseRate);
		sensorInformationDependantFeatures.add(temperature);
		sensorInformationDependantFeatures.add(position);
		sensorInformationDependantFeatures.add(fall);
		FeatureModel sensorInformationProjection = bdd1.createProjection(sensorInformationDependantFeatures); 
		Assert.assertTrue(sensorInformationProjection.getFeatures().contains(oxygenation));
		Assert.assertTrue(sensorInformationProjection.getFeatures().contains(pulseRate));
		Assert.assertTrue(sensorInformationProjection.getFeatures().contains(temperature));
		Assert.assertTrue(sensorInformationProjection.getFeatures().contains(position));
		Assert.assertTrue(sensorInformationProjection.getFeatures().contains(fall));
		Assert.assertEquals(5, sensorInformationProjection.getFeatures().size());
		
		fail("Finish this implementation: it is necessary ensure the FM rules of the projection "
				+ "are valid. #FM rules = 5");
	}
	
	
	/**
	 * This test case is to ensure that given a feature model a set of valid partial confi-
	 * gurations will be returned as answer. 
	 */
	@Test
	public void testGetValidPartialConfigurationsForBasicNodes() {
		HashSet<Feature> dependantFeatures = new HashSet<Feature>();
		bdd1.addSetOfDependantFeatures(dependantFeatures);
		FeatureModel temp = bdd1.createProjection(dependantFeatures);
		HashSet<PartialConfiguration> configurations = bdd1.getValidPartialConfigurations();
		
		Assert.assertNotNull(configurations);
		Assert.assertEquals(0, configurations.size());
	}
	
	
	@Test
	public void testGetValidPartialConfigurationsForNodesWithDependencies() {
		HashSet<Feature> dependantFeatures = new HashSet<Feature>();
		dependantFeatures.add(oxygenation); 
		dependantFeatures.add(pulseRate); 
		dependantFeatures.add(temperature); 
		dependantFeatures.add(position); 
		dependantFeatures.add(fall); 
		bdd1.addSetOfDependantFeatures(dependantFeatures);
		FeatureModel temp = bdd1.createProjection(dependantFeatures); 
		HashSet<PartialConfiguration> partialConfigurations = bdd1.getValidPartialConfigurations(); 

		Assert.assertNotNull(partialConfigurations);
		Assert.assertEquals(32, partialConfigurations.size());
		
		//Verificar se cada Configuracao Parcial Valida esta contida no resultado de getValidPartialConfigurations
		Iterator<PartialConfiguration> itProbe = partialConfigurationsSensorInformation.iterator(); 
		while (itProbe.hasNext()) {
			PartialConfiguration partialProbe = itProbe.next(); 
			HashSet<Feature> featuresProbe = partialProbe.getFeatures();
			//verificar se essas features aparecem no conjunto de configuracoes validas retornada pela
			//funcao getValidPartialConfiguration()
			
			boolean found = false;
			Iterator<PartialConfiguration> itPartialConfiguration = partialConfigurations.iterator();
			while (itPartialConfiguration.hasNext() && !found) {
				PartialConfiguration validPartialConfiguration = itPartialConfiguration.next();
				HashSet<Feature> featuresValidPartialConfiguration = validPartialConfiguration.getFeatures();
				
				if (featuresValidPartialConfiguration.containsAll(featuresProbe)) { //encontrou config
					found = true; 
					Assert.assertTrue(partialConfigurations.remove(validPartialConfiguration));
				}
			}
		}
	}

}
