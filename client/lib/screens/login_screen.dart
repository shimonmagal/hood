import 'dart:convert';

import 'package:hood/screens/login/facebook.dart';
import 'package:hood/screens/login/google.dart';
import 'package:flutter/material.dart';
import 'login/session.dart';
import 'package:http/http.dart' as http;
import 'package:global_configuration/global_configuration.dart';

class LoginView extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return LoginViewState();
  }
}

class LoginViewState extends State<LoginView> {
  LOGIN_TYPES loginType = LOGIN_TYPES.NONE;
  FacebookLoginView facebookProvider;
  GoogleLoginView googleProvider;

  @override
  void initState() {
    super.initState();

    this.facebookProvider =
        new FacebookLoginView(this.loginType, this.logIn, this.logOut);
    this.googleProvider =
        new GoogleLoginView(this.loginType, this.logIn, this.logOut);

    useSessionIfPossible();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[this.facebookProvider, this.googleProvider],
        ),
      ),
    );
  }

  logIn(LOGIN_TYPES loginType, String session, Map<String, Object> user) async {
    this.setState(() {
      this.loginType = loginType;
    });

    String username = user["email"];

    if (await SessionHelper.internal()
        .saveSession(new Session(session, loginType, username))) {
      goToApp(context, user, loginType);
    }
  }

  goToApp(context, userData, loginType) {
    var provider;

    switch (loginType) {
      case LOGIN_TYPES.NONE:
        return;
      case LOGIN_TYPES.FACEBOOK:
        provider = this.facebookProvider;
        break;
      case LOGIN_TYPES.GOOGLE:
        provider = this.googleProvider;
        break;
    }

    Navigator.pushNamed(context, '/',
        arguments: {"userData": userData, "provider": provider});
  }

  logOut(context) async {
    var response = await http.delete(
        '${GlobalConfiguration().getString("apiUrl")}/session',
        headers: await SessionHelper.internal().authHeaders());

    if (response.statusCode != 200) {
      return;
    }

    bool result = await SessionHelper.internal().removeSession();

    if (result) {
      Navigator.pushNamed(context, '/login');
    }
  }

  void useSessionIfPossible() async {
    var session = await SessionHelper.internal().getSession();

    if (session == null) {
      return;
    }

    var response = await http.get(
        '${GlobalConfiguration().getString("apiUrl")}/session',
        headers: {"session": session.session});

    if (response.statusCode == 200) {
      logIn(session.loginType, session.session, json.decode(response.body));
    }
  }
}

abstract class LoginProvider {
  logout(BuildContext context);
}

enum LOGIN_TYPES { NONE, GOOGLE, FACEBOOK }
