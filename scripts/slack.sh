#!/bin/bash

######
# Send notification messages to a Slack channel by using Slack webhook
#
# input parameters:
#   $1 - pretext
#   $2 - main text
######
color=danger
if [ -n "$2" ]; then
  color=$2
fi

if [ color eq "good" ]; then
  tag=$(git tag --points-at HEAD | sort -V | tail -n1)
  messageLine="com.cloudinary:cloudinary:$tag"
  message="payload={\"channel\": \"#$SLACK_CHANNEL\",\"attachments\":[{\"pretext\":\"$1\",\"text\":\"$messageLine\",\"color\":\"$color\"}]}"
else
  message="payload={\"channel\": \"#$SLACK_CHANNEL\",\"attachments\":[{\"pretext\":\"$1\",\"text\":\"\",\"color\":\"$color\"}]}"
fi

curl -X POST --data-urlencode "$message" ${SLACK_WEBHOOK_URL}