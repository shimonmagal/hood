import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_auth_buttons/flutter_auth_buttons.dart';
import 'package:hood/screens/login_screen.dart';
import 'package:global_configuration/global_configuration.dart';

class GoogleLoginView extends StatefulWidget implements LoginProvider {
  final LOGIN_TYPES loginType;
  final Function logInCallback;
  final Function logOutCallback;
  final GoogleSignIn googleSignIn = GoogleSignIn(
    scopes: ['email'],
  );

  GoogleLoginView(this.loginType, this.logInCallback, this.logOutCallback);

  @override
  State<StatefulWidget> createState() {
    return GoogleLoginViewState();
  }

  @override
  logout(BuildContext context) {
    googleSignIn.signOut();

    this.logOutCallback(context);
  }
}

class GoogleLoginViewState extends State<GoogleLoginView> {
  Map userProfile;

  GoogleLoginViewState();

  _loginWithGoogle(context) async {
    final GoogleSignInAccount googleSignInAccount =
        await widget.googleSignIn.signIn();

    if (googleSignInAccount == null) {
      return;
    }

    final GoogleSignInAuthentication googleSignInAuthentication =
        await googleSignInAccount.authentication;

    final serverResponse = await http.get(
        '${GlobalConfiguration().getString("apiUrl")}/google?id_token=${googleSignInAuthentication.idToken}');

    if (serverResponse.statusCode != 200) {
      widget.logOutCallback(context);

      return;
    }

    final profile = jsonDecode(serverResponse.body);

    setState(() {
      userProfile = profile;
    });

    widget.logInCallback(
        LOGIN_TYPES.GOOGLE, serverResponse.headers['session'], userProfile);
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
