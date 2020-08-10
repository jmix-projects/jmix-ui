package io.jmix.uiexport.action;

import io.jmix.ui.action.ActionType;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.meta.StudioAction;
import io.jmix.uiexport.exporter.excel.ExcelExporter;

@StudioAction(category = "List Actions", description = "Export selected entities to Excel")
@ActionType(ExcelExportAction.ID)
public class ExcelExportAction extends ExportAction {

    public static final String ID = "excelExport";

    public ExcelExportAction(String id) {
        this(id,null);
    }

    public ExcelExportAction() {
        this(ID);
    }

    public ExcelExportAction(String id, String shortcut) {
        super(id, shortcut);
        withExporter(ExcelExporter.class);
    }

    @Override
    public String getIcon() {
        return JmixIcon.EXCEL_ACTION.source();
    }
}