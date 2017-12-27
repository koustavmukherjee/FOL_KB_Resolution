package fol.utility;

import java.util.Comparator;
import java.util.List;

import fol.parsing.Constant;
import fol.parsing.Literal;
import fol.parsing.Term;

public class LiteralSorter implements Comparator<Literal> {

	@Override
	public int compare(Literal l1, Literal l2) {
		if (l1.isNegated() != l2.isNegated()) {
			if (!l1.isNegated())
				return 1;
			else
				return -1;
		}
		int diff = l1.getPredicate().getName().compareTo(l2.getPredicate().getName());
		if (diff == 0) {
			diff = copmareTerms(l1.getPredicate().getTerms(), l2.getPredicate().getTerms());
		}
		return diff;
	}

	private int copmareTerms(List<Term> terms1, List<Term> terms2) {
		if (terms1.size() != terms2.size())
			// Unnecessary because a A given predicate name will not appear with
			// different number of arguments.
			return terms1.size() - terms2.size();
		if(terms1.size() == 0)
			return 0;
		int retVal = 0;
		Term term1 = terms1.get(0);
		Term term2 = terms2.get(0);
		if (term1.getClass() == term2.getClass()) {
			if (term1 instanceof Constant) {
				retVal = ((Constant) term1).getName().compareTo(((Constant) term2).getName());
			}
			if(retVal == 0) {
				return copmareTerms(terms1.subList(1, terms1.size()), terms2.subList(1, terms2.size()));
			}
			return retVal;
		} else {
			if (term1 instanceof Constant)
				return 1;
			else
				return -1;
		}
	}

}
