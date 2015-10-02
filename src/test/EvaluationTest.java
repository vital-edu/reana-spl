package test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import fdtmc.Evaluation;
import fdtmc.Feature;


public class EvaluationTest {

	Feature sqlite, 
	        mem, 
	        file;
	
	Collection <Evaluation> evaluations;  
	
	@Before
	public void setUp() {
		evaluations = new ArrayList<Evaluation>(); 
		
		sqlite = Feature.getFeatureByName("Sqlite");
		mem = Feature.getFeatureByName("Mem");
		file = Feature.getFeatureByName("File");
	}
	
	
	
	@Test
	public void testCreateOneEvaluation() {
		Collection <Feature> setSqlite = new HashSet<Feature>();
		setSqlite.add(sqlite); 
		double reliability = Math.pow(0.999, 2);
		
		assertTrue(evaluations.add(new Evaluation(setSqlite, reliability)));
	}

	
	
	@Test
	public void testCreateSetOfEvaluations() {
		Collection <Feature> setSqlite, setMem, setFile;
		setSqlite = new HashSet<Feature>();
		setSqlite.add(sqlite); 
		
		setMem = new HashSet<Feature>();
		setMem.add(mem);
		
		setFile = new HashSet<Feature>();
		setFile.add(file);
		
		assertTrue(evaluations.add(new Evaluation(setSqlite, Math.pow(0.999, 2))));
		assertTrue(evaluations.add(new Evaluation(setMem, Math.pow(0.999, 2))));
		assertTrue(evaluations.add(new Evaluation(setFile, Math.pow(0.999, 3))));
	}
	
	
	
	@Test
	public void testAddFeature() {
		Evaluation ev = new Evaluation(); 
		assertTrue(ev.addFeature(sqlite));
		assertTrue(ev.addFeature(mem));
		assertTrue(ev.addFeature(file));
	}
	
	
	
	@Test
	public void testAddDuplicatedFeature() {
		Evaluation ev = new Evaluation(); 
		assertTrue(ev.addFeature(sqlite));
		assertFalse(ev.addFeature(sqlite));
	}
	
	
	
	@Test 
	public void testRemoveFeature() {
		Collection <Feature> features = new HashSet<Feature>();
		features.add(sqlite);
		features.add(mem);
		features.add(file);
		
		Evaluation ev = new Evaluation(features, 0.999);
		
		assertTrue(ev.getFeatures().contains(sqlite));
		assertTrue(ev.getFeatures().contains(mem));
		assertTrue(ev.getFeatures().contains(file));
		
		assertTrue(ev.remove(sqlite));
		assertFalse(ev.getFeatures().contains(sqlite));		
	}

}
