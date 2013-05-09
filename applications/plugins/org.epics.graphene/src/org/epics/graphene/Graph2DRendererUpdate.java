/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public class Graph2DRendererUpdate<T extends Graph2DRendererUpdate<T>> {
    
    private Integer imageHeight;
    private Integer imageWidth;
    private AxisRange xAxisRange;
    private AxisRange yAxisRange;
    private ValueScale xValueScale;
    private ValueScale yValueScale;
    
    protected T self() {
        return (T) this;
    }
    
    public T imageHeight(int height) {
        this.imageHeight = height;
        return self();
    }
    
    public T imageWidth(int width) {
        this.imageWidth = width;
        return self();
    }
    
    public T xAxisRange(AxisRange xAxisRange) {
        this.xAxisRange = xAxisRange;
        return self();
    }
    
    public T yAxisRange(AxisRange yAxisRange) {
        this.yAxisRange = yAxisRange;
        return self();
    }
    
    public T xValueScale(ValueScale xValueScale) {
        this.xValueScale = xValueScale;
        return self();
    }
    
    public T yValueScale(ValueScale yValueScale) {
        this.yValueScale = yValueScale;
        return self();
    }
    
    public Integer getImageHeight() {
        return imageHeight;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public AxisRange getXAxisRange() {
        return xAxisRange;
    }

    public AxisRange getYAxisRange() {
        return yAxisRange;
    }

    public ValueScale getXValueScale() {
        return xValueScale;
    }

    public ValueScale getYValueScale() {
        return yValueScale;
    }
    
    
}
