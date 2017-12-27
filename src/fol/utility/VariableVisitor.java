package fol.utility;

import java.util.Set;

import fol.parsing.Literal;
import fol.parsing.Predicate;
import fol.parsing.Term;
import fol.parsing.Variable;

public class VariableVisitor {
	public void visitLiteral(Literal literal, Set<Variable> variables) {
		literal.getPredicate().accept(this, variables);
	}

	public void visitPredicate(Predicate predicate, Set<Variable> variables) {
		for (Term term : predicate.getTerms()) {
			term.accept(this, variables);
		}
	}

	public void visitVariable(Variable variable, Set<Variable> variables) {
		variables.add(variable);
	}
}
