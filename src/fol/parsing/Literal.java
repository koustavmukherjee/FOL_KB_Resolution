package fol.parsing;

import java.util.Map;

import fol.utility.SubstitutionVisitor;

public class Literal {
	private Predicate predicate;
	private boolean isNegated;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (!(obj instanceof Literal))
			return false;
		else {
			Literal l = (Literal) obj;
			return this.isNegated() == l.isNegated() && this.getPredicate().equals(l.getPredicate());
		}
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + predicate.hashCode() + (this.isNegated ? "-".hashCode() : "+".hashCode());
		return result;
	}

	public Literal(Predicate predicate) {
		super();
		this.predicate = predicate;
	}

	public Literal(Predicate predicate, boolean isNegated) {
		this.predicate = predicate;
		this.isNegated = isNegated;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	public boolean isNegated() {
		return isNegated;
	}

	public void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}

	public Literal accept(SubstitutionVisitor subst, Map<Variable, Term> theta) {
		return new Literal(subst.visitPredicate(this.predicate, theta), isNegated);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.isNegated())
			sb.append("~");
		sb.append(this.predicate);
		return sb.toString();
	}

	public Literal copy() {
		return new Literal(this.getPredicate().copy(), this.isNegated());
	}
}
