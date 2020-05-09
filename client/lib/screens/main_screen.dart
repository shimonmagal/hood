import 'package:flutter/material.dart';
import 'package:hood/model/flyer.dart';
import 'package:hood/services/flyer_services.dart';
import 'package:hood/screens/login_screen.dart';
import 'package:hood/screens/add_flyer_screen.dart';
import 'package:hood/components/flyers_grid.dart';

class MainScreen extends StatefulWidget {
  MainScreen({Key key}) : super(key: key);

  @override
  MainScreenState createState() => MainScreenState();
}

class MainScreenState extends State<MainScreen> {
  Future<List<Flyer>> flyers;
  
  @override
  void initState() {
    super.initState();
    flyers = FlyerServices.fetchFlyers();
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("Board"),
        ),
        body: FutureBuilder(
          future: flyers,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              List<Flyer> flyers = snapshot.data;
              
              return FlyersGrid(flyers);
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
      flyers = FlyerServices.fetchFlyers();
    });
  }
}
