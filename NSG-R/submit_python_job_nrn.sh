#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$USERNAME for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/job/$USERNAME \
     -F tool='NEURON77_TG' \
     -F input.infile_=@../examples/NEURON/input.zip \
     -F metadata.clientJobId=NRN_PG \
     -F metadata.statusEmail=true \
     -F vparam.number_cores_=16 \
     -F vparam.number_nodes_=4
