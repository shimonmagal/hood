import 'package:flutter/material.dart';
import 'package:hood/model/flyer.dart';

class FlyerViewer extends StatelessWidget {
  final Flyer flyer;
  
  FlyerViewer(this.flyer);
  
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Column(
          children: <Widget>[
            Text(flyer.title, style: Theme.of(context).textTheme.headline),
            Container(
              margin: const EdgeInsets.all(10.0),
              child: Image.network("http://10.0.2.2:8080/api/file?key=${flyer.imageKey}", height: 300.0)
            ),
            Text(flyer.description, style: Theme.of(context).textTheme.body1),
          ]
        )
      ),
    );
  }
}
