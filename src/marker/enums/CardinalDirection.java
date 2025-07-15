package marker.enums;


public enum CardinalDirection{
	NW(225, "Northwest"), N_(270, "North"), NE(315, "Northeast"), 
	W_(180, "West")		,					E_(  0, "East"),
	SW(135, "Southwest"), S_( 90, "South"), SE( 45, "Southeast");
	
	public final float rad;
	public final String str;
	CardinalDirection(int deg, String str) {
		this.rad = (float)Math.toRadians(deg);
		this.str = str;
	}
}
