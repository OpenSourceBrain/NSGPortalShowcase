#!/bin/bash

source ./read_env_variables_admin_osb

url=$URL/job/$UMBRELLA_APPNAME.$USER_USERNAME

echo "Accessing: $url for user $ADMIN_USERNAME on behalf of $USER_USERNAME"

curl -i -u $ADMIN_USERNAME:$ADMIN_PASSWORD \
     -H cipres-appkey:$UMBRELLA_APPID \
     -H cipres-eu:$USER_USERNAME \
     -H cipres-eu-email:$USER_EMAIL \
     -H cipres-eu-institution:$USER_INSTITUTION \
     -H cipres-eu-country:$USER_COUNTRY \
     -F tool='NEURON_EXPANSE' \
     -F input.infile_=@../examples/NetPyNE/input.zip \
     -F vparam.filename_=init.py \
     -F metadata.clientJobId=NetPyNETestAdmin \
     -F metadata.statusEmail=true \
     -F vparam.number_cores_=1 \
     -F vparam.number_nodes_=1 \
     -F vparam.tasks_per_node_=1  \
      $url
