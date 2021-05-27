#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$USERNAME for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/job/$USERNAME \
     -F tool='PY_EXPANSE' \
     -F input.infile_=@../examples/Python/input.zip \
     -F metadata.clientJobId=TestInstalledPython \
     -F metadata.statusEmail=true\
     -F vparam.number_cores_=1 \
     -F vparam.number_nodes_=1 \
     -F vparam.tasks_per_node_=1
