import 'package:hood/screens/login/facebook.dart';
import 'package:hood/screens/login/google.dart';
import 'package:hood/screens/main_screen.dart';
import 'package:flutter/material.dart';

class LoginView extends StatefulWidget {
  final MainScreenState parent;

  LoginView(this.parent);

  @override
  State<StatefulWidget> createState() {
    return LoginViewState(this.parent);
  }
}

enum LOGIN_TYPES { NONE, GOOGLE, FACEBOOK }

class LoginViewState extends State<LoginView> {
  final MainScreenState parent;
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
