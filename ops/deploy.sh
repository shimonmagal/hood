#!/bin/bash

declare -r script_dir=`cd "$( dirname "$0" )" && pwd`

pushd $script_dir
cd ../server
./gradlew distTar || exit 1

sftp_batch_file=$(mktemp)
echo "put build/distributions/hood-1.0-SNAPSHOT.tar /tmp/hood-1.0-SNAPSHOT.tar" >$sftp_batch_file
sftp -i /home/david/keys/hood-key.pem -o StrictHostKeyChecking=no -q -b $sftp_batch_file hood-server@3.133.96.64 || exit 1

ssh -i /home/david/keys/hood-key.pem -o StrictHostKeyChecking=no -q hood-server@3.133.96.64 \
	"tar xvf /tmp/hood-1.0-SNAPSHOT.tar --strip-components=1 -C /opt/hood-server" || exit 1

popd
