package marker.enums;

public enum DiscreteSize {
	A(1),B(5),C(10),D(50),E(100),F(500),G(1000);
	
	public final int base_value, size;
	DiscreteSize(int value) {
		this.base_value = value;
		size = (ordinal()+1) * 10;
	}
}
