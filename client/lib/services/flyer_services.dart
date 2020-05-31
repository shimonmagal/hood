import 'dart:io';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:hood/model/flyer.dart';
import 'package:hood/model/position.dart';

class FlyerServices {
  static Future<List<Flyer>> fetchFlyers(Position position) async {
    String query = "longitude=${position.longitude}&latitude=${position.latitude}&maxDistanceInMetters=2500";
    final response = await http.get('http://10.0.2.2:8080/api/flyers?$query');
    
    if (response.statusCode == 200) {
      var flyersJson = json.decode(response.body) as List<dynamic>;
      
      try {
        List<Flyer> result = flyersJson.map((flyerJson) => Flyer.fromJson(flyerJson)).toList();
        
        return result;
      } catch (e) {
        print (e);
        throw Exception('Failed to parse flyers');
      }
    } else {
      throw Exception('Failed to load flyers');
    }
  }
  
  static Future<SendFlyerResponse> sendFlyer(
      String title, String description, File image, double latitude, double longitude) async {
    try {
      var uri = Uri.parse('http://10.0.2.2:8080/api/file');
      var request = http.MultipartRequest('PUT', uri)
        ..files.add(await http.MultipartFile.fromPath('file', image.path));
      
      var uploadImageResponse = await request.send();
      
      if (uploadImageResponse.statusCode != 200) {
        return SendFlyerResponse(false, "Error uploading image: ${uploadImageResponse.statusCode}", null);
      }
      
      final imageKey = await uploadImageResponse.stream.bytesToString();
      
      if (imageKey.isEmpty) {
        return SendFlyerResponse(false, "Missing image key: ${uploadImageResponse.statusCode}", null);
      }
      
      final response = await http.post('http://10.0.2.2:8080/api/flyers',
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(<String, dynamic>{
          'title': title,
          'description': description,
          'imageKey': imageKey,
          'location': {
            'latitude': latitude,
            'longitude': longitude
          }
        }),
      );
      
      if (response.statusCode != 200) {
        return SendFlyerResponse(false, "Error saving flyer: ${response.statusCode}", null);
      }
      
      // Todo - getting new id from the backend and creating a valid flyer from it
      
      return SendFlyerResponse(true, null, /*Flyer()*/ null);
    } catch (e) {
      return SendFlyerResponse(false, "Error saving flyer: ${e}", null);
    }
  } 
}

class SendFlyerResponse {
  bool success;
  String errorMessage;
  Flyer createdFlyer;
  
  SendFlyerResponse(this.success, this.errorMessage, this.createdFlyer);
}