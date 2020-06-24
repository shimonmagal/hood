import 'package:flutter/material.dart';
import 'package:hood/model/flyer.dart';
import 'package:hood/screens/login/session.dart';
import 'package:hood/screens/messages_conversation_screen.dart';
import 'package:hood/services/blob_services.dart';
import 'package:geolocator/geolocator.dart';

class FlyerViewerForm extends StatefulWidget {
  Flyer flyer;
  
  FlyerViewerForm(this.flyer);

  @override
  FlyerViewerFormState createState() {
    return FlyerViewerFormState();
  }
}

class FlyerViewerFormState extends State<FlyerViewerForm> {
  Future<List<Placemark>> flyerLocationAddress;
  
  @override
  void initState() {
    super.initState();
    flyerLocationAddress = widget.flyer.getLocationAddress();
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Column(
          children: <Widget>[
            Text(widget.flyer.title, style: Theme.of(context).textTheme.headline),
            Container(
              margin: const EdgeInsets.all(10.0),
              child: Image.network(BlobServices.getBlobUrl(widget.flyer.imageKey), height: 300.0)
            ),
            Container(
              margin: const EdgeInsets.all(10.0),
              child: Text(widget.flyer.description, style: TextStyle(fontSize: 20)),
            ),
            FutureBuilder(
              future: flyerLocationAddress,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  List<Placemark> flyerLocationAddress = snapshot.data;
                  Placemark location = flyerLocationAddress[0];
                  
                  return Text(location.locality + " - " + location.thoroughfare, 
                    style: TextStyle(fontSize: 15, fontStyle: FontStyle.italic, color: Colors.black.withOpacity(0.6))
                  );
                }
                
                return CircularProgressIndicator();
              }
            ),
            Container(
              margin: const EdgeInsets.all(10.0),
              child: Column(
                children: <Widget>[
                  IconButton(
                    icon: new Icon(Icons.message, size: 40, color: Colors.orangeAccent),
                    onPressed: () {
                      SessionHelper.internal().getSession().then((session) =>
                        Navigator.of(context).push(
                          MaterialPageRoute(builder: (context) => ConversationForm(widget.flyer, session.username))
                        )
                      );
                    },
                  ),
                  Text("Message publisher", style: TextStyle(fontSize: 15))
                ]
              )
            ),
          ]
        )
      ),
    );
  }
}
