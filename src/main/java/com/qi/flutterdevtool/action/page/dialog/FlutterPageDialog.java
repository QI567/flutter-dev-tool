package com.qi.flutterdevtool.action.page.dialog;

import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class FlutterPageDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JRadioButton statefulRB;
    private JRadioButton statelessRB;
    private String mvcName;
    private boolean useGoRouter;
    private boolean isOk;

    public FlutterPageDialog() {
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
        mvcName = nameField.getText();
        useGoRouter = statefulRB.isSelected();
        if (StringUtils.isEmpty(mvcName)) {
            Messages.showErrorDialog("请输入MVC名称", "错误");
            return;
        }
        dispose();
    }
    private void onCancel() {
        isOk = false;
        dispose();
    }
    public String getMvcName() {
        return mvcName;
    }
    public boolean getUseGoRouter() {
        return useGoRouter;
    }
    public boolean isOk() {
        return isOk;
    }
    public static void main(String[] args) {
        FlutterPageDialog dialog = new FlutterPageDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
