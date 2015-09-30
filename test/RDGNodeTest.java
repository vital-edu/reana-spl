package test;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import FeatureFamilyBasedAnalysisTool.Evaluation;
import FeatureFamilyBasedAnalysisTool.Feature;
import FeatureFamilyBasedAnalysisTool.RDGNode;


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
	
	
	
	@Test
	public void testEmptyRDGNode() {
		assertTrue(node.getEvaluations().isEmpty());
		assertTrue(node.getFeatureDependencies().isEmpty());
	}
	
	
	
	@Test
	public void testAddFeatureDependency() {
		assertTrue(node.addFeatureDependency(sqlite));
		assertTrue(node.getFeatureDependencies().contains(sqlite));
	}
	
	
	
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
		
	}
	
	
	
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
	}

	
	@Test
	public void testAddEvaluation() {
		Collection <Feature> partialConf = new HashSet<Feature>();
		partialConf.add(sqlite);
		Evaluation ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		assertTrue(node.addEvaluation(ev));
		
		partialConf = new HashSet<Feature>();
		partialConf.add(mem);
		ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		assertTrue(node.addEvaluation(ev));
		
		partialConf = new HashSet<Feature>();
		partialConf.add(file);
		ev = new Evaluation(partialConf, Math.pow(0.999, 3));
		assertTrue(node.addEvaluation(ev));
	}
	
	
	/**
	 * This test is to ensure that a partial configuration will be used in only one evaluation. In case another reliability value
	 * is defined for the evaluation, it will not be added to the evaluation set.
	 */
	@Test
	public void testAddDuplicatedEvaluation() {
		Collection <Feature> partialConf = new HashSet<Feature>();
		partialConf.add(sqlite);
		Evaluation ev = new Evaluation(partialConf, Math.pow(0.999, 2));
		assertTrue(node.addEvaluation(ev));
		
		ev = new Evaluation(partialConf, Math.pow(0.999, 3));
		assertFalse(node.addEvaluation(ev));
	}

	
	/**
	 * This test is to ensure the delete function works accordingly. Given a partial configuration (expressed in terms of dependant 
	 * features, it will be excluded in case the partial configuration has an evaluation in the evaluation set. 
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
