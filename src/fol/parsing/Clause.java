package fol.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fol.utility.LiteralSorter;
import fol.utility.StandardizeVariableNames;
import fol.utility.SubstitutionVisitor;
import fol.utility.Unifier;
import fol.utility.VariableVisitor;

public class Clause implements Sentence {
	private Set<Literal> literals;
	private Set<Literal> positiveLiterals;
	private Set<Literal> negativeLiterals;
	private static final VariableVisitor varVisitor = new VariableVisitor();
	private static final SubstitutionVisitor subVisitor = new SubstitutionVisitor();
	private static final StandardizeVariableNames resStandardization = new StandardizeVariableNames("r");
	private static final Unifier unifier = new Unifier();
	private String internalRepresentation;
	private boolean containsOnlyLiteralsWithConstants = true;

	public boolean isContainsOnlyLiteralsWithConstants() {
		return this.containsOnlyLiteralsWithConstants;
	}

	public Clause() {
		super();
	}

	public Clause(Set<Literal> literals) {
		super();
		for (Literal literal : literals)
			this.addLiteral(literal);
	}

	public Set<Literal> getLiterals() {
		if (null == literals)
			literals = new HashSet<>();
		return Collections.unmodifiableSet(literals);
	}

	public void addLiteral(Literal literal) {
		if (null == literals)
			literals = new HashSet<>();
		literals.add(literal);
		if (literal.isNegated())
			getModifiableNegetiveLiterals().add(literal);
		else
			getModifiablePositiveLiterals().add(literal);
		if (this.containsOnlyLiteralsWithConstants) {
			for (Term term : literal.getPredicate().getTerms()) {
				if (!(term instanceof Constant)) {
					this.containsOnlyLiteralsWithConstants = false;
					break;
				}
			}
		}
	}

	private Set<Literal> getModifiablePositiveLiterals() {
		if (null == positiveLiterals)
			positiveLiterals = new HashSet<>();
		return positiveLiterals;
	}

	public Set<Literal> getPositiveLiterals() {
		return Collections.unmodifiableSet(getModifiablePositiveLiterals());
	}

	private Set<Literal> getModifiableNegetiveLiterals() {
		if (null == negativeLiterals)
			negativeLiterals = new HashSet<>();
		return negativeLiterals;
	}

	public Set<Literal> getNegativeLiterals() {
		return Collections.unmodifiableSet(getModifiableNegetiveLiterals());
	}

	public void setNegativeLiterals(Set<Literal> negativeLiterals) {
		this.negativeLiterals = negativeLiterals;
	}

	public VariableVisitor getVarVisitor() {
		return varVisitor;
	}

	public SubstitutionVisitor getSubVisitor() {
		return subVisitor;
	}

	public String getInternalRepresentation() {
		if (this.internalRepresentation != null)
			return this.internalRepresentation;
		List<Literal> literalsList = new ArrayList<>(this.getLiterals());
		Collections.sort(literalsList, new LiteralSorter());
		StringBuffer rep = new StringBuffer();
		boolean firstLiteral = true;
		int varPosCount = 0;
		Map<String, List<Integer>> varPositions = new HashMap<>();
		for (Literal literal : literalsList) {
			if (firstLiteral)
				firstLiteral = false;
			else
				rep.append("|");
			if (literal.isNegated())
				rep.append("~");
			rep.append(literal.getPredicate().getName());
			rep.append("(");
			boolean firstTerm = true;
			for (Term term : literal.getPredicate().getTerms()) {
				if (firstTerm)
					firstTerm = false;
				else
					rep.append(",");
				if (term instanceof Constant)
					rep.append(((Constant) term).getName());
				else {
					rep.append("*");
					Variable var = (Variable) term;
					String varName = var.getName();
					List<Integer> varPositionList = new ArrayList<>();
					if (varPositions.containsKey(varName))
						varPositionList = varPositions.get(varName);
					varPositionList.add(varPosCount);
					varPosCount++;
					varPositions.put(varName, varPositionList);
				}
			}
			rep.append(")");
		}
		int maxPositionDigitCount = 0;
		while (varPosCount > 0) {
			varPosCount /= 10;
			maxPositionDigitCount++;
		}
		List<String> positionalIndices = new ArrayList<>();
		for (Entry<String, List<Integer>> entry : varPositions.entrySet()) {
			List<Integer> positions = entry.getValue();
			Collections.sort(positions);
			StringBuilder sb = new StringBuilder();
			for (int pos : positions)
				sb.append(String.format("%" + maxPositionDigitCount + "d", pos));
			positionalIndices.add(sb.toString());
		}
		Collections.sort(positionalIndices);
		for (int i = 0; i < positionalIndices.size(); i++) {
			rep.append(positionalIndices.get(i));
			if (i != positionalIndices.size() - 1) {
				rep.append(",");
			}
		}
		internalRepresentation = rep.toString();
		return internalRepresentation;
	}

	@Override
	public String toString() {
		return literals.toString();
	}

	public Set<Variable> getVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		for (Literal literal : this.getLiterals()) {
			varVisitor.visitLiteral(literal, variables);
		}
		return variables;
	}

	public Clause getSubstClause(Map<Variable, Term> theta) {
		Set<Literal> subsLiterals = new HashSet<>();
		for (Literal literal : this.literals) {
			Literal substitutedLiteral = subVisitor.visitLiteral(literal, theta);
			if (null != substitutedLiteral) {
				subsLiterals.add(substitutedLiteral);
			}
		}
		return new Clause(subsLiterals);
	}

	public Set<Clause> resolve(Clause c) {
		Set<Clause> res = new HashSet<>();
		if (this.getLiterals().size() == 0 && c.getLiterals().size() == 0) {
			res.add(new Clause());
			return res;
		}
		if (c == this) {
			Set<Variable> thisVars = this.getVariables();
			Set<Variable> cVars = c.getVariables();

			Set<Variable> combinedVars = new HashSet<>();
			combinedVars.addAll(thisVars);
			combinedVars.addAll(cVars);

			if (combinedVars.size() < (thisVars.size() + cVars.size()))
				c = c.getSubstClause(resStandardization.getSubstutions(c));
		}

		Set<Literal> unionPositives = new HashSet<>();
		unionPositives.addAll(this.getPositiveLiterals());
		unionPositives.addAll(c.getPositiveLiterals());

		Set<Literal> unionNegatives = new HashSet<>();
		unionNegatives.addAll(this.getNegativeLiterals());
		unionNegatives.addAll(c.getNegativeLiterals());

		Set<Clause> resSet1 = resolveLiterals(this.getPositiveLiterals(), c.getNegativeLiterals(), unionPositives,
				unionNegatives);
		Set<Clause> resSet2 = resolveLiterals(c.getPositiveLiterals(), this.getNegativeLiterals(), unionPositives,
				unionNegatives);

		res.addAll(resSet1);
		res.addAll(resSet2);

		return res;
	}

	@Override
	public int hashCode() {
		return this.getInternalRepresentation().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clause other = (Clause) obj;
		if (literals == null) {
			if (other.literals != null)
				return false;
		} else if (!this.getInternalRepresentation().equals(other.getInternalRepresentation()))
			return false;
		return true;
	}

	public Set<Clause> resolveLiterals(Set<Literal> positiveLiterals, Set<Literal> negativeLiterals,
			Set<Literal> unionPositives, Set<Literal> unionNegatives) {
		Set<Literal> newPositiveLiterals = new HashSet<>();
		Set<Literal> newNegativeLiterals = new HashSet<>();
		Set<Literal> resolved = new HashSet<>();
		Set<Clause> res = new HashSet<>();
		for (Literal positiveLiteral : positiveLiterals) {
			for (Literal negativeLiteral : negativeLiterals) {
				Map<Variable, Term> theta = new HashMap<>();
				theta = unifier.unifyPredicate(positiveLiteral.getPredicate(), negativeLiteral.getPredicate(), theta);
				if (theta != null) {
					newPositiveLiterals.clear();
					newNegativeLiterals.clear();
					populateResolvantsLiteral(unionPositives, newPositiveLiterals, positiveLiteral, theta);
					populateResolvantsLiteral(unionNegatives, newNegativeLiterals, negativeLiteral, theta);
					resolved.clear();
					resolved.addAll(newPositiveLiterals);
					resolved.addAll(newNegativeLiterals);
					Clause c = new Clause(resolved);
					Map<Variable, Term> resSubstutions = resStandardization.getSubstutions(c);
					res.add(c.getSubstClause(resSubstutions));
				}
			}
		}
		return res;
	}

	private void populateResolvantsLiteral(Set<Literal> union, Set<Literal> newLiterals, Literal literal,
			Map<Variable, Term> theta) {
		boolean encountered = false;
		for (Literal unionPositiveLiteral : union) {
			if (!encountered && literal.equals(unionPositiveLiteral)) {
				encountered = true;
				continue;
			}
			newLiterals.add(subVisitor.visitLiteral(unionPositiveLiteral, theta));
		}
	}

	public Clause copy() {
		Set<Literal> literalsCopy = new HashSet<>();
		for (Literal literal : this.literals) {
			literalsCopy.add(literal.copy());
		}
		return new Clause(literalsCopy);
	}
}
