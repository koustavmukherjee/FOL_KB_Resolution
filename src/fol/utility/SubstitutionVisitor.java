package fol.utility;

import java.util.Map;

import fol.parsing.Literal;
import fol.parsing.Predicate;
import fol.parsing.Term;
import fol.parsing.Variable;

public class SubstitutionVisitor {

	public Literal visitLiteral(Literal literal, Map<Variable, Term> theta) {
		return literal.accept(this, theta);
	}

	public Predicate visitPredicate(Predicate predicate, Map<Variable, Term> theta) {
		return predicate.accept(this, theta);
	}

	public Term visitTerm(Term term, Map<Variable, Term> theta) {
		if (theta.containsKey(term))
			return theta.get(term).copy();
		return term.copy();
	}

}
