import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(MyApp());
}

class Flyer {
  final String title;
  final String description;
  final String imageUrl;
  
  Flyer(this.title, this.description, this.imageUrl);
  
  factory Flyer.fromJson(Map json) {
    return Flyer(json['title'], json['description'], json['imageUrl']);
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
                child: Image.network(flyers[index].imageUrl)
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
              child: Image.network(flyer.imageUrl, height: 300.0)
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
  String existingPhotoUrl;
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Add Flyer')), 
      body: Form(
        key: _formKey,
        child: Column(
          children: <Widget>[
            TextFormField(
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

            //FormField<String>(
            //  builder:
            //),
            
            new Container(
              padding: const EdgeInsets.only(left: 40.0, top: 20.0),
              child: RaisedButton(
                child: Text('Add Flyer'),
                onPressed: () {
                  if (_formKey.currentState.validate()) {
                    print ("Pressed");
                  }
                }
              ),
            ),
          ]
        )
      ),
    );
  }
}
