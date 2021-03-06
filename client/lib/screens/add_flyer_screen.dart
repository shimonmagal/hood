import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:progress_dialog/progress_dialog.dart';
import 'package:hood/services/flyer_services.dart';
import 'package:geolocator/geolocator.dart';

class AddFlyerForm extends StatefulWidget {
  Function() refreshCallback;
  
  AddFlyerForm(this.refreshCallback);
  
  @override
  AddFlyerFormState createState() {
    return AddFlyerFormState();
  }
}

class AddFlyerFormState extends State<AddFlyerForm> {
  final _formKey = GlobalKey<FormState>();
  final _title = TextEditingController();
  final _description = TextEditingController();
  File _image;
  String _errorStatus;
  ProgressDialog pr;
  
  @override
  void dispose() {
    _title.dispose();
    _description.dispose();
    super.dispose();
  }
  
  @override
  Widget build(BuildContext context) {
    pr = new ProgressDialog(context);
    pr.style(
      message: 'Uploading flyer...',
      borderRadius: 10.0,
      backgroundColor: Colors.white,
      progressWidget: CircularProgressIndicator(),
      elevation: 10.0,
      insetAnimCurve: Curves.easeInOut,
      progress: 0.0,
      maxProgress: 100.0,
      progressTextStyle: TextStyle(
        color: Colors.black, fontSize: 13.0, fontWeight: FontWeight.w400),
      messageTextStyle: TextStyle(
        color: Colors.black, fontSize: 19.0, fontWeight: FontWeight.w600)
    );
    
    return Scaffold(
      appBar: AppBar(title: Text('Add Flyer')), 
      body: Form(
        key: _formKey,
        child: SingleChildScrollView(
          child: Column(
            children: <Widget>[
              TextFormField(
                controller: _title,
                validator: (value) {
                  if (value.isEmpty) {
                    return 'Please enter title';
                  }
                  
                  return null;
                },
                decoration: const InputDecoration(
                  icon: const Icon(Icons.title),
                  hintText: 'Enter the title'
                ),
              ),
              TextFormField(
                controller: _description,
                validator: (value) {
                  if (value.isEmpty) {
                    return 'Please enter description';
                  }
                  
                  return null;
                },
                decoration: const InputDecoration(
                  icon: const Icon(Icons.description),
                  hintText: 'Enter description'
                ),
                keyboardType: TextInputType.multiline,
                maxLines: null,
              ),
              FormField<String>(
                builder: (context) => Column(
                  children: <Widget>[
                    Container(
                      margin: const EdgeInsets.all(10.0),
                      child: _image == null ? Text('No image selected.') : Image.file(_image, height: 300.0),
                    ),
                    RaisedButton(
                      child: Text('Choose an image...'),
                      onPressed: getImage,
                    )
                  ]
                )
              ),
              new Container(
                padding: const EdgeInsets.only(left: 40.0, top: 20.0),
                child: RaisedButton(
                  child: Text('Add Flyer'),
                  onPressed: () {
                    if (!_formKey.currentState.validate()) {
                      return;
                    }
                    
                    sendFlyer(_title.text, _description.text, _image);
                  }
                ),
              ),
              Visibility(
                child: Text("$_errorStatus",
                  style: TextStyle(color: Colors.red),
                ),
                visible: _errorStatus != null
              )
            ]
          ),
        )
      ),
    );
  }
  
  void sendFlyer(String title, String description, File image) async {
    await pr.show();
    
    Position position = await Geolocator().getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    
    SendFlyerResponse result = await FlyerServices.sendFlyer(
        title, description, image, position.longitude, position.latitude);
    
    if (!result.success) {
      await pr.hide();
      setState(() {
        _errorStatus = result.errorMessage;
      });
      return;
    }
    
    pr.hide().whenComplete(() {
      Navigator.of(context).pop();
      widget.refreshCallback();
    });
  }
  
  Future<void> getImage() async {
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);
    setState(() {
      _image = image;
    });
  }
}
