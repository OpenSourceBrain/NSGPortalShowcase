#!/bin/sh
export URL=https://cipresrest.sdsc.edu/cipresrest/v1
export CRA_USER=changeme
export PASSWORD=changeme
export KEY=testapp-B6E2A1A0B5A049C48914EFEC8BE5D38B

#<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
#<jobstatus>
#    <selfUri>
#        <url>https://cipresrest.sdsc.edu/cipresrest/v1/job/kenneth/NGBW-JOB-RAXMLHPC8_REST_XSEDE-746C3EDDB32046C4B8CD0D2DB3131B03</url>
#        <rel>jobstatus</rel>
#        <title>NGBW-JOB-RAXMLHPC8_REST_XSEDE-746C3EDDB32046C4B8CD0D2DB3131B03</title>
#    </selfUri>
#    <jobHandle>NGBW-JOB-RAXMLHPC8_REST_XSEDE-746C3EDDB32046C4B8CD0D2DB3131B03</jobHandle>
#    <jobStage>QUEUE</jobStage>
#    <terminalStage>false</terminalStage>
#    <failed>false</failed>
#    <metadata>
#        <entry>
#            <key>statusEmail</key>
#            <value>true</value>
#        </entry>
#    </metadata>
#    <dateSubmitted>2015-04-23T12:32:27-07:00</dateSubmitted>
#    <resultsUri>
#        <url>https://cipresrest.sdsc.edu/cipresrest/v1/job/kenneth/NGBW-JOB-RAXMLHPC8_REST_XSEDE-746C3EDDB32046C4B8CD0D2DB3131B03/output</url>
#        <rel>results</rel>
#        <title>Job Results</title>
#    </resultsUri>
#    <workingDirUri>
#        <url>https://cipresrest.sdsc.edu/cipresrest/v1/job/kenneth/NGBW-JOB-RAXMLHPC8_REST_XSEDE-746C3EDDB32046C4B8CD0D2DB3131B03/workingdir</url>
#        <rel>workingdir</rel>
#        <title>Job Working Directory</title>
#    </workingDirUri>
#    <messages>
#        <message>
#            <timestamp>2015-04-23T12:32:27-07:00</timestamp>
#            <stage>QUEUE</stage>
#            <text>Added to cipres run queue.</text>
#        </message>
#    </messages>
#    <minPollIntervalSeconds>60</minPollIntervalSeconds>
#</jobstatus>

curl -u $CRA_USER:$PASSWORD -H cipres-appkey:$KEY $URL/job/$CRA_USER $1

