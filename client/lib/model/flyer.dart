class Flyer {
  final String title;
  final String description;
  final String imageKey;
  
  Flyer(this.title, this.description, this.imageKey);
  
  factory Flyer.fromJson(Map json) {
    return Flyer(json['title'], json['description'], json['imageKey']);
  }
}
