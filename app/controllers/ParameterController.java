package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.Institution;
import models.OrderOfService;
import models.Service;
import models.Step;
import play.data.binding.Binder;
import play.data.binding.ParamNode;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

@CRUD.For(models.Parameter.class)
public class ParameterController extends CRUD {

}
