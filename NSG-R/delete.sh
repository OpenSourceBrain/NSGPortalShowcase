#!/bin/bash

source ./read_env_variables

echo "Deleting job: $1 for user $ADMIN_USERNAME"

curl -i --user $ADMIN_USERNAME:$ADMIN_PASSWORD \
     -H cipres-appkey:$UMBRELLA_APPID \
     -X DELETE \
      $URL/job/$ADMIN_USERNAME/$1 
