package fol.parsing;

public class Token {
	private String name;
	private char type;

	public Token(String name, char type) {
		this.name = name;
		this.type = type;
	}

	public Token(char type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

}
