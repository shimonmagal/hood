class BlobServices {
	static String getBlobUrl(String key) {
		return "http://10.0.2.2:8080/api/file?key=$key";
	} 
}
