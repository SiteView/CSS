package org.csstudio.alarm.table.ui.messagetable;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class AlarmMessageTableMessageSorter extends ViewerComparator {

    private final TableViewer _tableViewer;

    public AlarmMessageTableMessageSorter(final TableViewer tableViewer) {
        super();
        _tableViewer = tableViewer;
    }

    @Override
    public int compare(final Viewer viewer, final Object o1, final Object o2) {

        AlarmMessage alarmMsg1;
        AlarmMessage alarmMsg2;

        if ((o1 != null) && (o2 != null)) {
            alarmMsg1 = (AlarmMessage) o2;
            alarmMsg2 = (AlarmMessage) o1;
        } else {
            return 0;
        }

        Integer severityNumberMsg1;
        Integer severityNumberMsg2;
        try {
            severityNumberMsg1 = new Integer(alarmMsg1.getSeverityNumber());
        } catch (final NumberFormatException e) {
            severityNumberMsg1 = new Integer(-1);
        }
        try {
            severityNumberMsg2 = new Integer(alarmMsg2.getSeverityNumber());
        } catch (final NumberFormatException e) {
            severityNumberMsg2 = new Integer(-1);
        }
        if (!severityNumberMsg1.equals(severityNumberMsg2)) {
            return (severityNumberMsg2 - severityNumberMsg1);
        }

        // false == 0, true == 1
        Integer outdatedMsg1 = 0;
        Integer outdatedMsg2 = 0;
        if (alarmMsg1.isOutdated()) {
            outdatedMsg1 = 1;
        }
        if (alarmMsg2.isOutdated()) {
            outdatedMsg2 = 1;
        }
        if (!outdatedMsg1.equals(outdatedMsg2)) {
            return (outdatedMsg2 - outdatedMsg1);
        }

        // false == 0, true == 1
        Integer acknowledgedMsg1 = 0;
        Integer acknowledgedMsg2 = 0;
        if (alarmMsg1.isAcknowledged()) {
            acknowledgedMsg1 = 1;
        }
        if (alarmMsg2.isAcknowledged()) {
            acknowledgedMsg2 = 1;
        }
        if (!acknowledgedMsg1.equals(acknowledgedMsg2)) {
            return (acknowledgedMsg2 - acknowledgedMsg1);
        }
        // FIXME (jhatje) : transform to Date and do before or after comparison
        return (super.compare(_tableViewer, alarmMsg1.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName()),
                alarmMsg2.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName())));
    }
}
