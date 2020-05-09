import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_facebook_login/flutter_facebook_login.dart';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';
import 'package:hood/screens/login_screen.dart';

class FacebookLoginView extends StatefulWidget {
  final LoginViewState parent;

  FacebookLoginView(this.parent);

  @override
  State<StatefulWidget> createState() {
    return FacebookLoginViewState(this.parent);
  }
}

class FacebookLoginViewState extends State<FacebookLoginView> {
  Map userProfile;
  final facebookLogin = FacebookLogin();
  final LoginViewState parent;

  FacebookLoginViewState(this.parent);

  _loginWithFB(context) async {
    final result = await facebookLogin.logInWithReadPermissions(['email']);

    switch (result.status) {
      case FacebookLoginStatus.loggedIn:
        final token = result.accessToken.token;

        final serverResponse = await http
            .get('http://10.0.2.2:8080/api/facebook?access_token=${token}');

        if (serverResponse.statusCode != 200) {
          this.parent.logOut(context);
          return;
        }

        final profile = jsonDecode(serverResponse.body);
        
        setState(() {
          userProfile = profile;
        });

        this.parent.setState(() {
          this.parent.logIn(LOGIN_TYPES.FACEBOOK);
        });

        break;

      case FacebookLoginStatus.cancelledByUser:
        this.parent.logOut(context);
        break;
      case FacebookLoginStatus.error:
        this.parent.logOut(context);
        break;
    }
  }

  _logout(context) {
    facebookLogin.logOut();
    this.parent.logOut(context);
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: this.parent.loginType == LOGIN_TYPES.FACEBOOK
          ? Column(
              children: <Widget>[
                Image.network(
                  userProfile["picture"]["data"]["url"],
                  height: 50.0,
                  width: 50.0,
                ),
                Text(userProfile["name"]),
                OutlineButton(
                    child: Text("Continue to app >>"),
                    onPressed: () {
                      this.parent.goToApp(context);
                    }),
                OutlineButton(
                  child: Text("Logout"),
                  onPressed: () {
                    _logout(context);
                  },
                )
              ],
            )
          : Center(
              child: this.parent.loginType != LOGIN_TYPES.NONE
                  ? null
                  : OutlineButton(
                      child: FacebookSignInButton(
                        onPressed: () {
                          _loginWithFB(context);
                        },
                      ),
                    ),
            ),
    );
  }
}
