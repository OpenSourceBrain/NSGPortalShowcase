#!/bin/bash 

export simRef="Sim2"
export projName="Parallel"

export targetDir="/home/ucgbpgl/nCsims"
export remoteHost="smp-test.rc.ucl.ac.uk"
export remoteUser="ucgbpgl"


projDir=$targetDir"/"$projName
simDir=$projDir"/"$simRef

export localDir="simulations/"$simRef"/"


echo "Going to get files from dir: "$simDir" on "$remoteHost" and place them locally on "$localDir

zipFile=$simRef".tar.gz"

echo "Going to zip files into "$zipFile

ssh $remoteUser@$remoteHost "cd $simDir;pwd;tar czf $zipFile *.dat *.props *.mod *.hoc"

scp  $remoteUser@$remoteHost:$simDir"/"$zipFile $localDir

cd $localDir
tar xzf $zipFile
rm $zipFile