package test;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.State;
import FeatureFamilyBasedAnalysisTool.Transition;

public class FDTMCTest {

	FDTMC fdtmc1;
	
	@Before
	public void setUp() throws Exception {
		fdtmc1 = new FDTMC();
	}

	@Test
	public void testEmptyFDTMC() {
		Assert.assertTrue(fdtmc1.getStates().isEmpty());
		Assert.assertNull(fdtmc1.getInitialState());
		Assert.assertTrue(fdtmc1.getLabels().isEmpty());
		Assert.assertEquals(0, fdtmc1.getVariableIndex());
	}
	
	@Test
	public void testSetVariableName() {
		fdtmc1.setVariableName("x");
		Assert.assertEquals("x", fdtmc1.getVariableName());
	}
	
	@Test
	public void testCreateState() {
		fdtmc1.setVariableName("x");
		State temp = fdtmc1.createState();
		Assert.assertNotNull(temp);
		Assert.assertTrue(fdtmc1.getStates().contains(temp));
		Assert.assertEquals(0, temp.getIndex());
		Assert.assertEquals("x", temp.getVariableName()); 
	}
	
	@Test
	public void testCreateLotsOfStates() {
		fdtmc1.setVariableName("x");
		State s0, s1, s2, s3, s4, s5;
		s0 = fdtmc1.createState();
		s1 = fdtmc1.createState();
		s2 = fdtmc1.createState();
		s3 = fdtmc1.createState();
		s4 = fdtmc1.createState();
		s5 = fdtmc1.createState();
		
		Assert.assertNotNull(s0); 
		Assert.assertNotNull(s1); 
		Assert.assertNotNull(s2); 
		Assert.assertNotNull(s3); 
		Assert.assertNotNull(s4); 
		Assert.assertNotNull(s5);
		
		Assert.assertTrue(fdtmc1.getStates().contains(s0));
		Assert.assertTrue(fdtmc1.getStates().contains(s1));
		Assert.assertTrue(fdtmc1.getStates().contains(s2));
		Assert.assertTrue(fdtmc1.getStates().contains(s3));
		Assert.assertTrue(fdtmc1.getStates().contains(s4));
		Assert.assertTrue(fdtmc1.getStates().contains(s5));
		
		Assert.assertEquals(0, s0.getIndex());
		Assert.assertEquals(1, s1.getIndex());
		Assert.assertEquals(2, s2.getIndex());
		Assert.assertEquals(3, s3.getIndex());
		Assert.assertEquals(4, s4.getIndex());
		Assert.assertEquals(5, s5.getIndex());
	}
	
	
	@Test
	public void testCreateLabeledState() {
		fdtmc1.setVariableName("x");
		State s0, s1, s2; 
		
		s0 = fdtmc1.createState("init");
		s1 = fdtmc1.createState("sucess");
		s2 = fdtmc1.createState("error");
		
		Assert.assertEquals("init", s0.getLabel());
		Assert.assertEquals("sucess", s1.getLabel());
		Assert.assertEquals("error", s2.getLabel());
	}
	
	
	@Test
	public void testCreateTransition() {
		State s0, s1, s2; 
		s0 = fdtmc1.createState("init");
		s1 = fdtmc1.createState("success");
		s2 = fdtmc1.createState("error");
		
		Assert.assertTrue(fdtmc1.createTransition(s0, s1, "alpha", Double.toString(0.95)));
		Assert.assertTrue(fdtmc1.createTransition(s0, s2, "alpha_error", Double.toString(0.05)));
	}
	
	
	@Test
	public void testCreateTransitionWithParameter() {
		State s0, s1, s2; 
		s0 = fdtmc1.createState("init");
		s1 = fdtmc1.createState("success");
		s2 = fdtmc1.createState("error");

		Assert.assertTrue(fdtmc1.createTransition(s0, s1, "alpha", "rAlpha"));
		Assert.assertTrue(fdtmc1.createTransition(s0, s2, "alpha_error", "1-rAlpha"));
	}
	
	
	@Test
	public void testGetStateByLabel() {
		State s0, s1, s2; 
		s0 = fdtmc1.createState("init");
		s1 = fdtmc1.createState("success");
		s2 = fdtmc1.createState("error");
		
		State t0, t1, t2;
		t0 = fdtmc1.getStateByLabel("init");
		t1 = fdtmc1.getStateByLabel("success");
		t2 = fdtmc1.getStateByLabel("error");
		
		Assert.assertEquals(t0, s0);
		Assert.assertEquals(t1, s1);
		Assert.assertEquals(t2, s2);
	}
	
	
	@Test
	public void testGetTransitionByAction() {
		State s0, s1, s2; 
		s0 = fdtmc1.createState("init");
		s1 = fdtmc1.createState("sucess");
		s2 = fdtmc1.createState("error");
		
		Assert.assertTrue(fdtmc1.createTransition(s0, s1, "alpha", "rAlpha"));
		Assert.assertTrue(fdtmc1.createTransition(s0, s2, "alpha_error", "1-rAlpha"));
		
		Transition t1, t2;
		t1 = fdtmc1.getTransitionByActionName("alpha");
		t2 = fdtmc1.getTransitionByActionName("alpha_error");
		
		Assert.assertNotNull(t1);
		Assert.assertEquals("alpha", t1.getActionName());
		Assert.assertEquals("rAlpha", t1.getProbability());
		Assert.assertEquals(s0, t1.getSource());
		Assert.assertEquals(s1, t1.getTarget());
		
		Assert.assertNotNull(t2);
		Assert.assertEquals("alpha_error", t2.getActionName());
		Assert.assertEquals("1-rAlpha", t2.getProbability());
		Assert.assertEquals(s0, t2.getSource());
		Assert.assertEquals(s2, t2.getTarget());
	}
	
	
	
	
	

}
