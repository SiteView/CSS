package org.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

public class MyWizard extends Wizard {

    public MyWizard() {
        this.setDialogSettings(new DialogSettings("导入工程"));
    }

    @Override
    public void addPages() {
        this.addPage(new MyWizardPage1());
        this.addPage(new MyWizardPage2());
    }

    /**
     * 获取向导里面的数据
     */
    public boolean performFinish() {
        IDialogSettings dialogSettings2 = this.getDialogSettings();
        final String username = dialogSettings2.get("用户名");
        String password = dialogSettings2.get("密  码");
        String city = dialogSettings2.get("城  市");
        String address = dialogSettings2.get("地  址");
        System.out.println("用户名"+username+"密码"+password);
        
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException {
                try {
                    MessageDialog.openConfirm(MyWizard.this.getShell(),
                            "请确认以下信息", "username=" + username);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            getContainer().run(true, true, new LongRunningOperation(false));
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException
                    .getMessage());
            return false;
        }
        return true;
    }
}


class LongRunningOperation implements IRunnableWithProgress {
    // The total sleep time
    private static final int TOTAL_TIME = 10000;

    // The increment sleep time
    private static final int INCREMENT = 500;

    private boolean indeterminate;

   
    public LongRunningOperation(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

   
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        monitor.beginTask("Running long running operation",
                indeterminate ? IProgressMonitor.UNKNOWN : TOTAL_TIME);
        // monitor.subTask("Doing first half");
        for (int total = 0; total < TOTAL_TIME && !monitor.isCanceled(); total += INCREMENT) {
            Thread.sleep(INCREMENT);
            monitor.worked(INCREMENT);
            if (total == TOTAL_TIME / 2)
                monitor.subTask("Doing second half");
        }
        monitor.done();
        if (monitor.isCanceled())
            throw new InterruptedException(
                    "The long running operation was cancelled");
    }
}