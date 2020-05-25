import 'package:flutter/material.dart';
import 'package:hood/model/flyer.dart';
import 'package:hood/services/flyer_services.dart';
import 'package:hood/screens/login_screen.dart';
import 'package:hood/screens/add_flyer_screen.dart';
import 'package:hood/components/flyers_grid.dart';
import 'package:hood/model/position.dart';
import 'package:geolocator/geolocator.dart' as Geolocator;

class MainScreen extends StatefulWidget {
  MainScreen({Key key}) : super(key: key);

  @override
  MainScreenState createState() => MainScreenState();
}

class MainScreenState extends State<MainScreen> {
  Future<List<Flyer>> flyers;
  Future<Position> positionFuture;
  Position position;
  
  @override
  void initState() {
    super.initState();
    positionFuture = getCurrentLocation(); 
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("Board"),
        ),
        body: FutureBuilder(
          future: Future.wait([flyers, positionFuture]),
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              List<Flyer> flyers = snapshot.data[0];
              position = snapshot.data[1];
              
              return FlyersGrid(flyers, position);
            }
            
            return CircularProgressIndicator();
          }
        ),
        floatingActionButton: Builder(
          builder: (BuildContext context) {
            return FloatingActionButton(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute(builder: (context) => AddFlyerForm(refreshCallback))
                );
              },
              child: Icon(Icons.add_circle),
              backgroundColor: Colors.blue,
            );
          }
        ),
      );
  }
  
  void refreshCallback() {
    setState(() {
      flyers = FlyerServices.fetchFlyers(position);
    });
  }
  
  Future<Position> getCurrentLocation() async {
    return Geolocator.Geolocator().getCurrentPosition(desiredAccuracy: Geolocator.LocationAccuracy.high).then((value) {
      Position position = new Position(value.longitude, value.latitude);
      
      setState(() {
        flyers = FlyerServices.fetchFlyers(position);
      });
      
      return position;
    });
  }
}
