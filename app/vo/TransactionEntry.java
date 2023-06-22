package vo;

import play.db.jpa.Model;

public class TransactionEntry extends Model {
	public String transactionDate;
	public String transactionType;
	public String transactionCode;
	public String accountKey;
	public String securityType;
	public String securitySubType;
	public String securityTypeId;
	public String securityTypeIdDesc;
	public String settlementDate;
	public String customerName;
	public String transactionTypeDesc;
	public String transactionCodeDesc;
	public String securityTypeDesc;
	public String securitySubTypeDesc;
	public String brokerStatus;
	public String brokerStatusDesc;
	public String proceed;
	public String totalFee;
	public String settleAmount;
	public String fee1;
	public String fee2;
	public String fee3;
	public String feeAmount1;
	public String feeAmount2;
	public String feeAmount3;
	public String coba;
	// transaction entry Bonds & SHS
	public String quantity;
	public String price;
	public String nominal;
	public String capGainTax;
	// transaction Entry bonds
	public String interestRate;
	public String accruedInterest;
	public String accuralDays;
	public String interestTax;
	public String netInterest;
	// transaction Entry SBI
	public String discountAmount;
	public String discountTax;
	public String maturityDate;
	public String discountDays;
	public String discountRate;
	// Transaction entry doc
	public String interestAmount;
	public String netInterestRate;
	public String bilyetNumber;

	// transaction Entry SHS
	public TransactionEntry(String transactionDate, String accountKey, String transactionType, String transactionCode, String securityType, String securitySubType, String securityTypeId, String settlementDate, String customerName, String transactionTypeDesc, String transactionCodeDesc, String securityTypeDesc, String securitySubTypeDesc, String securityTypeIdDesc, String brokerStatus, String brokerStatusDesc, String proceed, String totalFee, String settleAmount, String fee1, String fee2, String fee3, String feeAmount1, String feeAmount2, String feeAmount3, String quantity, String price, String nominal, String capGainTax) {
		this.transactionDate = transactionDate;
		this.settlementDate = settlementDate;
		this.transactionType = transactionType;
		this.transactionCode = transactionCode;
		this.accountKey = accountKey;
		this.securityType = securityType;
		this.securitySubType = securitySubType;
		this.securityTypeId = securityTypeId;
		this.securityTypeIdDesc = securityTypeIdDesc;
		this.customerName = customerName;
		this.transactionTypeDesc = transactionTypeDesc;
		this.transactionCodeDesc = transactionCodeDesc;
		this.securityTypeDesc = securityTypeDesc;
		this.securitySubTypeDesc = securitySubTypeDesc;
		this.brokerStatus = brokerStatus;
		this.brokerStatusDesc = brokerStatusDesc;
		this.proceed = proceed;
		this.totalFee = totalFee;
		this.settleAmount = settleAmount;
		this.fee1 = fee1;
		this.fee2 = fee2;
		this.fee3 = fee3;
		this.feeAmount1 = feeAmount1;
		this.feeAmount2 = feeAmount2;
		this.feeAmount3 = feeAmount3;
		// transaction entry SHS
		this.quantity = quantity;
		this.price = price;
		this.nominal = nominal;
		this.capGainTax = capGainTax;

	}

	// transaction entry Bonds
	public TransactionEntry(String transactionDate, String accountKey, String transactionType, String transactionCode, String securityType, String securitySubType, String securityTypeId, String settlementDate, String customerName, String transactionTypeDesc, String transactionCodeDesc, String securityTypeDesc, String securitySubTypeDesc, String securityTypeIdDesc, String brokerStatus, String brokerStatusDesc, String proceed, String totalFee, String settleAmount, String fee1, String fee2, String fee3, String feeAmount1, String feeAmount2, String feeAmount3, String quantity, String price, String nominal, String capGainTax, String interestRate, String accuralDays, String accruedInterest, String interestTax, String netInterest) {
		this.transactionDate = transactionDate;
		this.settlementDate = settlementDate;
		this.transactionType = transactionType;
		this.transactionCode = transactionCode;
		this.accountKey = accountKey;
		this.securityType = securityType;
		this.securitySubType = securitySubType;
		this.securityTypeId = securityTypeId;
		this.securityTypeIdDesc = securityTypeIdDesc;
		this.customerName = customerName;
		this.transactionTypeDesc = transactionTypeDesc;
		this.transactionCodeDesc = transactionCodeDesc;
		this.securityTypeDesc = securityTypeDesc;
		this.securitySubTypeDesc = securitySubTypeDesc;
		this.brokerStatus = brokerStatus;
		this.brokerStatusDesc = brokerStatusDesc;
		this.proceed = proceed;
		this.totalFee = totalFee;
		this.settleAmount = settleAmount;
		this.fee1 = fee1;
		this.fee2 = fee2;
		this.fee3 = fee3;
		this.feeAmount1 = feeAmount1;
		this.feeAmount2 = feeAmount2;
		this.feeAmount3 = feeAmount3;
		// Transaction entry Bonds
		this.quantity = quantity;
		this.price = price;
		this.nominal = nominal;
		this.capGainTax = capGainTax;
		this.interestRate = interestRate;
		this.accruedInterest = accruedInterest;
		this.accuralDays = accuralDays;
		this.interestTax = interestTax;
		this.netInterest = netInterest;
	}

	// transaction entry SBI
	public TransactionEntry(String transactionDate, String accountKey, String transactionType, String transactionCode, String securityType, String securitySubType, String securityTypeId, String settlementDate, String customerName, String transactionTypeDesc, String transactionCodeDesc, String securityTypeDesc, String securitySubTypeDesc, String securityTypeIdDesc, String brokerStatus, String brokerStatusDesc, String proceed, String totalFee, String settleAmount, String fee1, String fee2, String fee3, String feeAmount1, String feeAmount2, String feeAmount3, String nominal, String discountAmount, String discountTax, String maturityDate, String discountDays, String discountRate) {
		this.transactionDate = transactionDate;
		this.settlementDate = settlementDate;
		this.transactionType = transactionType;
		this.transactionCode = transactionCode;
		this.accountKey = accountKey;
		this.securityType = securityType;
		this.securitySubType = securitySubType;
		this.securityTypeId = securityTypeId;
		this.securityTypeIdDesc = securityTypeIdDesc;
		this.customerName = customerName;
		this.transactionTypeDesc = transactionTypeDesc;
		this.transactionCodeDesc = transactionCodeDesc;
		this.securityTypeDesc = securityTypeDesc;
		this.securitySubTypeDesc = securitySubTypeDesc;
		this.brokerStatus = brokerStatus;
		this.brokerStatusDesc = brokerStatusDesc;
		this.proceed = proceed;
		this.totalFee = totalFee;
		this.settleAmount = settleAmount;
		this.fee1 = fee1;
		this.fee2 = fee2;
		this.fee3 = fee3;
		this.feeAmount1 = feeAmount1;
		this.feeAmount2 = feeAmount2;
		this.feeAmount3 = feeAmount3;
		// Transaction entrySBI
		this.nominal = nominal;
		this.discountAmount = discountAmount;
		this.discountTax = discountTax;
		this.maturityDate = maturityDate;
		this.discountDays = discountDays;
		this.discountRate = discountRate;

	}

	// transaction entry DOC
	public TransactionEntry(String transactionDate, String accountKey, String transactionType, String transactionCode, String securityType, String securitySubType, String securityTypeId, String settlementDate, String customerName, String transactionTypeDesc, String transactionCodeDesc, String securityTypeDesc, String securitySubTypeDesc, String securityTypeIdDesc, String brokerStatus, String brokerStatusDesc, String proceed, String totalFee, String settleAmount, String fee1, String fee2, String fee3, String feeAmount1, String feeAmount2, String feeAmount3, String nominal, String maturityDate, String accuralDays, String interestRate, String interestAmount, String interestTax, String netInterestRate, String bilyetNumber) {
		this.transactionDate = transactionDate;
		this.settlementDate = settlementDate;
		this.transactionType = transactionType;
		this.transactionCode = transactionCode;
		this.accountKey = accountKey;
		this.securityType = securityType;
		this.securitySubType = securitySubType;
		this.securityTypeId = securityTypeId;
		this.securityTypeIdDesc = securityTypeIdDesc;
		this.customerName = customerName;
		this.transactionTypeDesc = transactionTypeDesc;
		this.transactionCodeDesc = transactionCodeDesc;
		this.securityTypeDesc = securityTypeDesc;
		this.securitySubTypeDesc = securitySubTypeDesc;
		this.brokerStatus = brokerStatus;
		this.brokerStatusDesc = brokerStatusDesc;
		this.proceed = proceed;
		this.totalFee = totalFee;
		this.settleAmount = settleAmount;
		this.fee1 = fee1;
		this.fee2 = fee2;
		this.fee3 = fee3;
		this.feeAmount1 = feeAmount1;
		this.feeAmount2 = feeAmount2;
		this.feeAmount3 = feeAmount3;
		// Transaction entry DOC
		this.nominal = nominal;
		this.maturityDate = maturityDate;
		this.accuralDays = accuralDays;
		this.interestRate = interestRate;
		this.interestAmount = interestAmount;
		this.interestTax = interestTax;
		this.netInterestRate = netInterestRate;
		this.bilyetNumber = bilyetNumber;

	}

}
