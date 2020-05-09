import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';
import 'package:hood/screens/login_screen.dart';

class GoogleLoginView extends StatefulWidget {
  final LoginViewState parent;

  GoogleLoginView(this.parent);

  @override
  State<StatefulWidget> createState() {
    return GoogleLoginViewState(this.parent);
  }
}

class GoogleLoginViewState extends State<GoogleLoginView> {
  Map userProfile;
  final GoogleSignIn googleSignIn = GoogleSignIn(
    scopes: ['email'],
  );
  final LoginViewState parent;

  GoogleLoginViewState(this.parent);

  _loginWithGoogle() async {
    final GoogleSignInAccount googleSignInAccount = await googleSignIn.signIn();

    if (googleSignInAccount == null) {
      return;
    }

    final GoogleSignInAuthentication googleSignInAuthentication =
        await googleSignInAccount.authentication;

    final serverResponse = await http.get(
        'http://10.0.2.2:8080/api/google?id_token=${googleSignInAuthentication.idToken}');

    if (serverResponse.statusCode != 200) {
      this.parent.logOut();

      return;
    }

    final profile = jsonDecode(serverResponse.body);

    setState(() {
      userProfile = profile;
    });

    this.parent.logIn(LOGIN_TYPES.GOOGLE);
  }

  _logout() {
    googleSignIn.signOut();

    this.parent.logOut();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: this.parent.loginType == LOGIN_TYPES.GOOGLE
          ? Column(
              children: <Widget>[
                Image.network(
                  userProfile["picture"],
                  height: 50.0,
                  width: 50.0,
                ),
                Text(userProfile["name"]),
                OutlineButton(
                    child: Text("Continue to app >>"),
                    onPressed: () {
                      this.parent.goToApp();
                    }),
                OutlineButton(
                  child: Text("Logout"),
                  onPressed: () {
                    _logout();
                  },
                )
              ],
            )
          : Center(
              child: this.parent.loginType != LOGIN_TYPES.NONE
                  ? null
                  : OutlineButton(
                      child: GoogleSignInButton(
                        onPressed: () {
                          _loginWithGoogle();
                        },
                      ),
                    ),
            ),
    );
  }
}
