#!/bin/bash

function authenticate() {
	local mail=$1
	local pass=$2
	local session_id_output_file=$3

	local session=$(curl -s -G -i http://localhost:8080/api/emailpassword \
		--data-urlencode "email=$mail" --data-urlencode "password=$pass" | \
		/bin/grep "Session: " | sed s/Session:\ //g)
	
	if [ "$?" != "0" ]; then
		echo "Error authenticating with $mail"
		return 1
	fi
	
	if [ -z "$session" ]; then
		echo "Fail to authenticate $mail"
		return 1
	fi
	
	echo "$session" > $session_id_output_file
	
	local session_without_newlines=$(cat $session_id_output_file | tr -d '\n' | tr -d '\r')
	echo -n "$session_without_newlines" > $session_id_output_file
	
	return 0
}

function upload_flyer() {
	local title=$1
	local description=$2
	local image_url=$3
	local session=$4
	local latitude=$5
	local longitude=$6
	
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
	sed -i "s/@title@/$title/g" $temp_upload_flyer_json
	sed -i "s/@description@/$description/g" $temp_upload_flyer_json
	sed -i "s/@imageKey@/$image_key/g" $temp_upload_flyer_json
	sed -i "s/@longitude@/$longitude/g" $temp_upload_flyer_json
	sed -i "s/@latitude@/$latitude/g" $temp_upload_flyer_json
	
	if ! curl -s -X POST -d @$temp_upload_flyer_json --header "Content-Type: application/json" \
			--header "session: $session" "http://localhost:8080/api/flyers"; then
		echo "Error uploading flyer"
		return 1
	fi
	
	rm $temp_upload_flyer_json
	
	return 0
}

function main() {
	local session_file=$(mktemp)
	
	authenticate "hoodevtest@gmail.com" "" "$session_file" || exit 1

	local session=$(cat $session_file)
	
	upload_flyer "Motorcycle Rides" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://images-na.ssl-images-amazon.com/images/I/71%2B1tRw991L._AC_SX466_.jpg" \
		"$session" "32.083454" "34.773047" || exit 1
	
	upload_flyer "Guitar Lessons" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://img-a.udemycdn.com/course/750x422/667186_6d70_5.jpg" \
		"$session" "32.084735" "34.775436" || exit 1
	
	upload_flyer "Piece Of Cake" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://pinoydeal.ph/oc-content/uploads/170/17958_original.jpg" \
		"$session" "32.083113" "34.772402" || exit 1
	
	upload_flyer "Cat Babysitting" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://i.pinimg.com/736x/d7/4d/96/d74d96a203054f7200b85b45a0816fcd.jpg" \
		"$session" "32.083731" "34.781879" || exit 1
	
	upload_flyer "Bed" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://secure.img1-fg.wfcdn.com/im/27291551/compr-r85/7262/72624026/rhoton-low-profile-platform-bed.jpg" \
		"$session" "32.086985" "34.785913" || exit 1
	
	upload_flyer "Babysitting" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQxVoZfUo7IYjqpP1wfccyGbpd5JoVQUcef1rR8hwM7IUW7DFP0&usqp=CAU" \
		"$session" "32.074786" "34.779261" || exit 1
	
	upload_flyer "Chess Partner" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://miro.medium.com/max/1400/1*FVfdYFOkVlf2lPswre6iXg.jpeg" \
		"$session" "32.072386" "34.768232" || exit 1
	
	upload_flyer "Books" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://cdn.elearningindustry.com/wp-content/uploads/2016/05/top-10-books-every-college-student-read-e1464023124869.jpeg" \
		"$session" "32.064749" "34.768618" || exit 1
	
	upload_flyer "Pool Party" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://i2.wp.com/blessedbeyondcrazy.com/wp-content/uploads/2018/05/graphicstock-happy-young-couples-swimming-in-pool-and-having-fun_H_eeaP4Bhl.jpg" \
		"$session" "32.069985" "34.778274" || exit 1
	
	upload_flyer "Healthy Lunch" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://www.bbcgoodfood.com/sites/default/files/recipe-collections/collection-image/2013/05/quick-chicken-and-hummus-bowl.jpg" \
		"$session" "32.064130" "34.778875" || exit 1
	
	upload_flyer "Comunity Garden" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://en-lifesci.tau.ac.il/sites/lifesci_en.tau.ac.il/files/styles/reaserch_main_image_580_x_330/public/580bota.jpg?itok=TAcZhDie" \
		"$session" "32.062748" "34.767759" || exit 1
	
	upload_flyer "Dog Walker" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://www.hillingdon.gov.uk/image/569/Dog-walker/standard.jpg?m=1570788933693" \
		"$session" "32.099619" "34.786814" || exit 1
	
	upload_flyer "Handyman" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"http://www.one40fivestore.com/wp-content/uploads/2019/12/15.jpg" \
		"$session" "32.085622" "34.805267" || exit 1
	
	upload_flyer "Clothes To Pass" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://www.avenuecalgary.com/wp-content/uploads/2018/11/SalvEdge_17thAveShops_MariahWilson-3389-edit-960x641.jpg" \
		"$session" "32.072749" "34.795268" || exit 1
	
	upload_flyer "Yoga Class" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://media-cdn.tripadvisor.com/media/photo-s/16/8c/e9/f1/outdoor-yoga-class-on.jpg" \
		"$session" "32.075040" "34.780720" || exit 1
	
	upload_flyer "Massage" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://anastasia-massage.com/wp-content/uploads/2019/04/columbia-integrated-health-new-westminster-massage-therapy-768x512.jpg" \
		"$session" "32.073949" "34.765013" || exit 1
	
	upload_flyer "Gel Nail Polish" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/beauty-athomegel-1574871622.jpg?crop=1.00xw:1.00xh;0,0&resize=980:*" \
		"$session" "32.077113" "34.766944" || exit 1

	upload_flyer "Kids Play Date" "$(curl "https://baconipsum.com/api/?type=meat-and-filler&sentences=2&format=text")" \
		"https://previews.123rf.com/images/stylephotographs/stylephotographs1704/stylephotographs170400239/76608217-happy-interracial-group-of-kids-playing-in-summer.jpg" \
		"$session" "32.044198" "34.793723" || exit 1
}

main $@
