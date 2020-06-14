import 'package:global_configuration/global_configuration.dart';

class BlobServices {
	static String getBlobUrl(String key) {
		return "${GlobalConfiguration().getString("apiUrl")}/file?key=$key";
	} 
}
