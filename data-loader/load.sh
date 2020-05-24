#!/bin/bash

function upload_flyer() {
	local title=$1
	local description=$2
	local image_url=$3
	local longitude=$4
	local latitude=$5
	
	local temp_image_file=$(mktemp)
	
	if ! wget -O $temp_image_file $image_url; then
		echo "Error downloading $image_url to $temp_image_file"
		return 1
	fi
	
	local image_key=$(curl -X PUT -F "file=@$temp_image_file" http://localhost:8080/api/file)
	
	if [ "$?" != "0" ]; then
		echo "Error uploading image to server"
		return 1
	fi
	
	rm $temp_image_file
	
	local temp_upload_flyer_json=$(mktemp)
	
	cp flyer.template.json $temp_upload_flyer_json
	sed -i s/@title@/$title/g $temp_upload_flyer_json
	sed -i s/@description@/$description/g $temp_upload_flyer_json
	sed -i s/@imageKey@/$image_key/g $temp_upload_flyer_json
	sed -i s/@longitude@/$longitude/g $temp_upload_flyer_json
	sed -i s/@latitude@/$latitude/g $temp_upload_flyer_json
	
	if ! curl -X POST -d @$temp_upload_flyer_json --header "Content-Type: application/json" \
			"http://localhost:8080/api/flyers"; then
		echo "Error uploading flyer"
		return 1
	fi
	
	rm $temp_upload_flyer_json
	
	return 0
}

function main() {
	upload_flyer "Motorcycle" "description" \
		"https://lemonsquad.com/images/inspections/moto_sample_overall_lg.jpg" \
		"32.083454" "34.773047" || exit 1
	
}

main $@
