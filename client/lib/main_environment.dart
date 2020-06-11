import 'package:flutter/material.dart';
import "screens/main_screen.dart";
import 'package:hood/services/flyer_services.dart';
import 'package:hood/screens/login_screen.dart';
import 'package:hood/screens/add_flyer_screen.dart';
import 'package:hood/screens/flyer_viewer_screen.dart';
import 'package:global_configuration/global_configuration.dart';

void main(String env) async {
  WidgetsFlutterBinding.ensureInitialized();
  await GlobalConfiguration().loadFromAsset("${env}.hood.json");
  
  runApp(MaterialApp(
    initialRoute: '/login',
    routes: {
        '/': (context) => MainScreen(),
        '/login': (context) => LoginView()
    }
  ));
}
