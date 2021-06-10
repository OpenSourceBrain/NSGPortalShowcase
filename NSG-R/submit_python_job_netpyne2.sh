#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$USERNAME for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/job/$USERNAME \
     -F tool='NEURON_EXPANSE' \
     -F input.infile_=@../examples/NetPyNE/input.zip \
     -F vparam.filename_=init.py \
     -F metadata.clientJobId=NetPyNE_multinode_test \
     -F metadata.statusEmail=true \
     -F vparam.number_cores_=8 \
     -F vparam.number_nodes_=2 \
     -F vparam.tasks_per_node_=1
