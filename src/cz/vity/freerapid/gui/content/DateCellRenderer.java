package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

/**
 * @author VitasekL
 */

class DateCellRenderer extends DefaultTableCellRenderer {

    private final String dateFormat;

    DateCellRenderer() {
        dateFormat = AppPrefs.getProperty(UserProp.CONTENT_TABLE_DATE_FORMAT, "%1$tB %1$te");
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        } else {
            final DownloadFile downloadFile = (DownloadFile) value;
            final Date dateInserted = downloadFile.getDateInserted();
            if (dateInserted == null) {
                value = "";
                setToolTipText(null);
            } else {
                value = millisToString(dateInserted);
                final long time = dateInserted.getTime();
                setToolTipText(String.format(dateFormat + " %tH:%tM", time, time));
            }
        }
        this.setHorizontalAlignment(CENTER);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    private String millisToString(Date value) {
//        final Calendar valueDate = Calendar.getInstance();
//        valueDate.setTimeInMillis(value);
//        final Calendar today = Calendar.getInstance();
//        today.set(Calendar.HOUR_OF_DAY, 0);
//        today.set(Calendar.MINUTE, 0);
//        today.set(Calendar.SECOND, 1);
//        //long todayStart = today.getTimeInMillis();
//        if (valueDate.after(today)) {
//            return String.format("%tH:%tM", value, value);
//        }
//        today.add(Calendar.DATE, -1);
//        //  System.out.printf("today = %1$tm %1$te,%1$tY %1$tH:%1$tM", today);
//        if (valueDate.after(today)) {
//            return yesterday;
//        }
//        today.add(Calendar.DATE, -6);
//        if (valueDate.after(today)) {
//            return String.format("%tA", value);
//        }
//        //jinak
        return String.format(dateFormat, value.getTime());
    }
}