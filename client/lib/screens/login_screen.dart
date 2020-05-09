import 'package:hood/screens/login/facebook.dart';
import 'package:hood/screens/login/google.dart';
import 'package:hood/screens/main_screen.dart';
import 'package:flutter/material.dart';

class LoginView extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return LoginViewState();
  }
}

enum LOGIN_TYPES { NONE, GOOGLE, FACEBOOK }

class LoginViewState extends State<LoginView> {
  LOGIN_TYPES loginType = LOGIN_TYPES.NONE;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[FacebookLoginView(this), GoogleLoginView(this)],
        ),
      ),
    );
  }

  logIn(LOGIN_TYPES loginType) {
    this.setState(() {
      this.loginType = loginType;
    });
  }

  goToApp(context) {
    Navigator.pushNamed(context, '/');
  }

  logOut(context) {
    Navigator.pushNamed(context, '/login');
  }
}
