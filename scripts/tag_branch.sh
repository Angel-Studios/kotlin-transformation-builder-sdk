#!/bin/bash

git fetch --tags
CURRENT_TAG=$(git tag --points-at HEAD | sort -V | tail -n1)
if [[ ! -z "$CURRENT_TAG" ]]; then
  echo "Skipping tag step for commit with existing tag $CURRENT_TAG"
  exit 0
fi

MAJOR=$(date +%y)
MINOR=$(date +%-V)

LAST_PATCH=$(git tag | grep "$MAJOR.$MINOR" | sort -V | tail -n1 | cut -d'.' -f3)
if [[ -z $LAST_PATCH ]]; then
  PATCH=0
  BASE_BRANCH=main
  RELEASE_TYPE=Release
else
  PATCH=$((LAST_PATCH+1))
  BASE_BRANCH="release/$MAJOR.$MINOR.$LAST_PATCH"
  RELEASE_TYPE=Hotfix
fi

# Remove once tagging can be used instead
if [[ ! -z $BITRISE_BUILD_NUMBER ]]; then
  PATCH=$BITRISE_BUILD_NUMBER
fi

VERSION="$MAJOR.$MINOR.$PATCH"

TAG=$VERSION
if [[ ! -z $1 ]]; then
  TAG="$1-$VERSION"
fi

git tag -fa "$TAG" -m "$RELEASE_TYPE $TAG"
git push origin $TAG

echo "Created git tag $TAG"