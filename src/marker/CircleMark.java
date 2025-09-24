package marker;

import java.awt.Graphics2D;
import java.awt.Point;

import marker.enums.CardinalDirection;
import marker.enums.DiscreteSize;
import marker.enums.Polarity;

public class CircleMark {
	private int orbit, value;
	private DiscreteSize discrete_size;
	private CardinalDirection direction;
	private Polarity polarity;
	private CircleTag parent;
	boolean isMarkerChanged;
	
	public CircleMark(int orbit, CardinalDirection direction, DiscreteSize discrete_size,  Polarity polarity) {
		setOrbit(orbit);
		setDirection(direction);
		setDiscreteSize(discrete_size);
		setPolarity(polarity);
	}
	
	@Override
	public String toString() {
		return String.valueOf(getOrbit()) + getCardinalDirection() + getDiscreteSize() + getPolarity().sym;
	}
	
	public void setOrbit(int orbit) {
		this.orbit = orbit;
		setMarkerChanged();
	}
	
	public int getOrbit() {
		return orbit;
	}
	
	public void setDirection(CardinalDirection direction) {
		this.direction = direction;
		setMarkerChanged();
	}
	
	public CardinalDirection getCardinalDirection() {
		return direction;
	}
	
	public void setPolarity(Polarity polarity) {
		this.polarity = polarity;
		setMarkerChanged();
	}

	public Polarity getPolarity() {
		return polarity;
	}

	public void setDiscreteSize(DiscreteSize discrete_size) {
		this.discrete_size = discrete_size;
		setMarkerChanged();
	}
	
	public DiscreteSize getDiscreteSize() {
		return discrete_size;
	}

	void setParent(CircleTag parent) {
		this.parent = parent;
	}
	
	public CircleTag getParent(CircleTag parent) {
		return parent;
	}
	
	void setMarkerChanged() {
		isMarkerChanged = true;
		if(parent != null) parent.setTagChanged();
	}
	
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
	
	public int getX() {
		return (parent.getTagSize()/2) + Math.round((int)(Math.cos(getCardinalDirection().rad) * getDisplacement()));
	}
	
	public int getY() {
		return (parent.getTagSize()/2) + Math.round((int)(Math.sin(getCardinalDirection().rad) * getDisplacement()));
	}
	
	public int getRadius() {
		return Math.round(getDiameter() / 2f);
	}
	
	public int getDiameter() {
		return Math.round((getDiscreteSize().size * parent.getScale()));
	}
	
	public int getDisplacement() {
		return Math.round(parent.getBodySize() / (CircleTag.ORBITS * 2f) * getOrbit());
	}
	
	public Point getPoint() {
		return new Point(getX(), getY());
	}

	public int calculateValue() {
		return getOrbit() * getBaseValue() * getPolarity().value;
	}
	
	public int getBaseValue() {
		return discrete_size.base_value;
	}
	
	public int getValue() {
		if(isMarkerChanged) {
			value = calculateValue();
			isMarkerChanged = false;
		}
		
		return value;
	}

	boolean intersects(CircleMark circle_mark) {
	    int dx = getX() - circle_mark.getX();
	    int dy = getY() - circle_mark.getY();
	    int distance = (int)Math.round(Math.sqrt((dx * dx) + (dy * dy)));
	    
	    return distance <= ((getRadius() + circle_mark.getRadius()));
	}
	
	boolean isInsideCircleTag() {
		if(parent == null) return false;
		
	    return 
	    	(((this.getX() - getRadius()) >= 1) && ((this.getX() + getRadius()) <= (parent.getTagSize()-2)))
	    	&&
	    	(((this.getY() - getRadius()) >= 1) && ((this.getY() + getRadius()) <= (parent.getTagSize()-2)));
	 }
}
