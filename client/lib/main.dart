import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:image_picker/image_picker.dart';
import 'package:progress_dialog/progress_dialog.dart';

void main() {
  runApp(MyApp());
}

class Flyer {
  final String title;
  final String description;
  final String imageKey;
  
  Flyer(this.title, this.description, this.imageKey);
  
  factory Flyer.fromJson(Map json) {
    return Flyer(json['title'], json['description'], json['imageKey']);
  }
}

Future<List<Flyer>> fetchFlyers() async {
  final response = await http.get('http://10.0.2.2:8080/api/flyers');
  
  if (response.statusCode == 200) {
    var flyersJson = json.decode(response.body) as List<dynamic>;
    
    try {
      List<Flyer> result = flyersJson.map((flyerJson) => Flyer.fromJson(flyerJson)).toList();
      
      return result;
    } catch (e) {
      print (e);
      throw Exception('Failed to parse flyers');
    }
    
  } else {
    throw Exception('Failed to load flyers');
  }
}

class MyApp extends StatefulWidget {
  MyApp({Key key}) : super(key: key);

  @override
  MyAppState createState() => MyAppState();
}

class MyAppState extends State<MyApp> {
  Future<List<Flyer>> flyers;
  
  @override
  void initState() {
    super.initState();
    flyers = fetchFlyers();
  }
  
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text("Board"),
        ),
        body: FutureBuilder(
          future: flyers,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              List<Flyer> flyers = snapshot.data;
              
              return FlyersView(flyers);
            }
            
            return CircularProgressIndicator();
          }
        ),
        floatingActionButton: Builder(
          builder: (BuildContext context) {
            return FloatingActionButton(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute(builder: (context) => AddFlyerForm())
                );
              },
              child: Icon(Icons.add_circle),
              backgroundColor: Colors.blue,
            );
          }
        ),
      ),
    );
  }
}

class FlyersView extends StatelessWidget {
  final List<Flyer> flyers;
  
  FlyersView(this.flyers);
  
  Widget build(BuildContext context) {
    return GridView.count(
      crossAxisCount: 3,
      crossAxisSpacing: 4.0,
      mainAxisSpacing: 8.0,
      children: List.generate(flyers.length, (index) {
        return InkWell(
          child: Column(
            children: <Widget>[
              Expanded(
                child: Image.network(flyers[index].imageKey)
              ),
              Expanded(
                child: Text(flyers[index].title,
                  style: Theme.of(context).textTheme.caption,
                ),
              ),
            ],
          ),
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => FlyerView(flyers[index]))
            );
          }
        );
      }
    ));
  }
}

class FlyerView extends StatelessWidget {
  final Flyer flyer;
  
  FlyerView(this.flyer);
  
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Column(
          children: <Widget>[
            Text(flyer.title, style: Theme.of(context).textTheme.headline),
            Container(
              margin: const EdgeInsets.all(10.0),
              child: Image.network(flyer.imageKey, height: 300.0)
            ),
            Text(flyer.description, style: Theme.of(context).textTheme.body1),
          ]
        )
      ),
    );
  }
}

class AddFlyerForm extends StatefulWidget {
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
    
    try {
      var uri = Uri.parse('http://10.0.2.2:8080/api/file');
      var request = http.MultipartRequest('PUT', uri)
        ..files.add(await http.MultipartFile.fromPath('file', image.path));
      
      var uploadImageResponse = await request.send();
      
      if (uploadImageResponse.statusCode != 200) {
        await pr.hide();
        setState(() {
          _errorStatus = "Error uploading image: ${uploadImageResponse.statusCode}";
        });
        return;
      }
      
      final imageKey = await uploadImageResponse.stream.bytesToString();
      
      if (imageKey.isEmpty) {
        await pr.hide();
        setState(() {
          _errorStatus = "Missing image key: ${uploadImageResponse.statusCode}";
        });
        return;
      }
      
      final response = await http.post('http://10.0.2.2:8080/api/flyers',
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(<String, String>{
          'title': title,
          'description': description,
          'imageKey': imageKey
        }),
      );
      
      if (response.statusCode != 200) {
        await pr.hide();
        setState(() {
          _errorStatus = "Error saving flyer: ${response.statusCode}";
        });
        return;
      }
      
      pr.hide().whenComplete(() {
        Navigator.of(context).pop();
      });
    } catch (e) {
      await pr.hide();
      setState(() {
        _errorStatus = "Error saving flyer: ${e}";
      });
      print("ERROR: $e");
    }
  }
  
  Future<void> getImage() async {
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);
    setState(() {
      _image = image;
    });
  }
}
