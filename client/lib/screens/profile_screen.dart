import 'package:flutter/material.dart';
import 'login_screen.dart';

class ProfileForm extends StatefulWidget {
  @override
  ProfileFormState createState() {
    return ProfileFormState();
  }
}

class ProfileFormState extends State<ProfileForm> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final Map<String, Object> arguments = ModalRoute.of(context).settings.arguments;
    final Map<String, Object> userData = arguments["userData"];
    final LoginProvider provider = arguments["provider"];

    return Scaffold(
      body: Center(
        child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
            Text("You are logged in as:"),
        Image.network(
        userData["picture"],
          height: 50.0,
          width: 50.0,
        ),
        Text(userData["email"]),
          OutlineButton(
            child: Text("Logout"),
            onPressed: () {
              provider.logout(context);
            },
          )
        ])
      )
    );
  }
}
