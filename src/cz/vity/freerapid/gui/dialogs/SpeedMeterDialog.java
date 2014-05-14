package cz.vity.freerapid.gui.dialogs;

import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.sandbox.SpeedMonitor;
import cz.vity.freerapid.utilities.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class SpeedMeterDialog extends AppFrame implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(SpeedMeterDialog.class.getName());
    private final ManagerDirector director;
    private SpeedMonitor comp;
    private TimerTask task;


    public SpeedMeterDialog(Frame owner, ManagerDirector director) throws HeadlessException {
        super(owner);
        this.director = director;

        this.setName("SpeedMeterDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
        }

    }

    @org.jdesktop.application.Action
    public void cancelBtnAction() {
        if (task != null) {
            logger.info("Stopping update timer");
            task.cancel();
        }
        doClose();
    }

    @Override
    public void doClose() {

        super.doClose();
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return cancelButton;
    }

    private void build() {
        this.cancelButton = new JButton();
        setAction(cancelButton, "cancelBtnAction");
        //   director.getDataManager().addPropertyChangeListener("averageSpeed", this);
        java.util.Timer timer = new java.util.Timer();
        task = new TimerTask() {
            public void run() {
                //   comp.setCurrentSpeed(director.getDataManager().getAverageSpeed());
            }
        };
        timer.schedule(task, 300, 1000);
    }

    private void initComponents() {
        comp = new SpeedMonitor();
        this.getContentPane().add(comp);
        this.pack();
    }

    private JButton cancelButton;

    public void propertyChange(PropertyChangeEvent evt) {
        comp.setCurrentSpeed((Float) evt.getNewValue());
    }
}
