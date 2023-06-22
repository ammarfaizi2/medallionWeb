package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CpSector;
import com.simian.medallion.model.CpSectorDetail;
import com.simian.medallion.model.GnMaintenanceLog;

@With(Secure.class)
public class SecuritiesSectors extends MedallionController {
	private static Logger log = Logger.getLogger(SecuritiesSectors.class);

	private static JsonHelper jsonSectorLimitDetail = new JsonHelper().getCpSectorLimitDetailSerializer();

	@Before
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("operators", UIHelper.yesNoOperators());
	}

	@Check("administration.securitiesSectors")
	public static void list() {
		log.debug("list. ");

		List<CpSector> sectors = generalService.listSector();
		render("SecuritiesSectors/list.html", sectors);
	}

	@Check("administration.securitiesSectors")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		CpSector sector = new CpSector();
		String sectorLimitDetails = null;
		sector.setActive(new Boolean(false));
		try {
			sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
		} catch (JsonGenerationException e) {
			log.debug(e);
		} catch (JsonMappingException e) {
			log.debug(e);
		} catch (IOException e) {
			log.debug(e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
		render("SecuritiesSectors/entry.html", sector, sectorLimitDetails, mode);
	}

	@Check("administration.securitiesSectors")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpSector sector = generalService.getSector(id);
		String sectorLimitDetails = null;

		try {
			sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
		} catch (JsonGenerationException e) {
			log.debug(e);
		} catch (JsonMappingException e) {
			log.debug(e);
		} catch (IOException e) {
			log.debug(e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
		render("SecuritiesSectors/entry.html", sector, sectorLimitDetails, mode);
	}

	@Check("administration.securitiesSectors")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;

		CpSector sector = generalService.getSector(id);
		String sectorLimitDetails = null;
		try {
			sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
		} catch (JsonGenerationException e) {
			log.debug(e);
		} catch (JsonMappingException e) {
			log.debug(e);
		} catch (IOException e) {
			log.debug(e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
		render("SecuritiesSectors/entry.html", sector, sectorLimitDetails, mode);
	}

	@Check("administration.securitiesSectors")
	public static void save(CpSector sector, List<CpSectorDetail> sectorDetails, String mode) {
		log.debug("save. sector: " + sector + " sectorDetails: " + sectorDetails + " mode: " + mode);

		if (sector == null)
			entry();

		if (sectorDetails != null) {
			for (CpSectorDetail cpSectorDetail : sectorDetails) {
				sector.getSectorDetails().add(cpSectorDetail);
			}

			if (validation.errorsMap().values().containsAll(sectorDetails) == false) {
				validation.clear();
			}
		}

		validation.required("Rule Code", sector.getComplianceRule().getRuleCode());

		if ((sector.getSectorDetails() == null) || (sector.getSectorDetails().size() == 0)) {
			validation.required("Positive Securities Sector List", sector.getSectorDetails());
		}

		if ((sectorDetails != null) && (sectorDetails.size() > 0)) {
			for (CpSectorDetail cpSectorDetail : sectorDetails) {
				if (cpSectorDetail.getMaximumRange().compareTo(cpSectorDetail.getMinimumRange()) < 0) {
					validation.addError("", "Maximum for " + cpSectorDetail.getSectorLookup().getLookupCode() + " should be greater than Minimum");
				}
			}
		}

		if (validation.hasErrors()) {
			String sectorLimitDetails = null;

			if (sectorDetails == null) {
				sectorDetails = new ArrayList<CpSectorDetail>();
			}
			try {
				sectorLimitDetails = jsonSectorLimitDetail.serialize(sectorDetails);
			} catch (JsonGenerationException e) {
				log.debug(e);
			} catch (JsonMappingException e) {
				log.debug(e);
			} catch (IOException e) {
				log.debug(e);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
			render("SecuritiesSectors/entry.html", sector, sectorLimitDetails, mode);
		} else {
			Long id = sector.getSectorLimitKey();
			serializerService.serialize(session.getId(), id, sector);
			confirming(id, mode);
		}
	}

	@Check("administration.securitiesSectors")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CpSector sector = serializerService.deserialize(session.getId(), id, CpSector.class);
		String sectorLimitDetails = null;
		try {
			sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
		} catch (JsonGenerationException e) {
			log.debug(e);
		} catch (JsonMappingException e) {
			log.debug(e);
		} catch (IOException e) {
			log.debug(e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
		render("SecuritiesSectors/entry.html", sector, mode, sectorLimitDetails);
	}

	@Check("administration.securitiesSectors")
	public static void confirm(CpSector sector, List<CpSectorDetail> sectorDetails, String mode) {
		log.debug("confirm. sector: " + sector + " sectorDetails: " + sectorDetails + " mode: " + mode);

		if (sector == null)
			back(null, mode);

		try {
			if (sectorDetails != null) {
				for (CpSectorDetail cpSectorDetail : sectorDetails) {
					sector.getSectorDetails().add(cpSectorDetail);
				}
			}
			validation.clear();
			generalService.saveSector(MenuConstants.CP_SECURITY_SECTOR, sector, session.get("username"), session.get("userKey"));
			mode = UIConstants.DISPLAY_MODE_CONFIRM;

			list();
		} catch (MedallionException ex) {

			flash.error("Rule ID : ' " + sector.getComplianceRule().getRuleCode() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);

			String sectorLimitDetails = null;
			try {
				sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
			} catch (JsonGenerationException e) {
				log.debug(e);
			} catch (JsonMappingException e) {
				log.debug(e);
			} catch (IOException e) {
				log.debug(e);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
			render("SecuritiesSectors/entry.html", sector, sectorLimitDetails, mode);
		}
	}

	@Check("administration.securitiesSectors")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		CpSector sector = serializerService.deserialize(session.getId(), id, CpSector.class);
		String sectorLimitDetails = null;
		try {
			sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
		} catch (JsonGenerationException e) {
			log.debug(e);
		} catch (JsonMappingException e) {
			log.debug(e);
		} catch (IOException e) {
			log.debug(e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_SECTOR));
		render("SecuritiesSectors/entry.html", sector, sectorLimitDetails, mode);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			JsonHelper jsonPorto = new JsonHelper().getCpPortfolioLimitSerializer();
			CpSector sector = jsonPorto.deserialize(maintenanceLog.getNewData(), CpSector.class);
			String sectorLimitDetails = null;
			sectorLimitDetails = jsonSectorLimitDetail.serialize(sector.getSectorDetails());
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("SecuritiesSectors/approval.html", sector, sectorLimitDetails, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveSector(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveSector(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
