package com.qi.flutterdevtool.action.widget.dialog;

import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FlutterWidgetDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JRadioButton statefulRB;
    private JRadioButton statelessRB;
    private String name;
    private boolean isStateful;
    private boolean isOk;

    public FlutterWidgetDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        isOk = true;
        name = nameField.getText();
        isStateful = statefulRB.isSelected();
        if (StringUtils.isEmpty(name)) {
            Messages.showErrorDialog("请输入组件名称", "错误");
            return;
        }
        dispose();
    }
    private void onCancel() {
        isOk = false;
        dispose();
    }
    public String getName() {
        return name;
    }
    public boolean isStateful() {
        return isStateful;
    }
    public boolean isOk() {
        return isOk;
    }
    public static void main(String[] args) {
        FlutterWidgetDialog dialog = new FlutterWidgetDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
