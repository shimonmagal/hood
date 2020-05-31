import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_facebook_login/flutter_facebook_login.dart';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';
import 'package:hood/screens/login_screen.dart';

class FacebookLoginView extends StatefulWidget implements LoginProvider{
  final LOGIN_TYPES loginType;
  final Function logInCallback;
  final Function logOutCallback;

  final facebookLogin = new FacebookLogin();

  FacebookLoginView(this.loginType, this.logInCallback, this.logOutCallback);

  @override
  logout(BuildContext context) {
    facebookLogin.logOut();
    this.logOutCallback(context);
  }

  @override
  State<StatefulWidget> createState() {
    return FacebookLoginViewState(this.loginType, this.logInCallback, this.logOutCallback, this.facebookLogin);
  }
}

class FacebookLoginViewState extends State<FacebookLoginView>{
  Map userProfile;

  final LOGIN_TYPES loginType;
  final Function logInCallback;
  final Function logOutCallback;
  final facebookLogin;

  FacebookLoginViewState(this.loginType, this.logInCallback, this.logOutCallback, this.facebookLogin);

  _loginWithFB(context) async {
    final result = await facebookLogin.logInWithReadPermissions(['email']);

    switch (result.status) {
      case FacebookLoginStatus.loggedIn:
        final token = result.accessToken.token;

        final serverResponse = await http
            .get('http://10.0.2.2:8080/api/facebook?access_token=${token}');

        if (serverResponse.statusCode != 200) {
          this.logOutCallback(context);
          return;
        }

        final profile = jsonDecode(serverResponse.body);
        
        setState(() {
          userProfile = profile;
        });

        userProfile["picture"] = userProfile["picture"]["data"]["url"];

        this.logInCallback(LOGIN_TYPES.FACEBOOK, serverResponse.headers['session'], profile);

        break;

      case FacebookLoginStatus.cancelledByUser:
        this.logOutCallback(context);
        break;
      case FacebookLoginStatus.error:
        this.logOutCallback(context);
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: OutlineButton(
              child: FacebookSignInButton(
                onPressed: () {
                  _loginWithFB(context);
                },
              ),
            ),
    );
  }
}
