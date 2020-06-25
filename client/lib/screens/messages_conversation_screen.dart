import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:global_configuration/global_configuration.dart';
import 'package:hood/screens/login/session.dart';
import 'package:intl/intl.dart';
import 'package:web_socket_channel/io.dart';

class ConversationForm extends StatefulWidget {
  final String flyerId;
  final String flyerUser;
  final String customerUser;

  ConversationForm(this.flyerId, this.flyerUser, this.customerUser);

  @override
  ConversationFormState createState() => ConversationFormState();
}

class ConversationFormState extends State<ConversationForm> {
  IOWebSocketChannel webSocketChannel;
  List<Map<String, dynamic>> messages;
  double height, width;
  TextEditingController textController;
  ScrollController scrollController;
  String currentUsername;

  @override
  void initState() {
    //Initializing the message list
    messages = List<Map<String, dynamic>>();
    //Initializing the TextEditingController and ScrollController
    textController = TextEditingController();
    scrollController = ScrollController();

    asyncInitState();

    super.initState();
  }

  void asyncInitState() async {
    currentUsername = (await SessionHelper.internal().getSession()).username;

    webSocketChannel = IOWebSocketChannel.connect(
        '${GlobalConfiguration().getString("websocketUrl")}/messages',
        headers: {
          "session": (await SessionHelper.internal().getSession()).session
        });
    webSocketChannel.sink.add(json.encode({
      'flyerId': widget.flyerId,
      'customerUser': widget.customerUser,
      'date': new DateTime.now().millisecondsSinceEpoch
    }));
//    Call init before doing anything with socket
    //  webSocketChannel..init();
    //   Subscribe to an event to listen to
    webSocketChannel.stream.listen((jsonData) {
      print(jsonData);
      List<dynamic> data = json.decode(jsonData);

      this.setState(() => messages.addAll(data.map((element) => {
            'date': element['date'],
            'text': element['text'],
            'senderUser': element['senderUser']
          })));

      scrollController.animateTo(
        scrollController.position.maxScrollExtent,
        duration: Duration(milliseconds: 600),
        curve: Curves.ease,
      );
    });
  }

  Widget buildSingleMessage(int index) {
    bool amITheSender = messages[index]["senderUser"] == this.currentUsername;

    return Container(
        alignment: amITheSender ? Alignment.centerLeft : Alignment.centerRight,
        child: Container(
            padding: const EdgeInsets.all(10.0),
            margin: const EdgeInsets.only(bottom: 0.0, left: 15.0, right: 15.0),
            child: ConstrainedBox(
                constraints: new BoxConstraints(minWidth: 90, minHeight: 50),
                child: DecoratedBox(
                    decoration: BoxDecoration(
                      color:
                          amITheSender ? Color(0xff25D366) : Color(0xffDCF8C6),
                      borderRadius: BorderRadius.circular(8.0),
                    ),
                    child: Stack(children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.only(
                            right: 8.0, left: 8.0, top: 8.0, bottom: 15.0),
                        child: Text(
                          messages[index]["text"],
                          style: TextStyle(color: Colors.black, fontSize: 20.0),
                        ),
                      ),
                      Positioned(
                        bottom: 5,
                        right: 10,
                        child: Text(
                          toTime(messages[index]["date"]),
                          style: TextStyle(
                              fontSize: 8,
                              color: Colors.black.withOpacity(0.6)),
                        ),
                      ),
                    ])))));
  }

  Widget buildMessageList() {
    return Container(
      height: height * 0.8,
      width: width,
      child: ListView.builder(
        controller: scrollController,
        itemCount: messages.length,
        itemBuilder: (BuildContext context, int index) {
          return buildSingleMessage(index);
        },
      ),
    );
  }

  Widget buildChatInput() {
    return Container(
      width: width * 0.7,
      padding: const EdgeInsets.all(2.0),
      margin: const EdgeInsets.only(left: 40.0),
      child: TextField(
        decoration: InputDecoration.collapsed(
          hintText: 'Send a message...',
        ),
        controller: textController,
      ),
    );
  }

  Widget buildSendButton(BuildContext context) {
    return FloatingActionButton(
      backgroundColor: Colors.deepPurple,
      onPressed: () async {
        //Check if the textfield has text or not
        if (textController.text.isNotEmpty) {
          //Send the message as JSON data to send_message event
          webSocketChannel.sink.add(json.encode({
            'text': textController.text,
            'flyerId': widget.flyerId,
            'customerUser': widget.customerUser,
            'receiverUser': (widget.customerUser == currentUsername)
                ? widget.flyerUser
                : widget.customerUser,
            'date': new DateTime.now().millisecondsSinceEpoch
          }));
          //Add the message to the list
          this.setState(() => messages.add({
                'date': new DateTime.now().millisecondsSinceEpoch,
                'text': textController.text,
                'senderUser': currentUsername
              }));
          textController.text = '';
          //Scrolldown the list to show the latest message
          scrollController.animateTo(
            scrollController.position.maxScrollExtent,
            duration: Duration(milliseconds: 600),
            curve: Curves.ease,
          );
        }
      },
      child: Icon(
        Icons.send,
        size: 30,
      ),
    );
  }

  Widget buildInputArea(BuildContext context) {
    return Container(
      height: height * 0.1,
      width: width,
      child: Row(
        children: <Widget>[
          buildChatInput(),
          buildSendButton(context),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    height = MediaQuery.of(context).size.height;
    width = MediaQuery.of(context).size.width;
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(
          children: <Widget>[
            SizedBox(height: height * 0.1),
            buildMessageList(),
            buildInputArea(context),
          ],
        ),
      ),
    );
  }

  String toTime(int epochMillis) {
    DateTime dateTime =
        DateTime.fromMillisecondsSinceEpoch(epochMillis).toUtc().toLocal();

    final now = DateTime.now();
    bool isToday = (now.day == dateTime.day &&
        now.month == dateTime.month &&
        now.year == dateTime.year);

    if (isToday) return new DateFormat('hh:mm aa').format(dateTime).toString();

    return new DateFormat('dd/MM/yy hh:mm aa').format(dateTime).toString();
  }

  @override
  void dispose() {
    this.webSocketChannel.sink.close();
    super.dispose();
  }
}
