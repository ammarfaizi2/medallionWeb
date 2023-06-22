package controllers;

import helpers.ApplicationProperties;
import helpers.MenuLoader;
import helpers.UIConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Router;
import play.mvc.With;

import com.fasterxml.jackson.module.hibernate.HibernateModule;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.ctplugin.service.CBestService;
import com.simian.medallion.helper.ClientFileService;
import com.simian.medallion.model.GnMenu;
import com.simian.medallion.model.GnPasswordParam;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.plugin.service.MailService;
import com.simian.medallion.plugin.service.UploadInvestorService;
import com.simian.medallion.plugin.service.UploadMarketPriceService;
import com.simian.medallion.plugin.service.UploadRedeemService;
import com.simian.medallion.plugin.service.UploadSubscriberService;
import com.simian.medallion.plugin.service.UploadSwitchingService;
import com.simian.medallion.pre.ReportGeneratorService;
import com.simian.medallion.service.AccountService;
import com.simian.medallion.service.AmlCustomerService;
import com.simian.medallion.service.ApplicationService;
import com.simian.medallion.service.ApprovalService;
import com.simian.medallion.service.BankAccountService;
import com.simian.medallion.service.BpjsService;
import com.simian.medallion.service.CAReportService;
import com.simian.medallion.service.CBestInterface;
import com.simian.medallion.service.CustomerService;
import com.simian.medallion.service.DepositoService;
import com.simian.medallion.service.DownloadUtilityService;
import com.simian.medallion.service.FundService;
import com.simian.medallion.service.GeneralService;
import com.simian.medallion.service.KycService;
import com.simian.medallion.service.MaintenanceService;
import com.simian.medallion.service.ReportQueueService;
import com.simian.medallion.service.SecurityService;
import com.simian.medallion.service.SerializerService;
import com.simian.medallion.service.SftpService;
import com.simian.medallion.service.ShareHolderService;
import com.simian.medallion.service.SimpleFileService;
import com.simian.medallion.service.SwiftService;
import com.simian.medallion.service.TaService;
import com.simian.medallion.service.TransactionService;
import com.simian.medallion.service.UploadMonitorService;
import com.simian.medallion.service.UploadUtilityService;
import com.simian.medallion.service.WebserviceBridgeService;
import com.simian.medallion.service.WorkflowService;

@With(Secure.class)
public abstract class MedallionController extends Controller {
	public static Logger log = Logger.getLogger(MedallionController.class);

	@Inject
	protected static AccountService accountService;

	@Inject
	protected static ApplicationService applicationService;

	@Inject
	protected static ApprovalService approvalService;

	@Inject
	protected static BankAccountService bankAccountService;

	@Inject
	protected static CustomerService customerService;

	@Inject
	protected static FundService fundService;

	@Inject
	protected static GeneralService generalService;

	@Inject
	protected static TaService taService;

	@Inject
	protected static SecurityService securityService;

	@Inject
	protected static SerializerService serializerService;

	@Inject
	protected static TransactionService transactionService;

	@Inject
	protected static WorkflowService workflowService;

	@Inject
	protected static MaintenanceService maintenanceService;

	@Inject
	protected static UploadInvestorService uploadInvestorService;

	@Inject
	protected static UploadSubscriberService uploadSubscriberService;

	@Inject
	protected static UploadRedeemService uploadRedeemService;

	@Inject
	protected static UploadSwitchingService uploadSwitchingService;

	@Inject
	protected static UploadMarketPriceService uploadMarketPriceService;

	@Inject
	protected static ShareHolderService shareHolderService;

	@Inject
	protected static CBestInterface cbestInterface;

	@Inject
	protected static CBestService cbestService;

	@Inject
	protected static ApplicationProperties appProp;

	@Inject
	protected static DownloadUtilityService downloadUtilityService;

	@Inject
	protected static UploadUtilityService uploadUtilityService;

	@Inject
	protected static SimpleFileService simpleFileService;

	@Inject
	protected static ClientFileService clientFileService;

	@Inject
	protected static ReportQueueService reportQueueService;

	@Inject
	protected static ReportGeneratorService reportGeneratorService;

	@Inject
	protected static UploadMonitorService uploadMonitorService;

	@Inject
	protected static CAReportService caReport;

	@Inject
	protected static KycService kycService;

	@Inject
	protected static DepositoService depositoService;
	
	@Inject
	protected static WebserviceBridgeService webserviceBridgeService;

	@Inject
	protected static MailService mailService;

	@Inject
	protected static SftpService sftpService;

	@Inject
	protected static BpjsService bpjsService;
	
	@Inject
	protected static SwiftService swiftService;
	
	@Inject
	protected static AmlCustomerService amlCustomerService;

	@Inject
	protected static String sessionTimeout;

	protected static ObjectMapper json = null;

	@Before(priority = 1, unless = { "login", "open" })
	public static void preceed() {
		Date dateCurrent = new Date();
		Long dateCekDateExpire = dateCurrent.getTime();
		if (session.contains("loginStartDate")) {
			Long startDateLogin = Long.parseLong(session.get(UIConstants.SESSION_LOGIN_START));
			if ((dateCekDateExpire - startDateLogin) < (Long.parseLong(sessionTimeout) * 60 * 1000)) {
				session.put(UIConstants.SESSION_LOGIN_START, dateCurrent.getTime());
			} else {
				generalService.unregisterSession(session.getId());
				String url = Router.getFullUrl("Application.index");
				try {
					Secure.logout(Long.parseLong(session.get(UIConstants.SESSION_AUDIT_TRAIL_KEY)));
				} catch (NumberFormatException e) {
					log.error(e.getMessage(), e);
					session.clear();
					redirect(url);
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					session.clear();
					redirect(url);
				}

			}
		}

		if (session.contains(UIConstants.SESSION_USERNAME)) {
			// request.remoteAddress
			// logger.debug(">>> [SHS] requestRemote "+ request.remoteAddress );
			// logger.debug(">>> [SHS] User : " + session.get("username"));
			// logger.debug(">>> [SHS] URL"+ request.url);

			// save log access
			applicationService.logAccess(session.get(UIConstants.SESSION_USERNAME), request.remoteAddress, request.url);

			String sessionId = session.get(UIConstants.SESSION_USERNAME);

			String menu = Cache.get("m_" + sessionId, String.class);
			if (menu == null) {
				//String access = "", shortcut = "";

				String username = session.get(UIConstants.SESSION_USERNAME);
				// String groupName = (String) Cache.get("g_" + sessionId);
				// String groupName = session.get("groupName");
				// String roleId = session.get("roleId");
				// logger.debug("groupName" +groupName);
				// logger.debug("ROLE KEY" +roleId);
				// String[] groups = groupName.split("\\|\\|");
				// logger.debug("groupName=" +groupName + "; groups.size=" +
				// groups.length);
				GnUser user = applicationService.getUser(username);

				// if time of password period > expire password warning period
				String loginType = generalService.getValueParam(SystemParamConstants.PARAM_SECURITY_LOGIN_APP_TYPE);

				if ("SYSTEM".equals(loginType)) {
					GnPasswordParam passParam = applicationService.getPasswordParam();
					Date today = new Date();
					if (user.getPasswordNeverExpired() == false) {
						if (passParam != null) {
							if ((passParam.getExpireWarningPeriod() != null) && (user.getPasswordLastChangeDate() != null)) {
								Integer longTimePassPeriod = today.getDate() - user.getPasswordLastChangeDate().getDate();
								if (longTimePassPeriod >= passParam.getExpireWarningPeriod()) {
									String warningError = "Password will expired soon. Please change your password!";
									flash.put("warningError", warningError);
								}
							}
						}
					}
				}

				if (user != null) {
					// String[] result =
					// MenuLoader.parseUserMenu(applicationService.listMenusForUserByGroupName(Arrays.asList(groups)));
					List<GnMenu> listMenu = applicationService.listMenusForUser(user.getUserKey());
					// logger.debug("MENU SIZE MED CONTRL = " +listMenu.size());
					if (listMenu.size() > 0) {
						String[] result = MenuLoader.parseUserMenu(listMenu);
						// logger.debug(">>> [JUN] result[1]=", result[1]);
						// logger.debug(">>> [JUN] result[2]=", result[2]);
						Cache.add("m_" + sessionId, result[0]);
						Cache.add("s_" + sessionId, result[1]);
						Cache.add("a_" + sessionId, result[2]);
					} else {
						String[] result = MenuLoader.parseUserMenu(null);
						// logger.debug(">>> [JUN] result[1]=", result[1]);
						// logger.debug(">>> [JUN] result[2]=", result[2]);
						Cache.add("m_" + sessionId, result[0]);
						Cache.add("s_" + sessionId, result[1]);
						Cache.add("a_" + sessionId, result[2]);
					}
					// logger.debug(">>> [JUN] result[0]=", result[0]);

				}
			}
			renderArgs.put("menu", Cache.get("m_" + sessionId));
			renderArgs.put("shortcut", Cache.get("s_" + sessionId));
			renderArgs.put("appProp", appProp);

			// String context = Play.configuration.getProperty("war.config",
			// "");
			// String baseUrl = request.url.replaceFirst(context, "");
			String baseUrl = request.url;
			Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
			if (urlList == null) {
				urlList = new HashMap<String, GnMenu>();
				Map<String, String> programList = new HashMap<String, String>();
				Map<String, String> menuAccessList = new HashMap<String, String>();
				MenuLoader.parseMenu(applicationService.listMenus(), urlList, programList, menuAccessList);
				Cache.set(UIConstants.CACHE_URLS, urlList);
				Cache.set(UIConstants.CACHE_PROGRAMS, programList);
				Cache.set(UIConstants.CACHE_MENU_ACCESS, menuAccessList);
				urlList = Cache.get("urlList", Map.class);
			}
			if (urlList.containsKey(baseUrl)) {
				GnMenu menuItem = urlList.get(baseUrl);
				String breadCrumb = String.format("%s (%s)", menuItem.getMenuBreadCrumb(), menuItem.getMenuNumber());
				flash.put("breadcrumb", breadCrumb);
			} else if ("/".equals(baseUrl)) {
				flash.remove("breadcrumb");
			}
		}
	}

	@Before(unless = { "entry", "list", "charges" })
	public static void setupJSON() {
		json = new ObjectMapper();
		json.registerModule(new HibernateModule());
		json.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		json.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
		json.configure(Feature.WRITE_NULL_MAP_VALUES, false);
		json.getSerializationConfig().withSerializationInclusion(Inclusion.NON_NULL);
	}

	protected static String serialize(Object object) {
		try {
			return json.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
			return "";
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
			return "";
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return "";
		}
	}

	private static GsonBuilder gsonBuilder = new GsonBuilder();
	private static DateTimeSerializer dateTimeSerializer = new DateTimeSerializer();
	private static DateTimeDeserializer dateTimeDeserializer = new DateTimeDeserializer();

	static {
		gsonBuilder.registerTypeAdapter(Date.class, dateTimeSerializer);
		gsonBuilder.registerTypeAdapter(Date.class, dateTimeDeserializer);
	}

	private static class DateTimeSerializer implements JsonSerializer<Date> {
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getTime());
		}
	}

	private static class DateTimeDeserializer implements JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return new Date(json.getAsJsonPrimitive().getAsLong());
		}
	}

	protected static <T> T toObject(String json, Class<T> clazz) {
		return gsonBuilder.create().fromJson(json, clazz);
	}

	protected static void renderGSON(Object o) {
		renderJSON(o, dateTimeSerializer);
	}

	protected static final String PROCESSMARK = "PROCESSMARK";
	protected static final String SUCCESS_COMPLIANCE_PRE = "Success";

	protected static void markSubmission(String menuCode) {
		String uuid = UUID.randomUUID().toString();
		session.put(PROCESSMARK + menuCode, uuid); // put to session
		params.put(PROCESSMARK, uuid); // put to param
	}

	protected static boolean isDoubleSubmission(String menuCode) {
		String sessionUuid = session.get(PROCESSMARK + menuCode); // sessionUUID
		String paramUuid = params.get(PROCESSMARK); // param UUID
		if (sessionUuid == null || paramUuid == null)
			return true;
		boolean result = !sessionUuid.equals(paramUuid);

		if (result == false) {
			String uuid = UUID.randomUUID().toString();
			session.put(PROCESSMARK + menuCode, uuid); // put to session
			params.put(PROCESSMARK, uuid); // put to param
		}

		return result;
	}

	@Finally
	public static void compress() throws IOException {
		// only zip html and json content type
		if (response != null && response.contentType != null && (response.contentType.indexOf("html") >= 0 || response.contentType.indexOf("json") >= 0)) {
			String text = response.out.toString();

			// if ("text/xml".equals(response.contentType)) {
			// text = new
			// com.googlecode.htmlcompressor.compressor.XmlCompressor().compress(text);
			// } else if
			// ("text/html; charset=utf-8".equals(response.contentType)) {
			// text = new
			// com.googlecode.htmlcompressor.compressor.HtmlCompressor().compress(text);
			// }
			final ByteArrayOutputStream gzip = gzip(text);
			response.setHeader("Content-Encoding", "gzip");
			response.setHeader("Content-Length", gzip.size() + "");
			response.out = gzip;
		}
	}

	public static ByteArrayOutputStream gzip(final String input) throws IOException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		final ByteArrayOutputStream stringOutputStream = new ByteArrayOutputStream((int) (input.length() * 0.75));
		final OutputStream gzipOutputStream = new GZIPOutputStream(stringOutputStream);

		final byte[] buf = new byte[5000];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			gzipOutputStream.write(buf, 0, len);
		}

		inputStream.close();
		gzipOutputStream.close();

		return stringOutputStream;
	}
}