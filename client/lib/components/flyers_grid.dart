import 'package:flutter/material.dart';
import 'package:hood/model/flyer.dart';
import 'package:hood/screens/flyer_viewer_screen.dart';
import 'package:hood/services/blob_services.dart';

class FlyersGrid extends StatelessWidget {
  final List<Flyer> flyers;
  
  FlyersGrid(this.flyers);
  
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
                child: Text(flyers[index].title,
                  style: Theme.of(context).textTheme.caption,
                ),
              ),
            ],
          ),
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => FlyerViewer(flyers[index]))
            );
          }
        );
      }
    ));
  }
}
