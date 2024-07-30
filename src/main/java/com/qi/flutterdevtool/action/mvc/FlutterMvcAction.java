package com.qi.flutterdevtool.action.mvc;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.qi.flutterdevtool.action.mvc.dialog.FlutterMvcDialog;
import com.qi.flutterdevtool.utils.DartFileUtil;

import java.io.File;
import java.util.HashMap;

public class FlutterMvcAction extends AnAction {
    DartFileUtil dartFileUtil = new DartFileUtil();

    @Override
    public void actionPerformed(AnActionEvent e) {
        FlutterMvcDialog flutterMvcDialog = new FlutterMvcDialog();
        flutterMvcDialog.pack();
        flutterMvcDialog.setLocationRelativeTo(null);
        flutterMvcDialog.setVisible(true);
        if (!flutterMvcDialog.isOk()) {
            return;
        }
        String name = flutterMvcDialog.getMvcName();
        boolean useGoRouter = flutterMvcDialog.getUseGoRouter();
        // 将驼峰转下划线
        name = dartFileUtil.toUnderline(name);
        String pageFileName = name + "_page.dart";
        String controllerFileName = name + "_controller.dart";
        // 将下划线转驼峰
        String humpName = dartFileUtil.toHump(name);
        String pageClassName = humpName + "Page";
        String controllerClassName = humpName + "Controller";
        VirtualFile selectedFile = dartFileUtil.getSelectedFile(e);
        File dir = new File(selectedFile.getPath(), name);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Messages.showErrorDialog("创建MVC路径失败", "错误");
                return;
            }
        }
        generatePage(pageClassName, controllerClassName, controllerFileName, useGoRouter, new File(dir, pageFileName));
        generateController(controllerClassName, new File(dir, controllerFileName));
    }

    private void generateController(String controllerClassName, File file) {
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("controllerClassName", controllerClassName);
                dartFileUtil.generateDartFile(map, "templates/controller.ftl", file);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    void generatePage(String className, String controllerClassName, String controllerFileFile, boolean userGoRouter, File destFile) {
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("pageClassName", className);
                map.put("controllerClassName", controllerClassName);
                map.put("controllerFile", controllerFileFile);
                map.put("useGoRouter", userGoRouter);
                dartFileUtil.generateDartFile(map, "templates/page.ftl", destFile);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }


}
