class Position {
  final double longitude;
  final double latitude;
  
  Position(this.longitude, this.latitude);
  
  factory Position.fromJson(Map json) {
    return Position(json['longitude'], json['latitude']);
  }
}
