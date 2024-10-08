# Unreal Engine Plugin Builder

Unreal Engine Plugin Builder is a Java-based application designed to assist in the building and packaging of Unreal Engine plugins. This tool provides a graphical user interface to simplify the process and integrates logging functionality for better debugging and monitoring.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Logging](#logging)
- [Contributing](#contributing)
- [License](#license)

## Features
- Custom path for Unreal Engine
- Real-time logging to JTextPane
- Building plugins from local directories or git repositories
- Simple and user-friendly GUI

## Prerequisites
Before you begin, ensure you have met the following requirements:
- **Java Development Kit (JDK)** version 21 or later is installed.
- **IntelliJ IDEA 2024.2.3 Ultimate Edition** or another Java IDE is set up.
- Basic knowledge of Java and Swing is recommended but not required.

## Installation
1. Clone the repository:
    ```sh
    git clone https://github.com/spacemex/UnrealEnginePluginBuilder.git
    cd UnrealEnginePluginBuilder
    ```
2. Open the project in IntelliJ IDEA or your preferred Java IDE.

## Building the Project
1. **Compile the project**:
   Ensure all dependencies are correctly configured in your IDE. Typically, you would use a build tool like Maven or Gradle, but for simplicity, compile directly within IntelliJ:
    - Go to **Build** > **Build Project** or press **Ctrl+F9**.

2. **Create a JAR file**:
    - Go to **File** > **Project Structure**.
    - In the **Artifacts** section, click the **+** icon and choose **JAR** > **From modules with dependencies**.
    - Select the main class `com.github.spacemex.Main`.
    - Click **OK** and then **Apply**. Use **Build** > **Build Artifacts** to create the JAR.

## Running the Application
Once you have built the JAR file, you can run the application using the following command:
```sh
java -jar out/artifacts/UnrealEnginePluginBuilder_jar/UnrealEnginePluginBuilder.jar
```

## Usage
1. **Custom Engine Path**: Specify the custom path for the Unreal Engine installation.
2. **Engine Version**: Set the Unreal Engine version.
3. **Plugin Directory**: Input the directory of the plugin or the git repository URL.
4. **Output Directory**: Specify the directory where the built plugin will be placed.
5. **Build Plugin**: Click the "Build Plugin" button to start the build process.

### Example
1. Set `Custom Engine Path` to `C:/Program Files/Epic Games/UE_5.4`.
2. Set `Engine Version` to `5.4`.
3. Set `Plugin Directory` to the path of your plugin.
4. Set `Output Directory` to the desired output location.
5. Click "Build Plugin".

## Logging
The application utilizes a JTextPane to display logs in real-time. Logs are categorized by their severity levels and color-coded for easy identification:
- **INFO**: White
- **WARNING**: Yellow
- **ERROR**: Red

Logs are also printed to the console for convenience.

## Contributing
Contributions are welcome! Please follow these steps:
1. Fork the project.
2. Create a feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a pull request.

## License
Distributed under your chosen license. See `LICENSE` for more information.