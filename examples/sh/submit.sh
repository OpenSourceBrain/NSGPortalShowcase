#!/bin/sh
export URL=https://cipresrest.sdsc.edu/cipresrest/v1
export CRA_USER=changeme
export PASSWORD=changeme
export KEY=testapp-B6E2A1A0B5A049C48914EFEC8BE5D38B

curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY $URL/job/$CRA_USER -F tool=RAXMLHPC8_REST_XSEDE  -F input.infile_=@./raxml_inputphy.txt -F metadata.statusEmail=true

