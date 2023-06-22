package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnLookupDetail;
import com.simian.medallion.model.GnLookupGroup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Lookups extends MedallionController {
	private static Logger log = Logger.getLogger(Lookups.class);
	
	@Before(unless={"list", "save", "confirm"})
	public static void setup() {
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
		
		List<SelectItem> currencyOpt = generalService.listCurrenciesAsSelectItem2();
		renderArgs.put("currencyOpt", currencyOpt);
	}

	@Check("administration.lookup")
	public static void group() {
		log.debug("group");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		render();
	}
	
	@Check("administration.lookup")
	public static void paging(Paging page){
		log.debug("paging "+page);
		
		page.addParams("1", page.EQUAL, 1);
		page.addParams(Helper.searchAll("(d.lookupGroup||d.systemLookup)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = generalService.pagingLookupGroup(page);

		renderJSON(page);
	}

	@Check("administration.lookup")
	public static void list(String group) {
		log.debug("list group="+group);
		
		GnLookupGroup lookupGroup = generalService.getLookupGroup(group);
		List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, group);

		String mode = "mix";
		if (lookupGroup.getSystemLookup()) {
			renderArgs.put("noNewData", true);			
		}
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		render(lookups, group, mode);
	}
	
	@Check("administration.lookup")
	public static void listpartial(String group) {
		log.debug("listpartial group="+group);
		
		GnLookupGroup lookupGroup = generalService.getLookupGroup(group);
		List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, group);
		String mode = "mix";
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
		renderArgs.put("canGoBack", false);
		if (lookupGroup.getSystemLookup()) {
			renderArgs.put("noNewData", true);			
		}
		renderArgs.put("isPartial", true);
		
		render("Lookups/list.html",lookups, group, mode);
	}
	
	@Check("administration.lookup")
	@Transactional
	public static void viewpartial(String id, String group, String status) {
		log.debug("viewpartial id="+id+", group="+group+", status="+status);
		
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		id = changeId(id, false);
		GnLookup lookup = generalService.getLookupWithDetail(id);
		lookup = reorderLookupDetail(lookup);		
		status = lookup.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
		
		mappedUdfMaster(lookup);
		
		Boolean isPartial = true;
		renderArgs.put( "isPartial", isPartial );
		render("Lookups/detail.html",id, group, mode, lookup, status);
		
		view(id, group, status);
	}
		
	@Check("administration.lookup")
	@Transactional
	public static void view(String id, String group, String status) {
		log.debug("view id="+id+", group="+group+", status="+status);
		
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		id = changeId(id, false);
		GnLookup lookup = generalService.getLookupWithDetail(id);
		lookup = reorderLookupDetail(lookup);
		status = lookup.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		
		mappedUdfMaster(lookup);
		render("Lookups/detail.html",id, group, mode, lookup, status);
	}
	
	@Check("administration.lookup")
	public static void entry(String group) {
		log.debug("entry group="+group);
		
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnLookup lookup = new GnLookup();
		GnLookupGroup lookupGroup = generalService.getLookupGroup(group);
		lookup.setLookupGroup( lookupGroup );
		lookup.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		mappedUdfMaster( lookup );
		
		if (lookup.getLookupId() == null) {
			if (lookup.getLookupGroup() != null && lookup.getLookupGroup().getLookupGroup() != null) {
				List<GnUdfMaster> gnUdfMasters = generalService.listUdfMastersByLookupGroup(lookup.getLookupGroup().getLookupGroup());
				
				Set<GnLookupDetail> detailSet = new HashSet<GnLookupDetail>();
				for(GnUdfMaster gnUdfMaster: gnUdfMasters) {
					GnLookupDetail detail = new GnLookupDetail();
					detail.setUdfMaster(gnUdfMaster);
					detail.setDetailValue("");
					detail.setLookup(lookup);
					detailSet.add(detail);
				}
				lookup.setDetail(detailSet);
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		renderTemplate("Lookups/detail.html", group, mode, lookup);
	}
	
	@Check("administration.lookup")
	public static void editpartial(String id, String group) {
		log.debug("editpartial id"+id+", group="+group);
		
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		id = changeId(id, false);
		GnLookup lookup = generalService.getLookupWithDetail(id);
		lookup = reorderLookupDetail(lookup);
		String status = lookup.getRecordStatus();
		
		mappedUdfMaster( lookup );
		
		Boolean isPartial = true;
		renderArgs.put( "isPartial", isPartial );
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
		render("Lookups/detail.html", id, group, mode, lookup, status);
	}

	@Check("administration.lookup")
	private static String changeId(String id, Boolean rollback){
		log.debug("changeId id="+id+", rollback="+rollback);
		
		if (rollback){
			if (id != null && id.contains("/")) { id = id.replace("/", "|"); }
		}else{
			if (id != null && id.contains("|")) { id = id.replace("|", "/"); }
		}
		return id;
	}

	@Check("administration.lookup")
	private static void mappedUdfMaster(GnLookup lookup){
		log.debug("mappedUdfMaster lookup="+lookup);
		
		List<GnUdfMaster> udfExternal = new ArrayList<GnUdfMaster>();
		if (lookup.getLookupGroup() != null) {			
			udfExternal = generalService.listUdfMastersByExternal(lookup.getLookupGroup().getLookupGroup());
			renderArgs.put("udfExternal", udfExternal);
		}
		
		Map<Object, GnLookupDetail> mappedDetail = new HashMap<Object, GnLookupDetail>();
		Set<GnLookupDetail> details = lookup.getDetail();
		for (GnLookupDetail detail_ : details){
			mappedDetail.put( detail_.getUdfMaster().getUdfMasterKey(), detail_);
			if (detail_.getUdfMaster().getLookupGroupExternal() != null) {
				detail_.getUdfMaster().setOptions(generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, detail_.getUdfMaster().getLookupGroupExternal()));
			}
		}

		for (GnUdfMaster udf_ : udfExternal){
			if (mappedDetail.get( udf_.getUdfMasterKey() ) == null) {
				GnLookupDetail detail = new GnLookupDetail();
				detail.setDetailValue("");
				detail.setUdfMaster(udf_);
				detail.setLookup(lookup);
				lookup.getDetail().add(detail);
			}
		}
	}
	
	@Check("administration.lookup")
	private static GnLookup reorderLookupDetail(GnLookup lookup){
		log.debug("reorderLookupDetail lookup="+lookup);
		
		if (lookup.getDetail() != null) {
			TreeSet<GnLookupDetail> tmpNewTree = new TreeSet<GnLookupDetail>(lookup.getDetail());
			lookup.setDetail(tmpNewTree);
		}
		
		return lookup;
	}

	@Check("administration.lookup")
	public static void edit(String id, String group) {
		log.debug("edit id="+id+", group="+group);
		
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		id = changeId(id, false);
		
		//List<SelectItem> currencyOpt = generalService.listCurrenciesAsSelectItem();
		
		//udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
		
		GnLookup lookup = generalService.getLookupWithDetail(id);
		lookup = reorderLookupDetail(lookup);
		String status = lookup.getRecordStatus();
		
		mappedUdfMaster( lookup );
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		render("Lookups/detail.html", id, group, mode, lookup, status);
	}

	@Check("administration.lookup")
	public static void save(String id, String group, String mode, GnLookup lookup, String status, Boolean isPartial) {
		log.debug("save id="+id+", group="+group+", mode="+mode+", lookup="+lookup+", status="+status+", isPartial="+isPartial);
		
		if (status.equals(LookupConstants.__RECORD_STATUS_REJECTED)) {
			lookup.setIsActive(false);
		}
		
		for (GnLookupDetail det : lookup.getDetail()) {
			GnUdfMaster udfMaster = generalService.getUdfMaster(det.getUdfMaster().getUdfMasterKey());
			if (udfMaster.getRequired() != null && udfMaster.getRequired().booleanValue()) {
				validation.required(udfMaster.getLabel()+" is", det.getDetailValue());	
			}
		}
		
		if (lookup != null) {
			validation.required("Code is", lookup.getLookupCode());
			validation.required("Description is", lookup.getLookupDescription());
			Pattern pattern = Pattern.compile("[a-z_A-Z_0-9]*");
			 
		    Matcher matcher = pattern.matcher(lookup.getLookupCode());
		    if (matcher.matches()) {
		         //tidak ada special char
		    } else {
		    // throw new Exception("Code can only be filled using alphanumeric and underscore");
		    	validation.addError(null, "Code can only be filled using alphanumeric and underscore", lookup.getLookupCode());
		    }
		    
			if (validation.hasErrors()) {
				List<SelectItem> operators = UIHelper.yesNoOperators();
				renderArgs.put("operators", operators);
				if( isPartial != null ){
					if( isPartial ){
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
					}else{
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
					}
				}else{
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
				}
				render("Lookups/detail.html", lookup, group, mode,operators, status, isPartial);
			} else {
				if (lookup.getLookupSequence() == null) {
					lookup.setLookupSequence(0);
				}
				id = lookup.getLookupId();
				log.debug("ID SAVE 2>> " +id);
				
				lookup = reorderLookupDetail(lookup);
				id = changeId(id, false);
				serializerService.serialize(session.getId(), id, lookup);
				confirming(id, group, mode, status, isPartial);
			}
		} else {
			flash.error(ExceptionConstants.PARAMETER_NULL, lookup);
		}
	}
	
	@Check("administration.lookup")
	public static void confirming(String id, String group, String mode, String status, Boolean isPartial) {
		log.debug("confirming id="+id+", group="+group+", mode="+mode+", status="+status+", isPartial="+isPartial);
		
		confirmingHelper(id, group, mode, status, isPartial);
	}
	
	@Check("administration.lookup")
	private static void confirmingHelper(String id, String group, String mode, String status, Boolean isPartial) {
		log.debug("confirmingHelper id="+id+", group"+group+", mode="+mode+", status="+status+", isPartial="+isPartial);
		
		renderArgs.put("confirming", true);
		id = changeId(id, false);
		GnLookup lookup = serializerService.deserialize(session.getId(), id, GnLookup.class);
		id = changeId(id, true);
		lookup = reorderLookupDetail(lookup);
		log.debug("IS ACTIVE CONFIRMING >>>" +lookup.getIsActive());
		log.debug("ID Confirming 2>>> " +id);
		if (isPartial != null) {
			if (isPartial){
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
			}else{
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
			}
		}else{
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		}
		
		render("Lookups/detail.html", id, group, mode, lookup, status, isPartial);
	}
	
	@Check("administration.lookup")
	public static void confirmingpartial(String id, String group, String mode, String status, Boolean isPartial) {
		log.debug("confirmingpartial id="+id+", group="+group+", mode="+mode+", status="+status+", isPartial="+isPartial);
		
		confirmingHelper(id, group, mode, status, isPartial);
	}

	@Check("administration.lookup")
	public static void confirm(String id, String group, String mode, GnLookup lookup, String status, Boolean isPartial) {
		log.debug("confirm id="+id+", group="+group+", mode="+mode+", lookup="+lookup+", status="+status+", isPartial="+isPartial);
		
		try {
			List<SelectItem> currencyOpt = generalService.listCurrenciesAsSelectItem2();
			renderArgs.put("currencyOpt", currencyOpt);
			
			lookup.setLookupId(changeId(lookup.getLookupId(), false));
			lookup = reorderLookupDetail(lookup);
			List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, lookup.getLookupGroup().getLookupGroup());
			for (GnLookup lookupInTable : lookups){
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)){
					if (lookupInTable.getLookupGroup().getLookupGroup().equals(lookup.getLookupGroup().getLookupGroup())){
						if (lookupInTable.getLookupCode().equals(lookup.getLookupCode())){
							throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
						}
					}
				}
			}
			log.debug("IS ACTIVE confirm >>" +lookup.getIsActive());
			log.debug("status confirm >>" +status);
			if (status.equals(LookupConstants.__RECORD_STATUS_REJECTED)){
				lookup.setIsActive(false);
			}
			// make sure udf master not change, we are just referred to it, not updating it, so reload back and get all the proper property
			for( GnLookupDetail detail_ : lookup.getDetail() ){
				GnUdfMaster udfMaster = generalService.getUdfMaster( detail_.getUdfMaster().getUdfMasterKey() );
				detail_.setUdfMaster(udfMaster);
			}
			generalService.saveLookup(MenuConstants.GN_LOOKUP, lookup,session.get(UIConstants.SESSION_USERNAME), "",session.get(UIConstants.SESSION_USER_KEY));
			
			if (isPartial != null){
				if (isPartial) {
					listpartial(group);
				}else{
					list(group);
				}
			}else{				
				list(group);
			}
		} catch (MedallionException e) {
			log.debug("lookupId >>" +lookup.getLookupId());
			id = lookup.getLookupId();
			if ( id != null){
				log.debug("NOTNULL");
				mode = UIConstants.DISPLAY_MODE_EDIT;
			} else {
				mode = UIConstants.DISPLAY_MODE_ENTRY;
			}
			List<SelectItem> operators = UIHelper.yesNoOperators();
			renderArgs.put("operators", operators);
			renderArgs.put("confirming", true);
			flash.error("Lookup Code : ' "+ lookup.getLookupCode() +" ' " + Messages.get(e.getMessage()));
			if( isPartial != null ){
				if(isPartial){
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
				}else{
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
				}
			}else{				
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
			}			
			render("Lookups/detail.html", id, group, mode, lookup, status, isPartial);
		}
	}
	
	@Check("administration.lookup")
	public static void back(String id, String mode, String group, String status, Boolean isPartial) {
		log.debug("back id="+id+", mode="+mode+", group="+group+", status="+status+", isPartial="+isPartial);
		
		if (mode==null){
			id = "";
			mode = UIConstants.DISPLAY_MODE_ENTRY;
		} else {
			mode = UIConstants.DISPLAY_MODE_EDIT;
		}

		id = changeId(id, false);
		log.debug("Status back >>> " +status+"---");
		GnLookup lookup = serializerService.deserialize(session.getId(), id, GnLookup.class);		
		lookup = reorderLookupDetail(lookup);
		
		log.debug("ID>>" +id);
		if (isPartial != null) {
			if(isPartial){
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP_PAYMENT));
			}else{
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
			}
		}else{				
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_LOOKUP));
		}			
		render("Lookups/detail.html",id, lookup, mode, group, status, isPartial);
	}
	
	@Check("administration.lookup")
	public static void delete(String id) {
		log.debug("delete id="+id);
		
		try {
			generalService.deleteLookup(id);
		} catch (MedallionException ex) {
			renderText(ExceptionConstants.DATA_REFERRED);
		}
	}
	
	@Check("administration.lookup")
	public static void saveGroup(String mode, GnLookup lookup, GnLookupGroup group){
		log.debug("saveGroup mode="+mode+", lookup"+lookup+", group="+group);
		
		try {
			Pattern pattern = Pattern.compile("[a-z_A-Z_0-9]*");
			 
		     Matcher matcher = pattern.matcher(group.getLookupGroup());
		     if (matcher.matches()) {
		         //tidak ada special char
		     } else {
		    	 throw new MedallionException("Code can only be filled using alphanumeric and underscore");
		      }
			// Validasi data duplicate for Lookup Group
		     List<GnLookupGroup> lookupGroups = generalService.listLookupGroups();
			for (GnLookupGroup lookupGroupInTable : lookupGroups){
				if (lookupGroupInTable.getLookupGroup().equals(group.getLookupGroup())){
					throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
				}
			}
			generalService.saveLookupGroup(group);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			renderJSON(result);
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error", Messages.get(ExceptionConstants.DATA_DUPLICATE, group.getLookupGroup()));
			renderJSON(result);
		}
	}
	
	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval taskId="+taskId+", group="+group+", keyId="+keyId+", operation="+operation+", maintenanceLogKey="+maintenanceLogKey+", from="+from);
		
		try {			
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnLookup lookup = json.readValue(maintenanceLog.getNewData(), GnLookup.class);
			group = lookup.getLookupGroup().getLookupGroup();
			validation.clear();
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			lookup = reorderLookupDetail(lookup);
			render("Lookups/approval.html", lookup, mode, taskId, operation, maintenanceLogKey,group, from );
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve taskId="+taskId+", maintenanceLogKey="+maintenanceLogKey+", operation="+operation);
		
		try {
			generalService.approveLookup(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
	
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject taskId="+taskId+", maintenanceLogKey="+maintenanceLogKey+", operation="+operation);
		try {
			generalService.approveLookup(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
	}
}
}
