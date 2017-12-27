package fol.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fol.utility.SubstitutionVisitor;
import fol.utility.VariableVisitor;

public class Predicate implements Sentence {
	private String name;
	private List<Term> terms;

	public Predicate(String name) {
		super();
		this.name = name;
	}

	public Predicate(String name, List<Term> terms) {
		super();
		this.name = name;
		this.terms = terms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Term> getTerms() {
		if (this.terms == null) {
			this.terms = new ArrayList<>();
		}
		return terms;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("(");
		for (int i = 0; i < this.getTerms().size(); i++) {
			sb.append(this.getTerms().get(i));
			if (i != this.getTerms().size() - 1)
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	public void accept(VariableVisitor variableVisitor, Set<Variable> variables) {
		variableVisitor.visitPredicate(this, variables);
	}

	public Predicate accept(SubstitutionVisitor subst, Map<Variable, Term> theta) {
		List<Term> newTerms = new ArrayList<Term>();
		for (Term term : this.getTerms()) {
			Term newTerm = subst.visitTerm(term, theta);
			newTerms.add(newTerm);
		}
		return new Predicate(name, newTerms);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Predicate)) {
			return false;
		}
		Predicate p = (Predicate) obj;
		return this.getName().equals(p.getName()) && this.getTerms().equals(p.getTerms());
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + name.hashCode();
		for (Term t : this.getTerms())
			result = 37 * result + t.hashCode();
		return result;
	}

	public Predicate copy() {
		List<Term> copyTerms = new ArrayList<>();
		for (Term term : this.getTerms())
			copyTerms.add(term.copy());
		return new Predicate(name, copyTerms);
	}
}
