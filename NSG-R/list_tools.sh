#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/tool for user $USERNAME"

curl -i --user $USERNAME:$PASSWORD \
     -H cipres-appkey:$DIRECT_APPID \
      $URL/tool
