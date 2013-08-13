package com.siteview.css.topo.editparts;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

import com.siteview.css.topo.figure.DumbFigure;
import com.siteview.css.topo.models.DumbModel;

public class DumbEdit extends AbstractPVWidgetEditPart {

	DumbFigure dumbFigure;

	@Override
	protected IFigure doCreateFigure() {
		dumbFigure = new DumbFigure();
		dumbFigure.setMin(getTopoModel().getMin());
		dumbFigure.setMax(getTopoModel().getMax());
		return dumbFigure;
	}

	@Override
	public AbstractWidgetModel getWidgetModel() {
		return (DumbModel) getModel();
	}

	public DumbModel getTopoModel() {
		return (DumbModel) getModel();
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// the handle when pv value changed;
		IWidgetPropertyChangeHandler handlerValue = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if (newValue == null) {
					return false;
				}
				String newValueClassname = newValue.getClass().getName();
				// ((TopologyFigure)figure).setValue(ValueUtil.getDouble((IValue)newValue));
				dumbFigure
						.setLabelValue(ValueUtil.getString((IValue) newValue));
				dumbFigure.changeBackGroundColor(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE,
				handlerValue);
		// the handle whem max prperty value changed;
		IWidgetPropertyChangeHandler maxHandle = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((DumbFigure) figure).setMax(ValueUtil
						.getDouble((IValue) newValue));
				return false;
			}
		};
		setPropertyChangeHandler(DumbModel.PROP_MAX, maxHandle);

	}

	@Override
	public Border calculateBorder() {
		return super.calculateBorder();
	}

}
