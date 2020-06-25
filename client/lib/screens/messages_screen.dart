import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:global_configuration/global_configuration.dart';
import 'package:hood/services/blob_services.dart';
import 'package:intl/intl.dart';
import 'package:web_socket_channel/io.dart';

import 'login/session.dart';
import 'messages_conversation_screen.dart';

class MessagesForm extends StatefulWidget {
  @override
  MessagesFormState createState() {
    return new MessagesFormState();
  }
}

class MessagesFormState extends State<MessagesForm> {
  Future<List<dynamic>> conversations;
  IOWebSocketChannel webSocketChannel;

  @override
  void initState() {
    super.initState();
    webSocketChannel = IOWebSocketChannel.connect(
        '${GlobalConfiguration().getString("websocketUrl")}/conversations');

    asyncInitState();
  }

  Future<void> asyncInitState() async {
    webSocketChannel = IOWebSocketChannel.connect(
        '${GlobalConfiguration().getString("websocketUrl")}/conversations',
        headers: {
          "session": (await SessionHelper.internal().getSession()).session
        });
    webSocketChannel.sink.add(json.encode({}));
    webSocketChannel.stream.listen((jsonData) {
      print(jsonData);
      this.setState(() {
        conversations = asyncParse(jsonData);
      });
    });
  }

  Future<List> asyncParse(jsonData) async {
    var y = json.decode(jsonData);

    print("--->");
    print(y);

    return y;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FutureBuilder(
          future: Future.wait([conversations]),
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              return ListView.builder(
                  padding: EdgeInsets.all(10.0),
                  itemBuilder: (context, index) =>
                      buildItem(context, snapshot.data[0][index]),
                  itemCount: snapshot.data[0].length);
            } else {
              return CircularProgressIndicator();
            }
          }),
    );
  }

  Widget buildItem(BuildContext context, dynamic conversation) {
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
                  toPhotoUrl(conversation),
                  width: 50.0,
                  height: 50.0,
                )),
            Flexible(
              child: Container(
                child: Column(
                  children: <Widget>[
                    Container(
                      child: Text(
                        conversation["title"] != null
                            ? conversation["title"]
                            : conversation["customerUser"],
                        style: TextStyle(color: Colors.black, fontSize: 14),
                      ),
                      alignment: Alignment.centerLeft,
                      margin: EdgeInsets.fromLTRB(10.0, 0.0, 0.0, 5.0),
                    ),
                    Container(
                      child: Text(
                        conversation["text"],
                        style: TextStyle(color: Colors.black45, fontSize: 14),
                      ),
                      alignment: Alignment.centerLeft,
                      margin: EdgeInsets.fromLTRB(10.0, 0.0, 0.0, 5.0),
                    ),
                    Container(
                      child: Text(
                        toTime(conversation["date"]),
                        style: TextStyle(color: Colors.black45, fontSize: 10),
                      ),
                      alignment: Alignment.centerRight,
                      margin: EdgeInsets.fromLTRB(10.0, 0.0, 0.0, 5.0),
                    )
                  ],
                ),
                margin: EdgeInsets.only(left: 20.0),
              ),
            ),
          ],
        ),
        onPressed: () {
          Navigator.of(context).push(MaterialPageRoute(
              builder: (context) => ConversationForm(
                  conversation["flyerId"], "", conversation["customerUser"])));
        },
        color: Colors.grey,
        padding: EdgeInsets.fromLTRB(25.0, 10.0, 25.0, 10.0),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(0.0)),
      ),
      margin: EdgeInsets.only(bottom: 2.0, left: 0.0, right: 0.0),
    );
  }

  String toPhotoUrl(conversation) {
    String photoUrl = conversation["photoUrl"];

    if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
      return photoUrl;
    }

    return BlobServices.getBlobUrl(photoUrl);
  }

  String toTime(epochMillis) {
    DateTime dateTime =
        DateTime.fromMillisecondsSinceEpoch(epochMillis).toUtc().toLocal();

    final now = DateTime.now();
    bool isToday = (now.day == dateTime.day &&
        now.month == dateTime.month &&
        now.year == dateTime.year);

    if (isToday) return new DateFormat('hh:mm aa').format(dateTime).toString();

    return new DateFormat('dd/MM/yy').format(dateTime).toString();
  }
}
