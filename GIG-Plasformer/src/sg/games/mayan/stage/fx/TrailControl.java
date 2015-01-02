package sg.games.mayan.stage.fx;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Attach this to a node. A trail will start following it. The line which you need to inject should be attached to a
 * Geometry node, which should be attached to the root. Moving that Geometry node around has rather weird results, since
 * the points for the trail are computed in world space.
 * 
 * @author cvlad
 */
public class TrailControl extends AbstractControl {

	float lifeSpan = 5;
	LinkedList<Vector3f> bin = new LinkedList<Vector3f>();
	LinkedList<Double> birthTime = new LinkedList<Double>();
	LineControl line;
	Vector3f lastSpawnPos;
	Vector3f difference = new Vector3f();
	float segmentLength = 0.1f;
	float segmentLengthSqr = this.segmentLength * this.segmentLength;
	float startWidth = 1;
	float endWidth = 1;
	double localTime = 0;

	/**
	 * Constructor
	 * 
	 * @param line
	 *            Inject a line which is attached to a Geometry node which is attached to the root node.
	 */
	public TrailControl(final LineControl line) {
		this.line = line;
	}

	@Override
	public void setSpatial(final Spatial spatial) {
		super.setSpatial(spatial);
		this.lastSpawnPos = spatial.getWorldTranslation().clone();
	}

	@Override
	protected void controlUpdate(final float f) {
		if (this.line == null) {
			final LineControl l = this.spatial.<LineControl> getControl(LineControl.class);
			if (l == null) {
				return;
			}
			this.line = l;
		}
		this.localTime += f;
		final Vector3f currentPos = this.spatial.getWorldTranslation();
		this.lastSpawnPos.subtract(currentPos, this.difference);
		boolean changed = false;
		final float differenceSqr = this.difference.lengthSquared();
		if (differenceSqr != 0) {
			if (this.line.getNumPoints() == 0 || differenceSqr > this.segmentLengthSqr) {
				Vector3f newPoint = null;
				if (this.bin.isEmpty()) {
					newPoint = currentPos.clone();
				} else {
					newPoint = this.bin.remove();
					newPoint.set(currentPos);
				}
				this.lastSpawnPos.set(newPoint);
				this.line.addPoint(newPoint, this.startWidth);
				this.birthTime.add(this.localTime + this.lifeSpan);
			} else {
				this.line.setPoint(this.line.getNumPoints() - 1, currentPos);
			}
			changed = true;
		}
		final Iterator<Double> itBirthTime = this.birthTime.iterator();

		while (itBirthTime.hasNext()) {
			if (this.localTime >= itBirthTime.next() + this.lifeSpan) {
				itBirthTime.remove();
				final Vector3f removed = this.line.removePoint(0);
				if (this.bin.size() < this.line.getNumPoints()) {
					this.bin.add(removed);
				}
				changed = true;
			} else {
				break;
			}
		}

		if (changed && this.startWidth != this.endWidth) {
			final List<Float> widths = this.line.getHalfWidths();
			final int widthsSize = widths.size();
			widths.clear();
			if (widthsSize != 0) {
				if (widthsSize == 1) {
					widths.add(this.startWidth);
				} else {
					final int widthSizeMinusOne = widthsSize - 1;
					for (int i = 0; i < widthsSize; i++) {
						widths.add(this.startWidth * (widthSizeMinusOne - i) / widthSizeMinusOne + this.endWidth * i
								/ widthSizeMinusOne);
					}
				}
			}
		}
	}

	@Override
	protected void controlRender(final RenderManager rm, final ViewPort vp) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Control cloneForSpatial(final Spatial sptl) {
		final TrailControl clone = new TrailControl(sptl.<LineControl> getControl(LineControl.class));
		clone.setLifeSpan(this.lifeSpan);
		clone.setSegmentLength(this.segmentLength);
		sptl.addControl(clone);
		return clone;
	}

	/**
	 * Sets how fast shall the trail disappear
	 * 
	 * @param lifeSpan
	 *            Lifespan of the points which make up the trail
	 */
	public void setLifeSpan(final float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	/**
	 * Gets how fast shall the trail disappear
	 * 
	 * @return Lifespan of the points which make up the trail
	 */
	public float getLifeSpan() {
		return this.lifeSpan;
	}

	/**
	 * Sets after how much distance shall a new point be generated. The last point which was added to the trail will be
	 * moved around until a new point is generated. So lowering this value generally gives better results.
	 * 
	 * @param segmentLength
	 */
	public void setSegmentLength(final float segmentLength) {
		this.segmentLength = segmentLength;
		this.segmentLengthSqr = segmentLength * segmentLength;
	}

	/**
	 * Gets after how much distance shall a new point be generated.
	 * 
	 * @return after how much distance shall a new point be generated.
	 */
	public float getSegmentLength() {
		return this.segmentLength;
	}

	/**
	 * Gets after how much distance shall a new point be generated. Distance is squared.
	 * 
	 * @return after how much distance shall a new point be generated. Distance is squared.
	 */
	public float getSegmentLengthSqr() {
		return this.segmentLengthSqr;
	}

	/**
	 * Sets the width at the starting point of the trail. So the oldest point in this trail has this width.
	 * 
	 * @param startWidth
	 */
	public void setStartWidth(final float startWidth) {
		this.startWidth = startWidth / 2;
	}

	public float getStartWidth() {
		return this.startWidth * 2;
	}

	/**
	 * Sets the width at the ending point of the trail. So the newest point in this trail has this width. You know, the
	 * point closest to the spatial onto which you attached this control.
	 * 
	 * @param endWidth
	 *            Width at the ending point of the trail.
	 */
	public void setEndWidth(final float endWidth) {
		this.endWidth = endWidth / 2;
	}

	/**
	 * Gets the width at the ending point of the trail. So the newest point in this trail has this width. You know, the
	 * point closest to the spatial onto which you attached this control.
	 * 
	 * @return Width at the ending point of the trail.
	 */
	public float getEndWidth() {
		return this.endWidth * 2;
	}
}