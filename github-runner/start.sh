#!/bin/bash

if [ -f ".runner" ]; then
  log "Runner already configured. Skipping registration..."
else
  if [ -n "${GITHUB_PAT}" ]; then
    echo "Getting registration token from GitHub API..."

    REPO_PATH=$(echo ${GITHUB_URL} | sed -E 's/https:\/\/github\.com\///')

    REGISTRATION_TOKEN=$(curl -s -L -X POST \
        -H "Accept: application/vnd.github+json" \
        -H "Authorization: Bearer ${GITHUB_PAT}" \
        -H "X-GitHub-Api-Version: 2022-11-28" \
        https://api.github.com/repos/${REPO_PATH}/actions/runners/registration-token | jq -r '.token')

    if [ -z "${REGISTRATION_TOKEN}" ] || [ "${REGISTRATION_TOKEN}" = "null" ]; then
        echo "Failed to get registration token. Check your GITHUB_PAT and permissions."
        exit 1
    fi

    echo "Successfully got registration token"
  else
      echo "GITHUB_PAT is not set. Cannot register runner."
      exit 1
  fi
fi

if [ -n "${GITHUB_URL}" ] && [ -n "${REGISTRATION_TOKEN}" ] && [ -n "${RUNNER_NAME}" ]; then
    echo "Configuring runner for ${GITHUB_URL}..."
    ./config.sh --url ${GITHUB_URL} --token ${REGISTRATION_TOKEN} --name ${RUNNER_NAME} --work _work --unattended --replace --disableupdate

    if [ $? -eq 0 ]; then
        echo "Runner configured successfully"
    else
        echo "Failed to configure runner"
        exit 1
    fi
else
    echo "Missing required environment variables"
    exit 1
fi

echo "Starting runner..."
./run.sh