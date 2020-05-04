import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

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
  Widget build(BuildContext ctx) {
    
 
   // List flyers = new List();
    
   // for (int i = 0; i < 100; i++) {
   //   String image = null;
      
   //   if (i % 2 == 0) {
   //     image = 'https://www.handletheheat.com/wp-content/uploads/2015/03/Best-Birthday-Cake-with-milk-chocolate-buttercream-SQUARE.jpg';
   //   } else {
   //     image = 'https://api.time.com/wp-content/uploads/2019/11/top-10-fiction-2019.jpg';
   //   }
      
   //   flyers.add(Flyer('Item $i', 'The item number $i', image));
   //}
    
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
                            style: Theme.of(ctx).textTheme.caption,
                          ),
                        ),
                      ],
                    ),
                    onTap: () {
                      Navigator.of(ctx).push(
                        MaterialPageRoute(builder: (ctx) => FlyerView(flyers[index]))
                      );
                    }
                  );
                }
              ));
            }
            
            return CircularProgressIndicator();
          }
        ),
      ),
    );
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
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Text(flyer.title, style: Theme.of(context).textTheme.headline),
            Text(flyer.description, style: Theme.of(context).textTheme.body1),
          ]
        )
      ),
    );
  }
}
