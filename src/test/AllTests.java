package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BDDTest.class, EvaluationTest.class, RDGNodeTest.class, FDTMCTest.class })
public class AllTests {

}
