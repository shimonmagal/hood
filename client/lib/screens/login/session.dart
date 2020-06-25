import 'dart:async';
import 'dart:collection';
import 'dart:io' as io;

import 'package:hood/screens/login_screen.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path_provider/path_provider.dart';
import 'package:flutter/material.dart';

class SessionHelper {
  static final SessionHelper _instance = new SessionHelper.internal();

  static const String DB_FILENAME = "session.db";
  static const String TABLE_NAME = "session_attributes";
  static const String KEY_COLUMN = "k";
  static const String VALUE_COLUMN = "val";

  factory SessionHelper() => _instance;

  static Database _db;

  Future<Database> get db async {
    if (_db != null) return _db;
    _db = await initDb();
    return _db;
  }

  SessionHelper.internal();

  initDb() async {
    io.Directory documentsDirectory = await getApplicationDocumentsDirectory();
    String path = join(documentsDirectory.path, DB_FILENAME);
    var theDb = await openDatabase(path, version: 1, onCreate: _onCreate);
    return theDb;
  }

  void _onCreate(Database db, int version) async {
    // When creating the db, create the table

    await db.execute(
        "CREATE TABLE if not exists $TABLE_NAME($KEY_COLUMN TEXT PRIMARY KEY, $VALUE_COLUMN TEXT)");
  }

  Future<bool> saveSession(Session session) async {
    var dbClient = await db;
    Map<String, String> map = new HashMap();
    map.putIfAbsent(KEY_COLUMN, () => "session");
    map.putIfAbsent(VALUE_COLUMN, () => session.session);

    int res = await dbClient.insert(TABLE_NAME, map,
        conflictAlgorithm: ConflictAlgorithm.replace);

    if (res <= 0) {
      return false;
    }

    map = new HashMap();
    map.putIfAbsent(KEY_COLUMN, () => "login_type");
    map.putIfAbsent(VALUE_COLUMN, () => session.loginType.toString());

    res = await dbClient.insert(TABLE_NAME, map,
        conflictAlgorithm: ConflictAlgorithm.replace);

    if (res <= 0) {
      return false;
    }

    map = new HashMap();
    map.putIfAbsent(KEY_COLUMN, () => "username");
    map.putIfAbsent(VALUE_COLUMN, () => session.username.toString());

    res = await dbClient.insert(TABLE_NAME, map,
        conflictAlgorithm: ConflictAlgorithm.replace);

    return res > 0;
  }

  Future<Session> getSession() async {
    initDb();

    var dbClient = await db;

    var result = await dbClient.rawQuery("select * from $TABLE_NAME");

    if (result.length == 0) {
      return null;
    }

    String session;
    var loginType;
    String username;

    for (var row in result) {
      if (row[KEY_COLUMN] == "session") {
        session = row[VALUE_COLUMN];
      } else if (row[KEY_COLUMN] == "login_type") {
        switch (row[VALUE_COLUMN]) {
          case "LOGIN_TYPES.GOOGLE":
            loginType = LOGIN_TYPES.GOOGLE;
            break;
          case "LOGIN_TYPES.FACEBOOK":
            loginType = LOGIN_TYPES.FACEBOOK;
            break;
          default:
            loginType = LOGIN_TYPES.NONE;
        }
      } else if (row[KEY_COLUMN] == "username") {
        username = row[VALUE_COLUMN];
      }
    }

    return new Session(session, loginType, username);
  }

  Future<bool> removeSession() async {
    var dbClient = await db;
    int res = await dbClient.delete(TABLE_NAME, where: "1 = 1");

    return res > 0;
  }

  Future<Map<String, String>> authHeaders(
      {Map<String, String> originalHeaders}) async {
    var headers = new Map<String, String>();

    if (originalHeaders != null) {
      headers.addAll(originalHeaders);
    }

    var session = await SessionHelper.internal().getSession();

    if (session == null) {
      return {};
    }

    headers.addAll(<String, String>{"session": session.session});

    return headers;
  }
}

class Session {
  String session;
  LOGIN_TYPES loginType;
  String username;

  Session(this.session, this.loginType, this.username);
}
