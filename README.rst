=======
Scala-Cryptu-Lib
=======

Scala-Cryptu-Lib: Scala AES(256+) encryption/decryption application (folder/file)

.. image:: https://img.shields.io/badge/Scala_Cryptu_Lib-v1.0.0-green.svg
        :target: https://github.com/janrock-hwx?tab=repositories
        :alt: Release Status

Features
--------

| This code provides an example of usage Scala for fast AES encryption/descryption with option for more than 128bit keys.

Usage
-----
1) The easiest way to clone the repository: git clone ...
2) sbt clean, build, run (assembly)

3) Example of the parameters
-p /Users/jan.rock/Documents/CryptuLib/input/ --f * --k g --i g

Version Support
---------------
v0.1: Initial Commit

Requirements
------------
1) JDK8, Scala
2) nonUS lib (limit for 128bit encryption)

Download and replace:
sudo cp /Users/jan.rock/Downloads/US_export_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/security/
sudo cp /Users/jan.rock/Downloads/local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/security/
