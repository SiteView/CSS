
package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.dnd.SelectionDragSourceListener;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

public class AlarmbearbeitergruppenView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.alarmbearbeitergruppen"; //$NON-NLS-1$

	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return AlarmbearbeiterGruppenBean.class;
	}

	@Override
	protected IConfigurationBean[] getTableContent() {
		return AbstractNamsView.getConfigurationBeanService()
				.getAlarmBearbeiterGruppenBeans();
	}

	@Override
	protected void initDragAndDrop(final FilterableBeanList filterableBeanList) {
		filterableBeanList.getTable().addDragSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new SelectionDragSourceListener(filterableBeanList.getTable()));
	}
}
