#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$USERNAME for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/job/$USERNAME \
     -F tool='NEURON74_PY_TG' \
     -F input.infile_=@../examples/NetPyNE/input.zip \
     -F metadata.clientJobId=NetPyNE_test \
     -F metadata.statusEmail=true \
     -F vparam.number_cores_=1 \
     -F vparam.number_nodes_=1
