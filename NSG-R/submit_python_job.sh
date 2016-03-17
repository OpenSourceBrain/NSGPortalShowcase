#!/bin/bash

source ./read_env_variables

echo "Accessing: $URL/job/$ADMIN_USERNAME for user $ADMIN_USERNAME"

curl -i --user $ADMIN_USERNAME:$ADMIN_PASSWORD \
     -H cipres-appkey:$UMBRELLA_APPID \
     -H cipres-eu:$ADMIN_USERNAME \
     -H cipres-eu-email:$ADMIN_EMAIL \
     -H cipres-eu-institution:$ADMIN_INSTITUTION \
     -H cipres-eu-country:$ADMIN_COUNTRY \
      $URL/job/$ADMIN_USERNAME \
     -F tool='PY_TG' \
     -F input.infile_=@../examples/Python/input.zip \
     -F metadata.clientJobId=1234546 \
     -F metadata.statusEmail=true
