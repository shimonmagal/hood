import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';
import 'package:hood/screens/login_screen.dart';

class GoogleLoginView extends StatefulWidget implements LoginProvider{
  final LOGIN_TYPES loginType;
  final Function logInCallback;
  final Function logOutCallback;
  final GoogleSignIn googleSignIn = GoogleSignIn(
    scopes: ['email'],
  );

  GoogleLoginView(this.loginType, this.logInCallback, this.logOutCallback);

  @override
  State<StatefulWidget> createState() {
    return GoogleLoginViewState(this.loginType, this.logInCallback, this.logOutCallback, this.googleSignIn);
  }

  @override
  logout(BuildContext context) {
    googleSignIn.signOut();

    this.logOutCallback(context);
  }
}

class GoogleLoginViewState extends State<GoogleLoginView> {
  Map userProfile;

  final LOGIN_TYPES loginType;
  final Function logInCallback;
  final Function logOutCallback;
  final googleSignIn;

  GoogleLoginViewState(this.loginType, this.logInCallback, this.logOutCallback, this.googleSignIn);

  _loginWithGoogle(context) async {
    final GoogleSignInAccount googleSignInAccount = await googleSignIn.signIn();

    if (googleSignInAccount == null) {
      return;
    }

    final GoogleSignInAuthentication googleSignInAuthentication =
        await googleSignInAccount.authentication;

    final serverResponse = await http.get(
        'http://10.0.2.2:8080/api/google?id_token=${googleSignInAuthentication.idToken}');

    if (serverResponse.statusCode != 200) {
      this.logOutCallback(context);

      return;
    }

    final profile = jsonDecode(serverResponse.body);

    setState(() {
      userProfile = profile;
    });

    this.logInCallback(LOGIN_TYPES.GOOGLE, serverResponse.headers['session'], userProfile);
  }

  @override
  Widget build(BuildContext context) {
    return Center(
    	child: OutlineButton(
              child: GoogleSignInButton(
                onPressed: () {
                  _loginWithGoogle(context);
                },
              ),
            ),
    );
  }
}
