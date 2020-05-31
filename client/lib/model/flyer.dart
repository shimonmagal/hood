import 'package:hood/model/position.dart';
import 'package:geolocator/geolocator.dart' as Geolocator;

class Flyer {
  final String title;
  final String description;
  final String imageKey;
  final Position location;
  
  Flyer(this.title, this.description, this.imageKey, this.location);
  
  factory Flyer.fromJson(Map json) {
    return Flyer(json['title'], json['description'], json['imageKey'], Position.fromJson(json['location']));
  }
  
  Future<List<Geolocator.Placemark>> getLocationAddress() {
  	return Geolocator.Geolocator().placemarkFromCoordinates(location.latitude, location.longitude);
  }
  
  Future<double> getDistanceInMetters(double longitude, double latitude) {
  	return Geolocator.Geolocator().distanceBetween(latitude, longitude, location.latitude, location.longitude);
  }
}
