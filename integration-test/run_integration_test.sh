#!/bin/bash

# example: sh ./run_integration_test.sh <local|dev|uat|prod>
set -e

# run integration tests
cd ./src || exit
yarn install
yarn add crypto-js
yarn test:"$1"