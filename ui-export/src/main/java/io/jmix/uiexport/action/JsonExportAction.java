package io.jmix.uiexport.action;

import io.jmix.core.BeanLocator;
import io.jmix.ui.action.ActionType;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.meta.StudioAction;
import io.jmix.uiexport.exporter.json.JsonExporter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Action for export table content as JSON
 * <p>
 * Should be defined for a list component ({@code Table}, {@code DataGrid}, etc.) in a screen XML descriptor.
 */
@StudioAction(category = "List Actions", description = "Export selected entities to JSON")
@ActionType(JsonExportAction.ID)
public class JsonExportAction extends ExportAction {

    public static final String ID = "jsonExport";

    public JsonExportAction(String id) {
        this(id, null);
    }

    public JsonExportAction() {
        this(ID);
    }

    public JsonExportAction(String id, String shortcut) {
        super(id, shortcut);
    }

    @Autowired
    @Override
    protected void setBeanLocator(BeanLocator beanLocator) {
        super.setBeanLocator(beanLocator);
        withExporter(JsonExporter.class);
    }

    @Override
    public String getIcon() {
        return JmixIcon.CODE.source();
    }
}