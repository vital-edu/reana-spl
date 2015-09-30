package FeatureFamilyBasedAnalysisTool;

import java.util.HashMap;
import java.util.Iterator;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Formula {

	public static double evalFormula(String formula) {
		ExpressionBuilder e = new ExpressionBuilder(formula);
		Expression e1 = (Expression)e.build(); 
		double result = e1.evaluate();
		return result;
	}

	public static double evalFormula(String formula,
			HashMap<Feature, Double> featureReliability) {
		ExpressionBuilder e = new ExpressionBuilder(formula) ;

		Iterator<Feature> itVariables = featureReliability.keySet().iterator();
		while (itVariables.hasNext()) {
			Feature f = itVariables.next();
			String variableName = "r"+f.getName();
			e.variable(variableName);
		}
		
		Expression expression = e.build(); 
		
		itVariables = featureReliability.keySet().iterator(); 
		while (itVariables.hasNext()) {
			Feature f = itVariables.next();
			expression.setVariable("r" + f.getName(), featureReliability.get(f).doubleValue()); 
		}
		
		return expression.evaluate();
	}

}
