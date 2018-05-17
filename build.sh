#!/bin/bash -e

read LINES COLUMNS < <(stty size)
eval "line() { echo -e ''\$_{1..$((${COLUMNS:-80}-2))}'\\b#'; }"

cd $(dirname $0)

artifactory_upload=0

if [[ $USER == "bamboo" ]] ; then
    if [[ $# -ne 1 ]] ; then
        echo "Usage (on Bamboo build server): ./$(basename $0) \${bamboo_buildNumber}"
        exit 1
    fi

    if grep '^artifactory' gradle.properties | grep -q artifactory_user && \
       grep '^artifactory' gradle.properties | grep -q artifactory_password && \
       grep '^artifactory' gradle.properties | grep -q artifactory_contextUrl
    then
        artifactory_upload=1
    fi
fi

branch=$(git symbolic-ref HEAD)
branch=${branch#refs/heads/}
branch=${branch//\//-}

# extra "echo" strips leading/trailing whitespace
ver=$(echo $(grep version gradle.properties | cut -f2 -d=))
if [[ "${branch}" == "master" || "${branch}" == "release" ]] ; then
    if [[ $# -gt 0 ]] ; then
        version=${ver%-SNAPSHOT}.$1
    else
        version=${ver}
    fi
else
    if [[ $# -gt 0 ]] ; then
        version=${ver%-SNAPSHOT}.$1.${branch}
    else
        version=${ver%-SNAPSHOT}.${branch}-SNAPSHOT
    fi

    artifactory_upload=0
fi

line
echo "Branch is ${branch}"
echo "Version is ${version}"
echo -n "Will "
[[ $artifactory_upload -ne 0 ]] || echo -n "not "
echo "upload to artifactory"
if [[ "${branch}" == "release" ]] ; then
    if ! git tag release/${version} ; then
        echo "If this is in error, delete the git tag"
        line
        exit
    fi
    echo "Will tag this build in git"
    echo
fi
echo "Beginning clean build with tests"
line

# Different certificates are needed for downloading dependencies than are
# needed for uploading to artifactory.  Therefore, "build" and "publish"
# need to be done as separate steps
./gradlew clean build javadoc -Pversion=${version}

line
echo "Build complete"
if [[ $USER == "bamboo" ]] ; then
    if [[ $artifactory_upload -ne 0 ]] ; then
        echo "Beginning upload to artifactory"
        line

        ./gradlew artifactoryPublish -Pversion=${version}

        line
        echo "Upload complete"
    fi
    echo "Tagging build in git"
    line

    git tag builds/${version}
    git push --tags -v
else
    echo "Not running on build machine, therefore not uploading to artifactory and not tagging in git."
fi

line
echo "Complete"
line
