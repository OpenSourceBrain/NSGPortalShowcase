
mv MediumNet/*.dat /tmp
rm -rf MediumNet/x86_64 simulator.props report.medium.txt MediumNet/__pycache__
zip -x MediumNet/*~ -r input MediumNet
