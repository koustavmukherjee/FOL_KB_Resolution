package fol.utility;

import java.util.List;
import java.util.Map;

import fol.parsing.Predicate;
import fol.parsing.Term;
import fol.parsing.Variable;

public class Unifier {

	SubstitutionVisitor substitutionVisitor;

	public Unifier() {
		substitutionVisitor = new SubstitutionVisitor();
	}

	
	public Unifier(SubstitutionVisitor substitutionVisitor) {
		super();
		this.substitutionVisitor = substitutionVisitor;
	}


	public Map<Variable, Term> unifyPredicate(Predicate x, Predicate y, Map<Variable, Term> theta) {
		return unifyTerms(x.getTerms(), y.getTerms(), x.getName().equals(y.getName()) ? theta : null);
	}

	public Map<Variable, Term> unifyTerms(List<Term> xTerms, List<Term> yTerms, Map<Variable, Term> theta) {
		if (theta == null) {
			return null;
		} else if (xTerms.size() != yTerms.size()) {
			return null;
		} else if (xTerms.size() == 0 && yTerms.size() == 0) {
			return theta;
		} else if (xTerms.size() == 1 && yTerms.size() == 1) {
			return unifyTerm(xTerms.get(0), yTerms.get(0), theta);
		} else {
			return unifyTerms(xTerms.subList(1, xTerms.size()), yTerms.subList(1, yTerms.size()),
					unifyTerm(xTerms.get(0), yTerms.get(0), theta));
		}
	}

	private Map<Variable, Term> unifyTerm(Term x, Term y, Map<Variable, Term> theta) {
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			return theta;
		} else if (x instanceof Variable) {
			return unifyVariable((Variable) x, y, theta);
		} else if (y instanceof Variable) {
			return unifyVariable((Variable) y, x, theta);
		} else {
			return null; // If both terms are constants
		}
	}

	private Map<Variable, Term> unifyVariable(Variable x, Term y, Map<Variable, Term> theta) {
		if (theta.containsKey(x))
			return unifyTerm(theta.get(x), y, theta);
		else if (theta.containsKey(y))
			return unifyTerm(x, theta.get(y), theta);
		else if(y instanceof Variable)
			return null;
		else {
			theta.put(x, y);
			for (Variable v : theta.keySet()) {
				theta.put(v, substitutionVisitor.visitTerm(theta.get(v), theta));
			}
			return theta;
		}
	}
}
