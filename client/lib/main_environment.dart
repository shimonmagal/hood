import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:global_configuration/global_configuration.dart';
import 'package:hood/screens/login_screen.dart';
import "package:hood/screens/main_screen.dart";

void main(String env) async {
  WidgetsFlutterBinding.ensureInitialized();
  await GlobalConfiguration().loadFromAsset("${env}.hood.json");
  
  runApp(MaterialApp(
    initialRoute: '/login',
    routes: {
        '/': (context) => MainScreen(),
        '/login': (context) => LoginView()
    },
    supportedLocales: [
	  const Locale('en'), // English
	  const Locale('he'), // Hebrew
	]
  ));
}
