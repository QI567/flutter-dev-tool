package com.qi.flutterdevtool.action.launcher;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.ImageUtil;
import com.qi.flutterdevtool.action.launcher.dialog.FlutterProjectConfigDialog;
import com.qi.flutterdevtool.utils.FlutterProjectConstant;
import com.qi.flutterdevtool.utils.QiFileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlutterProjectConfigAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        String projectPath = project.getBasePath();
        FlutterProjectConfigDialog flutterProjectConfigDialog = new FlutterProjectConfigDialog();
        flutterProjectConfigDialog.pack();
        flutterProjectConfigDialog.setLocationRelativeTo(null);
        flutterProjectConfigDialog.setVisible(true);
        // 保存图标
        if (flutterProjectConfigDialog.getIconFile() != null) {
            saveIcons(flutterProjectConfigDialog.getIconFile(), projectPath);
        }
        // 保存启动图
        if (flutterProjectConfigDialog.getLaunchImageFile() != null) {
            saveIosLaunchImage(projectPath, flutterProjectConfigDialog.getLaunchImageFile());
            saveAndroidLaunchImages(projectPath, flutterProjectConfigDialog.getLaunchImageFile());
        }
        // 保存app名称
        if (!StringUtils.isEmpty(flutterProjectConfigDialog.getAppName())) {
            modifyAppName(projectPath, flutterProjectConfigDialog.getAppName());
        }
        // 保存appId
        if (!StringUtils.isEmpty(flutterProjectConfigDialog.getAppId())) {
            String oldAppId = modifyAndroidAppId(projectPath, flutterProjectConfigDialog.getAppId());
            // 将旧包下的类，移动到appId目录下
            String appId = flutterProjectConfigDialog.getAppId();
            File packageFile = moveOldPackageToNewPackage(projectPath, appId, oldAppId);
            // 修改所有类文件的package
            modifyPackageOfJava(packageFile, oldAppId, appId);
        }
        // 保存bundleId
        if (!StringUtils.isEmpty(flutterProjectConfigDialog.getBundleIdentifier())) {
            modifyBundleIdentifier(projectPath, flutterProjectConfigDialog.getBundleIdentifier());
        }
    }

    private void modifyPackageOfJava(File packageFile, String oldAppId, String appId) {
        if (packageFile == null)
            return;
        Iterator<File> fileIterator = FileUtils.iterateFiles(packageFile, new String[]{"java", "kt"}, true);
        while (fileIterator.hasNext()) {
            File file = fileIterator.next();
            StringBuilder sb = getJavaFileString(oldAppId, appId, file);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(sb.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static @NotNull StringBuilder getJavaFileString(String oldAppId, String appId, File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("package " + oldAppId)) {
                    line = line.replace(oldAppId, appId);
                }
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return sb;
    }

    private File moveOldPackageToNewPackage(String projectPath, String appId, String oldAppId) {
        File kotlinFile = new File(projectPath, FlutterProjectConstant.ANDROID_APP_MAIN_DIR + "kotlin");
        File javaFile = new File(projectPath, FlutterProjectConstant.ANDROID_APP_MAIN_DIR + "java");
        String oldPackageSubPath = oldAppId.replaceAll("\\.", File.separator);
        File oldPackageDir = new File(kotlinFile, oldPackageSubPath);
        if (!oldPackageDir.exists()) {
            oldPackageDir = new File(javaFile, oldPackageSubPath);
            if (!oldPackageDir.exists()) {
                // 寻找MainActivity
                File activityFile = QiFileUtil.findFile(kotlinFile, ".*Activity\\.kt");
                if (activityFile == null) {
                    activityFile = QiFileUtil.findFile(javaFile, ".*Activity\\.java");
                    if (activityFile == null) {
                        System.err.println("未发现Android Java/kotlin 目录");
                        return null;
                    }
                }
                oldPackageDir = activityFile.getParentFile();
            }
        }
        File sourceDir;
        if (oldPackageDir.getPath().contains(FlutterProjectConstant.ANDROID_APP_MAIN_DIR + "kotlin")) {
            sourceDir = kotlinFile;
        } else {
            sourceDir = javaFile;
        }
        File packagePath = new File(sourceDir, appId.replaceAll("\\.", File.separator));
        try {
            if (packagePath.exists()) {
                FileUtils.deleteDirectory(packagePath);
            }
            FileUtils.moveDirectory(oldPackageDir, packagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return packagePath;
    }

    private void saveAndroidLaunchImages(String projectPath, File launchImageFile) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(launchImageFile);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            saveImage(bufferedImage, new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_XXHDPI_DIR), "launch_image.png"); // 3倍图
            saveImage(bufferedImage, (int) (width / 3.0 * 2), (int) (height / 3.0 * 2), new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_XHDPI_DIR), "launch_image.png");
            saveImage(bufferedImage, (int) (width / 3.0), (int) (height / 3.0), new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_MDPI_DIR), "launch_image.png");
            modifyAndroidLaunchBackgrounds(projectPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void saveIcons(File iconFile, String projectPath) {
        try {
            BufferedImage bufferedImage = ImageIO.read(iconFile);
            saveImage(bufferedImage, 48, 48, new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_MDPI_DIR), "ic_launcher.png");
            saveImage(bufferedImage, 72, 72, new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_HDPI_DIR), "ic_launcher.png");
            saveImage(bufferedImage, 96, 96, new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_XHDPI_DIR), "ic_launcher.png");
            saveImage(bufferedImage, 144, 144, new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_XXHDPI_DIR), "ic_launcher.png");
            saveImage(bufferedImage, 192, 192, new File(projectPath, FlutterProjectConstant.ANDROID_MIPMAP_XXXHDPI_DIR), "ic_launcher.png");
            saveIosIcon(projectPath, bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveIosIcon(String projectPath, BufferedImage bufferedImage) throws IOException {
        File file = new File(projectPath, FlutterProjectConstant.IOS_ASSET_APP_ICON_DIR);
        if (file.exists()) {
            if (file.delete()) {
                boolean res = file.mkdirs();
                if (res) {
                    System.out.println("创建ios icon 目录失败");
                }
            }
        }
        // 保存Contents
        saveContents("ios/AppIconContents.json", new File(file, "Contents.json"));
        saveImage(bufferedImage, file, "icon.png");
    }

    private void saveIosLaunchImage(String projectPath, File launchImageFile) {
        // 保存LaunchScreen
        try {
            saveLaunchScreen(projectPath);
            File file = new File(projectPath, FlutterProjectConstant.IOS_ASSET_LAUNCH_IMAGE_DIR);
            if (file.exists() && file.delete() && file.mkdirs()) {
                System.out.println("创建ios LaunchImage 目录成功");
            } else {
                System.out.println("创建ios LaunchImage 目录失败");
            }
            // 保存Contents
            saveContents("ios/LaunchImageContents.json", new File(file, "Contents.json"));
            BufferedImage bufferedImage = ImageIO.read(launchImageFile);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            saveImage(bufferedImage, width / 3, height / 3, file, "LaunchImage@1x.png");
            saveImage(bufferedImage, (int) (width / 3.0 * 2), (int) (height / 3.0 * 2), file, "LaunchImage@2x.png");
            ImageIO.write(bufferedImage, "png", new File(file, "LaunchImage@3x.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveLaunchScreen(String projectPath) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("ios/LaunchScreen.storyboard");
        if (inputStream == null) return;
        FileUtils.copyInputStreamToFile(inputStream, new File(projectPath, FlutterProjectConstant.IOS_LAUNCH_SCREEN_STORYBOARD_PATH));
    }

    private void saveContents(String resourcePath, File output) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) return;
        FileUtils.copyInputStreamToFile(inputStream, output);
    }

    private void saveImage(BufferedImage bufferedImage, int width, int height, File outputDir, String imageName) throws IOException {
        Image image = ImageUtil.scaleImage(bufferedImage, width, height);
        saveImage((BufferedImage) image, outputDir, imageName);
    }

    private void saveImage(BufferedImage bufferedImage, File outDir, String name) throws IOException {
        if (!outDir.exists() && !outDir.mkdirs()) {
            System.out.println("创建" + outDir.getPath() + "失败");
            return;
        }
        ImageIO.write(bufferedImage, "png", new File(outDir, name));
    }

    private String modifyAndroidAppId(String projectPath, String appId) {
        String oldAppId = null;
        File file = new File(projectPath, FlutterProjectConstant.ANDROID_APP_DIR + "build.gradle");
        if (!file.exists()) {
            file = new File(projectPath, FlutterProjectConstant.ANDROID_APP_DIR + "build.gradle.kts");
        }
        if (!file.exists()) {
            Messages.showErrorDialog("未发现android\\app\\build.gradle", "错误");
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches(".*namespace.*=.*")) {
                    stringBuilder.append("\tnamespace = \"").append(appId).append("\"");
                } else if (line.matches(".*applicationId.*=.*")) {
                    Pattern pattern = Pattern.compile(".*applicationId.*=.*\"(.*)\"");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        oldAppId = matcher.group(1);
                    }
                    stringBuilder.append("\t\tapplicationId = \"").append(appId).append("\"");
                } else {
                    stringBuilder.append(line);
                }
                stringBuilder.append(System.lineSeparator());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return oldAppId;
    }

    private void modifyBundleIdentifier(String projectPath, String bundleIdentifier) {
        File file = new File(projectPath, FlutterProjectConstant.IOS_XCODEPROJ_PATH);
        if (!file.exists()) {
            Messages.showErrorDialog("未发现" + FlutterProjectConstant.IOS_XCODEPROJ_PATH, "错误");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean bundleIdentifierStart = false;
            while ((line = reader.readLine()) != null) {
                if (bundleIdentifierStart && line.matches(".*PRODUCT_BUNDLE_IDENTIFIER.*=.*")) {
                    stringBuilder.append("\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = ").append(bundleIdentifier).append(";");
                    bundleIdentifierStart = false;
                } else {
                    stringBuilder.append(line);
                }
                if (line.contains("LD_RUNPATH_SEARCH_PATHS")) {
                    bundleIdentifierStart = true;
                }
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void modifyAppName(String projectPath, String appName) {
        modifyIosAppName(projectPath, appName);
        modifyAndroidAppName(projectPath, appName);
    }

    private void modifyAndroidAppName(String projectPath, String appName) {
        File file = new File(projectPath, FlutterProjectConstant.ANDROID_MANIFEST_PATH);
        if (!file.exists()) {
            Messages.showErrorDialog("未发现" + FlutterProjectConstant.ANDROID_MANIFEST_PATH, "错误");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean appNameStart = false;
            while ((line = reader.readLine()) != null) {
                if (appNameStart) {
                    stringBuilder.append("\t\tandroid:label=\"").append(appName).append("\"");
                    appNameStart = false;
                } else {
                    stringBuilder.append(line);
                }
                if (line.contains("<application")) {
                    appNameStart = true;
                }
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void modifyIosAppName(String projectPath, String appName) {
        File file = new File(projectPath, FlutterProjectConstant.IOS_INFO_PATH);
        if (!file.exists()) {
            Messages.showErrorDialog("未发现" + FlutterProjectConstant.IOS_INFO_PATH, "错误");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean appNameStart = false;
            while ((line = reader.readLine()) != null) {
                if (appNameStart) {
                    stringBuilder.append("\t<key>").append(appName).append("</key>");
                    appNameStart = false;
                } else {
                    stringBuilder.append(line);
                }
                if (line.contains("CFBundleName") || line.contains("CFBundleDisplayName")) {
                    appNameStart = true;
                }
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void modifyAndroidLaunchBackgrounds(String projectPath) {
        File resDir = new File(projectPath, FlutterProjectConstant.ANDROID_RES_DIR);
        if (resDir.exists()) {
            File[] dirs = resDir.listFiles();
            if (dirs == null) {
                return;
            }
            for (File dir : dirs) {
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files == null) {
                        continue;
                    }
                    for (File file : files) {
                        if ("launch_background.xml".equals(file.getName())) {
                            modifyAndroidLaunchBackground(file);
                        }
                    }
                }
            }
        }
    }

    private void modifyAndroidLaunchBackground(File backgroundFile) {
        if (!backgroundFile.exists()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(backgroundFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<item>")) {
                    stringBuilder.append("\t<item>");
                } else if (line.contains("</item>")) {
                    stringBuilder.append("\t</item>");
                } else {
                    stringBuilder.append(line);
                }
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backgroundFile))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


