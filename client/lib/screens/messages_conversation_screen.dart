import 'package:flutter/material.dart';
import 'package:hood/screens/add_flyer_screen.dart';
import 'package:hood/components/flyers_grid.dart';
import 'package:hood/model/position.dart';
import 'package:geolocator/geolocator.dart' as Geolocator;
import 'package:hood/model/flyer.dart';
import 'package:hood/services/flyer_services.dart';

import 'login_screen.dart';

class MessagesForm extends StatefulWidget {
  @override
  MessagesFormState createState() {
    return new MessagesFormState();
  }
}

class MessagesFormState extends State<MessagesForm> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
            Text("You are logged in as:"),
        ])
      )
    );
  }
}
