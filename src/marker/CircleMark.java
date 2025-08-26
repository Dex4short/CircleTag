package marker;

import java.awt.Graphics2D;
import java.awt.Point;

import marker.enums.CardinalDirection;
import marker.enums.DiscreteSize;
import marker.enums.Polarity;

public class CircleMark {
	private int orbit, value;
	private DiscreteSize discrete_size;
	private Polarity polarity;
	private CardinalDirection direction;
	private CircleTag parent;
	
	public CircleMark(int orbit, CardinalDirection direction, DiscreteSize discrete_size,  Polarity polarity) {
		setOrbit(orbit);
		setDirection(direction);
		setDiscreteSize(discrete_size);
		setPolarity(polarity);
	}
	@Override
	public String toString() {
		return getOrbit() + "" + getCardinalDirection() + "" + getPolarity().sym + "" + getDiscreteSize();
	}
	
	//setters
	public void setDiscreteSize(DiscreteSize discrete_size) {
		this.discrete_size = discrete_size;
	}
	public void setOrbit(int orbit) {
		this.orbit = orbit;
	}
	public void setDirection(CardinalDirection direction) {
		this.direction = direction;
	}
	public void setPolarity(Polarity polarity) {
		this.polarity = polarity;
	}
	void setParent(CircleTag parent) {
		this.parent = parent;
	}
	
	//getters
	public DiscreteSize getDiscreteSize() {
		return discrete_size;
	}
	public int getOrbit() {
		return orbit;
	}
	public CardinalDirection getCardinalDirection() {
		return direction;
	}
	public Polarity getPolarity() {
		return polarity;
	}
	public CircleTag getParent(CircleTag parent) {
		return parent;
	}
	
	//drawing function
	public void drawCircleMark(Graphics2D g2d) {
		switch (polarity) {
		case HOLLOW:
			g2d.drawOval(getX()-getRadius(), getY()-getRadius(), getDiameter(), getDiameter());
			break;
		case SOLID:
			g2d.fillOval(getX()-getRadius(), getY()-getRadius(), getDiameter(), getDiameter());
			break;
		}
	}
	
	//value
	public void updateValue() {
		value = polarity.value * discrete_size.base_value * orbit;
	}
	public int getBaseValue() {
		return discrete_size.base_value;
	}
	public int getValue() {
		updateValue();
		return value;
	}
	
	//geometry
	public int getX() {
		return (parent.getTagSize()/2) + Math.round((int)(Math.cos(getCardinalDirection().rad) * getDisplacement()));
	}
	public int getY() {
		return (parent.getTagSize()/2) + Math.round((int)(Math.sin(getCardinalDirection().rad) * getDisplacement()));
	}
	public int getRadius() {
		return Math.round(getDiscreteSize().size * parent.getScale() / 2);
	}
	public int getDiameter() {
		return Math.round(getDiscreteSize().size * parent.getScale());
	}
	public int getDisplacement() {
		return Math.round(parent.getBodySize() / (parent.getOrbits()*2) * getOrbit());
	}
	public Point getPoint() {
		return new Point(getX(), getY());
	}
	boolean intersects(CircleMark circle_mark) {
	    double dx = getX() - circle_mark.getX();
	    double dy = getY() - circle_mark.getY();
	    double distance = Math.sqrt(dx * dx + dy * dy);
	    
	    return distance <= (getRadius() + circle_mark.getRadius());
	}
	boolean isInsideCircleTag(double tag_size) {
	    return 
	    	((this.getX() - getRadius() >= 1) && (this.getX() + getRadius() <= tag_size-2))
	    	&&
	    	((this.getY() - getRadius() >= 1) && (this.getY() + getRadius() <= tag_size-2));
	 }	
}
