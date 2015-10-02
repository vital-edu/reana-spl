package test;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fdtmc.Evaluation;
import fdtmc.Feature;
import tool.RDGNode;


public class RDGNodeTest {

	RDGNode node; 
	Feature sqlite, 
	        mem, 
	        file;
	
	@Before
	public void setUp() {
		node = new RDGNode();
		sqlite = Feature.getFeatureByName("Sqlite");
		mem = Feature.getFeatureByName("Mem"); 
		file = Feature.getFeatureByName("File");
	}
	
	
	/**
	 * This basic test ensures a new RDG node has an evaluations set and a 
	 * FeatureDependencies set different than NULL but both sets are empty.
	 */
	@Test
	public void testEmptyRDGNode() {
		assertTrue(node.getEvaluations().isEmpty());
		assertTrue(node.getFeatureDependencies().isEmpty());
	}
	
	
	/**
	 * This test ensures the RDGNode's function for adding a feature dependency 
	 * is working accordingly. 
	 */
	@Test
	public void testAddFeatureDependency() {
		assertTrue(node.addFeatureDependency(sqlite));
		assertTrue(node.getFeatureDependencies().contains(sqlite));
	}
	
	
	/**
	 * This test ensures the feature dependency removal works accordingly. In case 
	 * a feature dependency does not exist, the answer for the function must be 
	 * false.
	 */
	@Test
	public void testRemoveFeatureDependency() {
		Collection <Feature> dependencies = new HashSet<Feature>();
		dependencies.add(sqlite);
		dependencies.add(mem); 
		dependencies.add(file);
		
		node = new RDGNode(dependencies, null);
		
		assertTrue(node.getFeatureDependencies().contains(sqlite));
		assertTrue(node.getFeatureDependencies().contains(mem));
		assertTrue(node.getFeatureDependencies().contains(file));
		
		assertTrue(node.removeFeatureDependency(sqlite));
		assertTrue(!node.getFeatureDependencies().contains(sqlite));
		
		assertFalse(node.removeFeatureDependency(sqlite));
	}
	
	
	/**
	 * This test ensures an RDG node will never have duplicated features
	 * in the dependant features set.  
	 */
	@Test
	public void testAddDuplicatedFeatureDependency() {
		Collection <Feature> dependencies = new HashSet<Feature>();
		dependencies.add(sqlite);
		dependencies.add(mem); 
		dependencies.add(file);
		
		node = new RDGNode(dependencies, null);
		
		assertFalse(node.addFeatureDependency(sqlite));
		assertFalse(node.addFeatureDependency(mem));
		assertFalse(node.addFeatureDependency(file));
		
		assertFalse(node.addFeatureDependency(Feature.getFeatureByName("Sqlite")));
		assertFalse(node.addFeatureDependency(Feature.getFeatureByName("Mem")));
		assertFalse(node.addFeatureDependency(Feature.getFeatureByName("File")));
	}

	
	
	/**
	 * This test ensures the addition of an evaluation works accordingly.  
	 */
	@Test
	public void testAddEvaluation() {
		Collection <Feature> partialConf = new HashSet<Feature>();
		partialConf.add(sqlite);
		Evaluation ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		assertTrue(node.addEvaluation(ev));
		assertTrue(node.getEvaluations().contains(ev));
		
		partialConf = new HashSet<Feature>();
		partialConf.add(mem);
		ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		assertTrue(node.addEvaluation(ev));
		assertTrue(node.getEvaluations().contains(ev));
		
		partialConf = new HashSet<Feature>();
		partialConf.add(file);
		ev = new Evaluation(partialConf, Math.pow(0.999, 3));
		assertTrue(node.addEvaluation(ev));
		assertTrue(node.getEvaluations().contains(ev));
		
		//test for adding duplicated evaluation
		partialConf = new HashSet<Feature>(); 
		partialConf.add(sqlite); 
		ev = new Evaluation(partialConf, Math.pow(0.999, 3));
		assertFalse(node.addEvaluation(ev));
		assertFalse(node.getEvaluations().contains(ev));
	}
	
	
	/**
	 * This test is to ensure that a partial configuration will be used in only one evaluation. 
	 * In case another reliability value is defined for an existing partial configuration, it will 
	 * not be added to the evaluation set.
	 */
	@Test
	public void testAddDuplicatedEvaluation() {
		Collection <Feature> partialConf = new HashSet<Feature>();
		partialConf.add(sqlite);
		Evaluation ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		assertTrue(node.addEvaluation(ev));
		assertTrue(node.getEvaluations().contains(ev)); 
		
		Evaluation ev2 = new Evaluation(partialConf, Math.pow(0.999, 3));
		assertFalse(node.addEvaluation(ev2));
		assertFalse(node.getEvaluations().contains(ev2));
	}

	
	/**
	 * This test is to ensure the delete function works accordingly. Given a partial configuration (expressed in terms of dependant 
	 * features) it will be excluded in case the partial configuration has an evaluation in the evaluation set. 
	 */
	@Test
	public void testRemoveEvaluation() {
		Collection <Feature> partialConf = new HashSet<Feature>();
		partialConf.add(sqlite);
		Evaluation ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		node.addEvaluation(ev);
		
		partialConf = new HashSet<Feature>();
		partialConf.add(mem);
		ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		node.addEvaluation(ev);
		
		partialConf = new HashSet<Feature>();
		partialConf.add(file);
		ev = new Evaluation(partialConf, Math.pow(0.999, 3));
		node.addEvaluation(ev);
		
		partialConf = new HashSet<Feature>();
		partialConf.add(sqlite); 
		partialConf.add(mem);
		ev = new Evaluation(partialConf, 2*Math.pow(0.999, 2));
		node.addEvaluation(ev);
		
		//Evaluations definition that should be deleted from the node RDG evaluations
		partialConf = new HashSet<Feature>();
		partialConf.add(sqlite);
		ev = new Evaluation(partialConf, 0);
		assertTrue(node.removeEvaluation(ev));
	}
	
	
	
	
	
	
}
