#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$USERNAME for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/job/$USERNAME \
     -F tool='BRIAN_TG' \
     -F input.infile_=@../examples/Python/input.zip \
     -F metadata.clientJobId=TestInstalledPython \
     -F metadata.statusEmail=true
