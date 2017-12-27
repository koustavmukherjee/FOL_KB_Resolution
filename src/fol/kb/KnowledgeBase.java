package fol.kb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fol.parsing.Clause;
import fol.parsing.Literal;
import fol.parsing.Parser;
import fol.parsing.Term;
import fol.parsing.Variable;
import fol.utility.StandardizeVariableNames;

public class KnowledgeBase {
	private static final Parser parser = new Parser();
	private static final StandardizeVariableNames tellStandardization = new StandardizeVariableNames("v");
	private Set<Clause> clauses;
	private Set<Clause> clausesWithOnlyConstantTerms;

	private Set<Clause> getClausesWithOnlyConstantTerms() {
		if (this.clausesWithOnlyConstantTerms == null)
			this.clausesWithOnlyConstantTerms = new HashSet<>();
		return this.clausesWithOnlyConstantTerms;
	}

	public void tell(String s) {
		Clause clause = parser.parseClause(s);
		if (clause != null) {
			Map<Variable, Term> substutions = tellStandardization.getSubstutions(clause);
			Clause newClause = clause.getSubstClause(substutions);
			getModifiableClausesFromKB().add(newClause);
			if (newClause.isContainsOnlyLiteralsWithConstants())
				this.getClausesWithOnlyConstantTerms().add(newClause);
		}
	}

	private Set<Clause> getModifiableClausesFromKB() {
		if (clauses == null) {
			clauses = new HashSet<>();
		}
		return clauses;
	}

	public Set<Clause> getClauses() {
		return Collections.unmodifiableSet(this.getModifiableClausesFromKB());
	}

	public boolean ask(String s) {
		Clause answerClause = parser.parseClause(s);
		long startTime = System.nanoTime();
		if (answerClause != null) {
			Clause answerClauseCopy = answerClause.copy();
			Clause notAlpha = negateAnswerClause(answerClauseCopy);
			Set<Clause> kbClauses = new HashSet<>();
			for (Clause kbClause : this.getClauses())
				kbClauses.add(kbClause.copy());
			kbClauses.add(notAlpha.copy());
			Set<Clause> newClauses = new HashSet<>();
			int kbClauseSize = kbClauses.size();

			Set<Clause> kbClausesContainingOnlyConstantTerms = new HashSet<>();
			for (Clause clauseWithOnlyConstantTerms : this.getClausesWithOnlyConstantTerms())
				kbClausesContainingOnlyConstantTerms.add(clauseWithOnlyConstantTerms);
			if (notAlpha.isContainsOnlyLiteralsWithConstants())
				kbClausesContainingOnlyConstantTerms.add(notAlpha.copy());
			do {
				newClauses.clear();
				Clause[] setI = new Clause[kbClauses.size()];
				kbClauses.toArray(setI);
				for (int i = 0; i < setI.length; i++) {
					for (int j = i; j < setI.length; j++) {
						long currentTime = System.nanoTime();
						if (((currentTime - startTime) / 1000000) > 120000)
							return false;
						Clause clauseI = setI[i];
						Clause clauseJ = setI[j];
						Set<Clause> resolvants = clauseI.resolve(clauseJ);
						if (resolvants.size() > 0) {
							for (Clause resolvedClause : resolvants) {
								if (resolvedClause.getLiterals().size() == 0) {
									return true;
								}
							}
							for (Clause resolvant : resolvants) {
								boolean addResolvantToKb = true;
								for (Clause constantTermClauses : kbClausesContainingOnlyConstantTerms) {
									if (resolvant.getLiterals().containsAll(constantTermClauses.getLiterals())) {
										addResolvantToKb = false;
										break;
									}
								}
								if (addResolvantToKb) {
									newClauses.add(resolvant);
									if (resolvant.isContainsOnlyLiteralsWithConstants())
										kbClausesContainingOnlyConstantTerms.add(resolvant);
								}
							}
						}
					}
				}
				kbClauseSize = kbClauses.size();
				if (newClauses.size() > 0)
					kbClauses.addAll(newClauses);
			} while (kbClauseSize < kbClauses.size());
		}
		return false;
	}

	private Clause negateAnswerClause(Clause answerClauseCopy) {
		if (answerClauseCopy.getLiterals().size() != 1) {
			throw new IllegalArgumentException("Answer Clause is expected to have just a single predicate");
		}
		Set<Literal> negatedLiterals = new HashSet<>();
		for (Literal answerClauseLiteral : answerClauseCopy.getLiterals()) {
			Literal answerClauseLiteralCopyNegated = answerClauseLiteral.copy();
			answerClauseLiteralCopyNegated.setNegated(!answerClauseLiteralCopyNegated.isNegated());
			negatedLiterals.add(answerClauseLiteralCopyNegated);
		}
		return new Clause(negatedLiterals);
	}
}