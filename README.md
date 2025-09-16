# BakPlus
<p align="center"><img src="https://github.com/Fedrix42/BakPlus/blob/main/GHLogo.png" alt="BakPlus Logo" width="400" height="250"></p>
<img src="https://github.com/Fedrix42/BakPlus/blob/main/Dark.png" alt="BakPlus GUI Dark">

Simple and easy to use backup software to make **incremental backups** on local folders.

 # Features

 - Java multiple platform supported, *simple installation and use* 
 - Multiple **backup strategies management** to save time
 - AES256 and Zip Standard encryption
 - None or Deflate compression
 - Automatic **integrity check** by comparing SHA256

# Supported Platform

 - Linux, Windows breafly tested and supported with Java
 - MACOS and other OS NOT tested but supported with Java

# Requirements

Required <a src="https://www.java.com/it/download/">Java 8</a> or latest version.
If you already got **a Java SDK or Java SE installation** you can try to run it with the version you have.
If you want to run it by cli: *java -jar BakPlus-<version>-fat.jar*

# Installation

 - Download the latest release by going to the release section
 - Extract the downloaded archive in the folder where you want to put the software (Ex. C:\Programs Files\)
 - BakPlus-<version>-fat.jar is ready to be executed with java, you can create a link to the desktop if you want

# Environment

BakPlus create a data folder called .bakplus_data in the user home folder to store it's data.
This folder cannot yet be changed but through the settings you can change the folder where the
strategies are saved, this can be useful to syncronize strategies across multiple users / computers.

# Dependencies
 - https://github.com/srikanth-lingala/zip4j
 - https://github.com/JFormDesigner/FlatLaf/blob/main/LICENSE

Thanks a lot to the creators of these libraries which are used in the development of BakPlus.
