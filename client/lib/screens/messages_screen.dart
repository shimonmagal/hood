import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class MessagesForm extends StatefulWidget {
  @override
  MessagesFormState createState() {
    return new MessagesFormState();
  }
}

class MessagesFormState extends State<MessagesForm> {
  Future<List<String>> shit;

  @override
  void initState() {
    super.initState();
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FutureBuilder(
          future: Future.wait([shit]),
          builder: (context, snapshot) {
            if (1 == 1) {

              return ListView.builder(
                  padding: EdgeInsets.all(10.0),
                  itemBuilder: (context, index) => buildItem(context, index),
                  itemCount: 1
              );
            }
            else
              {
                return CircularProgressIndicator();
              }
          }
      ),
    );
  }
}

  Widget buildItem(BuildContext context, int index) {
    if (1 == 0) {
      return Container();
    }
    return Container(
      child: FlatButton(
        child: Row(
          children: <Widget>[
            Material(
                borderRadius: BorderRadius.all(Radius.circular(25.0)),
                clipBehavior: Clip.hardEdge,
                child: Image.network(
                    "https://cdn4.iconfinder.com/data/icons/little-boy/1067/Little_Boy_Black.png",
                    width: 50.0,
                    height: 50.0,
                )
            ),
            Flexible(
              child: Container(
                child: Column(
                  children: <Widget>[
                    Container(
                      child: Text(
                        'Nickname: "sami"',
                        style: TextStyle(color: Colors.black),
                      ),
                      alignment: Alignment.centerLeft,
                      margin: EdgeInsets.fromLTRB(10.0, 0.0, 0.0, 5.0),
                    ),
                  ],
                ),
                margin: EdgeInsets.only(left: 20.0),
              ),
            ),
          ],
        ),
        onPressed: () {
          //          Navigator.push(
          //              context,
          //             MaterialPageRoute(
          //                 builder: (context) => Chat(
          //                  peerId: document.documentID,
          //                  peerAvatar: document['photoUrl'],
          //                )));
        },
        color: Colors.grey,
        padding: EdgeInsets.fromLTRB(25.0, 10.0, 25.0, 10.0),
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10.0)),
      ),
      margin: EdgeInsets.only(bottom: 10.0, left: 5.0, right: 5.0),
    );
}
