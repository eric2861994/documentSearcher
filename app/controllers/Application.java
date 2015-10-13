package controllers;

import stbi.ApplicationLogic;
import formstubs.DocumentSearcherStub;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.result;
import views.html.template;

import java.io.File;
import java.io.IOException;

public class Application extends Controller {

    public static Result index() {
        Form<DocumentSearcherStub> documentSearcherStubForm = Form.form(DocumentSearcherStub.class);
        DocumentSearcherStub documentSearcherStub = new DocumentSearcherStub();
        return ok(template.render(documentSearcherStubForm));
    }

    public static Result submit() {
        DynamicForm form = Form.form().bindFromRequest();
        System.out.println(form.get("documentLocation"));
        System.out.println(form.get("queryLocation"));
        System.out.println(form.get("relevantJudgement"));
        System.out.println(form.get("stopwordLocation"));
        System.out.println(form.get("dIdf"));
        System.out.println(form.get("dTf"));
        System.out.println(form.get("dNormalization"));
        System.out.println(form.get("qIdf"));
        System.out.println(form.get("qTf"));
        System.out.println(form.get("qNormalization"));
        return ok(result.render());
    }

    public static Result indexDocuments() {

    }

    public static final ApplicationLogic appLogic = ApplicationLogic.getInstance();
}
