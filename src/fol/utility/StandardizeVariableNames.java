package fol.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fol.parsing.Clause;
import fol.parsing.Term;
import fol.parsing.Variable;

public class StandardizeVariableNames {
	private String variableName;
	private int index;

	public StandardizeVariableNames(String variableName) {
		super();
		this.variableName = variableName;
		this.index = 0;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getNextIndex() {
		return index++;
	}
	
	public Map<Variable, Term> getSubstutions(Clause clause) {
		Map<Variable, Term> substutions = new HashMap<>();
		Set<Variable> variables = clause.getVariables();
		for(Variable variable : variables) {
			Variable substution = null;
			do {
				substution = new Variable(variableName + getNextIndex());
			}while(variables.contains(substution));
			substutions.put(variable, substution);
		}
		return substutions;
	}
}
