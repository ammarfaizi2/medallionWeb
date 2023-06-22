package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Router;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CpComplianceProfile;
import com.simian.medallion.model.FaInvestmentModel;
import com.simian.medallion.model.FaMaster;
import com.simian.medallion.model.FaPublishSchedule;
import com.simian.medallion.model.FaPublishScheduleId;
import com.simian.medallion.model.GnCalendar;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class FundSetups extends MedallionController {
	private static Logger log = Logger.getLogger(FundSetups.class);

	// static GnApplicationDate current = generalService
	// .getApplicationDate(UIConstants.SIMIAN_BANK_ID);

	// Date currentDateNonformat = current.getCurrentBusinessDate();

	@Before(only = { "entry", "saveDate", "backCalender", "view", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> scheduleTypeOperators = UIHelper.scheduleTypeOperators();
		renderArgs.put("scheduleTypeOperators", scheduleTypeOperators);

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);

		List<SelectItem> nabPublish = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._NAB_PUBLISH);
		renderArgs.put("nabPublish", nabPublish);

		List<SelectItem> years = UIHelper.years();
		renderArgs.put("years", years);

		List<SelectItem> eTime = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._EREPORT_GENERATE);
		renderArgs.put("eTime", eTime);

	}

	@Check("maintenance.fundSetup")
	public static void list() {
		log.debug("list. ");

		// List<FaMaster> faMasters = fundService.listFaMaster();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render();
	}

	@Check("maintenance.fundSetup")
	public static void fsPaging(Paging page) {
		log.debug("fsPaging. page: " + page);

		page.addParams(Helper.searchAll("(fmas.fundKey||fmas.fundCode||fmas.fundDescription||ct.customerNo||" + "fm.thirdPartyCode||fmas.recordStatus||fmas.isActive)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = fundService.pagingFundSetups(page);
		renderJSON(page);
	}

	@Check("maintenance.fundSetup")
	public static void viewCalender(int year, long key, FaMaster faMaster, String mode, String isUsedAppDate, List<FaInvestmentModel> faInvestmentModels, List<FaPublishSchedule> listPublishSchedule, String status) {
		log.debug("viewCalender. year: " + year + " key: " + key + " faMaster: " + faMaster + " mode: " + mode + " isUsedAppDate: " + isUsedAppDate + " faInvestmentModels: " + faInvestmentModels + " listPublishSchedule: " + listPublishSchedule + " status: " + status);

		String[] isUsedAppDateArr = isUsedAppDate.split(",");
		String isUsedAppDateStr = isUsedAppDateArr[0];
		isUsedAppDate = isUsedAppDateStr;
		log.debug("isUsedAppDate " + isUsedAppDate);

		boolean started = true;
		int month = 0;
		List<SelectItem> months = UIHelper.months;
		List<SelectItem> years = UIHelper.years();
		java.util.Calendar calNull = java.util.Calendar.getInstance();
		Date dateDummy = calNull.getTime();
		log.debug("current Date ?-->" + dateDummy.toString());

		if (year == 0) {
			started = false;
			year = calNull.get(java.util.Calendar.YEAR);
		} else {
			started = true;
		}
		// year = dateDummy.getYear();
		if (month == 0)
			month = calNull.get(java.util.Calendar.MONTH);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		// cal.set(year, month, 1);
		cal.set(year, 0, 0, 00, 00, 00);
		// cal.set
		Date from = cal.getTime();
		log.debug("from" + cal);
		// cal.set(java.util.Calendar.DATE,
		// cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		cal.set(year, 11, 31, 00, 00, 00);
		Date to = cal.getTime();
		log.debug("to" + cal);
		log.debug("year --> " + year);

		List<FaPublishSchedule> publishDates = new ArrayList<FaPublishSchedule>();
		if (faInvestmentModels != null) {
			for (FaInvestmentModel faInvestmentModel : faInvestmentModels) {
				if (faInvestmentModel != null) {
					faMaster.getFaInvestmentModels().add(faInvestmentModel);
				}
			}
		}

		// cek, jika tipenya calendar, ambil dari gn_calendr, tapi jika
		// specific, ambil dari fa_publish_schedule
		if (UIConstants.SCHEDULE_TYPE_CALENDAR.equalsIgnoreCase(isUsedAppDate)) {
			// ambil semua hari kerja
			List<GnCalendar> holidays = generalService.listHolidaysBetween(from, to, "");
			log.debug("holidays.size --> " + holidays.size());

			if (holidays.size() > 0) {

				for (GnCalendar workDay : holidays) {
					FaPublishSchedule faPublishScheduleCal = new FaPublishSchedule();
					FaPublishScheduleId faPublishScheduleId = new FaPublishScheduleId();
					int yearWork = Integer.parseInt((new SimpleDateFormat("yyyy")).format(workDay.getId().getCalendarDate()));
					if (yearWork == year) {
						faPublishScheduleId.setFundKey(new Long(0));
						faPublishScheduleId.setPublishDate(workDay.getId().getCalendarDate());
						faPublishScheduleCal.setFaMaster(faMaster);
						faPublishScheduleCal.setId(faPublishScheduleId);
						publishDates.add(faPublishScheduleCal);
					}
					// faMaster.getFaPublishSchedules().add(faPublishScheduleCal);
				}
			}
		}

		if (UIConstants.SCHEDULE_TYPE_SPECIFIC.equalsIgnoreCase(isUsedAppDate)) {
			if (listPublishSchedule != null) {
				for (FaPublishSchedule faPublishSchedule : listPublishSchedule) {
					if (faPublishSchedule != null) {
						int yearPublishSchedule = Integer.parseInt((new SimpleDateFormat("yyyy")).format(faPublishSchedule.getId().getPublishDate()));
						log.debug("yearPublishSchedule --> " + yearPublishSchedule);
						if (yearPublishSchedule == year) {
							publishDates.add(faPublishSchedule);
						}
						faMaster.getFaPublishSchedules().add(faPublishSchedule);
					}
				}
			}
		}

		log.debug("Size publishDates " + publishDates.size());

		serializerService.serialize(session.getId(), faMaster.getFundKey(), faMaster);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render(publishDates, year, months, years, started, key, mode, isUsedAppDate, faMaster, mode, status);
	}

	@Check("maintenance.fundSetup")
	public static void entry() {
		log.debug("entry. ");

		int year = 0;
		boolean started = false;
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaMaster faMaster = new FaMaster();

		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);
		log.debug("Year now " + faMaster.getYearNow());
		// faMaster.setFaPublishSchedules(new ArrayList<FaPublishSchedule>());
		// feeMasters.setSaFeeTiers(new ArrayList<SaFeeTier>());
		faMaster.setFinancialYear(LookupConstants.FINANCIAL_YEAR);
		faMaster.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));

		String investmentModels = null;
		String publishSchedule = null;

		try {
			investmentModels = json.writeValueAsString(faMaster.getFaInvestmentModels());
			publishSchedule = json.writeValueAsString(faMaster.getFaPublishSchedules());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", faMaster, year, investmentModels, mode, publishSchedule, started);
	}

	// public static void saveDate (Long id,String mode,Long[] dates, Long key){
	@Check("maintenance.fundSetup")
	public static void saveDate(Long id, String mode, int year, List<Long> dates, Long key, String status, String isUsedAppDate) {
		log.debug("saveDate. id: " + id + " mode: " + mode + " year: " + year + " dates: " + dates + " key: " + key + " status: " + status + " isUsedAppDate: " + isUsedAppDate);

		boolean tabSchedule = true;
		List<FaPublishSchedule> publishDates = new ArrayList<FaPublishSchedule>();
		FaMaster faMaster = serializerService.deserialize(session.getId(), id, FaMaster.class);
		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);
		// setAbstractCustomer(faMaster);
		for (FaPublishSchedule faPublishSchedule : faMaster.getFaPublishSchedules()) {
			if (faPublishSchedule != null) {
				int yearPublishSchedule = Integer.parseInt((new SimpleDateFormat("yyyy")).format(faPublishSchedule.getId().getPublishDate()));
				if (year != yearPublishSchedule) {
					publishDates.add(faPublishSchedule);
				}
			}
		}

		if (dates != null) {
			for (Long date : dates) {
				log.debug("date --> " + date);
				// Date newDate = new Date(date);
				// logger.debug("new date "+newDate);
				FaPublishScheduleId faPublishScheduleId = new FaPublishScheduleId();
				if (key != null) {
					faPublishScheduleId.setFundKey(key);
				}
				faPublishScheduleId.setPublishDate(new Date(date));
				log.debug("faPublishScheduleId date " + faPublishScheduleId.getPublishDate());
				FaPublishSchedule newPublishSchedule = new FaPublishSchedule();
				newPublishSchedule.setId(faPublishScheduleId);
				newPublishSchedule.setFaMaster(faMaster);
				log.debug("newPublishSchedule date " + newPublishSchedule.getId().getPublishDate());
				publishDates.add(newPublishSchedule);
			}
		}

		log.debug("Size after dd " + publishDates.size());
		String investmentModels = null;
		String publishSchedule = null;
		try {
			JsonHelper jsons = new JsonHelper().getFaPublishScheduleSerializer();
			investmentModels = json.writeValueAsString(faMaster.getFaInvestmentModels());
			publishSchedule = jsons.serialize(publishDates);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// logger.debug("Publish schedule "+publishSchedule);
		// logger.debug("Fund code "+faMaster.getFundCode());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", faMaster, investmentModels, publishSchedule, mode, tabSchedule, status, isUsedAppDate);

	}

	@Check("maintenance.fundSetup")
	public static void backCalender(Long id, String mode, String status, String isUsedAppDate) {
		log.debug("backCalender. id: " + id + " mode: " + mode + " status: " + status + " isUsedAppDate: " + isUsedAppDate);

		boolean tabSchedule = true;
		FaMaster faMaster = serializerService.deserialize(session.getId(), id, FaMaster.class);
		log.debug("faMaster --> " + faMaster);
		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);

		// Set<FaInvestmentModel> faInvestmentModels =
		// faMaster.getFaInvestmentModels();

		// Set<FaInvestmentModel> investmentModels =
		// faMaster.getinvestmentModels();

		String investmentModels = null;
		String publishSchedule = null;
		try {
			investmentModels = json.writeValueAsString(faMaster.getFaInvestmentModels());
			publishSchedule = json.writeValueAsString(faMaster.getFaPublishSchedules());

		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		log.debug("Fund code " + faMaster.getFundCode());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", faMaster, investmentModels, publishSchedule, mode, tabSchedule, status, isUsedAppDate);
	}

	@Check("maintenance.fundSetup")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaMaster faMaster = fundService.getFaMaster(id);
		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);

		List<FaInvestmentModel> faInvestmentModels = fundService.listFaInvestmentModels(faMaster.getFundKey());
		List<FaPublishSchedule> faPublishSchedule = fundService.listFaPublishSchedules(faMaster.getFundKey());
		String investmentModels = null;
		String publishSchedule = null;
		try {
			investmentModels = json.writeValueAsString(faInvestmentModels);
			publishSchedule = json.writeValueAsString(faPublishSchedule);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// setAbstractCustomer(faMaster);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", mode, faMaster, publishSchedule, investmentModels);

	}

	@Check("maintenance.fundSetup")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaMaster faMaster = fundService.getFaMaster(id);
		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);
		// faMaster.setOldCustomerNo(faMaster.getCustomerNo());

		List<FaInvestmentModel> FaInvestmentModels = fundService.listFaInvestmentModels(faMaster.getFundKey());
		List<FaPublishSchedule> FaPublishSchedule = fundService.listFaPublishSchedules(faMaster.getFundKey());
		String status = faMaster.getRecordStatus();
		log.debug("status = --" + status + "--");
		log.debug("currentYear = --" + currentYear + "--");
		String investmentModels = null;
		String publishSchedule = null;
		try {
			investmentModels = json.writeValueAsString(FaInvestmentModels);
			publishSchedule = json.writeValueAsString(FaPublishSchedule);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// setAbstractCustomer(faMaster);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", mode, faMaster, investmentModels, status, publishSchedule);
	}

	private static void setData(FaMaster faMaster, List<FaInvestmentModel> faInvestmentModels, FaPublishSchedule[] listPublishSchedule) {
		log.debug("setData. faMaster: " + faMaster + " faInvestmentModels: " + faInvestmentModels + " listPublishSchedule: " + listPublishSchedule);

		if (faMaster != null) {
			if (faInvestmentModels != null) {
				faMaster.setFaInvestmentModels(new HashSet<FaInvestmentModel>(faInvestmentModels));
			}
			if (listPublishSchedule != null) {
				for (FaPublishSchedule faPublishSchedule : listPublishSchedule) {
					faMaster.getFaPublishSchedules().add(faPublishSchedule);
				}
				// faMaster.setFaPublishSchedules(new HashSet
				// FaPublishSchedule[] listPublishSchedule));
			}
		}
	}

	@Check("maintenance.fundSetup")
	public static void save(FaMaster faMaster, int year, String mode, List<FaInvestmentModel> faInvestmentModels, FaPublishSchedule[] listPublishSchedule, String status, String isUsedAppDate) {
		log.debug("save. faMaster: " + faMaster + " year: " + year + " mode: " + mode + " faInvestmentModels: " + faInvestmentModels + " listPublishSchedule: " + listPublishSchedule + " status: " + status + " isUsedAppDate: " + isUsedAppDate);

		try {
			String investmentModels = null;
			String publishSchedule = null;

			// List<FaMaster> faMasters = fundService.listFaMaster();
			// logger.debug("size list faMasters ? " + faMasters.size());
			if (faMaster != null) {

				if (faInvestmentModels != null) {
					for (FaInvestmentModel faInvestmentModel : faInvestmentModels) {
						if (faInvestmentModel != null) {
							faMaster.getFaInvestmentModels().add(faInvestmentModel);
						}
					}
				}
				// logger.debug("Size faInvestmentModels  after add "+faMaster.getFaInvestmentModels().size());
				// if (!faMaster.getUsedAppDate()) {
				if (listPublishSchedule != null) {
					for (FaPublishSchedule faPublishSchedule : listPublishSchedule) {
						if (faPublishSchedule != null) {

							if (isUsedAppDate.equalsIgnoreCase(UIConstants.SCHEDULE_TYPE_CALENDAR)) {

								int yearPublishSchedule = Integer.parseInt((new SimpleDateFormat("yyyy")).format(faPublishSchedule.getId().getPublishDate()));
								if (year != yearPublishSchedule) {
									faMaster.getFaPublishSchedules().add(faPublishSchedule);
								}
							} else {

								faMaster.getFaPublishSchedules().add(faPublishSchedule);
							}
						}
					}
				}
				// }

				try {
					investmentModels = json.writeValueAsString(faMaster.getFaInvestmentModels());
					publishSchedule = json.writeValueAsString(faMaster.getFaPublishSchedules());
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}

				validation.required("Fund Code is", faMaster.getFundCode());
				validation.required("Fund Name is", faMaster.getFundDescription());
				validation.required("Customer CIF No is", faMaster.getCustomer().getCustomerNo());
				validation.required("Fund Manager Code is", faMaster.getFundManager().getThirdPartyKey());
				validation.required("Fund Type is", faMaster.getFundType().getLookupId());
				validation.required("Fund Class is", faMaster.getFundClass().getLookupId());
				validation.required("Backdated Allow is", faMaster.getBackDatedAllowed());
				validation.required("Posting Profile is", faMaster.getFaPostingProfile().getPostingProfileKey());
				validation.required("Currency is", faMaster.getCurrency().getCurrencyCode());

				if (faMaster.getReportTime() == true) {
					validation.required("Generate to E-Reporting is", faMaster.geteReportTime().getLookupId());
					validation.required("E-Report Code is", faMaster.geteReportCode());

				}
				// // UNIQUE CONSTRAINT VALIDATION
				// if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
				// for (FaMaster faMasterInTable : faMasters) {
				// // FUND CODE && ORGANIZATION
				// if (faMaster.getFundCode() != null) {
				// if
				// ((faMasterInTable.getFundCode().equals(faMaster.getFundCode()))
				// &&
				// (faMasterInTable.getOrganization().getOrganizationId().equals(faMaster.getOrganization().getOrganizationId())))
				// {
				// flash.error("Duplicate Error! Code : " +
				// faMaster.getFundCode() + " Already Exist Data at Fund Code");
				// }
				// }
				// //=============================
				//
				// // ACCOUNT
				// if (faMaster.getAccount().getCustodyAccountKey() != null ||
				// faMaster.getAccount() != null) {
				// if
				// (faMasterInTable.getAccount().getCustodyAccountKey().equals(faMaster.getAccount().getCustodyAccountKey()))
				// {
				// flash.error("Duplicate Error! Account No : '" +
				// faMaster.getAccount().getAccountNo() +
				// "' Already Exist at Custody Account No");
				// }
				// }
				// //=============================
				// }
				// }
				// //=============================
				if (validation.hasErrors()) {
					log.debug("Masuk error");
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
					render("FundSetups/detail.html", faMaster, mode, investmentModels, faInvestmentModels, publishSchedule, listPublishSchedule, status);
				} else {

					if (isUsedAppDate.equalsIgnoreCase(UIConstants.SCHEDULE_TYPE_CALENDAR)) {
						faMaster.setUsedAppDate(true);
					} else {
						faMaster.setUsedAppDate(false);
					}

					// logger.debug("Size faPublish schedule after add "+faMaster.getFaPublishSchedules().size());
					Long id = faMaster.getFundKey();
					// String oldCustomerNo =
					// faMaster.getCustomer().getCustomerNo();
					serializerService.serialize(session.getId(), faMaster.getFundKey(), faMaster);
					confirming(id, mode, status, isUsedAppDate, year);
				}
			} else {
				log.debug("flash error");
				// flash.error(ExceptionConstants.PARAMETER_NULL, faMaster);
				entry();
			}
		} catch (Exception er) {
			String url = flash.get("url");
			url = Router.getFullUrl("Application.index");
			redirect(url);
		}
	}

	public static void confirming(Long id, String mode, String status, String isUsedAppDate, int year) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status + " isUsedAppDate: " + isUsedAppDate + " year: " + year);

		renderArgs.put("confirming", true);
		FaMaster faMaster = serializerService.deserialize(session.getId(), id, FaMaster.class);
		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);
		log.debug("Email 2 -->" + faMaster.getFundManager().getEmail());
		log.debug("Year now confirming " + faMaster.getYearNow());
		// setAbstractCustomer(faMaster);
		// Set<FaInvestmentModel> investmentModels =
		// faMaster.getinvestmentModels();
		String investmentModels = null;
		String publishSchedule = null;

		try {
			investmentModels = json.writeValueAsString(faMaster.getFaInvestmentModels());
			publishSchedule = json.writeValueAsString(faMaster.getFaPublishSchedules());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		log.debug("--investmentModels--" + investmentModels);
		log.debug("--publishSchedule--" + publishSchedule);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", faMaster, mode, investmentModels, publishSchedule, status, isUsedAppDate, year);
	}

	@Check("maintenance.fundSetup")
	public static void confirm(FaMaster faMaster, String mode, List<FaInvestmentModel> faInvestmentModels, FaPublishSchedule[] listPublishSchedule, String status) {
		log.debug("confirm. faMaster: " + faMaster + " mode: " + mode + " faInvestmentModels: " + faInvestmentModels + " listPublishSchedule: " + listPublishSchedule + " status: " + status);

		if (faInvestmentModels == null) {
			faInvestmentModels = new ArrayList<FaInvestmentModel>();

		}

		ArrayList<FaPublishSchedule> confPublish = new ArrayList<FaPublishSchedule>();

		if (listPublishSchedule != null) {
			for (FaPublishSchedule faPublishSchedule : listPublishSchedule) {
				if (faPublishSchedule != null) {
					confPublish.add(faPublishSchedule);
				}
			}
		}
		String investmentModels = null;
		String publishSchedule = null;
		try {
			investmentModels = json.writeValueAsString(faInvestmentModels);
			publishSchedule = json.writeValueAsString(confPublish);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		try {
			// List<FaMaster> faMasters = fundService.listFaMaster();
			// for (FaMaster faMasterInTable : faMasters) {
			// // // FUND CODE && ORGANIZATION
			// // if (faMaster.getFundCode() != null && faMaster.getFundKey() ==
			// null) {
			// // if
			// ((faMasterInTable.getFundCode().equals(faMaster.getFundCode()))
			// &&
			// (faMasterInTable.getOrganization().getOrganizationId().equals(faMaster.getOrganization().getOrganizationId())))
			// {
			// // flash.error("Duplicate Error! Code : " +
			// faMaster.getFundCode() + " Already Exist Data");
			// // boolean confirming = true;
			// // render("FundSetups/detail.html", faMaster, mode, confirming,
			// investmentModels, faInvestmentModels);
			// // }
			// // }
			// // //=============================
			// //
			// // // ACCOUNT
			// // if ((faMaster.getAccount().getCustodyAccountKey() != null ||
			// faMaster.getAccount() != null) && faMaster.getFundKey() == null)
			// {
			// // if
			// (faMasterInTable.getAccount().getCustodyAccountKey().equals(faMaster.getAccount().getCustodyAccountKey()))
			// {
			// // flash.error("Duplicate Error! Account No : '" +
			// faMaster.getAccount().getAccountNo() + "' Already Exist");
			// // boolean confirming = true;
			// // render("FundSetups/detail.html", faMaster, mode, confirming,
			// investmentModels, faInvestmentModels);
			// // }
			// // }
			// // //=============================
			//
			// // CUSTOMER NO
			// // if
			// (faMasterInTable.getCustomerNo().equals(faMaster.getCustomerNo()))
			// {
			// // if
			// (faMasterInTable.getIsActive().equals(faMaster.getIsActive()) ) {
			// // if (faMasterInTable.getIsActive() == true) {
			// // flash.error("Customer CIF No : '" + faMaster.getCustomerNo() +
			// "' has been exist");
			// // boolean confirming = true;
			// // render("FundSetups/detail.html", faMaster, mode, confirming,
			// investmentModels, faInvestmentModels);
			// // }
			// // }
			// // }
			// //=============================
			// }
			List<FaMaster> faMasters = fundService.listFaMaster();
			log.debug("Size famasetr " + faMasters.size());
			try {
				log.debug("faMaster.getCustomer().getCustomerNo() " + faMaster.getCustomer().getCustomerNo());
			} catch (Exception e) {
				String url = Router.getFullUrl("Application.index");
				redirect(url);
			}

			for (FaMaster faMasterInTable : faMasters) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					if (faMasterInTable.getCustomer() != null) {
						if (faMaster.getCustomer().getCustomerNo().equals(faMasterInTable.getCustomer().getCustomerNo()) && (faMasterInTable.getIsActive().equals(true))) {
							// logger.debug("key "+faMasterInTable.getFundKey());
							// logger.debug("key fa master "+faMaster.getFundKey());
							// if ((faMaster.getFundKey() != null &&
							// faMaster.getFundKey().compareTo(faMasterInTable.getFundKey())==0
							// )){
							//
							// }else{
							log.debug("key" + faMasterInTable.getFundKey() + "-" + faMaster.getFundKey() + "-");
							flash.error("Customer Code : '" + faMaster.getCustomer().getCustomerNo() + "' has been exist");
							boolean confirming = true;
							render("FundSetups/detail.html", faMaster, mode, confirming, investmentModels, faInvestmentModels, publishSchedule, status);

							// }
						}
					}
				} else {
					if (faMasterInTable.getCustomer() != null) {
						if ((!faMasterInTable.getFundKey().equals(faMaster.getFundKey())) && faMaster.getCustomer().getCustomerNo().equals(faMasterInTable.getCustomer().getCustomerNo()) && (faMasterInTable.getIsActive().equals(true)) && (faMaster.getIsActive().equals(true))) {
							// logger.debug("key "+faMasterInTable.getFundKey());
							// logger.debug("key fa master "+faMaster.getFundKey());
							// if ((faMaster.getFundKey() != null &&
							// faMaster.getFundKey().compareTo(faMasterInTable.getFundKey())==0
							// )){
							//
							// }else{
							log.debug("key" + faMasterInTable.getFundKey() + "-" + faMaster.getFundKey() + "-");
							flash.error("Customer Code : '" + faMaster.getCustomer().getCustomerNo() + "' has been exist");
							boolean confirming = true;
							render("FundSetups/detail.html", faMaster, mode, confirming, investmentModels, faInvestmentModels, publishSchedule, status);
							// }
						}
					}
				}
			}

			setData(faMaster, faInvestmentModels, listPublishSchedule);
			if (UIConstants.DISPLAY_MODE_EDIT.equals(mode)) {
				for (FaInvestmentModel invModel : faInvestmentModels) {
					if (invModel.getInvestmentModelKey() == null) {
						FaInvestmentModel model = new FaInvestmentModel();
						model = fundService.getFaInvestmentModel(faMaster.getFundKey(), invModel.getSecurityClass().getLookupId(), invModel.getMin(), invModel.getMax());
						if (model != null)
							invModel.setInvestmentModelKey(model.getInvestmentModelKey());
					}
					faMaster.getFaInvestmentModels().add(invModel);
				}

			}
			log.debug("faMaster COFIRMED ==> " + faMaster.getUsedAppDate());
			fundService.saveFaMaster(MenuConstants.FA_FUND_SETUP, faMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			// e.getErrorCode();
			// flash.error("Duplicate Error! Code : ' " +
			// faMaster.getFundCode()+" ' Already Exist",
			// faMaster.getFundCode());
			validation.clear();
			flash.error("Fund Code : ' " + faMaster.getFundCode() + "  " + Messages.get(e.getMessage()));

			// flash.error("Fund Code : ' "+
			// faMaster.getFundCode()+" ' or Customer CIF No : '" +
			// faMaster.getCustomer().getCustomerNo() + "' " +
			// Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
			render("FundSetups/detail.html", faMaster, mode, confirming, investmentModels, publishSchedule, status);
		}
	}

	@Check("maintenance.fundSetup")
	public static void back(Long id, String mode, String isUsedAppDate, String status, int year) {
		log.debug("back. id: " + id + " mode: " + mode + " isUsedAppDate: " + isUsedAppDate + " status: " + status + " year: " + year);

		FaMaster faMaster = serializerService.deserialize(session.getId(), id, FaMaster.class);
		log.debug("faMaster BACK ==> " + faMaster.getUsedAppDate());
		Set<FaInvestmentModel> faInvestmentModels = faMaster.getFaInvestmentModels();
		Set<FaPublishSchedule> faPublishSchedules = faMaster.getFaPublishSchedules();
		Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
		faMaster.setYearNow(currentYear);
		if (isUsedAppDate.equalsIgnoreCase(UIConstants.SCHEDULE_TYPE_CALENDAR)) {
			faMaster.setUsedAppDate(true);
		} else {
			faMaster.setUsedAppDate(false);
		}
		// setAbstractCustomer(faMaster);
		String investmentModels = null;
		String publishSchedule = null;
		try {
			investmentModels = json.writeValueAsString(faInvestmentModels);
			publishSchedule = json.writeValueAsString(faPublishSchedules);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FUND_SETUP));
		render("FundSetups/detail.html", faMaster, mode, investmentModels, isUsedAppDate, publishSchedule, status, year);
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			boolean approval = true;
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaMaster faMaster = json.readValue(maintenanceLog.getNewData(), FaMaster.class);
			faMaster.setFundManager(generalService.getThirdParty(faMaster.getFundManager().getThirdPartyKey()));
			log.debug("Key email " + faMaster.getFundManager().getThirdPartyKey());
			log.debug("Fa master email " + faMaster.getFundManager().getEmail());
			log.debug("Fa master usedappdate " + faMaster.getUsedAppDate());
			Integer currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID)));
			faMaster.setYearNow(currentYear);
			Set<FaInvestmentModel> faInvestmentModels = faMaster.getFaInvestmentModels();
			Set<FaPublishSchedule> faPublishSchedules = faMaster.getFaPublishSchedules();
			String investmentModels = null;
			String publishSchedule = null;
			try {
				investmentModels = json.writeValueAsString(faInvestmentModels);
				publishSchedule = json.writeValueAsString(faPublishSchedules);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			// setAbstractCustomer(faMaster);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			if (faMaster.getCpComplianceProfile() != null) {
				if (faMaster.getCpComplianceProfile().getComplianceProfCode() != null) {
					CpComplianceProfile cpProf = generalService.getComplianceProfile(faMaster.getCpComplianceProfile().getComplianceProfCode());
					faMaster.getCpComplianceProfile().setDescription(cpProf.getDescription());
				}
			}
			log.debug("Fa master usedappdat>>>>>e " + faMaster.getUsedAppDate());
			render("FundSetups/approval.html", faMaster, investmentModels, publishSchedule, mode, taskId, operation, maintenanceLogKey, from, approval);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("maintenance.fundSetup")
	// public static void reloadPublishSchedule(String scheduleType, String
	// year, String key) {
	//
	// java.util.Calendar cal = java.util.Calendar.getInstance();
	// int yearInt = Integer.parseInt(year);
	// cal.set(yearInt, 0, 1,00,00,00);
	// //cal.set
	// Date from = cal.getTime();
	// logger.debug("from"+ cal);
	//
	// cal.set(yearInt, 11, 31,00,00,00);
	// Date to = cal.getTime();
	// logger.debug("to"+ cal);
	// logger.debug("year --> " + year);
	//
	// List<Date> workDays = new ArrayList<Date>();
	//
	// if (scheduleType.equals(UIConstants.SCHEDULE_TYPE_CALENDAR)) {
	//
	// //jika tipe calendar, populate publish grid berdasarkan dari gn_calendar
	// //lempar array berisi hari kerja
	// Date dari = from;
	// Date ke = to;
	//
	// List<Date> dates = new ArrayList<Date>();
	// java.util.Calendar calendar = java.util.Calendar.getInstance();
	// calendar.setTime(dari);
	//
	// while (calendar.getTime().before(ke))
	// {
	// Date result = calendar.getTime();
	// dates.add(result);
	// calendar.add(java.util.Calendar.DATE, 1);
	// }
	//
	// List<GnCalendar> holidays = generalService.listHolidaysBetween(from, to,
	// "");
	//
	// SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-DD");
	// workDays.addAll(dates);
	//
	// for(Date tanggal : dates) {
	//
	// try {
	// if (holidays.size() > 0 ) {
	// Date current = formatDate.parse(formatDate.format(tanggal));
	//
	// for(GnCalendar holiday : holidays) {
	//
	// Date libur =
	// formatDate.parse(formatDate.format(holiday.getId().getCalendarDate()));
	//
	// if (libur.getTime() == current.getTime()) {
	// workDays.remove(tanggal);
	// break;
	// }
	// }
	// }
	//
	// } catch (ParseException e) {
	// logger.error(e.getMessage(), e);
	// }
	//
	// }
	//
	// }
	//
	// if (scheduleType.equals(UIConstants.SCHEDULE_TYPE_SPECIFIC)) {
	//
	// //jika tipe calendar, populate publish grid berdasarkan
	// fa_publish_schedule
	// if (!Helper.isNullOrEmpty(key)) {
	//
	// Long id = Long.parseLong(key);
	// // FaMaster faMaster = fundService.getFaMaster(id);
	//
	// List<FaPublishSchedule> listPublishSchedule =
	// fundService.listFaPublishSchedules(id);
	//
	// if (listPublishSchedule != null){
	// for (FaPublishSchedule faPublishSchedule : listPublishSchedule) {
	// if (faPublishSchedule != null){
	// int yearPublishSchedule = Integer.parseInt((new
	// SimpleDateFormat("yyyy")).format(faPublishSchedule.getId().getPublishDate()));
	// logger.debug("yearPublishSchedule --> "+yearPublishSchedule);
	// if (yearPublishSchedule == yearInt){
	// workDays.add(faPublishSchedule.getId().getPublishDate());
	// }
	// }
	// }
	// }
	//
	// }
	// }
	// renderJSON(workDays);
	// }

}
