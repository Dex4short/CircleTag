package marker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CircleTag {
	private int body_size, margine, orbits, id;
	private float scale;
	private boolean isOrbitsVisible, isMarkerBodyVisible;
	private ArrayList<CircleMark> circles;
	
	private int tp, ox, oy, o_size, o_gap;
	private BasicStroke stroke;
	
	public CircleTag() {
		body_size = 100;
		margine = 10;
		
		isOrbitsVisible = false;
		isMarkerBodyVisible = false;

		orbits = 5;
		circles = new ArrayList<CircleMark>();
		
		setScale(1);
	}
	
	//drawing function
	public void draw(Graphics2D g2d, int x, int y) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(stroke);
		
		g2d.translate(x, y);
		
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getTagSize(), getTagSize());
		g2d.setColor(Color.black);
		g2d.drawRect(0, 0, getTagSize(), getTagSize());

		g2d.setColor(Color.lightGray);
		if(isMarkerBodyVisible) drawMarkerBody(g2d);
		if(isOrbitsVisible) drawOrbits(g2d);
		
		g2d.setColor(Color.black);
		forEachCircle( c -> {
			if(c != null) drawCircleMark(g2d, c);
		});
		
		g2d.translate(-x, -y);
	}
	
	protected void drawOrbits(Graphics2D g2d) {
		o_gap = Math.round(body_size/(orbits*2));
		o_size = 0;

		tp = getTagSize()/2;
		g2d.translate(tp, tp);
		for(int o=0; o<orbits; o++) {
			o_size += o_gap*2;
			g2d.drawOval(
					Math.round((ox - (o_size/2)) * scale),
					Math.round((oy - (o_size/2)) * scale),
					Math.round(o_size * scale),
					Math.round(o_size * scale)
			);
		}
		g2d.translate(-tp,-tp);
	}
	
	protected void drawMarkerBody(Graphics2D g2d) {
		g2d.drawRect(getMargine(), getMargine(), getBodySize(), getBodySize());
	}
	
	protected void drawCircleMark(Graphics2D g2d, CircleMark circle_mark) {
		circle_mark.drawCircleMark(g2d);
	}
	
	//circle marks
	public void addCircleMark(CircleMark circle_mark) {
		circles.add(circle_mark);
		circle_mark.setParent(this);
	}
	
	public void removeCircleMark(CircleMark circle_mark) {
		circles.remove(circle_mark);
		circle_mark.setParent(null);
	}
	
	public CircleMark getCircleMark(int index) {
		return circles.get(index);
	}
	
	public CircleMark[] getCircleMarks() {
		return circles.toArray(new CircleMark[circles.size()]);
	}
	
	public ArrayList<CircleMark> getCircleMarkList() {
		return circles;
	}
	
	public int getCircleMarksCount() {
		return circles.size();
	}
	
	public void setCircleMarks(CircleMark circle_marks[]) {
		circles.clear();
		for(CircleMark cm: circle_marks) {
			addCircleMark(cm);
		}
	}
	
	public void clearCircleMarks() {
		circles.clear();
	}
	
	public void forEachCircle(Consumer<CircleMark> c) {
		circles.forEach(c);
	}
	
	public void copyCircleMarks(CircleTag circleTag) {
		clearCircleMarks();
		for(CircleMark c: circleTag.getCircleMarks()) {
			addCircleMark(new CircleMark(c.getOrbit(), c.getCardinalDirection(), c.getDiscreteSize(), c.getPolarity()));
		}
	}
	
	//tag feature visibility
	public void setOrbitsVisible(boolean isOrbitsVisible) {
		this.isOrbitsVisible = isOrbitsVisible;
	}
	
	public boolean isOrbitsVisible() {
		return isOrbitsVisible;
	}
	
	public void setMarkerBodyVisible(boolean isMarkerBodyVisible) {
		this.isMarkerBodyVisible = isMarkerBodyVisible;
	}
	
	public boolean isMarkerBodyVisible() {
		return isMarkerBodyVisible;
	}
	
	//tag properties
	public void setScale(float scale) {
		this.scale = scale;
		stroke = new BasicStroke(scale);
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setMargine(int margine) {
		this.margine = margine;
	}
	
	public int getMargine() {
		return Math.round(margine * scale);
	}
	
	public int getBodySize() {
		return Math.round(body_size * scale);
	}
	
	public int getTagSize() {
		int tagSize = (getBodySize() + (getMargine() * 2));
		
		if(tagSize%2 == 0) {
			return tagSize + 1;
		}
		
		return tagSize;
	}
	
	public int getOrbits() {
		return orbits;
	}
	
	//CircleTag id
	public void calculateMarkerID() {
		id = 0;
		circles.forEach( c -> {
			id += c.getValue();
		});
	}
	
	public int getMarkerId() {
		calculateMarkerID();
		return id;
	}
	
	private int c1,c2;
	public boolean isMarkerValid() {
		for (c1 = 0; c1 < circles.size(); c1++) {
			if(!circles.get(c1).isInsideCircleTag(getTagSize())) 
				return false;
			
			for (c2 = c1 + 1; c2 < circles.size(); c2++) {
				if (circles.get(c1).intersects(circles.get(c2))) {
					return false;
				}
			}
		}
		return true;
	}
	
}
