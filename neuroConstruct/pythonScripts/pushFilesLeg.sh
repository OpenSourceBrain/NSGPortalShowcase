#!/bin/bash 

export simRef="Sim2"
export projName="Parallel"

export targetDir="/home/ucgbpgl/nCsims"
export remoteHost="legion.rc.ucl.ac.uk"
export remoteUser="ucgbpgl"
export nrnivLocation="/home/ucgbpgl/nrnmpi/x86_64/bin/nrniv"

projDir=$targetDir"/"$projName
simDir=$projDir"/"$simRef

echo "Going to send files to dir: "$simDir" on "$remoteHost

echo "mpirun -map localhost:localhost:localhost:localhost "$nrnivLocation" "$simDir"/"$projName".hoc">runmpi.sh

chmod u+x runmpi.sh

ssh $remoteUser@$remoteHost "mkdir $projDir"
ssh $remoteUser@$remoteHost "rm -rf $simDir"
ssh $remoteUser@$remoteHost "mkdir $simDir"

zipFile=$simRef".tar.gz"

echo "Going to zip files into "$zipFile

tar czf $zipFile generatedNEURON/*.mod generatedNEURON/*.hoc generatedNEURON/*.props generatedNEURON/*.dat runmpi.sh


echo "Going to send to: $simDir on $remoteUser@$remoteHost"
echo -e "put $zipFile">putFile.b
sftp -b putFile.b $remoteUser@$remoteHost
ssh $remoteUser@$remoteHost "cp ~/$zipFile $simDir/$zipFile"

echo "Sent..."


ssh $remoteUser@$remoteHost "cd $simDir;tar xzf $zipFile;mv generatedNEURON/* .;rm -r generatedNEURON; rm $zipFile"


ssh $remoteUser@$remoteHost "cd $simDir;/bin/bash -ic nrnivmodl"
ssh $remoteUser@$remoteHost "cd $simDir;/bin/bash -ic ./runmpi.sh"

