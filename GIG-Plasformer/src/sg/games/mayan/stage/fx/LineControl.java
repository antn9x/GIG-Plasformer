package sg.games.mayan.stage.fx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.util.BufferUtils;

/**
 * 3d Floating Billboarded Lines Attach this to a Geometry Node, which has a Material. The Material should have
 * FaceCulling set Off! The Mesh is procedurally created, texture coordinates are computed using the line segment
 * lengths. Normals are not computed, since it's billboarded anyway. Can be used for trails. In that case set
 * usingLinkedList true. If you do this, LinkedLists are used, which make adding/removing points more efficient. Note
 * however, that of course random access to points suffer from using LinkedLists. If you need to access points often,
 * for example to create lightning bolts, set usedAsTrails false. In this case ArrayLists are used to make accessing
 * points more efficient. Note: Calling setPoint(Vector3f) will not only set the points position, but also recompute the
 * texture coordinates for all points. If you want to access Points and modify their positions without costly
 * recomputing all the texture coordinates, then use getPoint(int index) and modify that vector directly or use
 * getPoints(). This way you can avoid recomputing texture coordinates, however if you want to have correctly computed
 * texture coordinates after modifying a bunch of points this way, then call recomputeTextureCoordinates() after moving
 * the vertices this way yourself.
 * 
 * @author cvlad
 */
public class LineControl extends AbstractControl {
	static final int EXPECTED_POINTS = 32;
	static final int MINIMUM_POS_BUFFER_SIZE = EXPECTED_POINTS * 6; // 2 verts per point, 3 floats per pos
	static final int MINIMUM_TEXCOORD_BUFFER_SIZE = EXPECTED_POINTS * 4; // 2 verts per point, 2 floats per texcoord
	static final int MINIMUM_INDEX_BUFFER_SIZE = EXPECTED_POINTS * 2; // 2 verts per point, 1 int per index
	List<Vector3f> points;
	List<Float> halfWidths;
	List<FloatWrapper> lengths;
	Mesh mesh;
	float totalLength = 0;
	boolean usingLinkedList = true;
	LineBehaviour behaviour;

	/**
	 * Constructor Standard line behaviour: Uses Algo1CamDirBB and usingLinkedList is set false.
	 */
	public LineControl() {
		this(new Algo1CamDirBB(), false);
	}

	/**
	 * Constructor
	 * 
	 * @param behaviour
	 *            There are different algorithms available to billboard the line. Inject one of those here.
	 * @param usingLinkedList
	 *            When set to true, LinkedLists are used to make adding/removing points more efficient. False means
	 *            ArrayLists will be used, which makes manipulating existing points more efficient.
	 */
	public LineControl(final LineBehaviour behaviour, final boolean usingLinkedList) {
		this.behaviour = behaviour;
		this.usingLinkedList = usingLinkedList;
		if (usingLinkedList) {
			this.points = new LinkedList<Vector3f>();
			this.halfWidths = new LinkedList<Float>();
			this.lengths = new LinkedList<FloatWrapper>();
		} else {
			this.points = new ArrayList<Vector3f>();
			this.halfWidths = new ArrayList<Float>();
			this.lengths = new ArrayList<FloatWrapper>();
		}
	}

	@Override
	public void setSpatial(final Spatial spatial) {
		if (!(spatial instanceof Geometry)) {
			throw new ClassCastException("LineControl can only be attached to Geometry Nodes");
		}
		super.setSpatial(spatial);

		this.mesh = new Mesh();
		this.mesh.setDynamic();

		// Setting buffers
		final FloatBuffer positions = BufferUtils.createFloatBuffer(MINIMUM_POS_BUFFER_SIZE);
		positions.limit(0);
		this.mesh.setBuffer(Type.Position, 3, positions);
		final FloatBuffer texCoord = BufferUtils.createFloatBuffer(MINIMUM_TEXCOORD_BUFFER_SIZE);
		texCoord.limit(0);
		this.mesh.setBuffer(Type.TexCoord, 2, texCoord);
		final IntBuffer indexes = BufferUtils.createIntBuffer(MINIMUM_INDEX_BUFFER_SIZE);
		indexes.limit(0);
		this.mesh.setBuffer(Type.Index, 3, indexes);
		this.mesh.updateBound();
		this.mesh.setMode(Mesh.Mode.TriangleStrip);
		((Geometry) spatial).setMesh(this.mesh);
	}

	@Override
	protected void controlUpdate(final float f) {
	}

	@Override
	protected void controlRender(final RenderManager rm, final ViewPort vp) {
		this.behaviour.update(this, rm, vp);
	}

	/**
	 * Returns line's billboarding behaviour.
	 * 
	 * @return line's billboarding behaviour.
	 */
	public LineBehaviour getBehaviour() {
		return this.behaviour;
	}

	@Override
	public Control cloneForSpatial(final Spatial sptl) {
		final LineControl clone = new LineControl(this.behaviour, this.usingLinkedList);
		clone.set(this.points, this.halfWidths);
		return clone;
	}

	/**
	 * Clears this line's points and halfWidths and copys(!) points and halfwidths into this line.
	 * 
	 * @param Points
	 *            through which the line will be drawn.
	 * @param halfWidths
	 *            Half of the width for each point.
	 */
	public void set(final List<Vector3f> points, final List<Float> halfWidths) {
		if (points == null || halfWidths == null) {
			throw new IllegalArgumentException("LineNodes.set: points and halfWidths may not be null");
		}
		if (points.size() != halfWidths.size()) {
			throw new IllegalArgumentException("LineNodes.set: points.size() has to be equal to halfWidths.size()");
		}
		this.points.clear();
		this.halfWidths.clear();
		for (final Vector3f point : points) {
			if (point == null) {
				throw new IllegalArgumentException("LineNodes.set: One of the vectors in the points parameter was null");
			}
			this.points.add(new Vector3f(point.x, point.y, point.z));
		}
		for (final float halfWidth : halfWidths) {
			this.halfWidths.add(halfWidth);
		}

		int p = 2;
		while (p < points.size()) {
			p *= 2;
		}

		// positions
		final FloatBuffer positions = BufferUtils.createFloatBuffer(p * 6);
		positions.limit(points.size() * 6);
		this.mesh.setBuffer(Type.Position, 3, positions);

		// indexes
		final IntBuffer indexes = BufferUtils.createIntBuffer(p * 2);
		for (int i = 0; i < points.size() * 2; i++) {
			indexes.put(i);
		}
		indexes.flip();
		this.mesh.setBuffer(Type.Index, 3, indexes);

		// texcoord
		this.totalLength = 0;
		this.lengths.clear();
		if (points.size() > 1) {
			final Vector3f distance = new Vector3f();
			final Iterator<Vector3f> itPoint = points.iterator();
			Vector3f point = itPoint.next();
			while (itPoint.hasNext()) {
				final Vector3f nextPoint = itPoint.next();
				this.totalLength += nextPoint.subtract(point, distance).length();
				this.lengths.add(new FloatWrapper(this.totalLength));
				point = nextPoint;
			}
		}

		final FloatBuffer texCoord = BufferUtils.createFloatBuffer(p * 4);
		texCoord.put(0.0f);
		texCoord.put(0.0f);
		texCoord.put(0.0f);
		texCoord.put(1.0f);
		if (this.totalLength == 0) {
			for (final FloatWrapper l : this.lengths) {
				texCoord.put(0.0f);
				texCoord.put(0.0f);
				texCoord.put(0.0f);
				texCoord.put(1.0f);
			}
		} else {
			for (final FloatWrapper l : this.lengths) {
				final float length = l.getValue() / this.totalLength;
				texCoord.put(length);
				texCoord.put(0.0f);
				texCoord.put(length);
				texCoord.put(1.0f);
			}
		}
		texCoord.flip();
		this.mesh.setBuffer(Type.TexCoord, 2, texCoord);
		this.behaviour.set();
	}

	/**
	 * Sets the width at the point whom index you provide.
	 * 
	 * @param index
	 *            Point's (ergo halfWidths) index in line's list.
	 * @param width
	 *            How width the line shall be at that point.
	 */
	public void setWidth(final int index, final float width) {
		this.halfWidths.set(index, width / 2);
	}

	/**
	 * Gets the width at the point whom index you provide.
	 * 
	 * @param index
	 *            Point's (ergo halfWidths) index in line's list.
	 * @return width How width the line is at that point.
	 */
	public float getWidth(final int index) {
		return this.halfWidths.get(index) * 2;
	}

	/**
	 * Sets the width at the point whom index you provide.
	 * 
	 * @param index
	 *            Point's (ergo halfWidths) index in line's list.
	 * @param halfWidth
	 *            Half of the desired width
	 */
	public void setHalfWidth(final int index, final float halfWidth) {
		this.halfWidths.set(index, halfWidth);
	}

	/**
	 * Gets the half of the width at the point whom index you provide.
	 * 
	 * @param index
	 *            Point's (ergo halfWidths) index in line's list.
	 * @return Half of the width at that point
	 */
	public float getHalfWidth(final int index) {
		return this.halfWidths.get(index);
	}

	/**
	 * Returns the list of the points which make up this line. Do NOT add or remove points. You can however set the
	 * points positions. After doing so you should call recomputeTextureCoordinates if you want the texture coordinates
	 * to be correct.
	 */
	public List<Vector3f> getPoints() {
		return this.points;
	}

	/**
	 * Returns the list of the widths of the points which make up this line. Do NOT add or remove items. You can however
	 * modify the existing widths The widths divided by two.
	 * 
	 * @return Returns the list of the widths of the points which make up this line. The widths divided by two.
	 */
	public List<Float> getHalfWidths() {
		return this.halfWidths;
	}

	/**
	 * Copys the point's coordinates into the point whom index you provide.
	 * 
	 * @param index
	 *            Index of the point which shall be modified.
	 * @param point
	 *            Point who's coordinates are copied into the point whom index you provide
	 */
	public void setPoint(final int index, final Vector3f point) {
		this.getPoint(index).set(point);
		this.recomputeTextureCoordinates();
	}

	/**
	 * Returns a point whom index you provide. Modifying that point will alter the line. However, after you do this, you
	 * should call recomputeTextureCoordinates if you want the texture coordinates to be correct.
	 * 
	 * @param index
	 *            Index of the point you want to get
	 * @return Point who's index you provided
	 */
	public Vector3f getPoint(final int index) {
		if (this.usingLinkedList) {
			if (index == 0) {
				return ((LinkedList<Vector3f>) this.points).getFirst();
			}
			if (index == this.points.size() - 1) {
				return ((LinkedList<Vector3f>) this.points).getLast();
			}
		}
		return this.points.get(index);
	}

	/**
	 * Returns the number of points in this line.
	 * 
	 * @return number of points in this line
	 */
	public int getNumPoints() {
		return this.points.size();
	}

	private class FloatWrapper {
		float value;

		public FloatWrapper(final float value) {
			this.value = value;
		}

		public void setValue(final float value) {
			this.value = value;
		}

		public float getValue() {
			return this.value;
		}
	}

	/**
	 * Removes the point who's index you provide. Returns a handle of that point, so you can recycle it if you want to.
	 * 
	 * @param index
	 *            Index of the point you want to remove.
	 * @return Point which was removed.
	 */
	public Vector3f removePoint(final int index) {
		if (index < 0 || index >= this.points.size()) {
			throw new IllegalArgumentException("LineNodes.removePoint: There is no point with such an index");
		}
		Vector3f oldPoint = null;
		if (this.usingLinkedList) {
			if (index == 0) {
				((LinkedList<Float>) this.halfWidths).removeFirst();
				oldPoint = ((LinkedList<Vector3f>) this.points).removeFirst();
			} else if (index == this.points.size() - 1) {
				((LinkedList<Float>) this.halfWidths).removeLast();
				oldPoint = ((LinkedList<Vector3f>) this.points).removeLast();
			} else {
				this.halfWidths.remove(index);
				oldPoint = this.points.remove(index);
			}
		} else {
			this.halfWidths.remove(index);
			oldPoint = this.points.remove(index);
		}

		// positions
		VertexBuffer pvb = this.mesh.getBuffer(Type.Position);
		final FloatBuffer positions = (FloatBuffer) pvb.getData();
		positions.limit(positions.limit() - 6);
		if (positions.capacity() / 4 >= MINIMUM_POS_BUFFER_SIZE && positions.limit() <= positions.capacity() / 4) {
			final FloatBuffer newBuffer = BufferUtils.createFloatBuffer(positions.capacity() / 2);
			newBuffer.limit(positions.limit());
			this.mesh.setBuffer(Type.Position, 3, newBuffer); // TODO: is this necessary???
		} else {
			pvb.updateData(positions); // TODO: is this necessary???
			// mesh.setBuffer(Type.Position, 3, positions); // TODO: shouldn't it work without this???
			// mesh.updateCounts();
		}

		// indexes
		pvb = this.mesh.getBuffer(Type.Index);
		final IntBuffer indexes = (IntBuffer) pvb.getData();
		indexes.limit(indexes.limit() - 2);
		if (indexes.capacity() / 4 >= MINIMUM_INDEX_BUFFER_SIZE && indexes.limit() <= indexes.capacity() / 4) {
			final IntBuffer newBuffer = BufferUtils.createIntBuffer(indexes.capacity() / 2);
			indexes.rewind();
			newBuffer.put(indexes);
			newBuffer.flip();
			this.mesh.setBuffer(Type.Index, 3, newBuffer); // TODO: is this necessary???
		} else {
			pvb.updateData(indexes); // TODO: is this necessary???
			// mesh.setBuffer(Type.Index, 3, indexes); // TODO: shouldn't it work without this???
			// mesh.updateCounts();
		}

		// texcoord
		if (this.points.size() > 1) {
			if (index == 0) {
				float removedLength;
				if (this.usingLinkedList) {
					removedLength = ((LinkedList<FloatWrapper>) this.lengths).remove().getValue();
				} else {
					removedLength = this.lengths.remove(0).getValue();
				}
				this.totalLength -= removedLength;

				final Iterator<FloatWrapper> itLengths = this.lengths.iterator();
				while (itLengths.hasNext()) {
					final FloatWrapper l = itLengths.next();
					l.setValue(l.getValue() - removedLength);
				}

			} else if (index == this.lengths.size()) { // this should mean that the last point is removed, hence index
														// == lengths.size()
				float removedLength;
				if (this.usingLinkedList) {
					removedLength = ((LinkedList<FloatWrapper>) this.lengths).removeLast().getValue();
					this.totalLength += ((LinkedList<FloatWrapper>) this.lengths).getLast().getValue() - removedLength;
				} else {
					removedLength = this.lengths.remove(this.lengths.size() - 1).getValue();
					this.totalLength += this.lengths.get(this.lengths.size() - 1).getValue() - removedLength;
				}
			} else {
				final float newSegmentLength = this.points.get(index - 1).subtract(this.points.get(index)).length();
				float lengthBeforeSegment = 0;
				if (index > 1) {
					lengthBeforeSegment = this.lengths.get(index - 2).getValue();
				}
				final float removedSegmentsLengths = this.lengths.remove(index).getValue() - lengthBeforeSegment;
				final float deltaLength = newSegmentLength - removedSegmentsLengths;
				this.totalLength += deltaLength;
				this.lengths.get(index - 1).setValue(newSegmentLength + lengthBeforeSegment);

				if (this.usingLinkedList) {
					final Iterator<FloatWrapper> itLengths = this.lengths.iterator();
					for (int i = 0; i < index; i++) {
						itLengths.next();
					}
					for (int i = index; i < this.lengths.size(); i++) {
						final FloatWrapper f = itLengths.next();
						f.setValue(f.getValue() + deltaLength);
					}
				} else {
					for (int i = index; i < this.lengths.size(); i++) {
						final FloatWrapper f = this.lengths.get(i);
						f.setValue(f.getValue() + deltaLength);
					}
				}
			}
		} else {
			this.totalLength = 0;
			this.lengths.clear();
		}

		pvb = this.mesh.getBuffer(VertexBuffer.Type.TexCoord);
		final FloatBuffer texCoord = (FloatBuffer) pvb.getData();
		texCoord.limit(texCoord.limit() - 4);
		if (texCoord.capacity() / 4 >= MINIMUM_TEXCOORD_BUFFER_SIZE && texCoord.limit() <= texCoord.capacity() / 4) { // need
																														// to
																														// create
																														// new
																														// buffer
			final FloatBuffer newBuffer = BufferUtils.createFloatBuffer(texCoord.capacity() / 2); // reasonable
																									// assumption: 2
																									// times the
																									// previous size
			newBuffer.limit(texCoord.limit()); // two new vertices, hence 4 new texture coordinates
			newBuffer.put(0.0f);
			newBuffer.put(0.0f);
			newBuffer.put(0.0f);
			newBuffer.put(1.0f);

			if (this.totalLength == 0) {
				for (final FloatWrapper l : this.lengths) {
					newBuffer.put(0.0f);
					newBuffer.put(0.0f);
					newBuffer.put(0.0f);
					newBuffer.put(1.0f);
				}
			} else {
				for (final FloatWrapper l : this.lengths) {
					final float length = l.getValue() / this.totalLength;
					newBuffer.put(length);
					newBuffer.put(0.0f);
					newBuffer.put(length);
					newBuffer.put(1.0f);
				}
			}

			newBuffer.flip();
			this.mesh.setBuffer(Type.TexCoord, 2, newBuffer); // TODO: is this necessary???
		} else {
			if (this.points.size() != 0) {
				texCoord.rewind();
				texCoord.put(0.0f);
				texCoord.put(0.0f);
				texCoord.put(0.0f);
				texCoord.put(1.0f);

				if (this.totalLength == 0) {
					for (final FloatWrapper l : this.lengths) {
						texCoord.put(0.0f);
						texCoord.put(0.0f);
						texCoord.put(0.0f);
						texCoord.put(1.0f);
					}
				} else {
					for (final FloatWrapper l : this.lengths) {
						final float length = l.getValue() / this.totalLength;
						texCoord.put(length);
						texCoord.put(0.0f);
						texCoord.put(length);
						texCoord.put(1.0f);
					}
				}
				texCoord.flip();
			}
			pvb.updateData(texCoord); // TODO: is this necessary???
			// mesh.setBuffer(Type.TexCoord, 2, texCoord); // TODO: shouldn't it work without this???
			// mesh.updateCounts(); // TODO: is this necessary???
		}
		this.mesh.updateCounts(); // TODO: is this necessary???
		this.behaviour.removedPoint(this, index, oldPoint);
		return oldPoint;
	}

	/**
	 * Recomputes the texture coordinates by using the line segments length. Call this after modifying the points
	 * directly through getPoint() or getPoints()
	 */
	public void recomputeTextureCoordinates() {
		this.totalLength = 0;
		this.lengths.clear();
		if (this.points.size() > 1) {
			final Vector3f distance = new Vector3f();
			final Iterator<Vector3f> itPoint = this.points.iterator();
			Vector3f point = itPoint.next();
			while (itPoint.hasNext()) {
				final Vector3f nextPoint = itPoint.next();
				this.totalLength += nextPoint.subtract(point, distance).length();
				this.lengths.add(new FloatWrapper(this.totalLength));
				point = nextPoint;
			}
		}

		final VertexBuffer pvb = this.mesh.getBuffer(Type.TexCoord);
		final FloatBuffer texCoord = (FloatBuffer) pvb.getData();
		texCoord.put(0.0f);
		texCoord.put(0.0f);
		texCoord.put(0.0f);
		texCoord.put(1.0f);
		if (this.totalLength == 0) {
			for (final FloatWrapper l : this.lengths) {
				texCoord.put(0.0f);
				texCoord.put(0.0f);
				texCoord.put(0.0f);
				texCoord.put(1.0f);
			}
		} else {
			for (final FloatWrapper l : this.lengths) {
				final float length = l.getValue() / this.totalLength;
				texCoord.put(length);
				texCoord.put(0.0f);
				texCoord.put(length);
				texCoord.put(1.0f);
			}
		}

		texCoord.flip();
		this.mesh.setBuffer(Type.TexCoord, 2, texCoord);
	}

	/**
	 * Adds a point to the line.
	 * 
	 * @param point
	 *            Point you want to add.
	 * @param width
	 *            Width of the point.
	 */
	public void addPoint(final Vector3f point, final float width) {
		if (point == null) {
			throw new IllegalArgumentException("LineNodes.addPoint: point may not be null");
		}
		this.halfWidths.add(width / 2);
		this.points.add(point);

		// positions
		VertexBuffer pvb = this.mesh.getBuffer(VertexBuffer.Type.Position);
		final FloatBuffer positions = (FloatBuffer) pvb.getData();
		if (positions.limit() == positions.capacity()) { // need to create new buffer
			final FloatBuffer newBuffer = BufferUtils.createFloatBuffer(positions.capacity() * 2); // reasonable
																									// assumption: 2
																									// times the
																									// previous size
			newBuffer.limit(positions.limit() + 6); // two new vertices added, hence six new coordinates
			this.mesh.setBuffer(Type.Position, 3, newBuffer); // TODO: is this necessary???
		} else {
			positions.limit(positions.limit() + 6); // two new vertices added, hence six new coordinates
			pvb.updateData(positions); // TODO: is this necessary???
			// mesh.setBuffer(Type.Position, 3, positions); // TODO: shouldn't it work without this???
			this.mesh.updateCounts(); // TODO: is this necessary???
		}

		// indexes
		pvb = this.mesh.getBuffer(VertexBuffer.Type.Index);
		final IntBuffer indexes = (IntBuffer) pvb.getData();
		final int indexLimit = indexes.limit();
		if (indexLimit == indexes.capacity()) { // need to create new buffer
			final IntBuffer newBuffer = BufferUtils.createIntBuffer(indexes.capacity() * 2); // reasonable assumption: 2
																								// times the previous
																								// size
			newBuffer.limit(indexLimit + 2); // two new vertices added, hence two new indexes
			newBuffer.put(indexes); // copy old buffer into new buffer
			newBuffer.put(indexLimit); // add first new index
			newBuffer.put(indexLimit + 1); // add second new index
			newBuffer.flip();
			this.mesh.setBuffer(Type.Index, 3, newBuffer); // TODO: is this necessary
		} else {
			indexes.limit(indexLimit + 2); // two new vertices added
			indexes.rewind();
			indexes.put(indexLimit, indexLimit); // add first new index
			indexes.put(indexLimit + 1, indexLimit + 1); // add second new index
			indexes.rewind(); // is the position set when using absolute puts??? doing this for safety
			pvb.updateData(indexes); // TODO: is this necessary
			// mesh.setBuffer(Type.Index, 3, indexes); // TODO: shouldn't it work without this???
			this.mesh.updateCounts(); // TODO: is this necessary
		}

		// texcoord
		if (this.points.size() > 1) {
			this.totalLength += point.subtract(this.points.get(this.points.size() - 2)).length();
			this.lengths.add(new FloatWrapper(this.totalLength));
		}

		pvb = this.mesh.getBuffer(VertexBuffer.Type.TexCoord);
		final FloatBuffer texCoord = (FloatBuffer) pvb.getData();
		final int texCoordLimit = texCoord.limit();
		if (texCoordLimit == texCoord.capacity()) { // need to create new buffer
			final FloatBuffer newBuffer = BufferUtils.createFloatBuffer(texCoord.capacity() * 2); // reasonable
																									// assumption: 2
																									// times the
																									// previous size
			newBuffer.limit(texCoordLimit + 4); // two new vertices, hence 4 new texture coordinates
			newBuffer.put(0.0f);
			newBuffer.put(0.0f);
			newBuffer.put(0.0f);
			newBuffer.put(1.0f);

			if (this.totalLength == 0) {
				for (final FloatWrapper l : this.lengths) {
					newBuffer.put(0.0f);
					newBuffer.put(0.0f);
					newBuffer.put(0.0f);
					newBuffer.put(1.0f);
				}
			} else {
				for (final FloatWrapper l : this.lengths) {
					final float length = l.getValue() / this.totalLength;
					newBuffer.put(length);
					newBuffer.put(0.0f);
					newBuffer.put(length);
					newBuffer.put(1.0f);
				}
			}

			newBuffer.flip();
			this.mesh.setBuffer(Type.TexCoord, 2, newBuffer); // TODO: is this necessary ???
		} else {
			texCoord.limit(texCoordLimit + 4); // two new vertices, hence 4 new texture coordinates
			texCoord.rewind();
			texCoord.put(0.0f);
			texCoord.put(0.0f);
			texCoord.put(0.0f);
			texCoord.put(1.0f);

			if (this.totalLength == 0) {
				for (final FloatWrapper l : this.lengths) {
					texCoord.put(0.0f);
					texCoord.put(0.0f);
					texCoord.put(0.0f);
					texCoord.put(1.0f);
				}
			} else {
				for (final FloatWrapper l : this.lengths) {
					final float length = l.getValue() / this.totalLength;
					texCoord.put(length);
					texCoord.put(0.0f);
					texCoord.put(length);
					texCoord.put(1.0f);
				}
			}

			texCoord.flip();
			pvb.updateData(texCoord); // TODO: is this necessary ???
			// mesh.setBuffer(Type.TexCoord, 2, texCoord); // TODO: shouldn't it work without this???
			this.mesh.updateCounts(); // TODO: is this necessary ???
		}
		this.behaviour.addedPoint(this);
	}

	/**
	 * Returns the Length of this line.
	 * 
	 * @return length of this line.
	 */
	public float getTotalLength() {
		return this.totalLength;
	}

	/**
	 * LineBehaviours define the billboarding algorithm of the line.
	 */
	public interface LineBehaviour {
		/**
		 * Update is called when the line is rendered. The billboarding algorithm should be implemented here.
		 * 
		 * @param line
		 *            Line which is billboarded.
		 * @param rm
		 *            RenderManager
		 * @param vp
		 *            ViewPort
		 */
		public void update(LineControl line, RenderManager rm, ViewPort vp);

		/**
		 * Called whenever a point was added to the line. The newly added point has the biggest index in the line's
		 * points list. In this method precomputations are done to make update() faster.
		 * 
		 * @param line
		 *            Line which was altered
		 */
		public void addedPoint(LineControl line);

		/**
		 * Called whenever a point was removed from the line. In this method precomputations are done to make update()
		 * faster.
		 * 
		 * @param line
		 *            Line which was altered.
		 * @param index
		 *            Index at which the point WAS in the line's point list.
		 * @param oldPoint
		 *            point which was removed from the line.
		 */
		public void removedPoint(LineControl line, int index, Vector3f oldPoint);

		/**
		 * Called whenever set was called on the line. In this method precomputations are done to make update() faster.
		 */
		public void set();
	}

	/**
	 * Algo1CamDirBB implements a billboarding algorithm for lines. It billboards the line according to the camera's
	 * look direction. Otherwise it's the same as Algo1CamPosBB
	 */
	public static class Algo1CamDirBB implements LineBehaviour {

		@Override
		public void update(final LineControl line, final RenderManager rm, final ViewPort vp) {
			final Mesh mesh = ((Geometry) line.getSpatial()).getMesh();
			final VertexBuffer pvb = mesh.getBuffer(VertexBuffer.Type.Position);
			final List<Vector3f> points = line.getPoints();
			final List<Float> halfWidths = line.getHalfWidths();

			final FloatBuffer positions = (FloatBuffer) pvb.getData();
			positions.rewind();

			final Vector3f direction = rm.getCurrentCamera().getDirection();
			line.getSpatial().getWorldRotation().inverse().multLocal(direction);

			if (points.size() < 2) {
				return;
			}

			final Iterator<Vector3f> itPoint = points.iterator();
			final Iterator<Float> itHalfWidth = halfWidths.iterator();

			final Vector3f axisBC = new Vector3f();
			Vector3f point = itPoint.next();

			float halfWidth = itHalfWidth.next();
			Vector3f nextPoint = itPoint.next();
			nextPoint.subtract(point, axisBC);
			axisBC.normalizeLocal();

			final Vector3f axis = axisBC.cross(direction);
			axis.normalizeLocal();

			final Vector3f vertex = point.add(axis.multLocal(halfWidth));
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			point.subtract(axis, vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			while (itPoint.hasNext()) {
				point = nextPoint;
				nextPoint = itPoint.next();
				halfWidth = itHalfWidth.next();
				nextPoint.subtract(point, axis).normalizeLocal();
				axisBC.addLocal(axis).crossLocal(direction).normalizeLocal();
				point.add(axisBC.multLocal(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				point.subtract(axisBC, vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				axisBC.set(axis);

			}

			halfWidth = itHalfWidth.next();
			axisBC.cross(direction, axis);

			axis.normalizeLocal();
			nextPoint.add(axis.multLocal(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			nextPoint.subtract(axis, vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			// mesh.updateBound(); // TODO: check if this is necessary
			positions.flip();
			pvb.updateData(positions);
			line.getSpatial().updateModelBound();
		}

		@Override
		public void addedPoint(final LineControl line) {

		}

		@Override
		public void removedPoint(final LineControl line, final int index, final Vector3f oldPoint) {

		}

		@Override
		public void set() {

		}
	}

	/**
	 * Algo1CamPosBB implements a billboarding algorithm for lines. It billboards the line according to the cameras
	 * position relative to the points. Otherwise it's the same as Algo1CamDirBB
	 */
	public static class Algo1CamPosBB implements LineBehaviour {
		Vector3f camPos;

		public Algo1CamPosBB() {
			this.camPos = new Vector3f();
		}

		@Override
		public void update(final LineControl line, final RenderManager rm, final ViewPort vp) {
			final Mesh mesh = ((Geometry) line.getSpatial()).getMesh();
			final VertexBuffer pvb = mesh.getBuffer(VertexBuffer.Type.Position);
			final List<Vector3f> points = line.getPoints();
			final List<Float> halfWidths = line.getHalfWidths();

			final FloatBuffer positions = (FloatBuffer) pvb.getData();
			positions.rewind();

			if (points.size() < 2) {
				return;
			}

			this.camPos.set(rm.getCurrentCamera().getLocation());
			this.camPos.subtractLocal(line.getSpatial().getWorldTranslation());

			final Iterator<Vector3f> itPoint = points.iterator();
			final Iterator<Float> itHalfWidth = halfWidths.iterator();

			final Vector3f axisBC = new Vector3f();
			Vector3f point = itPoint.next();
			final Vector3f direction = point.subtract(this.camPos);

			float halfWidth = itHalfWidth.next();
			Vector3f nextPoint = itPoint.next();
			nextPoint.subtract(point, axisBC);
			axisBC.normalizeLocal();

			final Vector3f axis = axisBC.cross(direction);
			axis.normalizeLocal();

			final Vector3f vertex = point.add(axis.multLocal(halfWidth));
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			point.subtract(axis, vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			while (itPoint.hasNext()) {
				point = nextPoint;
				point.subtract(this.camPos, direction);
				nextPoint = itPoint.next();
				halfWidth = itHalfWidth.next();
				nextPoint.subtract(point, axis).normalizeLocal();
				axisBC.addLocal(axis).crossLocal(direction).normalizeLocal();
				point.add(axisBC.multLocal(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				point.subtract(axisBC, vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				axisBC.set(axis);
			}

			nextPoint.subtract(this.camPos, direction);

			halfWidth = itHalfWidth.next();
			axisBC.cross(direction, axis);

			axis.normalizeLocal();
			nextPoint.add(axis.multLocal(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			nextPoint.subtract(axis, vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			// mesh.updateBound(); // TODO: check if this is necessary
			positions.flip();
			pvb.updateData(positions);
			line.getSpatial().updateModelBound();
		}

		@Override
		public void addedPoint(final LineControl line) {

		}

		@Override
		public void removedPoint(final LineControl line, final int index, final Vector3f oldPoint) {

		}

		@Override
		public void set() {

		}
	}

	/**
	 * Algo2CamDirBB implements a billboarding algorithm for lines. It billboards the line according to the camera's
	 * look direction.
	 */
	public static class Algo2CamDirBB implements LineBehaviour {

		@Override
		public void update(final LineControl line, final RenderManager rm, final ViewPort vp) {
			final Mesh mesh = ((Geometry) line.getSpatial()).getMesh();
			final VertexBuffer pvb = mesh.getBuffer(VertexBuffer.Type.Position);
			final List<Vector3f> points = line.getPoints();
			final List<Float> halfWidths = line.getHalfWidths();

			if (points.size() < 2) {
				return;
			}

			final FloatBuffer positions = (FloatBuffer) pvb.getData();
			positions.rewind();

			final Iterator<Vector3f> itPoints = points.iterator();
			final Iterator<Float> itHalfWidths = halfWidths.iterator();

			final Vector3f axisBC = new Vector3f();
			Vector3f point = itPoints.next();
			Vector3f nextPoint = itPoints.next();

			final Vector3f direction = rm.getCurrentCamera().getDirection();
			line.getSpatial().getWorldRotation().inverse().multLocal(direction);

			float halfWidth = itHalfWidths.next();
			nextPoint.subtract(point, axisBC);

			final Vector3f axis = axisBC.cross(direction);
			axis.normalizeLocal();

			final Vector3f vertex = point.add(axis.mult(halfWidth));
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			point.subtract(axis.mult(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			final Vector3f axis2 = new Vector3f();
			while (itPoints.hasNext()) {
				point = nextPoint;
				nextPoint = itPoints.next();
				halfWidth = itHalfWidths.next();

				nextPoint.subtract(point, axisBC);

				axisBC.cross(direction, axis2);
				axis2.normalizeLocal();
				axis.addLocal(axis2);

				point.add(axis.mult(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				point.subtract(axis.multLocal(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				axis.set(axis2);
			}

			halfWidth = itHalfWidths.next();

			axis.normalizeLocal();
			nextPoint.add(axis.mult(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			nextPoint.subtract(axis.multLocal(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			// mesh.updateBound(); // TODO: check if this is necessary
			positions.flip();
			pvb.updateData(positions);

			line.getSpatial().updateModelBound();
		}

		@Override
		public void addedPoint(final LineControl line) {

		}

		@Override
		public void removedPoint(final LineControl line, final int index, final Vector3f oldPoint) {

		}

		@Override
		public void set() {

		}
	}

	/**
	 * Algo2CamDirBB implements a billboarding algorithm for lines. It billboards the line according to the camera's
	 * look direction. It normalizes the axis which is used to set the vertices at that point.
	 */
	public static class Algo2CamDirBBNormalized implements LineBehaviour {

		@Override
		public void update(final LineControl line, final RenderManager rm, final ViewPort vp) {
			final Mesh mesh = ((Geometry) line.getSpatial()).getMesh();
			final VertexBuffer pvb = mesh.getBuffer(VertexBuffer.Type.Position);
			final List<Vector3f> points = line.getPoints();
			final List<Float> halfWidths = line.getHalfWidths();

			if (points.size() < 2) {
				return;
			}

			final FloatBuffer positions = (FloatBuffer) pvb.getData();
			positions.rewind();

			final Iterator<Vector3f> itPoints = points.iterator();
			final Iterator<Float> itHalfWidths = halfWidths.iterator();

			final Vector3f axisBC = new Vector3f();
			Vector3f point = itPoints.next();
			Vector3f nextPoint = itPoints.next();

			final Vector3f direction = rm.getCurrentCamera().getDirection();
			line.getSpatial().getWorldRotation().inverse().multLocal(direction);

			float halfWidth = itHalfWidths.next();
			nextPoint.subtract(point, axisBC);

			final Vector3f axis = axisBC.cross(direction);
			axis.normalizeLocal();

			final Vector3f vertex = point.add(axis.mult(halfWidth));
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			point.subtract(axis.mult(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			final Vector3f axis2 = new Vector3f();
			while (itPoints.hasNext()) {
				point = nextPoint;
				nextPoint = itPoints.next();
				halfWidth = itHalfWidths.next();

				nextPoint.subtract(point, axisBC);

				axisBC.cross(direction, axis2);
				axis.addLocal(axis2);
				axis.normalizeLocal();

				point.add(axis.mult(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				point.subtract(axis.multLocal(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				axis.set(axis2);
			}

			halfWidth = itHalfWidths.next();

			axis.normalizeLocal();
			nextPoint.add(axis.mult(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			nextPoint.subtract(axis.multLocal(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			// mesh.updateBound(); // TODO: check if this is necessary
			positions.flip();
			pvb.updateData(positions);

			line.getSpatial().updateModelBound();
		}

		@Override
		public void addedPoint(final LineControl line) {

		}

		@Override
		public void removedPoint(final LineControl line, final int index, final Vector3f oldPoint) {

		}

		@Override
		public void set() {

		}
	}

	/**
	 * Algo2CamPosBBNormalized implements a billboarding algorithm for lines. It billboards the line according to the
	 * camera's position relative to the points. It normalizes the axis which is used to set the vertices at that point.
	 * Probably the closest to Unity3d's LineRenderer implementation
	 */
	public static class Algo2CamPosBBNormalized implements LineBehaviour {

		Vector3f camPos;

		public Algo2CamPosBBNormalized() {
			this.camPos = new Vector3f();
		}

		@Override
		public void update(final LineControl line, final RenderManager rm, final ViewPort vp) {
			final Mesh mesh = ((Geometry) line.getSpatial()).getMesh();
			final VertexBuffer pvb = mesh.getBuffer(VertexBuffer.Type.Position);
			final List<Vector3f> points = line.getPoints();
			final List<Float> halfWidths = line.getHalfWidths();

			if (points.size() < 2) {
				return;
			}

			this.camPos.set(rm.getCurrentCamera().getLocation());
			this.camPos.subtractLocal(line.getSpatial().getWorldTranslation());

			final FloatBuffer positions = (FloatBuffer) pvb.getData();
			positions.rewind();

			final Iterator<Vector3f> itPoints = points.iterator();
			final Iterator<Float> itHalfWidths = halfWidths.iterator();

			final Vector3f axisBC = new Vector3f();
			Vector3f point = itPoints.next();
			Vector3f nextPoint = itPoints.next();

			final Vector3f direction = point.subtract(this.camPos);

			float halfWidth = itHalfWidths.next();
			nextPoint.subtract(point, axisBC);

			final Vector3f axis = axisBC.cross(direction);
			axis.normalizeLocal();

			final Vector3f vertex = point.add(axis.mult(halfWidth));
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			point.subtract(axis.mult(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			final Vector3f axis2 = new Vector3f();
			while (itPoints.hasNext()) {
				point = nextPoint;
				nextPoint = itPoints.next();

				point.subtract(this.camPos, direction);
				direction.normalizeLocal(); // TODO: check if this is necessary

				halfWidth = itHalfWidths.next();

				nextPoint.subtract(point, axisBC);

				axisBC.cross(direction, axis2);
				axis.addLocal(axis2);
				axis.normalizeLocal();

				point.add(axis.mult(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				point.subtract(axis.multLocal(halfWidth), vertex);
				positions.put(vertex.x).put(vertex.y).put(vertex.z);
				axis.set(axis2);
			}

			halfWidth = itHalfWidths.next();

			axis.normalizeLocal();
			nextPoint.add(axis.mult(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);
			nextPoint.subtract(axis.multLocal(halfWidth), vertex);
			positions.put(vertex.x).put(vertex.y).put(vertex.z);

			// mesh.updateBound(); // TODO: check if this is necessary
			positions.flip();
			pvb.updateData(positions);

			line.getSpatial().updateModelBound();
		}

		@Override
		public void addedPoint(final LineControl line) {

		}

		@Override
		public void removedPoint(final LineControl line, final int index, final Vector3f oldPoint) {

		}

		@Override
		public void set() {

		}
	}
}

/*
 * ---------------- unoptimized version, loop could run in parallel ----------------- Vector3f axisBC = Vector3f.ZERO;
 * Vector3f point = points.get(0);
 * 
 * float halfWidth = halfWidths.get(0); points.get(1).subtract(point, axisBC);
 * 
 * Vector3f axis = axisBC.cross(direction); axis.normalizeLocal();
 * 
 * Vector3f vertex = point.add(axis.mult(halfWidth)); positions.put(vertex.x).put(vertex.y).put(vertex.z);
 * point.subtract(axis.mult(halfWidth), vertex); positions.put(vertex.x).put(vertex.y).put(vertex.z);
 * 
 * int lastPoint = this.points.size() - 1; Vector3f axis2 = Vector3f.ZERO; for (int i = 1; i < lastPoint; i++){
 * halfWidth = halfWidths.get(i); Vector3f axe1 = points.get(i).subtract(points.get(i-1)).normalize(); Vector3f axe2 =
 * points.get(i+1).subtract(points.get(i)).normalize(); Vector3f normal = axe1.add(axe2).cross(direction);
 * normal.normalizeLocal(); points.get(i).add(normal.mult(halfWidth), vertex);
 * positions.put(vertex.x).put(vertex.y).put(vertex.z); points.get(i).subtract(normal.multLocal(halfWidth), vertex);
 * positions.put(vertex.x).put(vertex.y).put(vertex.z); }
 * 
 * point = points.get(lastPoint); halfWidth = halfWidths.get(lastPoint);
 * 
 * point.subtract(points.get(lastPoint-1), axisBC);
 * 
 * axis = axisBC.cross(direction);
 * 
 * axis.normalizeLocal(); point.add(axis.mult(halfWidth), vertex); positions.put(vertex.x).put(vertex.y).put(vertex.z);
 * point.subtract(axis.multLocal(halfWidth), vertex); positions.put(vertex.x).put(vertex.y).put(vertex.z);
 * --------------------------------------------------------------------------------------
 */

/*
 * ------ unoptimized version of alternative algorithm, loop could run in parallel -------- Vector3f axisAB =
 * Vector3f.ZERO; Vector3f axisBC = Vector3f.ZERO; for (int i = 0; i < this.points.size(); i++) { float halfWidth =
 * halfWidths.get(i);
 * 
 * if (i > 0) points.get(i).subtract(points.get(i-1), axisAB); else axisAB = Vector3f.ZERO;
 * 
 * if (i + 1 < (int) points.size()) points.get(i+1).subtract(points.get(i), axisBC); else axisBC = Vector3f.ZERO;
 * 
 * Vector3f axis1 = axisAB.cross(direction); // consider normalizing axis1 Vector3f axis2 = axisBC.cross(direction); //
 * consider normalizing axis2 axis1.addLocal(axis2).normalize(); // consider not normalizing axis1 //Vector3f axis =
 * axis1.add(axis2); //axis.normalize();
 * 
 * //Vector3f vertex = points.get(i).add(axis.mult(halfWidth)); Vector3f vertex =
 * points.get(i).add(axis1.mult(halfWidth)); positions.put(vertex.x).put(vertex.y).put(vertex.z); //vertex =
 * points.get(i).subtract(axis.mult(halfWidth)); vertex = points.get(i).subtract(axis1.mult(halfWidth));
 * positions.put(vertex.x).put(vertex.y).put(vertex.z); //vertices[i*2] = (points.get(i).add(axis.mult(halfWidth)));
 * //vertices[i*2 + 1] = (points.get(i).subtract(axis.mult(halfWidth))); }
 * ---------------------------------------------------------------------------------------
 */