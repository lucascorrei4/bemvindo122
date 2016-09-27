package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.User;
import play.mvc.With;
import util.Utils;

@CRUD.For(models.User.class)
@With(Secure.class)
public class UserController extends CRUD {
	
	
}
