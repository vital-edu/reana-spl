package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BDDTest.class, EvaluationTest.class, RDGNodeTest.class, FDTMCTest.class, 
	ReliabilityEvaluationTest.class, MappingPartialConfigurationsReliabilityTest.class, 
	DependantFeatureEvaluationTest.class})

public class AllTests {

}
