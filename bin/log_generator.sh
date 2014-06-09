#!/bin/bash

# Exit on failure
set -e

if [[ -z "$1" ]]; then
    LOGS=100
    echo "Generating default = 100 logs. Use program argument for custom amount."
else
    LOGS=$1
    echo "Generating $LOGS logs."
fi


COUNTER=0
while true
do
	logger "Auto generated log $COUNTER"
	echo -en "\rGenerated logs: $COUNTER"
	COUNTER=$((COUNTER + 1))
	if [[ $COUNTER -ge $LOGS ]]; then
		break
	fi
done

exit 0
