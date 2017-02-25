package controllers;

import java.io.File;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.Article;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.vfs.VirtualFile;

@CRUD.For(models.MailList.class)
public class MailListController extends CRUD {
	public static void listAll(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        orderBy = "id";
        order = "DESC";
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
}
