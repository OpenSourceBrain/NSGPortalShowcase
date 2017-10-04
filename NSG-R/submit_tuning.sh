#!/bin/bash

# WORK IN PROGRESS!!

source ./read_env_variables

echo "Accessing: $URL/job/$USERNAME for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/job/$USERNAME \
     -F tool='OSBPYNEURON74' \
     -F input.infile_=@../examples/tune/input.zip \
     -F metadata.clientJobId=2222222 \
     -F metadata.statusEmail=true \
     -F vparam.number_cores_=1 \
     -F vparam.number_nodes_=1
