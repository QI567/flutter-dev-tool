package com.qi.flutterdevtool.action.widget;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.qi.flutterdevtool.action.widget.dialog.FlutterWidgetDialog;
import com.qi.flutterdevtool.utils.DartFileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

public class FlutterWidgetAction extends AnAction {

    DartFileUtil dartFileUtil = new DartFileUtil();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FlutterWidgetDialog flutterWidgetDialog = new FlutterWidgetDialog();
        flutterWidgetDialog.pack();
        flutterWidgetDialog.setLocationRelativeTo(null);
        flutterWidgetDialog.setVisible(true);
        VirtualFile selectedFile = dartFileUtil.getSelectedFile(e);
        String name = flutterWidgetDialog.getName();
        String fileName = dartFileUtil.toUnderline(name);
        String classNae = dartFileUtil.toHump(name);
        File file = new File(selectedFile.getPath(), fileName + ".dart");
        generateFile(classNae, file, flutterWidgetDialog.isStateful());
    }


    void generateFile(String className, File destFile, boolean stateful) {
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("className", className);
                dartFileUtil.generateDartFile(map, stateful ? "templates/statefulWidget.ftl" : "templates/statelessWidget.ftl", destFile);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }
}
