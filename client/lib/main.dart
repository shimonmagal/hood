import 'package:flutter/material.dart';
import "screens/main_screen.dart";
import 'package:hood/services/flyer_services.dart';
import 'package:hood/screens/login_screen.dart';
import 'package:hood/screens/add_flyer_screen.dart';
import 'package:hood/screens/flyer_viewer_screen.dart';

void main() {
  runApp(MaterialApp(
    initialRoute: '/',
    routes: {
        '/': (context) => MainScreen(),
        '/login': (context) => LoginView()
    }
  ));
}
