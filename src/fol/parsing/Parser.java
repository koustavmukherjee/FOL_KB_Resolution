package fol.parsing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {

	public Clause parseClause(String input) {
		if (input.isEmpty()) {
			System.err.println("Empty clause encountered");
			return null;
		}
		input = input.replaceAll("\\s+", "");
		String sLiterals[] = input.split("\\|");
		Set<Literal> literals = new HashSet<>();
		for (String sLiteral : sLiterals) {
			literals.add(getLiteral(sLiteral));
		}
		return new Clause(literals);
	}

	private Literal getLiteral(String sLiteral) {
		if (sLiteral.isEmpty()) {
			System.err.println("Empty Literal encountered");
			return null;
		}
		boolean isNegated = sLiteral.charAt(0) == '~';
		String predicateName = sLiteral.substring(isNegated ? 1 : 0, sLiteral.indexOf('('));
		List<Term> terms = parseTerms(sLiteral.substring(sLiteral.indexOf('(') + 1, sLiteral.indexOf(')')));
		Predicate predicate = new Predicate(predicateName, terms);
		return new Literal(predicate, isNegated);
	}

	private List<Term> parseTerms(String sTerms) {
		if (sTerms.isEmpty()) {
			System.err.println("Empty Term encountered");
			return null;
		}
		List<Term> terms = new ArrayList<>();
		String sTermArr[] = sTerms.split(",");
		for (String sTerm : sTermArr) {
			if (Character.isUpperCase(sTerm.charAt(0)))
				terms.add(new Constant(sTerm));
			else
				terms.add(new Variable(sTerm));
		}
		return terms;
	}
}