package controllers;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.results.RenderTemplate;
import play.templates.Template;
import play.templates.TemplateLoader;

public class FreeDownload extends Controller {
	private static Logger log = Logger.getLogger(FreeDownload.class);

	public static void reportDownload(String downloadfile) {
		log.debug("reportDownload. downloadfile: " + downloadfile);

		try {
			Date processDate = new Date();
			Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
			Template template = TemplateLoader.load(template("Kyc/process.html"));
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("processDate", processDate);
			templateBinding.data.putAll(args);
			templateBinding.put("session", Scope.Session.current());
			templateBinding.put("request", Http.Request.current());
			templateBinding.put("flash", Scope.Flash.current());
			templateBinding.put("params", Scope.Params.current());
			templateBinding.put("errors", play.data.validation.Validation.errors());
			RenderTemplate renderedTemplate = new RenderTemplate(template, templateBinding.data);
			String content = renderedTemplate.getContent();
			log.debug("-----------------------------------------------------------------------------");
			log.debug(content);
			log.debug("-----------------------------------------------------------------------------");
		} catch (Exception ex) {

		}

		String uploadedDirOutput = Play.configuration.getProperty("upload.reportloaderoutput");
		String fullPath = uploadedDirOutput + downloadfile;
		renderBinary(new File(fullPath), downloadfile);
	}

}
