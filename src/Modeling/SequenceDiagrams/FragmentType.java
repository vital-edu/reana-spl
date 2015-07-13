package Modeling.SequenceDiagrams;

import Modeling.Exceptions.UnsupportedFragmentTypeException;

public enum FragmentType {
	loop, alternative, optional, parallel;
	
	/**
	 * gets the Fragment Type from the typeName
	 * @param typeName
	 * @return
	 * @throws UnsupportedFragmentTypeException
	 */
	public static FragmentType getType(String typeName) throws UnsupportedFragmentTypeException {
		if (typeName.equals("opt")) {
			return optional;
		} else if (typeName.equals("alt")) {
			return alternative;
		} else if (typeName.equals("loop")) {
			return loop;
		} else if (typeName.equals("par")) {
			return parallel;
		} else {
			throw new UnsupportedFragmentTypeException("Fragment of type " + typeName + " is not supported!");
		}
	}
}
