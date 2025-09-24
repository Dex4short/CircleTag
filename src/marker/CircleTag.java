package marker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class CircleTag {
	public static final int ORBITS = 5;
	
	private static final int BODY_SIZE = 101, MARGINE = 10;
	private int markerId;
	private float scale;
	private boolean isOrbitsVisible, isMarkerBodyVisible;
	private ArrayList<CircleMark> circles;
	private BasicStroke stroke;
	
	boolean isTagChanged;
	
	public CircleTag() {
		isOrbitsVisible = false;
		isMarkerBodyVisible = false;
		isTagChanged = false;
		circles = new ArrayList<CircleMark>();

		setScale(1);
	}
	
	public void addCircleMark(CircleMark circle_mark) {
		circles.add(circle_mark);
		circle_mark.setParent(this);
		
		setTagChanged();
	}
	
	public CircleMark getCircleMark(int index) {
		return circles.get(index);
	}

	public int getCircleMarksCount() {
		return circles.size();
	}
	
	public CircleMark[] getCircleMarks() {
		return circles.toArray(new CircleMark[getCircleMarksCount()]);
	}
	
	public ArrayList<CircleMark> getCircleMarkList() {
		return circles;
	}

	public void setCircleMarks(CircleMark circle_marks[]) {
		clearCircleMarks();
		
		for(CircleMark cm: circle_marks) {
			addCircleMark(cm);
		}
	}
	
	public void removeCircleMark(CircleMark circle_mark) {
		circles.remove(circle_mark);
		circle_mark.setParent(null);
		
		setTagChanged();
	}
	
	public void clearCircleMarks() {
		circles.clear();
		
		setTagChanged();
	}
	
	public void copyCircleMarks(CircleTag circleTag) {
		clearCircleMarks();
		
		for(CircleMark c: circleTag.getCircleMarks()) {
			addCircleMark(new CircleMark(c.getOrbit(), c.getCardinalDirection(), c.getDiscreteSize(), c.getPolarity()));
		}
	}
	
	void setTagChanged() {
		isTagChanged = true;
	}
	
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
	
	public void setScale(float scale) {
		this.scale = scale;
		
		stroke = new BasicStroke(scale);
	}
	
	public float getScale() {
		return scale;
	}

	public int getBodySize() {
		int bodySize = Math.round(BODY_SIZE * scale);
		
		if(bodySize%2 == 0) {
			return bodySize + 1;
		}
		
		return bodySize;
	}
	
	public int getMargine() {
		return Math.round(MARGINE * scale);
	}
	
	public int getTagSize() {		
		return getBodySize() + (getMargine() * 2);
	}

	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(stroke);
		
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getTagSize(), getTagSize());
		g2d.setColor(Color.black);
		g2d.drawRect(0, 0, getTagSize(), getTagSize());

		g2d.setColor(Color.lightGray);
		if(isMarkerBodyVisible) drawMarkerBody(g2d);
		if(isOrbitsVisible) drawOrbits(g2d);
		
		g2d.setColor(Color.black);
		for(int c=0; c<getCircleMarksCount(); c++) {
			drawCircleMark(g2d, getCircleMark(c));
		}
		
	}
	
	public void draw(Graphics2D g2d, int x, int y) {
		g2d.translate(x, y);
		draw(g2d);
		g2d.translate(-x, -y);
	}
	
	protected void drawOrbits(Graphics2D g2d) {
		int 
		tp 		= getTagSize()/2,
		o_size 	= 0, 
		o_gap 	= Math.round(getBodySize()/(ORBITS*2));

		g2d.translate(tp, tp);
		for(int o = 0; o < ORBITS; o++) {
			o_size += o_gap*2;
			g2d.drawOval(
					Math.round( -(o_size/2)),
					Math.round( -(o_size/2)),
					Math.round(o_size),
					Math.round(o_size)
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
	
	public int calculateMarkerID() {
		int id = 0;
		for(CircleMark circleMark : getCircleMarks()) {
			id += circleMark.getValue();
		}
		return id;
	}
	
	public int getTagId() {
		if(isTagChanged) {
			markerId = calculateMarkerID();
			isTagChanged = false;
		}
		
		return markerId;
	}
	
	public boolean isMarkerValid() {
		int c1,c2;
		
		for (c1 = 0; c1 < circles.size(); c1++) {
			if(!circles.get(c1).isInsideCircleTag()) 
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
