package com.github.spacemex.unreal;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class UnrealBuildPlugin {
    private double engineVersion = 5.4;
    private final List<String> commonPaths = new ArrayList<>();
    private final List<String> basePaths = new ArrayList<>();
    private String correctPath;
    private String buildPath;
    private File pluginFile;
    private String packagePath;
    private final Logger logger;

    public UnrealBuildPlugin(Logger logger) {
        this.logger = logger;
    }

    public void initialize() {
        setCommonPaths();
        appendCommonPaths();

        if (foundEngine()) {
            logger.info("Found Engine: " + correctPath);
            if (foundBuildPath()) {
                logger.info("Found Build Path: " + buildPath);
            } else {
                logger.severe("Build Path not found: " + buildPath);
            }
        } else {
            logger.severe("Engine not found");
        }
    }

    private void appendCommonPaths() {
        List<String> appendPaths = new ArrayList<>();

        if (!basePaths.isEmpty()) {
            for (String path : basePaths) {
                String newPath = path + "UE_" + engineVersion;
                appendPaths.add(newPath);
                logger.info("Appending Path: " + newPath);
            }
            commonPaths.clear();
            commonPaths.addAll(appendPaths);
        }
    }

    private void setCommonPaths() {
        basePaths.add(formatPath("C:/Program Files/Epic Games/"));
        basePaths.add(formatPath("C:/Program Files (x86)/Epic Games/"));
        appendCommonPaths();
    }

    public String formatPath(String path) {
        return path.replace("/", File.separator);
    }

    public void addCustomPath(String path) {
        if (path.isEmpty() || path.isBlank() || path.equalsIgnoreCase("null")) {
            return;
        }

        String sanitizedPath = sanitizePath(path);
        if (sanitizedPath.isBlank()) {
            return;
        }

        if (!sanitizedPath.endsWith(File.separator)) {
            sanitizedPath += File.separator;
        }

        String formattedPath = formatPath(sanitizedPath);
        String finalPath = formattedPath + "UE_" + engineVersion;

        if (commonPaths.contains(finalPath)) {
            return;
        }

        basePaths.add(formattedPath);
        commonPaths.add(finalPath);
    }

    private String sanitizePath(String path) {
        String[] parts = path.split("UE_");
        if (parts.length > 0) {
            return parts[0].trim();
        }
        return path;
    }

    public double getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(double engineVersion) {
        this.engineVersion = engineVersion;
        appendCommonPaths();
    }

    public Collection<String> getCommonPaths() {
        return Collections.unmodifiableCollection(commonPaths);
    }

    public boolean foundEngine() {
        for (String path : commonPaths) {
            logger.info("Checking path: " + path);
            File engineDir = new File(path);
            if (engineDir.exists() && engineDir.isDirectory()) {
                correctPath = path;
                buildPath = correctPath + File.separator + "Engine" + File.separator + "Build" + File.separator + "BatchFiles" + File.separator + "RunUAT.bat";
                logger.info("Engine found: " + correctPath);
                logger.info("Build path: " + buildPath);
                return true;
            }
        }
        logger.severe("Engine not found in any of the common paths.");
        return false;
    }

    public boolean isGitRepo(String path) {
        return path.contains(".git");
    }

    public boolean foundBuildPath() {
        return buildPath != null && new File(buildPath).exists();
    }

    public String getBuildPath() {
        return buildPath;
    }

    public File getFindUPlugin(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.severe("Invalid directory provided: " + path);
            return null;
        }

        File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".uplugin"));
        return (files != null && files.length > 0) ? files[0] : null;
    }

    private Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    private Path localrepo;

    private void downloadGitRepo(String path) {
        logger.warning("Downloading Repo: " + path);
        ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", path, tempDir.toString());
        localrepo = Paths.get(tempDir.toString());
        try {
            Process process = processBuilder.start();
            process.waitFor();
            pluginFile = getFindUPlugin(localrepo.toString());
            logger.warning("Plugin file found: " + (pluginFile != null ? pluginFile.getAbsolutePath() : "None"));
        } catch (Exception e) {
            logger.severe("Failed To Download Git Repo: " + e.getMessage());
        }
    }

    private void buildPlugin() {
        if (buildPath == null) {
            logger.severe("Build path is null. Cannot proceed with plugin build.");
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", buildPath, "BuildPlugin", "-plugin=" + pluginFile.getAbsolutePath(), "-package=" + packagePath, "-rocket");
        try {
            logger.warning("Building Plugin with command: " + String.join(" ", processBuilder.command()));
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info(line);
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Plugin built successfully.");
            } else {
                logger.severe("BuildPlugin process exited with code: " + exitCode);
            }

            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception e) {
            logger.severe("Failed to build plugin: " + e.getMessage() + " ");
        }
    }

    public void start(String dir, String packageDir) {
        this.packagePath = packageDir;
        if (dir != null && !dir.isEmpty()) {
            if (packageDir != null && !packageDir.isEmpty()) {
                if (isGitRepo(dir)) {
                    downloadGitRepo(dir);
                    if (pluginFile != null) {
                        buildPlugin();
                    } else {
                        logger.severe("Plugin not found in Git repository.");
                    }
                } else {
                    pluginFile = getFindUPlugin(dir);
                    if (pluginFile != null) {
                        buildPlugin();
                    } else {
                        logger.severe("Plugin not found in the local directory.");
                    }
                }
            } else {
                logger.severe("Invalid package directory provided.");
            }
        } else {
            logger.severe("Invalid directory provided.");
        }
    }
}