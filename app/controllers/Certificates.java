package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.Play;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.CertificateSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsCertificate;
import com.simian.medallion.model.CsPortfolioTransaction;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionTemplate;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Certificates extends MedallionController {
	private static Logger log = Logger.getLogger(Certificates.class);

	@Before(only = { "list", UIConstants.PARAM_CERTIFICATE_DEDUPE })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);

		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);

		// String activeCertificateNo = "";
		// List<String> activeSertificateIds =
		// customerService.listAllActiveCertificateId();
		// for (String certId : activeSertificateIds) {
		// activeCertificateNo += certId + "|";
		// }
		//
		// renderArgs.put("activeCertificateNo", activeCertificateNo);

		GnLookup portMoveSKP = generalService.getLookup(LookupConstants._PORT_MOVE_SKP);
		renderArgs.put("portMoveSKP", portMoveSKP);

	}

	@Check("maintenance.certificate.register")
	public static void dedupe() {
		log.debug("dedupe. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String param = UIConstants.PARAM_CERTIFICATE_DEDUPE;
		// String breadcrumb = "Register";
		boolean dedupe = true;
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
		render("Certificates/list.html", mode, param, dedupe);
	}

	@Check("maintenance.certificate.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		String breadcrumb = "Inquiry";
		String module = null;
		/*
		 * if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
		 * flash.put(UIConstants.BREADCRUMB,
		 * applicationService.getMenuBreadcrumb
		 * (MenuConstants.CS_CERTIFICATE_ENTRY)); } else if
		 * (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
		 * flash.put(UIConstants.BREADCRUMB,
		 * applicationService.getMenuBreadcrumb
		 * (MenuConstants.CS_CERTIFICATE_EDIT)); } else {
		 * flash.put(UIConstants.BREADCRUMB,
		 * applicationService.getMenuBreadcrumb
		 * (MenuConstants.CS_CERTIFICATE_VIEW)); }
		 */
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
		renderTemplate("Certificates/list.html", mode, param, module, breadcrumb);
	}

	@Check("maintenance.certificate.list")
	public static void search(CertificateSearchParameters params) {
		log.debug("search. params: " + params);

		List<CsCertificate> certificates = customerService.searchCsCertificate(params.customerCodeId, params.securityType, params.securityKey, params.csCertificateCertificateId);
		render("Certificates/grid.html", certificates);
	}

	@Check("maintenance.certificate.list")
	public static void paging(Paging page, CertificateSearchParameters param) {
		log.debug("paging. page: " + page + " param: " + param);

		page.addParams("a.accountNo", Paging.EQUAL, param.accountNo);
		page.addParams("st.securityType", Paging.EQUAL, param.securityType);
		page.addParams("sc.securityKey", Paging.EQUAL, param.securityKey);
		page.addParams("cc.certificateId", param.certificateNoOperator, UIHelper.withOperator(param.csCertificateCertificateId, param.certificateNoOperator));
		log.debug("get search " + page.getsSearch());
		page.addParams(Helper.searchAll("(cc.certificateId||a.accountNo||a.name||sc.securityId||cc.quantity||cc.denomination||pm.lookupId)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = customerService.searchCsCertificatePaging(page);

		renderJSON(page);
	}

	@Check("maintenance.certificate.view")
	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsCertificate certificate = customerService.getCertificate(id);
		String attachement = null;
		if (!Helper.isNullOrEmpty(certificate.getFileAttachment())) {
			int dashPos = certificate.getFileAttachment().indexOf("-");
			attachement = certificate.getFileAttachment().substring(dashPos + 1);
			attachement = Helper.removeExtensionZip(attachement);
		}

		TdMaster tdMaster = new TdMaster();
		BigDecimal totQuantity = accountService.getPortfolioQuantity(certificate.getAccount().getCustodyAccountKey(), certificate.getSecurity().getSecurityKey());
		if (totQuantity != null) {
			certificate.setCurrentHolding(totQuantity.longValue());
		}

		log.debug("id-->" + id);
		if (id != null) {
			log.debug("not null");
			// certificate.setSecurity(securityService.getSecurity(certificate.getSecurity().getSecurityKey()));
			List<TdTransactionTemplate> transTemplate = new ArrayList<TdTransactionTemplate>();
			transTemplate = accountService.listTdTransactionTemplate(certificate.getSecurity().getSecurityType().getSecurityType());
			if (transTemplate.size() == 0) {
				certificate.setTabCertificate(new BigDecimal(0));
			} else {
				certificate.setTabCertificate(new BigDecimal(1));
				Set<TdMaster> tdMasters = certificate.getTdMaster();
				tdMaster = tdMasters.iterator().next();

				TdTransaction tdTransactionTab = generalService.getAmountTransaction(tdMaster.getDepositoKey());
				if (tdTransactionTab != null) {
					if (tdTransactionTab.getAmount() != null) {
						tdMaster.setNominal(tdTransactionTab.getAmount());
					}
				}
			}

		}

		certificate.setApplicationDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		certificate.putToList();
		List<CsTransaction> transactions = new ArrayList<CsTransaction>(certificate.getListTransactions());

		for (CsTransaction transaction : transactions) {
			log.debug("Trans Number = " + transaction.getTransactionNumber());
			//List<CsTransaction> cstransactions = accountService.listTransactionByTransNo(transaction.getTransactionNumber());
			CsPortfolioTransaction portfolio = accountService.getPortfolioTransaction(transaction.getTransactionNumber(), LookupConstants.__RECORD_STATUS_SETTLED);

			if (portfolio != null) {
				transaction.setHoldingRefs(portfolio.getHoldingRefs());
				/*** TRANSACTION STATUS "O" ***/
				/*
				 * if ((transaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_OPEN) &&
				 * (transaction.getTransactionTemplate
				 * ().getTransactionCategory()
				 * .getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_TRANSACTION))) ||
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_OPEN) ||
				 * (portfolio.getTransactionStatus().isEmpty()))){
				 * transaction.setTransactionStatus
				 * (LookupConstants.__RECORD_STATUS_OPEN); }
				 */

				/*** TRANSACTION STATUS "A" ***/
				/*
				 * if (((transaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_APPROVED)) &&
				 * (transaction.getTransactionTemplate
				 * ().getTransactionCategory()
				 * .getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_TRANSACTION))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
				 * transaction
				 * .setTransactionStatus(LookupConstants.__RECORD_STATUS_APPROVED
				 * ); }
				 */

				/*
				 * if (cstransactions.size()>1){ for (CsTransaction
				 * csTransaction : cstransactions) { if
				 * (csTransaction.getSettlementNumber()!=null){
				 *//*** TRANSACTION STATUS "SE" ***/
				/*
				 * if (((csTransaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_OPEN)) &&
				 * (csTransaction.getTransactionTemplate
				 * ().getTransactionCategory
				 * ().getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
				 * transaction.setTransactionStatus(LookupConstants.
				 * _TRANSACTION_STATUS_SETTLE_ENTRY); }
				 *//*** TRANSACTION STATUS "SA" ***/
				/*
				 * if (((csTransaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_APPROVED)) &&
				 * (csTransaction.getTransactionTemplate
				 * ().getTransactionCategory
				 * ().getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_SETTLED))){
				 * transaction.setTransactionStatus(LookupConstants.
				 * _TRANSACTION_STATUS_SETTLE_APPROVE); }
				 *//*** TRANSACTION STATUS "A" (Another Condition) ***/
				/*
				 * if (((transaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_REJECTED)) &&
				 * (transaction.getTransactionTemplate
				 * ().getTransactionCategory()
				 * .getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
				 * transaction
				 * .setTransactionStatus(LookupConstants.__RECORD_STATUS_APPROVED
				 * ); } } } }
				 */
			}
			Date trxdate = new Date(transaction.getTransactionDate().getTime());
			Date setdate = new Date(transaction.getSettlementDate().getTime());
			transaction.setTransactionDate(trxdate);
			transaction.setSettlementDate(setdate);
		}
		String certificateTrans = "";
		try {
			certificateTrans = json.writeValueAsString(certificate.getTransactions());
			log.debug("certificateTrans >>> " + certificateTrans);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		/*
		 * if (param != null) { flash.put(UIConstants.BREADCRUMB,
		 * applicationService
		 * .getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY)); } else {
		 * flash.put(UIConstants.BREADCRUMB,
		 * applicationService.getMenuBreadcrumb
		 * (MenuConstants.CS_CERTIFICATE_VIEW)); }
		 */
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
		render("Certificates/detail.html", certificate, mode, param, certificateTrans, tdMaster, attachement);
	}

	public static void attachmentDownload(String downloadfile) throws IOException {
		log.debug("attachmentDownload. downloadfile: " + downloadfile);

		CsCertificate certificate = customerService.getCertificate(new Long(downloadfile));
		String uploadedDirOutput = Play.configuration.getProperty("upload.certificate");
		String fullPath = uploadedDirOutput + certificate.getFileAttachment();
		File fileDest = new File(fullPath);
		// if( fileDest.exists() ){
		// logger.debug("no need to download "+downloadfile+", it's already downloaded");
		// }else{
		// logger.debug("download "+downloadfile+"...");
		// clientFileService.doDownload(downloadfile, fileDest,
		// SimpleFileService.UPLOAD_CERTIFICATE);
		// }
		// logger.debug( "fullPath:"+fullPath );
		// renderBinary(new File(fullPath), downloadfile);

		File file = null;
		try {
			file = Helper.extractSingleZipFile(fileDest);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (file == null) {
			renderJSON(json.writeValueAsString("not"));
			return;
		}

		String filename = null;
		int index;

		try {
			filename = file.getName();
			index = filename.indexOf("-");
			if (index != -1) {
				filename = filename.substring(index + 1);
			}

			if ((file.exists()) && (!Helper.isNullOrEmpty(filename))) {
				renderBinary(file, filename);
			} else {
				renderJSON(json.writeValueAsString("not"));
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	private static String copyFile(File source, File dest) {
		String fileName = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			fileName = dest.getName();
			IOUtils.copy(fis, fos);
			return fileName;
		} catch (Exception ex) {
			throw new RuntimeException("Error when moving file " + ex.getMessage());
		} finally {
			try {
				fos.flush();
				fis.close();
				fos.close();
				fis = null;
				fos = null;
			} catch (Exception ex) {
			}
		}
	}

	@Check("maintenance.certificate.register")
	public static void entry(Long id, Long key, String certificateId) {
		log.debug("entry. id: " + id + " key: " + key + " certificateId: " + certificateId);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsCertificate certificate = new CsCertificate();
		certificate.setCertificateDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		certificate.setApplicationDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		certificate.setQuantity(new Long(1));
		List<TdTransactionTemplate> transTemplate = new ArrayList<TdTransactionTemplate>();
		log.debug("id-->" + id);
		if (id != null) {
			log.debug("not null");
			certificate.setSecurity(securityService.getSecurity(id));
			transTemplate = accountService.listTdTransactionTemplate(certificate.getSecurity().getSecurityType().getSecurityType());
			if (transTemplate.size() == 0) {
				certificate.setTabCertificate(new BigDecimal(0));
			} else {
				certificate.setTabCertificate(new BigDecimal(1));
			}
		}
		log.debug("Size trans template " + transTemplate.size());

		// certificate.setCertificateId(certificateId);
		// certificate.setAccount(accountService.getAccount(key));
		String certificateTrans = "";
		try {
			certificateTrans = json.writeValueAsString(certificate.getTransactions());
			log.debug(">>> certificateTrans=" + certificateTrans);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
		render("Certificates/detail.html", mode, certificate, certificateTrans);
	}

	@Check("maintenance.certificate.edit")
	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsCertificate certificate = customerService.getCertificate(id);
		TdMaster tdMaster = new TdMaster();

		BigDecimal totQuantity = accountService.getPortfolioQuantity(certificate.getAccount().getCustodyAccountKey(), certificate.getSecurity().getSecurityKey());
		if (totQuantity != null) {
			certificate.setCurrentHolding(totQuantity.longValue());
		}

		String attachement = null;
		if (!Helper.isNullOrEmpty(certificate.getFileAttachment())) {
			int dashPos = certificate.getFileAttachment().indexOf("-");
			attachement = certificate.getFileAttachment().substring(dashPos + 1);
			attachement = Helper.removeExtensionZip(attachement);
		}

		List<TdTransactionTemplate> transTemplate = new ArrayList<TdTransactionTemplate>();
		transTemplate = accountService.listTdTransactionTemplate(certificate.getSecurity().getSecurityType().getSecurityType());
		if (transTemplate.size() == 0) {
			certificate.setTabCertificate(new BigDecimal(0));
		} else {
			certificate.setTabCertificate(new BigDecimal(1));
			Set<TdMaster> tdMasters = certificate.getTdMaster();
			tdMaster = tdMasters.iterator().next();
			TdTransaction tdTransactionTab = generalService.getAmountTransaction(tdMaster.getDepositoKey());
			if (tdTransactionTab != null) {
				if (tdTransactionTab.getAmount() != null) {
					tdMaster.setNominal(tdTransactionTab.getAmount());
				}
			}
		}

		certificate.setApplicationDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		certificate.putToList();

		String status = "";
		String certificateTrans = "";
		List<CsTransaction> transactions = new ArrayList<CsTransaction>(certificate.getListTransactions());
		for (CsTransaction transaction : transactions) {
			log.debug("Trans Number = " + transaction.getTransactionNumber());
			//List<CsTransaction> cstransactions = accountService.listTransactionByTransNo(transaction.getTransactionNumber());
			CsPortfolioTransaction portfolio = accountService.getPortfolioTransaction(transaction.getTransactionNumber(), LookupConstants.__RECORD_STATUS_SETTLED);
			if (portfolio != null) {
				transaction.setHoldingRefs(portfolio.getHoldingRefs());
				/*** TRANSACTION STATUS "O" ***/
				/*
				 * if ((transaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_OPEN) &&
				 * (transaction.getTransactionTemplate
				 * ().getTransactionCategory()
				 * .getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_TRANSACTION))) ||
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_OPEN) ||
				 * (portfolio.getTransactionStatus().isEmpty()))){
				 * transaction.setTransactionStatus
				 * (LookupConstants.__RECORD_STATUS_OPEN); }
				 *//*** TRANSACTION STATUS "A" ***/
				/*
				 * if (((transaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_APPROVED)) &&
				 * (transaction.getTransactionTemplate
				 * ().getTransactionCategory()
				 * .getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_TRANSACTION))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
				 * transaction
				 * .setTransactionStatus(LookupConstants.__RECORD_STATUS_APPROVED
				 * ); }
				 * 
				 * if (cstransactions.size()>1){ for (CsTransaction
				 * csTransaction : cstransactions) { if
				 * (csTransaction.getSettlementNumber()!=null){
				 *//*** TRANSACTION STATUS "SE" ***/
				/*
				 * if (((csTransaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_OPEN)) &&
				 * (csTransaction.getTransactionTemplate
				 * ().getTransactionCategory
				 * ().getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
				 * transaction.setTransactionStatus(LookupConstants.
				 * _TRANSACTION_STATUS_SETTLE_ENTRY); }
				 *//*** TRANSACTION STATUS "SA" ***/
				/*
				 * if (((csTransaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_APPROVED)) &&
				 * (csTransaction.getTransactionTemplate
				 * ().getTransactionCategory
				 * ().getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_SETTLED))){
				 * transaction.setTransactionStatus(LookupConstants.
				 * _TRANSACTION_STATUS_SETTLE_APPROVE); }
				 *//*** TRANSACTION STATUS "A" (Another Condition) ***/
				/*
				 * if (((transaction.getTransactionStatus().trim().equals(
				 * LookupConstants.__RECORD_STATUS_REJECTED)) &&
				 * (transaction.getTransactionTemplate
				 * ().getTransactionCategory()
				 * .getLookupId().equals(LookupConstants
				 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
				 * (portfolio.getTransactionStatus
				 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED))){
				 * transaction
				 * .setTransactionStatus(LookupConstants.__RECORD_STATUS_APPROVED
				 * ); } } } }
				 */
			}
			Date trxdate = new Date(transaction.getTransactionDate().getTime());
			Date setdate = new Date(transaction.getSettlementDate().getTime());
			transaction.setTransactionDate(trxdate);
			transaction.setSettlementDate(setdate);
		}
		try {
			certificateTrans = json.writeValueAsString(certificate.getTransactions());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_EDIT));
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
		render("Certificates/detail.html", certificate, mode, certificateTrans, status, param, tdMaster, attachement);
	}

	@Check("maintenance.certificate.save")
	public static void save(String mode, CsCertificate certificate, TdMaster tdMaster, List<CsTransaction> listTransactions, String status, String param, File attachment) throws ZipException {
		log.debug("save. mode: " + mode + " certificate: " + certificate + " tdMaster: " + tdMaster + " listTransactions: " + listTransactions + " status: " + status + " param: " + param + " attachment: " + attachment);

		if (certificate != null) {

			// Untuk mengakali jika deposiNo terisi tetapi depositoKey kosong di
			// screen
			if (tdMaster.getDepositoNo() != null) {
				if (tdMaster.getDepositoKey() == null) {
					TdMaster deposito = depositoService.getSingleDeposito(tdMaster.getDepositoNo());
					if (deposito != null) {
						tdMaster.setDepositoKey(deposito.getDepositoKey());
					}
				}
			}

			validation.required("Account No is", certificate.getAccount().getAccountNo());
			validation.required("Security Type is", certificate.getSecurity().getSecurityType().getSecurityType());
			validation.required("Security Code is", certificate.getSecurity().getSecurityId());
			validation.required("Certificate No is", certificate.getCertificateId());
			validation.required("Certificate Date is", certificate.getCertificateDate());
			validation.required("Certificate Quantity", certificate.getQuantity());
			validation.required("Denomination is", certificate.getDenomination());
			validation.required("Port Move is", certificate.getPortMode().getLookupId());

			if (certificate.getCertificateDate() != null) {
				log.debug("certificate not null");
				log.debug("time cer date " + certificate.getCertificateDate().getTime() + "time app date " + generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate().getTime());
				if (certificate.getCertificateDate().getTime() > generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate().getTime()) {
					log.debug("masuk");
					validation.addError("", "Certificate Date can not greater than application date");
				}
			}

			if (certificate.getTabCertificate() != null) {

				if (certificate.getTabCertificate().equals(BigDecimal.ONE)) {
					validation.required("Deposito No is", tdMaster.getDepositoNo());
				}
			}

			if (certificate.isAttached()) {
				if (certificate.getFileAttachment().isEmpty()) {
					validation.required("Attach Document in tab Additional is", attachment);
				}
			}

			if ((certificate.getSecurity() != null) && (certificate.getCertificateId() != null)) {
				CsCertificate oldCertificate = null;
				oldCertificate = customerService.getCertificateBySecurity(certificate.getSecurity().getSecurityKey(), certificate.getCertificateId());
				if (certificate.getCertificateKey() != null && oldCertificate != null) {
					if (!certificate.getCertificateKey().equals(oldCertificate.getCertificateKey())) {
						validation.addError("", "Certificate No is Already Exist");
					}
				} else {
					if (oldCertificate != null)
						validation.addError("", "Certificate No is Already Exist");
				}

			}

			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				param = "";
			} else {
				param = "edit";
			}

			boolean isAttachment = false;

			if (attachment != null) {
				String attachement = null;
				if (!Helper.isNullOrEmpty(certificate.getFileAttachment())) {
					int dashPos = certificate.getFileAttachment().indexOf("-");
					attachement = certificate.getFileAttachment().substring(dashPos + 1);
					attachement = Helper.removeExtensionZip(attachement);
				}

				log.debug("Attachment not null");
				String uploadDir = Play.configuration.getProperty("upload.certificate");
				log.debug("uploadDir " + uploadDir);
				String newFileName = uploadDir + System.currentTimeMillis() + '-' + attachment.getName();
				// String newFileName = attachment.getName();
				log.debug("File name " + newFileName);
				File output = new File(newFileName);
				copyFile(attachment, output);

				File zipoutput = Helper.createZipFile(Helper.convertToZip(newFileName, true), output.getAbsolutePath());
				output.delete();

				certificate.setFileAttachment(zipoutput.getName());
				isAttachment = true;
			} else {
				log.debug("Attachment null");
			}

			if (validation.hasErrors()) {
				log.debug("masuk erroe");
				/*
				 * if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				 * flash.put(UIConstants.BREADCRUMB,
				 * applicationService.getMenuBreadcrumb
				 * (MenuConstants.CS_CERTIFICATE)); } else {
				 * flash.put(UIConstants.BREADCRUMB,
				 * applicationService.getMenuBreadcrumb
				 * (MenuConstants.CS_CERTIFICATE_EDIT)); }
				 */
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
				String certificateTrans = "";
				// if(listTransactions != null){(
				if (listTransactions == null) {
					listTransactions = new ArrayList<CsTransaction>();
				}
				for (CsTransaction csTransaction : listTransactions) {
					log.debug("Security key " + csTransaction.getSecurity().getSecurityKey());
					log.debug("Security id " + csTransaction.getSecurity().getSecurityId());
					log.debug("Quantity " + csTransaction.getQuantity());
				}

				try {
					certificateTrans = json.writeValueAsString(listTransactions);
					// logger.debug(">>> certificateTrans=" + certificateTrans);
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
				// }

				render("Certificates/detail.html", certificate, mode, status, param, tdMaster, certificateTrans, isAttachment, attachment);

			} else {

				if (listTransactions != null) {
					for (CsTransaction transaction : listTransactions) {
						if (transaction != null) {
							certificate.getTransactions().add(transaction);
						}
					}
				}
				Set<TdMaster> tdMasters = new HashSet<TdMaster>();
				if (tdMasters != null) {
					tdMasters.add(tdMaster);
				}
				certificate.setTdMaster(tdMasters);
				Long id = certificate.getCertificateKey();
				log.debug("ID conf >>>> " + id);
				serializerService.serialize(session.getId(), certificate.getCertificateKey(), certificate);
				confirming(id, mode, status, param, isAttachment);
			}
		} else {
			// flash.error("argument.null", certificate);
			dedupe();
		}
	}

	@Check("maintenance.certificate.save")
	public static void confirming(Long id, String mode, String status, String param, boolean isAttachment) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status + " param: " + param + " isAttachment: " + isAttachment);

		try {
			log.debug("ID conf >>>> " + id);
			renderArgs.put("confirming", true);
			CsCertificate certificate = serializerService.deserialize(session.getId(), id, CsCertificate.class);
			log.debug("Currenct2--> " + certificate.getSecurity().getCurrency().getCurrencyCode());
			log.debug("ID conf >>>> " + certificate.getCertificateKey());

			String attachement = null;
			if (!Helper.isNullOrEmpty(certificate.getFileAttachment())) {
				int dashPos = certificate.getFileAttachment().indexOf("-");
				attachement = certificate.getFileAttachment().substring(dashPos + 1);
				attachement = Helper.removeExtensionZip(attachement);
			}

			Set<TdMaster> tdMasters = certificate.getTdMaster();
			TdMaster tdMaster = tdMasters.iterator().next();
			String certificateTrans = "";
			try {
				certificateTrans = json.writeValueAsString(certificate.getTransactions());
				log.debug(">>> certificateTrans=" + certificateTrans);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			log.debug("Param >>>> " + param);
			/*
			 * if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			 * flash.put(UIConstants.BREADCRUMB,
			 * applicationService.getMenuBreadcrumb
			 * (MenuConstants.CS_CERTIFICATE_EDIT)); } else {
			 * flash.put(UIConstants.BREADCRUMB,
			 * applicationService.getMenuBreadcrumb
			 * (MenuConstants.CS_CERTIFICATE_ENTRY)); }
			 */
			// flash.put(UIConstants.BREADCRUMB,
			// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
			render("Certificates/detail.html", certificate, certificateTrans, mode, status, param, tdMaster, isAttachment, attachement);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("maintenance.certificate.save")
	public static void confirm(String mode, CsCertificate certificate, TdMaster tdMaster, List<CsTransaction> listTransactions, String status) {
		log.debug("confirm. mode: " + mode + " certificate: " + certificate + " tdMaster: " + tdMaster + " listTransactions: " + listTransactions + " status: " + status);

		boolean confirming = true;
		String certificateTrans = "";
		if (listTransactions == null) {
			listTransactions = new ArrayList<CsTransaction>();
		}

		try {
			// JsonHelper json = new
			// JsonHelper().getTransactionForCertificateSerializer();
			// certificateTrans = json.serialize(listTransactions);
			certificateTrans = json.writeValueAsString(listTransactions);
			log.debug(">>> certificateTrans=" + certificateTrans);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		Set<TdMaster> tdMasters = new HashSet<TdMaster>();
		if (tdMaster != null) {
			tdMasters.add(tdMaster);
		}
		certificate.setTdMaster(tdMasters);
		try {
			if (listTransactions != null) {
				for (CsTransaction transaction : listTransactions) {
					if (transaction != null) {
						certificate.getTransactions().add(transaction);
					}
				}
			}
			String menuCode = null;
			log.debug(">>> MODE = " + mode);
			/*
			 * if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) { menuCode =
			 * MenuConstants.CS_CERTIFICATE_ENTRY; } else { menuCode =
			 * MenuConstants.CS_CERTIFICATE_EDIT; }
			 */
			menuCode = MenuConstants.CS_CERTIFICATE;
			log.debug("certificate id >>>> " + certificate.getCertificateKey());
			CsCertificate csCertificate = customerService.saveCertificate(menuCode, certificate, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			log.debug("certificate no >>>> " + csCertificate.getCertificateId());
			log.debug("certificate id 2 --> " + certificate.getCertificateId());
			// CsCertificate csCertificate = new CsCertificate();

			if (csCertificate.getCertificateKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					log.debug("masuk edit");
					result.put("message", Messages.get("certificate.confirmed.successful.edit", certificate.getCertificateId()));
					// result.put("message", "TESTING EDIT");
				}
				if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
					log.debug("masuk entry" + csCertificate.getCertificateId());
					result.put("message", Messages.get("certificate.confirmed.successful", certificate.getCertificateId()));
				}
				renderJSON(result);
			} else {
				log.debug("gak masuk");
				// flash.put(UIConstants.BREADCRUMB,
				// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_EDIT));
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
				render("Certificates/detail.html", certificate, mode, confirming, certificateTrans, status, tdMaster);
			}

			// dedupe();
		} catch (MedallionException e) {
			validation.clear();
			String param = null;
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				param = UIConstants.DISPLAY_MODE_EDIT;
			}
			// flash.put(UIConstants.BREADCRUMB,
			// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_ENTRY));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
			if (param != null) {
				if (param.equals(UIConstants.DISPLAY_MODE_EDIT)) {
					// flash.put(UIConstants.BREADCRUMB,
					// applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE_EDIT));
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CERTIFICATE));
				}
			}
			log.debug("gak certificate");
			flash.error("Certificate : ' " + certificate.getCertificateId() + " ' " + Messages.get(e.getMessage()));
			render("Certificates/detail.html", certificate, mode, confirming, certificateTrans, status, param, tdMaster);
			log.debug("Masuk render");
			/*
			 * Map<String, Object> result = new HashMap<String, Object>();
			 * result.put("status", "error"); List<String> params = new
			 * ArrayList<String>(); if (ex.getParams()!=null){ for (Object
			 * paramItem : ex.getParams()) {
			 * params.add(Messages.get(paramItem)); }
			 * 
			 * result.put("error", Messages.get("error." + ex.getErrorCode(),
			 * params.toArray())); } else { result.put("error",
			 * Messages.get("error." + ex.getErrorCode())); }
			 * renderJSON(result);
			 */
		}
	}

	@Check("maintenance.certificate.save")
	public static void back(Long id, String mode, String status, String param) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status + " param: " + param);

		try {
			log.debug("ID >>>>" + id);
			CsCertificate certificate = serializerService.deserialize(session.getId(), id, CsCertificate.class);
			String certificateTrans = "";
			try {
				certificateTrans = json.writeValueAsString(certificate.getTransactions());
				log.debug(">>> certificateTrans=" + certificateTrans);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			Set<TdMaster> tdMasters = certificate.getTdMaster();
			TdMaster tdMaster = tdMasters.iterator().next();

			// if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			// flash.put(UIConstants.BREADCRUMB,
			// applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
			// } else {
			// flash.put(UIConstants.BREADCRUMB,
			// applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_EDIT));
			// }
			String attachement = null;
			if (!Helper.isNullOrEmpty(certificate.getFileAttachment())) {
				int dashPos = certificate.getFileAttachment().indexOf("-");
				attachement = certificate.getFileAttachment().substring(dashPos + 1);
				attachement = Helper.removeExtensionZip(attachement);
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CF_MASTER_ENTRY));
			log.debug("Cek id >>> " + certificate.getCertificateKey());
			render("Certificates/detail.html", certificate, mode, certificateTrans, status, param, tdMaster, attachement);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			log.debug("maintenanceLogKey = " + maintenanceLogKey);
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			boolean approval = true;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsCertificate certificate = json.readValue(maintenanceLog.getNewData(), CsCertificate.class);
			TdMaster tdMaster = new TdMaster();

			BigDecimal totQuantity = accountService.getPortfolioQuantity(certificate.getAccount().getCustodyAccountKey(), certificate.getSecurity().getSecurityKey());
			if (totQuantity != null) {
				certificate.setCurrentHolding(totQuantity.longValue());
			}
			String attachement = null;
			if (!Helper.isNullOrEmpty(certificate.getFileAttachment())) {
				int dashPos = certificate.getFileAttachment().indexOf("-");
				attachement = certificate.getFileAttachment().substring(dashPos + 1);
				attachement = Helper.removeExtensionZip(attachement);
			}

			List<TdTransactionTemplate> transTemplate = new ArrayList<TdTransactionTemplate>();
			transTemplate = accountService.listTdTransactionTemplate(certificate.getSecurity().getSecurityType().getSecurityType());
			if (transTemplate.size() == 0) {
				certificate.setTabCertificate(new BigDecimal(0));
			} else {
				certificate.setTabCertificate(new BigDecimal(1));
				Set<TdMaster> tdMasters = certificate.getTdMaster();
				tdMaster = tdMasters.iterator().next();
				TdTransaction tdTransactionTab = generalService.getAmountTransaction(tdMaster.getDepositoKey());
				if (tdTransactionTab != null) {
					if (tdTransactionTab.getAmount() != null) {
						tdMaster.setNominal(tdTransactionTab.getAmount());
					}
				}
			}

			ScMaster security = securityService.getSecurity(certificate.getSecurity().getSecurityKey());
			tdMaster.setSecurity(security);
			certificate.setApplicationDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
			certificate.setSecurity(security);

			Set<CsTransaction> transactions = certificate.getTransactions();
			for (CsTransaction transaction : transactions) {
				//List<CsTransaction> cstransactions = accountService.listTransactionByTransNo(transaction.getTransactionNumber());
				CsTransaction trx = accountService.getTransaction(transaction.getTransactionKey());
				CsPortfolioTransaction portfolio = accountService.getPortfolioTransaction(transaction.getTransactionNumber(), LookupConstants.__RECORD_STATUS_SETTLED);
				transaction.setTransactionTemplate(trx.getTransactionTemplate());
				transaction.setAccount(trx.getAccount());
				transaction.setSecurity(trx.getSecurity());
				if (portfolio != null) {
					transaction.setHoldingRefs(portfolio.getHoldingRefs());
					/*** TRANSACTION STATUS "O" ***/
					/*
					 * if ((transaction.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_OPEN) &&
					 * (transaction.getTransactionTemplate
					 * ().getTransactionCategory
					 * ().getLookupId().equals(LookupConstants
					 * .TRANSACTION_CATEGORY_TRANSACTION))) ||
					 * (portfolio.getTransactionStatus
					 * ().trim().equals(LookupConstants.__RECORD_STATUS_OPEN) ||
					 * (portfolio.getTransactionStatus().isEmpty()))){
					 * transaction
					 * .setTransactionStatus(LookupConstants.__RECORD_STATUS_OPEN
					 * ); }
					 *//*** TRANSACTION STATUS "A" ***/
					/*
					 * logger.debug("transaction.transactionTemplate = " +
					 * transaction.getTransactionTemplate()); logger.debug(
					 * "transaction.transactionTemplate.transactionCategory = "
					 * +
					 * transaction.getTransactionTemplate().getTransactionCategory
					 * ().getLookupId()); if
					 * (((transaction.getTransactionStatus(
					 * ).trim().equals(LookupConstants
					 * .__RECORD_STATUS_APPROVED)) &&
					 * (transaction.getTransactionTemplate
					 * ().getTransactionCategory
					 * ().getLookupId().equals(LookupConstants
					 * .TRANSACTION_CATEGORY_TRANSACTION))) &&
					 * (portfolio.getTransactionStatus
					 * ().trim().equals(LookupConstants
					 * .__RECORD_STATUS_APPROVED))){
					 * transaction.setTransactionStatus
					 * (LookupConstants.__RECORD_STATUS_APPROVED); }
					 * 
					 * if (cstransactions.size()>1){ for (CsTransaction
					 * csTransaction : cstransactions) { if
					 * (csTransaction.getSettlementNumber()!=null){
					 *//*** TRANSACTION STATUS "SE" ***/
					/*
					 * if (((csTransaction.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_OPEN)) &&
					 * (csTransaction.getTransactionTemplate
					 * ().getTransactionCategory
					 * ().getLookupId().equals(LookupConstants
					 * .TRANSACTION_CATEGORY_SETTLEMENT))) &&
					 * (portfolio.getTransactionStatus
					 * ().trim().equals(LookupConstants
					 * .__RECORD_STATUS_APPROVED))){
					 * transaction.setTransactionStatus
					 * (LookupConstants._TRANSACTION_STATUS_SETTLE_ENTRY); }
					 *//*** TRANSACTION STATUS "SA" ***/
					/*
					 * if (((csTransaction.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_APPROVED)) &&
					 * (csTransaction
					 * .getTransactionTemplate().getTransactionCategory
					 * ().getLookupId
					 * ().equals(LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT
					 * ))) && (portfolio.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_SETTLED))){
					 * transaction.setTransactionStatus
					 * (LookupConstants._TRANSACTION_STATUS_SETTLE_APPROVE); }
					 *//*** TRANSACTION STATUS "A" (Another Condition) ***/
					/*
					 * if (((transaction.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_REJECTED)) &&
					 * (transaction
					 * .getTransactionTemplate().getTransactionCategory
					 * ().getLookupId
					 * ().equals(LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT
					 * ))) && (portfolio.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_APPROVED))){
					 * transaction.setTransactionStatus
					 * (LookupConstants.__RECORD_STATUS_APPROVED); } } } }
					 */
				}
			}

			String certificateTrans = "";
			try {
				certificateTrans = json.writeValueAsString(certificate.getTransactions());
				log.debug(">>> certificateTrans=" + certificateTrans);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Certificates/approval.html", certificate, transactions, tdMaster, certificateTrans, mode, taskId, operation, maintenanceLogKey, from, attachement, approval);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			customerService.approveCertificate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			customerService.approveCertificate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
