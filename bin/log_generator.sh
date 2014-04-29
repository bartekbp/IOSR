#!/bin/bash

# Exit on failure
set -e

if [[ -z "$1" ]]; then
    LOGS_PER_MINUTE=60
    echo "Generating default = 60 logs per minute. Use program argument for custom amount."
else
    LOGS_PER_MINUTE=$1
    echo "Generating $LOGS_PER_MINUTE logs per minute."
fi

TIME_TO_SLEEP=`echo "scale=3; 60 / $LOGS_PER_MINUTE" | bc`

COUNTER=0
while true
do
	logger "Auto generated log $COUNTER"
	echo -en "\rGenerated logs: $COUNTER"
	COUNTER=$((COUNTER + 1))
	sleep $TIME_TO_SLEEP
done

exit 0