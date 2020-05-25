import 'package:flutter/material.dart';
import 'package:hood/model/flyer.dart';
import 'package:hood/screens/flyer_viewer_screen.dart';
import 'package:hood/services/blob_services.dart';
import 'package:hood/model/position.dart';

class FlyersGrid extends StatelessWidget {
  final List<Flyer> flyers;
  final Position location;
  
  FlyersGrid(this.flyers, this.location);
  
  Widget build(BuildContext context) {
    return GridView.count(
      crossAxisCount: 3,
      crossAxisSpacing: 4.0,
      mainAxisSpacing: 8.0,
      children: List.generate(flyers.length, (index) {
        return InkWell(
          child: Column(
            children: <Widget>[
              Expanded(
                child: Image.network(BlobServices.getBlobUrl(flyers[index].imageKey))
              ),
              Expanded(
                child: Column(
                  children: <Widget>[
                    Text(flyers[index].title,
                      style: TextStyle(fontSize: 15)
                    ),
                    FutureBuilder(
                      future: getDistance(flyers[index]),
                      builder: (context, snapshot) {
                        if (snapshot.hasData) {
                          String formattedDistance = snapshot.data;
                          
                          return Text("$formattedDistance away",
                            style: TextStyle(fontSize: 12, fontStyle: FontStyle.italic, color: Colors.black.withOpacity(0.6))
                          );
                        }
                        
                        return Text("");
                      }
                    ),
                  ]
                )
              ),
            ],
          ),
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => FlyerViewerForm(flyers[index]))
            );
          }
        );
      }
    ));
  }
  
  Future<String> getDistance(Flyer flyer) async {
    return flyer.getDistanceInMetters(location.longitude, location.latitude).then((distanceInMetters) {
      if (distanceInMetters < 1000) {
        int roundedMetters = (50 * (distanceInMetters.round() / 50).round());
        return "$roundedMetters meters";
      } else {
        int kilometers = (distanceInMetters / 1000).floor();
        int roundedMetters = (distanceInMetters % 1000 / 100).round();
        
        return "${kilometers}.${roundedMetters} kilometers";
      }
    });
  }
}
