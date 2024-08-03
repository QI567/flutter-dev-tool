package com.qi.flutterdevtool.action.launcher.dialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

public class FlutterProjectConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField applicationIdText;
    private JTextField bundleIdentifierText;
    private JButton iconImage;
    private JButton launchImage;
    private JTextField appNameText;
    private final int iconSize = 100;
    private final int launchWidth = 100;
    private final int launchHeight = 200;
    private File iconFile;
    private File launchImageFile;

    public FlutterProjectConfigDialog() {
        launchImage.setMinimumSize(new Dimension(launchWidth, launchHeight));
        iconImage.setMinimumSize(new Dimension(iconSize, iconSize));
        setContentPane(contentPane);
//        URL url = getClass().getClassLoader().getResource("icon.png");
//        if (url != null) {
//            ImageIcon imageIcon = new ImageIcon(url);
//            imageIcon.setImage(imageIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_FAST));
//            iconImage.setIcon(imageIcon);
//        }
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        iconImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pickIcon();
            }
        });
        launchImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pickLaunchImage();
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void pickLaunchImage() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new FileNameExtensionFilter("选择启动图", "png"));
        int res = jFileChooser.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            launchImageFile = jFileChooser.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(launchImageFile.getPath());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(launchWidth, launchHeight, Image.SCALE_FAST));
            launchImage.setIcon(imageIcon);
        }
    }

    private void pickIcon() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new FileNameExtensionFilter("选择图标", "png"));
        int res = jFileChooser.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            iconFile = jFileChooser.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(iconFile.getPath());
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_FAST));
            iconImage.setIcon(imageIcon);
        }
    }

    private void onOK() {
        // 保存icon到各个目录
        // android mipmap-mdpi
//        if (iconFile == null) {
//            Messages.showErrorDialog("请选择图标", "错误");
//            return;
//        }
//        if (launchImageFile == null) {
//            Messages.showErrorDialog("请选择启动图", "错误");
//            return;
//        }
//        if (StringUtils.isEmpty(appNameText.getText())) {
//            Messages.showErrorDialog("请输入App Name", "错误");
//            return;
//        }
//        if (StringUtils.isEmpty(applicationIdText.getText())) {
//            Messages.showErrorDialog("请输入Android Application Id", "错误");
//            return;
//        }
//        if (StringUtils.isEmpty(bundleIdentifierText.getText())) {
//            Messages.showErrorDialog("请输入Ios Bundle identifier", "错误");
//            return;
//        }

        dispose();
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public File getIconFile() {
        return iconFile;
    }

    public File getLaunchImageFile() {
        return launchImageFile;
    }

    public String getAppName() {
        return appNameText.getText();
    }

    public String getAppId() {
        return applicationIdText.getText();
    }

    public String getBundleIdentifier() {
        return  bundleIdentifierText.getText();
    }
}
