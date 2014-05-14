package org.jdesktop.appframework.swingx;

import com.jgoodies.common.collect.ArrayListModel;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.session.PropertySupport;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.beans.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Container class for SwingX specific SessionStorage Properties. Is Factory for custom PersistanceDelegates
 *
 * @author Kleopatra & Vity
 */
public class XProperties {

    /**
     * Registers all custom PersistenceDelegates needed by contained Property classes. <p>
     * <p/>
     * PersistenceDelegates are effectively static properties shared by all encoders. In other words: Register once on
     * an arbitrary encoder makes them available for all. Example usage:
     * <p/>
     * <pre><code>
     * new XProperties.registerPersistenceDelegates();
     * </code></pre>
     * @param context
     */
    public void registerPersistenceDelegates(ApplicationContext context) {
        final Map<Class<?>, PersistenceDelegate> persistentDelegates = context.getLocalStorage().getPersistentDelegates();
        persistentDelegates.put(SortKeyState.class, new DefaultPersistenceDelegate(new String[]{"sortOrder", "modelIndex"}));
        persistentDelegates.put(ColumnState.class, new DefaultPersistenceDelegate(new String[]{"width", "preferredWidth", "modelIndex", "visible", "viewIndex"}));
        persistentDelegates.put(XProperties.XTableState.class, new DefaultPersistenceDelegate(new String[]{"sortOrder", "modelIndex"}));
        persistentDelegates.put(XTableState.class, new DefaultPersistenceDelegate(new String[]{"columnStates", "sortKeyState", "horizontalScrollEnabled"}));
        persistentDelegates.put(ArrayListModel.class, new XMLEncoder(System.out).getPersistenceDelegate(List.class));
//PersistenceDelegate for URL class ~ This tells XMLEncoder how to deal with these objects
        final PrimitivePersistenceDelegate primitivePersistenceDelegate = new PrimitivePersistenceDelegate();
        persistentDelegates.put(URL.class, new PrimitivePersistenceDelegate());
        persistentDelegates.put(File.class, primitivePersistenceDelegate);

    }
//    static class FilePersistenceDelegate extends PersistenceDelegate {
//        protected Expression instantiate(Object oldInstance, Encoder out) {
//            File f = (File) oldInstance;
//            return new Expression(oldInstance, f.getClass(), "new", new Object[]{f.toString()});
//        }
//    }


    class PrimitivePersistenceDelegate extends PersistenceDelegate {

        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, oldInstance.getClass(),
                    "new", new Object[]{oldInstance.toString()});
        }
    }

    public static class XTableProperty implements PropertySupport {

        public Object getSessionState(Component c) {
            checkComponent(c);
            JXTable table = (JXTable) c;
            List<ColumnState> columnStates = new ArrayList<ColumnState>();
            List<TableColumn> columns = table.getColumns(true);
            List<TableColumn> visibleColumns = table.getColumns();
            for (TableColumn column : columns) {
                columnStates.add(new ColumnState((TableColumnExt) column,
                        visibleColumns.indexOf(column)));
            }
            XTableState tableState = new XTableState(columnStates.toArray(new ColumnState[columnStates.size()]));
            tableState.setHorizontalScrollEnabled(table.isHorizontalScrollEnabled());
            final List<? extends RowSorter.SortKey> keys = table.getRowSorter().getSortKeys();
            RowSorter.SortKey sortKey = null;
            for (RowSorter.SortKey key : keys) {
                if (!SortOrder.UNSORTED.equals(key.getSortOrder())) {
                    sortKey = key;
                    break;
                }
            }
            if (sortKey != null) {
                tableState.setSortKey(sortKey);
            }
            return tableState;
        }

        public void setSessionState(Component c, Object state) {
            checkComponent(c);
            JXTable table = (JXTable) c;
            XTableState tableState = ((XTableState) state);
            ColumnState[] columnState = tableState.getColumnStates();
            List<TableColumn> columns = table.getColumns(true);
            if (canRestore(columnState, columns)) {
                for (int i = 0; i < columnState.length; i++) {
                    columnState[i].configureColumn((TableColumnExt) columns
                            .get(i));
                }
                restoreVisibleSequence(columnState, table.getColumnModel());
            }
            table.setHorizontalScrollEnabled(tableState
                    .getHorizontalScrollEnabled());
            if (tableState.getSortKey() != null) {
                table.getRowSorter().setSortKeys(
                        Collections.singletonList(tableState.getSortKey()));
            }
        }

        private void restoreVisibleSequence(ColumnState[] columnStates, TableColumnModel model) {
            List<ColumnState> visibleStates = getSortedVisibleColumnStates(columnStates);
            for (int i = 0; i < visibleStates.size(); i++) {
                TableColumn column = model.getColumn(i);
                int modelIndex = visibleStates.get(i).getModelIndex();
                if (modelIndex != column.getModelIndex()) {
                    int currentIndex = -1;
                    for (int j = i + 1; j < model.getColumnCount(); j++) {
                        TableColumn current = model.getColumn(j);
                        if (current.getModelIndex() == modelIndex) {
                            currentIndex = j;
                            break;
                        }
                    }
                    model.moveColumn(currentIndex, i);
                }
            }

        }

        private List<ColumnState> getSortedVisibleColumnStates(ColumnState[] columnStates) {
            List<ColumnState> visibleStates = new ArrayList<ColumnState>();
            for (ColumnState columnState : columnStates) {
                if (columnState.getVisible()) {
                    visibleStates.add(columnState);
                }
            }
            Collections.sort(visibleStates, new VisibleColumnIndexComparator());
            return visibleStates;
        }

        /**
         * Returns a boolean to indicate if it's reasonably safe to restore the properties of columns in the list from
         * the columnStates. Here: returns true if the length of both are the same and the modelIndex of the items at
         * the same position are the same, otherwise returns false.
         *
         * @param columnState
         * @param columns
         * @return
         */
        private boolean canRestore(ColumnState[] columnState, List<TableColumn> columns) {
            if ((columnState == null) || (columnState.length > columns.size())) return false;
            for (int i = 0; i < columnState.length; i++) {
                if (columnState[i].getModelIndex() != columns.get(i).getModelIndex()) {
                    return false;
                }
            }
            return true;
        }

        private void checkComponent(Component component) {
            if (component == null) {
                throw new IllegalArgumentException("null component");
            }
            if (!(component instanceof JXTable)) {
                throw new IllegalArgumentException("invalid component - expected JXTable");
            }
        }

    }

    public static class XTableState {
        ColumnState[] columnStates = new ColumnState[0];
        boolean horizontalScrollEnabled;
        SortKeyState sortKeyState;

        public XTableState() {
        }

        public XTableState(ColumnState[] columnStates, SortKeyState sortKeyState, boolean horizontalScrollEnabled) {
            this.columnStates = copyColumnStates(columnStates);
            this.sortKeyState = sortKeyState;
            setHorizontalScrollEnabled(horizontalScrollEnabled);

        }

        public void setSortKey(RowSorter.SortKey sortKey) {
            this.sortKeyState = new SortKeyState(sortKey);

        }

        private RowSorter.SortKey getSortKey() {
            if (sortKeyState != null) {
                return sortKeyState.getSortKey();
            }
            return null;
        }

        public XTableState(ColumnState[] columnStates) {
            this.columnStates = copyColumnStates(columnStates);
        }

        public ColumnState[] getColumnStates() {
            return copyColumnStates(this.columnStates);
        }

        public boolean getHorizontalScrollEnabled() {
            return horizontalScrollEnabled;
        }

        public void setHorizontalScrollEnabled(boolean horizontalScrollEnabled) {
            this.horizontalScrollEnabled = horizontalScrollEnabled;
        }

        private ColumnState[] copyColumnStates(ColumnState[] states) {
            if (states == null) {
                throw new IllegalArgumentException("invalid columnWidths");
            }
            ColumnState[] copy = new ColumnState[states.length];
            System.arraycopy(states, 0, copy, 0, states.length);
            return copy;
        }

        public SortKeyState getSortKeyState() {
            return sortKeyState;
        }
    }

    /**
     * Quick hack to make SortKey encodable. How to write a PersistenceDelegate for a SortKey? Boils down to how to
     * write a delegate for the uninstantiable class (SwingX) SortOrder which does enum-mimickry (defines privately
     * intantiated constants)
     */
    public static class SortKeyState {
        int modelIndex;

        // Comparator comparator;

        int sortOrder;

        /**
         * Constructor used by the custom PersistenceDelegate.
         *
         * @param sortOrder
         * @param modelIndex
         * @param comparator
         */
        public SortKeyState(int sortOrder, int modelIndex) {
            this.sortOrder = sortOrder;
            this.modelIndex = modelIndex;
        }

        /**
         * Constructor used by property.
         *
         * @param sortKey
         */
        public SortKeyState(RowSorter.SortKey sortKey) {
            this(sortKey.getSortOrder().ordinal(), sortKey.getColumn());
        }

        protected RowSorter.SortKey getSortKey() {
            return new RowSorter.SortKey(getModelIndex(), SortOrder.values()[getSortOrder()]);
        }

        public int getSortOrder() {
            return sortOrder;
        }

        public int getModelIndex() {
            return modelIndex;
        }
    }

    public static class ColumnState {
        private int width;
        private int preferredWidth;
        private int modelIndex;
        private boolean visible;
        private int viewIndex;

        /**
         * Constructor used by the custom PersistenceDelegate.
         *
         * @param width
         * @param preferredWidth
         * @param modelColumn
         * @param visible
         * @param viewIndex
         */
        public ColumnState(int width, int preferredWidth, int modelColumn, boolean visible, int viewIndex) {
            this.width = width;
            this.preferredWidth = preferredWidth;
            this.modelIndex = modelColumn;
            this.visible = visible;
            this.viewIndex = viewIndex;
        }

        /**
         * Constructor used by the Property.
         *
         * @param columnExt
         * @param viewIndex
         */
        public ColumnState(TableColumnExt columnExt, int viewIndex) {
            this(columnExt.getWidth(), columnExt.getPreferredWidth(),
                    columnExt.getModelIndex(), columnExt.isVisible(), viewIndex);
        }

        /**
         * Restores column properties if the model index is the same as the column's model index. Does nothing
         * otherwise. <p>
         * <p/>
         * Here the properties are: width, preferredWidth, visible.
         *
         * @param columnExt the column to configure
         */
        public void configureColumn(TableColumnExt columnExt) {
            if (modelIndex != columnExt.getModelIndex()) return;
            columnExt.setPreferredWidth(preferredWidth);
            columnExt.setWidth(width);
            columnExt.setVisible(visible);
        }

        public int getModelIndex() {
            return modelIndex;
        }

        public int getViewIndex() {
            return viewIndex;
        }

        public boolean getVisible() {
            return visible;
        }

        public int getWidth() {
            return width;
        }

        public int getPreferredWidth() {
            return preferredWidth;
        }

    }

    public static class VisibleColumnIndexComparator implements Comparator<ColumnState> {

        public int compare(ColumnState o1, ColumnState o2) {
            return o1.getViewIndex() - o2.getViewIndex();
        }

    }
}