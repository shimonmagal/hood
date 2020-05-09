import 'package:dude/auth/login.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_facebook_login/flutter_facebook_login.dart';
import 'dart:convert';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';

class FacebookLoginView extends StatefulWidget{
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

  _loginWithFB() async{
    final result = await facebookLogin.logInWithReadPermissions(['email']);

    switch (result.status) {
      case FacebookLoginStatus.loggedIn:
        final token = result.accessToken.token;

        final serverResponse = await http.get('http://10.0.2.2:8080/api/facebook?access_token=${token}');

        final profile = jsonDecode(serverResponse.body);

        setState(() {
          userProfile = profile;
        });
        this.parent.setState(() {
          this.parent.isLoggedIn = true;
        });

        break;

      case FacebookLoginStatus.cancelledByUser:
        this.parent.setState(() => this.parent.isLoggedIn = false );
        break;
      case FacebookLoginStatus.error:
        this.parent.setState(() => this.parent.isLoggedIn = false );
        break;
    }

  }

  _logout(){
    facebookLogin.logOut();
    this.parent.setState(() {
      this.parent.isLoggedIn = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Center(
        child: this.parent.isLoggedIn
            ? Column(
          children: <Widget>[
            Image.network(userProfile["picture"]["data"]["url"], height: 50.0, width: 50.0,),
            Text(userProfile["name"]),
            OutlineButton( child: Text("Continue to app >>"), onPressed: (){
              this.parent.setState(() {
                this.parent.isLoggedIn = true;
              });
            }),
            OutlineButton( child: Text("Logout"), onPressed: (){
              _logout();
            },)
          ],
        )
            : Center(
          child: OutlineButton(
            child: FacebookSignInButton(
              onPressed: () {
                _loginWithFB();
              },
            ),
          ),
        ),
    );
  }
}
