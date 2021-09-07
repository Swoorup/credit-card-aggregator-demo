#! /usr/bin/env bash

if [[ -z "${HTTP_PORT}" ]]; then
  echo "HTTP_PORT is not set"
  exit 1
fi
if [[ -z "${CSCARDS_ENDPOINT}" ]]; then
  echo "CSCARDS_ENDPOINT is not set"
  exit 1
fi
if [[ -z "${SCOREDCARDS_ENDPOINT}" ]]; then
  echo "SCOREDCARDS_ENDPOINT is not set"
  exit 1
fi

./target/universal/stage/bin/credit-card-aggregator \
  -p $HTTP_PORT \
  -c $CSCARDS_ENDPOINT \
  -s $SCOREDCARDS_ENDPOINT