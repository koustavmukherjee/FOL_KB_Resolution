package fol.parsing;

import java.util.Map;
import java.util.Set;

import fol.utility.SubstitutionVisitor;
import fol.utility.VariableVisitor;

public class Variable implements Term {
	private String name;

	public Variable(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Variable)) {
			return false;
		}
		Variable v = (Variable) obj;
		return this.getName().equals(v.getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void accept(VariableVisitor variableVisitor, Set<Variable> variables) {
		variableVisitor.visitVariable(this, variables);
	}

	@Override
	public Term accept(SubstitutionVisitor subst, Map<Variable, Term> theta) {
		return subst.visitTerm(this, theta);
	}

	@Override
	public Term copy() {
		return new Variable(this.name);
	}

}
