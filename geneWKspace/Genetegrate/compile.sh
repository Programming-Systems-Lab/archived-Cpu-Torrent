export CLASSPATH=/nfs/home1/ssheth/Code/
javac -d . source/*.java
javac -d . middleware/*.java
javac -d . services/*.java

javac -d . client/*.java

rmic middleware.CPUTorrentRMIServer
cp middleware/CPUTorrentRMIServer_Stub.class client/ 