package org.csstudio.alarm.table.database;

import java.io.File;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.dbaccess.archivedb.Result;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class AsynchronousDatabaseAccess {

    public void readMessages(final IDatabaseAccessListener listener,
            final Filter filter) {
        final Filter newFilter = filter.copy();
        Job readJob = new Job("Reader") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                getArchiveAccess().getLogMessages(newFilter, result);
                listener.onReadFinished(result);
                return Status.OK_STATUS;
            }
        };
        readJob.schedule();
    }

    public void exportMessagesInFile(final IDatabaseAccessListener listener,
            final Filter filter, final File filePath, final String[] columnNames) {
        final Filter newFilter = filter.copy();
        Job exportJob = new Job("Export") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                getArchiveAccess().exportLogMessages(newFilter, result, filePath, columnNames);
                listener.onExportFinished(result);
                return Status.OK_STATUS;
            }
        };
        exportJob.schedule();
    }

    public void countMessages(final IDatabaseAccessListener listener,
            final Filter filter) {
        final Filter newFilter = filter.copy();
        Job countJob = new Job("CountMessages") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                getArchiveAccess().countDeleteLogMessages(newFilter, result);
                listener.onMessageCountFinished(result);
                return Status.OK_STATUS;
            }
        };
        countJob.schedule();
    }

    public void deleteMessages(final IDatabaseAccessListener listener,
            final Filter filter) {
        final Filter newFilter = filter.copy();
        Job deleteJob = new Job("DeleteMessages") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                result.setAccessResult(getArchiveAccess().deleteLogMessages(newFilter));
                listener.onDeletionFinished(result);
                return Status.OK_STATUS;
            }
        };
        deleteJob.schedule();
    }

    protected ILogMessageArchiveAccess getArchiveAccess() {
        return ServiceLocator.getService(ILogMessageArchiveAccess.class);
    }

}
