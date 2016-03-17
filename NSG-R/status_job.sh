#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$ADMIN_USERNAME for user $ADMIN_USERNAME"

curl -i --user $ADMIN_USERNAME:$ADMIN_PASSWORD \
     -H cipres-appkey:$UMBRELLA_APPID \
      $URL/job/$ADMIN_USERNAME/$1 
