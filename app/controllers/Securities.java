package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Securities extends MedallionController {
	private static Logger log = Logger.getLogger(Securities.class);

	public static String securityCategory;

	private static ObjectMapper json = new ObjectMapper();

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> udfs = UIHelper.udfOperators();
		renderArgs.put("udfs", udfs);

		List<SelectItem> frequency = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._FREQUENCY);
		renderArgs.put("frequency", frequency);

		List<SelectItem> accrualBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCRUAL_BASE);
		renderArgs.put("accrualBase", accrualBase);

		List<SelectItem> yearBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._YEAR_BASE);
		renderArgs.put("yearBase", yearBase);

		List<SelectItem> depositoryCode = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE);
		renderArgs.put("depositoryCode", depositoryCode);

		List<SelectItem> optionExternalTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SWIFT_INSTRUMENT_TYPE);
		renderArgs.put("optionExternalTypes", optionExternalTypes);
		
		List<SelectItem> valuationMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._VALUATION_METHOD);
		for (SelectItem selectItem : valuationMethod) {
			if (selectItem.value.equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
				renderArgs.put("valueForValuationFairValue", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
				renderArgs.put("valueForValuationAmortization", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.VALUATION_METHOD_NOT_AVAILABLE)) {
				renderArgs.put("valueForValuationNA", selectItem.text);
			}
		}
		renderArgs.put("valuationMethod", valuationMethod);

		List<SelectItem> amortizationMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._AMORTIZATION_METHOD);
		for (SelectItem selectItem : amortizationMethod) {
			if (selectItem.value.equals(LookupConstants.AMORTIZATION_METHOD_SL)) {
				renderArgs.put("valueForAmortizationSL", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.AMORTIZATION_METHOD_EIR)) {
				renderArgs.put("valueForAmortizationEIR", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.AMORTIZATION_METHOD_NPV)) {
				renderArgs.put("valueForAmortizationNPV", selectItem.text);
			}
		}
		renderArgs.put("amortizationMethod", amortizationMethod);

		GnLookup registrarNA = generalService.getLookup(LookupConstants._REGISTRAR_NA);
		renderArgs.put("registrarNA", registrarNA);

		List<SelectItem> marketPrice = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._MARKET_PRICE);
		for (SelectItem selectItem : marketPrice) {
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_CLOSING)) {
				renderArgs.put("valueForMarketPriceClose", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_LOW)) {
				renderArgs.put("valueForMarketPriceLow", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_HIGH)) {
				renderArgs.put("valueForMarketPriceHigh", selectItem.text);
			}
			if (selectItem.value.equals(LookupConstants.MARKET_PRICE_NA)) {
				renderArgs.put("valueForMarketPriceNA", selectItem.text);
			}
		}
		renderArgs.put("marketPrice", marketPrice);

		renderArgs.put("valuationMethodAmortized", LookupConstants.VALUATION_METHOD_AMORTIZED);
		renderArgs.put("valuationMethodFairValue", LookupConstants.VALUATION_METHOD_FAIR_VALUE);
		renderArgs.put("valuationMethodNA", LookupConstants.VALUATION_METHOD_NOT_AVAILABLE);
		renderArgs.put("marketPriceClose", LookupConstants.MARKET_PRICE_CLOSING);
		renderArgs.put("marketPriceHigh", LookupConstants.MARKET_PRICE_HIGH);
		renderArgs.put("marketPriceLow", LookupConstants.MARKET_PRICE_LOW);
		renderArgs.put("marketPriceNA", LookupConstants.MARKET_PRICE_NA);
		renderArgs.put("amortizationSL", LookupConstants.AMORTIZATION_METHOD_SL);
		renderArgs.put("amortizationEIR", LookupConstants.AMORTIZATION_METHOD_EIR);
		renderArgs.put("amortizationNPV", LookupConstants.AMORTIZATION_METHOD_NPV);
		// json.getSerializationConfig().enable(Feature.WRITE_NULL_MAP_VALUES);
		// json.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);

		List<ScTypeMaster> securityTypesDeposit = securityService.listSecurityTypesDepositTemplate();
		String scTypeStr = "";
		for (ScTypeMaster scType : securityTypesDeposit) {
			scTypeStr += scType.getSecurityType() + "|";
		}
		renderArgs.put("securityTypesDeposit", scTypeStr);

	}

	@Check("administration.securityInfo")
	public static void list() {
		log.debug("list. ");

		// List<ScMaster> securities =
		// securityService.listSecurities(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
		// render(securities);
		render("Securities/list.html");
	}

	@Check("administration.securityInfo")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		page.addParams(Helper.searchAll("(a.securityId||a.description||b.securityType.securityType||a.securityType.securityClass.lookupCode)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = securityService.pagingSecurities(page);
		log.debug("json ---> " + page);
		renderJSON(page);
	}

	@Check("administration.securityInfo")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			ScMaster security = securityService.getSecurity(id);
			security.setCheckTrade(security.getValuationMethodTrade() != null ? true : false);
			security.setCheckAfs(security.getValuationMethodAFS() != null ? true : false);
			security.setCheckHtm(security.getValuationMethodHTM() != null ? true : false);
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();
			Set<ScCouponSchedule> schedules = security.getCouponSchedules();

			// START UDFS
			if (security.getUdf() != null)
				data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);

			for (GnUdfMaster udf : udfs) {

				if (security.getUdf() != null)
					udf.setValue(data.get(udf.getFieldName()));
				else
					udf.setValue("");

				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
				// item.options =
				// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
				// item.getLookupGroup().getLookupGroup());
			}
			// END UDFS
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
			render("Securities/detail.html", security, mode, schedules, udfs);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		// try {
		// String mode = UIConstants.DISPLAY_MODE_VIEW;
		// ScMaster security = securityService.getSecurity(id);
		//
		// List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
		// Map<String, String> data = new HashMap<String, String>();
		// Set<ScCouponSchedule> schedules = security.getCouponSchedules();
		//
		// //START UDFS
		// if (security.getUdf() != null)
		// data = json.readValue(security.getUdf(),
		// TypeFactory.mapType(HashMap.class, String.class, String.class));
		//
		// udfs =
		// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
		//
		// for (GnUdfMaster udf : udfs) {
		//
		// if (security.getUdf() != null)
		// udf.setValue(data.get(udf.getFieldName()));
		// else udf.setValue("");
		//
		// if ( udf.getLookupGroup() != null ) {
		// udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
		// udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// udf.getLookupGroup().getLookupGroup()));
		// }
		// //item.options =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// item.getLookupGroup().getLookupGroup());
		// }
		// //END UDFS
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
		// render("Securities/detail.html", security, mode, schedules , udfs);
		// } catch (IOException e) {
		// logger.debug(e.getMessage(),e);
		// }
	}

	@Check("administration.securityInfo")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		ScMaster security = new ScMaster();
		security.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		security.setIsFraction(false);
		security.setIsExpire(false);
		security.setSettlementDays(0);
		security.setMinTrxQuantity(0);
		security.setCheckTrade(true);
		// security.setIssuedCapital(new BigDecimal(0.00));
		// security.setPaidUpCapital(new BigDecimal(0.00));
		// security.setInitialCapital(new BigDecimal(0.00));
		// security.setValuationMethodTrade(generalService.getLookup(LookupConstants.VALUATION_METHOD_FAIR_VALUE));
		// security.setValuationMethodAFS(generalService.getLookup(LookupConstants.VALUATION_METHOD_FAIR_VALUE));
		// security.setValuationMethodHTM(generalService.getLookup(LookupConstants.VALUATION_METHOD_FAIR_VALUE));
		// security.setMarketPriceTrade(generalService.getLookup(LookupConstants.MARKET_PRICE_CLOSING));
		// security.setMarketPriceAFS(generalService.getLookup(LookupConstants.MARKET_PRICE_CLOSING));
		// security.setMarketPriceHTM(generalService.getLookup(LookupConstants.MARKET_PRICE_CLOSING));
		// security.setDepositoryCode(generalService.getLookup(LookupConstants.DEPOSITORY_CODE_CBEST));
		// UdfMaster udf = new UdfMaster();
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
		if (udfs != null) {
			for (GnUdfMaster udf : udfs) {
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
		boolean isreload = false;
		render("Securities/detail.html", security, mode, udfs, isreload);
	}

	@Check("administration.securityInfo")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_EDIT;
			ScMaster security = securityService.getSecurity(id);

			security.setCheckTrade(security.getValuationMethodTrade() != null ? true : false);
			security.setCheckAfs(security.getValuationMethodAFS() != null ? true : false);
			security.setCheckHtm(security.getValuationMethodHTM() != null ? true : false);

			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();
			Set<ScCouponSchedule> schedules = security.getCouponSchedules();
			// START UDFS
			if (security.getUdf() != null)
				data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {

					if (security.getUdf() != null)
						udf.setValue(data.get(udf.getFieldName()));
					else
						udf.setValue("");

					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// item.options =
					// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
					// item.getLookupGroup().getLookupGroup());
				}
			}
			// END UDFS
			// logger.debug("price unit >>> "+security.getSecurityType().getPriceUnit());
			// logger.debug("isprice >> "+security.getSecurityType().getIsPrice());
			String status = security.getRecordStatus();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
			boolean isreload = true;
			render("Securities/detail.html", security, mode, schedules, udfs, status, isreload);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.securityInfo")
	public static void save(String mode, ScMaster security, Set<ScCouponSchedule> schedules, GnUdfMaster[] udfs, String status, boolean isreload) {
		log.debug("save. mode: " + mode + " security: " + security + " schedules: " + schedules + " udfs: " + udfs + " status: " + status + " isreload: " + isreload);

		if (security == null)
			entry();
		try {
			Map<String, String> data = new HashMap<String, String>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}
			}
			String udfString = json.writeValueAsString(data);
			security.setUdf(udfString);
		} catch (IOException e) {
			log.debug("IOException :>>> " + e);
			log.debug(e.getMessage(), e);
		}

		log.debug(">>> Is Trade = " + security.isCheckTrade());
		log.debug(">>> Is AFS = " + security.isCheckAfs());
		log.debug(">>> Is HTM = " + security.isCheckHtm());
		// validasi
		setData(security, schedules);
		// validation.required("security.securityType.securityClass.lookupCode",security.getSecurityType().getSecurityClass().getLookupCode());
		validation.required("Security Type is", security.getSecurityType().getSecurityType());
		validation.required("Security Code is", security.getSecurityId());
		validation.required("Description is", security.getDescription());
		validation.required("Issuer of Security is", security.getIssuer().getThirdPartyCode());
		validation.required("Currency is", security.getCurrency().getCurrencyCode());
		validation.required("Sector is", security.getSubSector().getLookupId());
		validation.required("Place of Exchange is", security.getMarketOfRisk().getLookupId());
		// validation.required("Registar Code is",
		// security.getRegistrarCode().getLookupId());
		validation.required("ISIN Code is", security.getIsinCode());
		validation.required("Settlement Period is", security.getSettlementDays());
		validation.required("Depository Code is", security.getDepositoryCode().getLookupId());

		if (!security.getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
			validation.required("External Code is", security.getExternalCode());
		}

		if (!security.isCheckTrade() && !security.isCheckAfs() && !security.isCheckHtm()) {
			security.setCheckTrade(null);
			validation.required("TRADE is", security.isCheckTrade());
		}

		if ((security.isCheckTrade() != null) && (security.isCheckTrade())) {
			validation.required("Valuation Method For TRADE is", security.getValuationMethodTrade().getLookupId());
		}

		if (security.isCheckAfs()) {
			validation.required("Valuation Method For AFS is", security.getValuationMethodAFS().getLookupId());
		}

		if (security.isCheckHtm()) {
			validation.required("Valuation Method For HTM is", security.getValuationMethodHTM().getLookupId());
		}

		if (security.getValuationMethodTrade() != null) {
			if (security.getValuationMethodTrade().getLookupId().equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
				validation.required("Market Price For TRADE is", security.getMarketPriceTrade().getLookupId());
			}

			if (security.getValuationMethodTrade().getLookupId().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
				validation.required("Market Price For TRADE is", security.getMarketPriceTrade().getLookupId());
				validation.required("Amortization Method For TRADE is", security.getAmortizationMethodTrade().getLookupId());
			}
		}

		if (security.getValuationMethodAFS() != null) {
			if (security.getValuationMethodAFS().getLookupId().equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
				validation.required("Market Price For AFS is", security.getMarketPriceAFS().getLookupId());
			}

			if (security.getValuationMethodAFS().getLookupId().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
				validation.required("Market Price For AFS is", security.getMarketPriceAFS().getLookupId());
				validation.required("Amortization Method For AFS is", security.getAmortizationMethodAFS().getLookupId());
			}
		}

		if (security.getValuationMethodHTM() != null) {
			if (security.getValuationMethodHTM().getLookupId().equals(LookupConstants.VALUATION_METHOD_FAIR_VALUE)) {
				validation.required("Market Price For HTM is", security.getMarketPriceHTM().getLookupId());
			}

			if (security.getValuationMethodHTM().getLookupId().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
				validation.required("Market Price For HTM is", security.getMarketPriceHTM().getLookupId());
				validation.required("Amortization Method For HTM is", security.getAmortizationMethodHTM().getLookupId());
			}
		}
		
		validation.required("Jenis Efek (APOLO) is", security.getApoloType().getLookupCode());

		// validation.required("Min. Transaction Quantity is",
		// security.getMinTrxQuantity());
		// validation.required("Initial Price is", security.getParUnitValue());

		if (security != null) {
			if (LookupConstants.SECURITY_CLASS_FIXED_INCOME_CODE.equals(security.getSecurityType().getSecurityClass().getLookupCode())) {
				validation.required("Issue Date is", security.getIssueDate());
				validation.required("Maturity Date is", security.getMaturityDate());
				validation.required("Accrual Type is", security.getAccrualBase().getLookupId());
				validation.required("Year Base is", security.getYearBase().getLookupId());
				validation.required("Frequency is", security.getFrequency().getLookupId());
				validation.required("Coupon Date is", security.getFirstCouponDate());
				validation.required("Interest Rate is", security.getInterestRate());
				validation.required("Total Coupons is", security.getTotalCoupon());
				if (security.getIsFraction() == true) {
					validation.required("Fraction Ratio is", security.getFractionRatioSource());
					validation.required("Fraction Amount is", security.getFractionRatioTarget());
				}
				if (security.getIsExpire() == true) {
					validation.required("Expired Date is", security.getExpiredDate());
				}
			}
			if (LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE.equals(security.getSecurityType().getSecurityClass().getLookupCode())) {

				validation.required("Issue Date is", security.getIssueDate());
				validation.required("Accrual Type is", security.getAccrualBase().getLookupId());
				validation.required("Year Base is", security.getYearBase().getLookupId());
				validation.required("Frequency is", security.getFrequency().getLookupId());
				if (security.getIsExpire() == true) {
					validation.required("Expired Date is", security.getExpiredDate());
				}
			}
			if (LookupConstants.SECURITY_CLASS_EQUITY_CODE.equals(security.getSecurityType().getSecurityClass().getLookupCode())) {
				validation.required("Issue Date is", security.getIssueDate());
				if (security.getIsExpire() == true) {
					validation.required("Expired Date is", security.getExpiredDate());
				}
			}

			if (validation.hasErrors()) {
				Map<String, String> data = new HashMap<String, String>();
				try {
					if (security.getUdf() != null)
						data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				}
				// udfs =
				// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							// udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
							// udf.getLookupGroup().getLookupGroup()));
						}
						// item.options =
						// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
						// item.getLookupGroup().getLookupGroup());
					}
				}
				// logger.debug("security frequency =>>>>     " +
				// security.getFrequency());
				// logger.debug("security parUnit =>>>>     " +
				// security.getParUnitValue());
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
				render("Securities/detail.html", security, mode, udfs, schedules, status, isreload);
			} else {
				Long id = security.getSecurityKey();
				serializerService.serialize(session.getId(), security.getSecurityKey(), security);
				confirming(id, mode, status, isreload);
			}
		} else {
			flash.error("argument.null", security);
		}
	}

	@Check("administration.securityInfo")
	public static void confirming(Long id, String mode, String status, boolean isreload) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status + " isreload: " + isreload);

		try {
			boolean confirming = true;
			ScMaster security = serializerService.deserialize(session.getId(), id, ScMaster.class);

			if (LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE.equals(security.getSecurityType().getSecurityClass().getLookupCode())) {
				security.setFrequency(generalService.getLookup(LookupConstants.FREQUENCY_YEAR));
			}
			Map<String, String> data = new HashMap<String, String>();
			if (security.getUdf() != null) {
				data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
			}
			List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// item.options =
					// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
					// item.getLookupGroup().getLookupGroup());
				}
			}
			if (security.getIsExpire() == true) {
				renderArgs.put("checkIsExpire", true);
			}

			security.setCheckTrade(security.getValuationMethodTrade() != null ? true : false);
			security.setCheckAfs(security.getValuationMethodAFS() != null ? true : false);
			security.setCheckHtm(security.getValuationMethodHTM() != null ? true : false);

			Set<ScCouponSchedule> schedules = security.getCouponSchedules();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
			render("Securities/detail.html", mode, confirming, security, schedules, udfs, status, isreload);

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.securityInfo")
	public static void confirm(ScMaster security, String mode, Set<ScCouponSchedule> schedules, GnUdfMaster[] udfs, String status, boolean isreload) {
		log.debug("confirm. security: " + security + " mode: " + mode + " schedules: " + schedules + " udfs: " + udfs + " status: " + status + " isreload: " + isreload);

		if (security == null)
			back(null, mode, security, null, status, isreload);
		try {
			Map<String, String> data = new HashMap<String, String>();
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}
			}
			String udfString = json.writeValueAsString(data);
			security.setUdf(udfString);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}

		if(security.getExternalType()==null)security.setExternalType(null);
		else if(security.getExternalType()!=null && security.getExternalType().getLookupId().equals("")) security.setExternalType(null);
		
		try {
			if (schedules != null) {
				for (ScCouponSchedule coupon : schedules) {
					if (coupon.getPaidInterest() == null)
						coupon.setPaidInterest(false);
					schedules.add(coupon);
				}
			}
			setData(security, schedules);
			securityService.saveSecurity(MenuConstants.SC_SECURITY, security, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			Map<String, String> data = new HashMap<String, String>();
			try {
				data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
			} catch (JsonParseException e1) {
				log.error(e1.getMessage(), e1);
			} catch (JsonMappingException e1) {
				log.error(e1.getMessage(), e1);
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}
			// udfs =
			// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
			
			
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						// udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
						// udf.getLookupGroup().getLookupGroup()));
					}
					// item.options =
					// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
					// item.getLookupGroup().getLookupGroup());
				}
			}
			flash.error("Security Code : ' " + security.getSecurityId() + " ' " + e.getMessage());
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
			render("Securities/detail.html", security, schedules, mode, confirming, udfs, status, isreload);
		}

	}

	@Check("administration.securityInfo")
	public static void back(Long id, String mode, ScMaster security, List<GnUdfMaster> udfs, String status, boolean isreload) {
		log.debug("back. id: " + id + " mode: " + mode + " security: " + security + " udfs: " + udfs + " status: " + status + " isreload: " + isreload);

		try {
			security = serializerService.deserialize(session.getId(), id, ScMaster.class);

			security.setCheckTrade(security.getValuationMethodTrade() != null ? true : false);
			security.setCheckAfs(security.getValuationMethodAFS() != null ? true : false);
			security.setCheckHtm(security.getValuationMethodHTM() != null ? true : false);

			// START UDFS
			Map<String, String> data = new HashMap<String, String>();
			if (security.getUdf() != null) {
				data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
			}

			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// item.options =
					// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
					// item.getLookupGroup().getLookupGroup());
				}
			}
			// END UDFS
			Set<ScCouponSchedule> schedules = security.getCouponSchedules();
			// status = status+" ";
			isreload = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_SECURITY));
			renderTemplate("Securities/detail.html", security, mode, schedules, udfs, status, isreload);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			List<GnUdfMaster> udfs = new ArrayList<GnUdfMaster>();
			Map<String, String> data = new HashMap<String, String>();

			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			ScMaster security = json.readValue(maintenanceLog.getNewData(), ScMaster.class);

			security.setCheckTrade(security.getValuationMethodTrade() != null ? true : false);
			security.setCheckAfs(security.getValuationMethodAFS() != null ? true : false);
			security.setCheckHtm(security.getValuationMethodHTM() != null ? true : false);

			// START UDFS
			if (security.getUdf() != null)
				data = json.readValue(security.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));

			udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_SECURITY);
			if (udfs != null) {
				for (GnUdfMaster udf : udfs) {

					if (security.getUdf() != null)
						udf.setValue(data.get(udf.getFieldName()));
					else
						udf.setValue("");

					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
					// item.options =
					// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
					// item.getLookupGroup().getLookupGroup());
				}
			}
			// END UDFS
			Set<ScCouponSchedule> schedules = security.getCouponSchedules();
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Securities/approval.html", security, mode, security.getCouponSchedules(), udfs, taskId, operation, maintenanceLogKey, schedules, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// //('${ctx}' + url + '?taskId=' + taskId + '&keyId=' + key +
		// '&maintenanceLogKey=' + maintenanceLogKey);
		// try {
		// securityService.approve(taskId, operation, maintenanceLogKey);
		// } catch (MedallionException me) {
		// logger.debug(me.getMessage(),me);
		// }
		//
	}

	// @Check("approvals")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveSecurity(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("approvals")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveSecurity(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	private static void setData(ScMaster security, Set<ScCouponSchedule> schedules) {
		if (security != null) {
			if (schedules != null) {
				security.setCouponSchedules(new HashSet<ScCouponSchedule>(schedules));
			}
		}
	}

	public static void populateCS(ScMaster security) {
		log.debug("populateCS. security: " + security);

		Set<ScCouponSchedule> schedules = securityService.populateCouponSchedule(security);
		render("Securities/gridFixedIncome.html", schedules);
	}

	public static void onChangeNextPaymentDate(Date date) {
		log.debug("onChangeNextPaymentDate. date: " + date);

		// Date paymentDate = generalService.addWorkingDate(date, 0); // redmine
		// #3226 date+1 sudah ada di securitiesDetail.js
		// $.post('@{onChangeNextPaymentDate()}
		// SimpleDateFormat dateFormat = new
		// SimpleDateFormat(UIConstants.DATE_FORMAT);
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		renderText(dateFormat.format(date));
	}

	// @Check("administration.securityInfo")
	public static void getValuationDefaultValues(String securityType) {
		log.debug("getValuationDefaultValues. securityType: " + securityType);

		// Boolean isPayable =
		// taService.isPayableTradeByProductCode(productCode, sellingAgentKey);
		ScTypeMaster scTypeMaster = securityService.getSecurityType(securityType);
		JsonHelper json = new JsonHelper().getSecurityTypeSerializer();
		String varsJson = "{}";
		try {
			varsJson = json.serialize(scTypeMaster);
		} catch (Exception ex) {
			log.error("error serializing", ex);
		}
		renderJSON(varsJson);
	}
}