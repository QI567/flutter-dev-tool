package com.qi.flutterdevtool.utils;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class DartFileUtil {

    public void generateDartFile(Map<String, Object> data, String templateName, File destFile) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "");
        OutputStreamWriter writer;
        try {
            Template template = configuration.getTemplate(templateName);
            writer = new OutputStreamWriter(new FileOutputStream(destFile));
            template.process(data, writer);
            writer.flush();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public VirtualFile getSelectedFile(AnActionEvent e) {
        IdeView ideView = e.getData(LangDataKeys.IDE_VIEW);
        if (ideView == null) {
            return null;
        }
        PsiDirectory chooseDirectory = ideView.getOrChooseDirectory();
        if (chooseDirectory == null) {
            return null;
        }
        return chooseDirectory.getVirtualFile();
    }

    public String toHump(String name) {
        boolean underline = false;
        StringBuilder sb = new StringBuilder();
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        for (char c : chars) {
            if (c == '_') {
                underline = true;
            } else {
                if (underline) {
                    sb.append(Character.toUpperCase(c));
                    underline = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public String toUnderline(String name) {
        if (name.matches(".*[A-Z].*")) {
            name = name.replaceAll("([A-Z])", "_$1").toLowerCase();
            if (name.startsWith("_")) {
                name = name.substring(1);
            }
        }
        return name;
    }
}
