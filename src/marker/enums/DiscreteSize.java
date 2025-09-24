package marker.enums;

public enum DiscreteSize {
	I(1),V(5),X(10),L(50),C(100),D(500),M(1000);
	
	public final int base_value, size;
	DiscreteSize(int value) {
		this.base_value = value;
		size = (ordinal()+1) * 10;
	}
	
}
