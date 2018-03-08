Requirements: nonUS lib (limit for 128bit encryption)
Download and replace:
sudo cp /Users/jan.rock/Downloads/US_export_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/security/
sudo cp /Users/jan.rock/Downloads/local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/security/

Example of the parameters
-p /Users/jan.rock/Documents/CryptuLib/input/ --f * --k g --i g
