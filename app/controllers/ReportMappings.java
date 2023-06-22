package controllers;

import helpers.UIConstants;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.FaReportMapping;
import com.simian.medallion.model.FaReportMappingDetail;
import com.simian.medallion.model.FaReportMappingDetailId;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.vo.SelectItem;

public class ReportMappings extends MedallionController {
	private static Logger log = Logger.getLogger(ReportMappings.class);

	@Before(only = { "entry", "edit", "save", "confirming", "confirm", "back" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> reportGroup = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._GROUP_REPORT);
		renderArgs.put("reportGroup", reportGroup);

		List<SelectItem> reportPosition = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._POSITION_REPORT);
		renderArgs.put("reportPosition", reportPosition);
	}

	@Check("administration.reportMapping")
	public static void group() {
		log.debug("group. ");

		List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._REPORT);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
		render(lookups);
	}

	@Check("administration.reportMapping")
	public static void list(String group) {
		log.debug("list. group: " + group);

		List<FaReportMapping> faReportMappings = fundService.listFaReportMappingByReportName(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
		render(faReportMappings, group);
	}

	public static void view(Long id, String group) {
		log.debug("view. id: " + id + " group: " + group);
	}

	@Check("administration.reportMapping")
	public static void entry(String group) {
		log.debug("entry. group: " + group);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaReportMapping faReportMapping = new FaReportMapping();
		faReportMapping.setReportName(new GnLookup(group));
		Set<FaReportMappingDetail> faReportMapDetails = faReportMapping.getFaReportMappingDetails();
		String reportMappingDetails = null;
		try {
			reportMappingDetails = json.writeValueAsString(faReportMapping.getFaReportMappingDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
		render("ReportMappings/detail.html", faReportMapping, group, mode, reportMappingDetails, faReportMapDetails);
	}

	@Check("administration.reportMapping")
	public static void edit(Long id, String group) {
		log.debug("edit. id: " + id + " group: " + group);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaReportMapping faReportMapping = null;
		String reportMappingDetails = null;
		if (id != null) {
			faReportMapping = fundService.getFaReportMapping(id);
			if (faReportMapping != null) {
				try {
					JsonHelper json = new JsonHelper().getFaReportMappingSerializer();
					reportMappingDetails = json.serialize(faReportMapping.getFaReportMappingDetails());
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
		render("ReportMappings/detail.html", faReportMapping, reportMappingDetails, group, mode);
	}

	@Check("administration.reportMapping")
	public static void save(String group, String mode, FaReportMapping faReportMapping, FaReportMappingDetail[] faReportMapDetails) {
		log.debug("save. group: " + group + " mode: " + mode + " faReportMapping: " + faReportMapping + " faReportMapDetails: " + faReportMapDetails);

		/*
		 * if (faReportMapDetails!=null){ for(FaReportMappingDetail rptDetail :
		 * faReportMapDetails){
		 * faReportMapping.getFaReportMappingDetails().add(rptDetail); } }
		 */

		if (faReportMapping != null) {
			String reportMappingDetails = null;

			if (faReportMapDetails != null) {
				// faReportMapping.getFaReportMappingDetails().addAll(faReportMapDetails);
				int i = 0;
				for (FaReportMappingDetail faReportMappingDetail : faReportMapDetails) {
					if (faReportMappingDetail != null) {
						if (faReportMapping.getReportKey() == null) {
							faReportMappingDetail.setId(new FaReportMappingDetailId(0, i++));
						} else {
							faReportMappingDetail.setId(new FaReportMappingDetailId(faReportMapping.getReportKey(), i + 1));
						}
						faReportMapping.getFaReportMappingDetails().add(faReportMappingDetail);
					}
				}
				/*
				 * for (int i=0; i<faReportMapDetails.size(); i++) {
				 * FaReportMappingDetail reportDetail =
				 * faReportMapDetails.get(i); if (reportDetail != null) { if
				 * (faReportMapping.getReportKey() == null){
				 * reportDetail.setId(new FaReportMappingDetailId(0, i+1)); }
				 * else { reportDetail.setId(new
				 * FaReportMappingDetailId(faReportMapping.getReportKey(),
				 * i+1)); }
				 * faReportMapping.getFaReportMappingDetails().add(reportDetail
				 * ); } }
				 */
			}
			try {
				/*
				 * if (faReportMapDetails==null){ faReportMapDetails = new
				 * ArrayList<FaReportMappingDetail>(); }
				 */
				reportMappingDetails = json.writeValueAsString(faReportMapping.getFaReportMappingDetails());
				log.debug("data >>" + reportMappingDetails);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			validation.required("ID is", faReportMapping.getReportLabelId());
			validation.required("Label Name in Report is", faReportMapping.getReportLabel());
			validation.required("Sequence No is", faReportMapping.getSequenceNo());
			validation.required("Group is", faReportMapping.getReportGroup().getLookupId());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
				render("ReportMappings/detail.html", faReportMapping, faReportMapDetails, mode, reportMappingDetails, group);
			} else {

				Long id = faReportMapping.getReportKey();
				serializerService.serialize(session.getId(), faReportMapping.getReportKey(), faReportMapping);
				confirming(id, group, mode);
			}
		} else {
			flash.error(ExceptionConstants.PARAMETER_NULL, faReportMapping);
		}
	}

	@Check("administration.reportMapping")
	public static void confirming(Long id, String group, String mode) {
		log.debug("confirming. id: " + id + " group: " + group + " mode: " + mode);

		boolean confirming = true;
		FaReportMapping faReportMapping = serializerService.deserialize(session.getId(), id, FaReportMapping.class);
		String reportMappingDetails = null;
		try {
			reportMappingDetails = json.writeValueAsString(faReportMapping.getFaReportMappingDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
		render("ReportMappings/detail.html", group, mode, faReportMapping, reportMappingDetails, confirming, id);
	}

	@Check("administration.reportMapping")
	public static void confirm(String group, String mode, FaReportMapping faReportMapping, FaReportMappingDetail[] faReportMapDetails) {
		log.debug("confirm. group: " + group + " mode: " + mode + " faReportMapping: " + faReportMapping + " faReportMapDetails: " + faReportMapDetails);

		if (faReportMapDetails != null) {
			for (FaReportMappingDetail faReportMapDetail : faReportMapDetails) {
				if (faReportMapDetail != null) {
					if (faReportMapping.getReportKey() != null) {
						// faReportMapDetail.getId().setReportKey(faReportMapping.getReportKey());
						faReportMapDetail.setFaReportMapping(faReportMapping);
					}
					faReportMapDetail.setReportPosition(generalService.getLookup(faReportMapDetail.getReportPosition().getLookupId()));
					faReportMapping.getFaReportMappingDetails().add(faReportMapDetail);
				}
			}
		}

		log.debug("reportMappingDetails >>> " + faReportMapping.getFaReportMappingDetails().size());
		String reportMappingDetails = null;
		try {
			JsonHelper json = new JsonHelper().getFaReportMappingDetailSerializer();
			reportMappingDetails = json.serialize(faReportMapping.getFaReportMappingDetails());
			// reportMappingDetails =
			// json.writeValueAsString(faReportMapDetails);
			log.debug("reportMappingDetails >>> " + reportMappingDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		try {
			// FaReportMapping faReportMappingOld =
			// fundService.getFaReportMapping(faReportMapping.getReportKey());
			// String oldFaReportMapping =
			// json.writeValueAsString(faReportMappingOld);
			// String newFaReportMapping =
			// json.writeValueAsString(faReportMapping);
			// logger.debug("[SFR] >>> oldReportMapping = " +
			// oldFaReportMapping);
			// logger.debug("[SFR] >>> newFaReportMapping = " +
			// newFaReportMapping);
			// if(oldFaReportMapping.equals(newFaReportMapping)){
			// throw new MedallionException(ExceptionConstants.DATA_NOCHANGE);
			// }

			fundService.saveReportMapping(faReportMapping);
			list(group);
		} catch (MedallionException e) {
			// flash.error("Exist Error !");
			flash.error("ID : ' " + faReportMapping.getReportLabelId() + " ' " + e.getMessage());
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
			render("ReportMappings/detail.html", faReportMapping, reportMappingDetails, group, mode, confirming);
			// } catch (JsonGenerationException e) {
			// // TODO Auto-generated catch block
			// logger.error(e.getMessage(), e);
			// } catch (JsonMappingException e) {
			// // TODO Auto-generated catch block
			// logger.error(e.getMessage(), e);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// logger.error(e.getMessage(), e);
		}
	}

	@Check("administration.reportMapping")
	public static void back(Long id, String group, String mode) {
		log.debug("back. id: " + id + " group: " + group + " mode: " + mode);

		FaReportMapping faReportMapping = serializerService.deserialize(session.getId(), id, FaReportMapping.class);
		String reportMappingDetails = null;
		try {
			reportMappingDetails = json.writeValueAsString(faReportMapping.getFaReportMappingDetails());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_REPORT_MAPPING));
		render("ReportMappings/detail.html", faReportMapping, reportMappingDetails, group, mode);
	}

	@Check("administration.reportMapping")
	public static void delete(Long id) {
		log.debug("delete. id: " + id);

		FaReportMapping reportMapping = fundService.deleteReportMapping(id);
		String status = "success";
		if (reportMapping != null) {
			status = "error";
		}
		renderText(status);
	}
}
