import 'package:dude/auth/facebook.dart';
import 'package:dude/auth/google.dart';
import 'package:dude/main.dart';
import 'package:flutter/material.dart';

class LoginView extends StatefulWidget {
  final MyAppState parent;

  LoginView(this.parent);

  @override
  State<StatefulWidget> createState() {
    return LoginViewState(this.parent);
  }
}

enum LOGIN_TYPES { NONE, GOOGLE, FACEBOOK }

class LoginViewState extends State<LoginView> {
  final MyAppState parent;
  LOGIN_TYPES loginType = LOGIN_TYPES.NONE;

  LoginViewState(this.parent);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
            body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[FacebookLoginView(this), GoogleLoginView(this)],
      ),
    )));
  }

  logIn(LOGIN_TYPES loginType) {
    this.setState(() {
      this.loginType = loginType;
    });
  }

  goToApp() {
    this.parent.setState(() {
      this.parent.loggedIn = true;
    });
  }

  logOut() {
    this.parent.setState(() {
      this.parent.loggedIn = false;
    });
  }
}
