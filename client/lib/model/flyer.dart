import 'package:hood/model/position.dart';

class Flyer {
  final String title;
  final String description;
  final String imageKey;
  final Position location;
  
  Flyer(this.title, this.description, this.imageKey, this.location);
  
  factory Flyer.fromJson(Map json) {
    return Flyer(json['title'], json['description'], json['imageKey'], Position.fromJson(json['location']));
  }
}
