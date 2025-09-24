package marker;


import java.util.ArrayList;
import java.util.Collections;

import marker.enums.CardinalDirection;
import marker.enums.DiscreteSize;
import marker.enums.Polarity;

public abstract class CircleTagGenerator {
	private CircleTag circleTag;
	private CircleMark mark;
	
	private DiscreteSize sizes[];
	private CardinalDirection directions[];
	
	private ArrayList<CircleTag> savedStates;
	
	private int d, s, o, direction, orbit, size, markerId, idDifference, leastIdDifference;
	private boolean hasDifference;
	
	public CircleTagGenerator() {
		circleTag 	= new CircleTag();
		savedStates = new ArrayList<CircleTag>();
		
		sizes		= DiscreteSize.values();
		directions 	= CardinalDirection.values();
		
	}
	
	public abstract void onIdGenerated(CircleTag circleTag);

	public CircleTag getCircleTag() {
		return circleTag;
	}

	public synchronized void generateCircleTag(int id) {		
		if(!regenerateCircleTag(id, 1, 1)) {
			clearCircleMarks();
		}
		
		onIdGenerated(circleTag);
	}
	
	protected boolean regenerateCircleTag(int id, int offset, int alt) {
		clearCircleMarks();
		clearSavedStates();
		
		int target_id = (id - (((offset)/2) * alt));
		int maxSize, minOrbit=1, added=-1, minDifference=Integer.MAX_VALUE;
		
		while(minOrbit < 5) {
			maxSize=7;
			
			while(maxSize > 0) {
				added = 0;
				
				do {
					if(markerId < id) {
						added = addHollows(id, maxSize, minOrbit);
					}
					else if(markerId > id){
						added = addSolids(id, maxSize, minOrbit);
					}
					else{
						if(markerId != target_id) {
							minOrbit = 4;
							break;
						}
						else {
							return true;
						}
					}

					if(leastIdDifference <= minDifference) {
						minDifference = leastIdDifference;
						
						saveCircleTagState();
					}
					
				} while(added != 0);
				
				clearCircleMarks();

				maxSize--;
			}

			minOrbit++;
		}
		
		for(int i=savedStates.size()-1; i>=0; i--) {
			if(adjustCircleMarks(savedStates.get(i), target_id)) return true;
		}
		
		if(isNoSolutionAt(offset)) return false;

		return regenerateCircleTag(id-(alt*offset), offset+1, alt*-1);
	}
	
	protected void clearCircleMarks() {
		circleTag.clearCircleMarks();
		
		markerId = 0;
	}
	
	protected void clearSavedStates() {
		savedStates.clear();
	}
	
	protected boolean isNoSolutionAt(int offset) {
		if(offset == 10) {
			System.err.println("Error: no solution");
			return true;
		}
		return false;
	}
	
	protected int addHollows(int targetId, int maxSizeLimit, int minOrbitLimit) {
		return addCircleMarks(targetId, maxSizeLimit, minOrbitLimit, Polarity.HOLLOW);
	}
	
	protected int addSolids(int targetId, int maxSizeLimit, int minOrbitLimit) {
		return addCircleMarks(targetId, maxSizeLimit, minOrbitLimit, Polarity.SOLID);
	}
	
	protected int addCircleMarks(int targetId, int maxSizeLimit, int minOrbitLimit, Polarity polarity) {
		leastIdDifference = Integer.MAX_VALUE;
		
		int added = 0;
		while(markerId != targetId) {
			mark = new CircleMark(1, CardinalDirection.NW, DiscreteSize.I, polarity);
			circleTag.addCircleMark(mark);

			for(d=7; d>=0; d--) {
				mark.setDirection(directions[d]);
				
				for(s=0; s<maxSizeLimit; s++) {
					mark.setDiscreteSize(sizes[s]);
					
					for(o=5; o>=minOrbitLimit; o--) {
						mark.setOrbit(o);

						if(circleTag.isMarkerValid()) {
							markerId = circleTag.getTagId();
							
							setPartialCircleMarkPosition(markerId, targetId, polarity);
						}
					}
				}
			}
			
			if(!setFinalCircleMarkPosition()) {
				return added;
			}

			added++;
		}
		
		return added;
	}
	
	protected void setPartialCircleMarkPosition(int markerId, int targetId, Polarity polarity) {
		idDifference = idDifference(markerId, targetId);
		
		if(idDifference < leastIdDifference){
			leastIdDifference = idDifference;
			
			direction = d;
			orbit = o;
			size = s;
			
			hasDifference = true;
		}
	}
	
	protected int idDifference(int id1, int id2) {
		return Math.abs(id1 - id2);
	}
	
	protected boolean setFinalCircleMarkPosition() {
		if(hasDifference) {
			mark.setDirection(CardinalDirection.values()[direction]);
			mark.setDiscreteSize(DiscreteSize.values()[size]);
			mark.setOrbit(orbit);

			markerId = circleTag.getTagId();
			hasDifference = false;
			return true;
		}
		else {
			circleTag.removeCircleMark(mark);
			
			markerId = circleTag.getTagId();
			return false;
		}
	}
	
	protected void saveCircleTagState() {
		CircleTag newSavedState = new CircleTag();
		newSavedState.copyCircleMarks(circleTag);
		
		savedStates.add(newSavedState);
	}
	
	protected boolean adjustCircleMarks(CircleTag savedState, int id) {
		Collections.sort(savedState.getCircleMarkList(), (a, b) -> a.getDiscreteSize().compareTo(b.getDiscreteSize()));
		
		CircleTag tempState = new CircleTag();
		CircleMark lastMark;
		
		tempState.copyCircleMarks(savedState);
		while(tempState.getCircleMarksCount() > 1) {
			circleTag.copyCircleMarks(tempState);
			
			if(adjustCircleMark(circleTag, 0, circleTag.getCircleMarksCount(), id)) {
				return true;
			}
			
			lastMark = tempState.getCircleMarkList().get(0);
			tempState.removeCircleMark(lastMark);
		}
		
		return false;
	}
	
	protected boolean adjustCircleMark(CircleTag circleTag, int circleMarkIndex, int circleMarkCount, int id) {		
		if(circleMarkIndex < circleMarkCount) {
			CircleTag tempState = new CircleTag();
			CircleMark mark = circleTag.getCircleMark(circleMarkIndex);
			
			int orbitOrigin = mark.getOrbit();
			
			for(int orbit=orbitOrigin; orbit<=5; orbit++) {
				tempState.copyCircleMarks(circleTag);
				
				mark.setOrbit(orbit);
				if(adjustCircleMark(circleTag, circleMarkIndex + 1, circleMarkCount, id)) {
					return true;
				}
				
				circleTag.copyCircleMarks(tempState);
			}

			mark.setOrbit(orbitOrigin);
		}
		
		if(circleTag.isMarkerValid()) {
			int added = -1;
			
			while(added != 0) {
				if(markerId < id) {
					added = addHollows(id, sizes.length, 1);
				}
				else if(markerId > id){
					added = addSolids(id, sizes.length, 1);
				}
				else {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
