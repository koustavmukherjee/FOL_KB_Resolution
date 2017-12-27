package fol.parsing;

import java.util.Map;
import java.util.Set;

import fol.utility.SubstitutionVisitor;
import fol.utility.VariableVisitor;

public interface Term {

	void accept(VariableVisitor variableVisitor, Set<Variable> variables);
	
	Term accept(SubstitutionVisitor subst, Map<Variable, Term> theta);

	Term copy();
}
