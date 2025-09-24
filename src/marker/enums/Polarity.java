package marker.enums;

public enum Polarity {
	SOLID('-'), HOLLOW('+');
	
	public final char sym;
	public final int value;
	
	Polarity(char sym) {
		this.sym = sym;
		
		value = Integer.parseInt(sym + "1");
	}
	
}
