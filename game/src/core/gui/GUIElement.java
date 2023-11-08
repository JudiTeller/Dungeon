package core.gui;

import core.gui.backend.BackendImage;
import core.utils.math.VectorF;

import java.awt.*;

public abstract class GUIElement {

    protected VectorF position;
    protected VectorF size;
    protected GUIElement parent;
    protected LayoutHint layoutHint;
    protected Color backgroundColor;
    protected BackendImage backgroundImage;
    protected boolean valid = false;

    public GUIElement() {
        this.position = new VectorF(0.0f, 0.0f);
        this.size = new VectorF(0.0f, 0.0f);
    }

    public GUIElement(VectorF position, VectorF size) {
        this.position = position;
        this.size = size;
    }

    /**
     * Get a copy of the position vector
     *
     * @return Vector
     */
    public VectorF position() {
        return this.position.copy();
    }

    /**
     * Get a copy of the size vector
     *
     * @return Vector
     */
    public VectorF size() {
        return this.size.copy();
    }

    /**
     * Get the parent element
     *
     * @return GUIElement
     */
    public GUIElement parent() {
        return this.parent;
    }

    /**
     * Get the current layout hint
     *
     * @return LayoutHint
     */
    public LayoutHint layoutHint() {
        return this.layoutHint;
    }

    /**
     * Set the current layout hint
     *
     * @param hint LayoutHint
     */
    public void layoutHint(LayoutHint hint) {
        this.layoutHint = hint;
    }

    /**
     * This method should be called when something about the element changes (e.g. size change) that
     * requires a redraw.
     */
    public final void invalidate() {
        this.valid = false;
        if (this.parent != null) this.parent.invalidate();
    }

    /**
     * Packs the element to its preferred or minimum size
     *
     * <p>The default behavior is to do nothing.
     */
    public void pack() {}

    /**
     * This method is called when there is an GUIEvent for this element.
     *
     * @param event GUIEvent
     */
    public void event(GUIEvent event) {}
}