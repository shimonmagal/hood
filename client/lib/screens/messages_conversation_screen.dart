import 'package:flutter/material.dart';
import 'package:global_configuration/global_configuration.dart';
import 'package:hood/model/flyer.dart';
import 'package:hood/services/blob_services.dart';

import 'login/session.dart';

class ConversationForm extends StatefulWidget {
  final Flyer flyer;

  ConversationForm(this.flyer);

  @override
  ConversationFormState createState() {
    return new ConversationFormState();
  }
}

class ConversationFormState extends State<ConversationForm> {
  ConversationFormState();

  final TextEditingController textEditingController = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
            child: Stack(
              children: <Widget>[
                Positioned(
                    child: Align(
                        alignment: Alignment.topCenter,
                        child: Row(
                          children: <Widget>[
                            Image.network(
                                BlobServices.getBlobUrl(widget.flyer.imageKey),
                                height: 50.0),
                            Text(widget.flyer.title,
                                style: TextStyle(fontSize: 30)),
                            Text(widget.flyer.description,
                                style: TextStyle(fontSize: 20)),
                          ],
                        ))
                ),
                Positioned(
                  child: Align(
                    alignment: Alignment.bottomCenter,
                    child: buildInput(),
                  ),
                )
              ],
            )
        )
    );
  }

  Widget buildInput() {
    return Container(
      child: Row(
        children: <Widget>[
          // Button send image
          Material(
            child: Container(
              margin: EdgeInsets.symmetric(horizontal: 1.0),
              child: IconButton(
                icon: Icon(Icons.image),
                //          onPressed: getImage,
                color: Colors.orange,
              ),
            ),
            color: Colors.white,
          ),
          Material(
            child: Container(
              margin: EdgeInsets.symmetric(horizontal: 1.0),
              child: IconButton(
                icon: Icon(Icons.face),
                //         onPressed: getSticker,
                //         color: Colors.orange,
              ),
            ),
            color: Colors.white,
          ),

          // Edit text
          Flexible(
            child: Container(
              child: TextField(
                style: TextStyle(color: Colors.amber, fontSize: 15.0),
                controller: textEditingController,
                decoration: InputDecoration.collapsed(
                  hintText: 'Type your message...',
                  hintStyle: TextStyle(color: Colors.orange),
                ),
                //          focusNode: focusNode,
              ),
            ),
          ),

          // Button send message
          Material(
            child: Container(
              margin: EdgeInsets.symmetric(horizontal: 8.0),
              child: IconButton(
                icon: Icon(Icons.send),
                onPressed: () => onSendMessage(textEditingController.text),
                color: Colors.grey,
              ),
            ),
            color: Colors.white,
          ),
        ],
      ),
      width: double.infinity,
      height: 50.0,
      decoration: BoxDecoration(
          border: Border(top: BorderSide(color: Colors.green, width: 0.5)),
          color: Colors.white),
    );
  }

  onSendMessage(String text) {}
}
//  async {
//    final response = await http.put('${GlobalConfiguration().getString("apiUrl")}/message?date={$}',
//        headers: await SessionHelper.internal().authHeaders(originalHeaders: {'Content-Type': 'plain/text; charset=UTF-8'}),
//    body: jsonEncode(<String, dynamic>{
//  'title': title,
//  'description': description,
//  'imageKey': imageKey,
//  'location': {
//  'longitude': longitude,
//  'latitude': latitude
//  }
//  }),
//    );
//
//  }
//}
