#!/bin/bash

source ./read_env_variables_admin_osb

echo "Accessing: $URL/job/$USERNAME/$1 for user $USERNAME"

curl -i -u $ADMIN_USERNAME:$ADMIN_PASSWORD \
     -H cipres-appkey:$UMBRELLA_APPID \
     -H cipres-eu:$USER_USERNAME \
     -H cipres-eu-email:$USER_EMAIL \
     -H cipres-eu-institution:$USER_INSTITUTION \
     -H cipres-eu-country:$USER_COUNTRY \
      $URL/job/$UMBRELLA_APPNAME.$USER_USERNAME/$1/output
