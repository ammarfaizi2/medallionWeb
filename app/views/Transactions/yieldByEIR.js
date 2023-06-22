//pake ini karena ie tidak support Math.trunc
Math.trunc = Math.trunc || function(x) {
  if (isNaN(x)) {
    return NaN;
  }
  if (x > 0) {
    return Math.floor(x);
  }
  return Math.ceil(x);
};

function yieldByEIR(
		iFaceValue, 
		iPurchaseValue, 
		iYearBase, 
		iInterestRate, 
		iStartDate,
		iEndDate) {
	if (
			((iFaceValue) > 0 ) &&
	        ((iPurchaseValue) > 0) &&
	        ((iYearBase) > 0) &&
	        ((iInterestRate) > 0) 
	       )
	    {
	        lGuessRate = iInterestRate;
	        lRateChange = 0.1;
	        lSign = 0;
			
			var lStartDate = new Date(iStartDate);
			var lEndDate = new Date(iEndDate);
			
			lCount = Math.round((lEndDate - lStartDate) / (1000 * 60 * 60 * 24) ) + 1;
		
			lAmount = iInterestRate / iYearBase * iFaceValue;
	        lAmount_residual = (1 + iInterestRate / iYearBase) * iFaceValue;
	        
	        for (x = 1; x <= 10000; x++)
	        {
	            lResidual = -iPurchaseValue;
	        
	            for (y = 2; y <= lCount; y++)
	            {
	                if (y == lCount)
	                {
	                    lBaseAmount = lAmount_residual;
	                }
	                else
	                {
	                    lBaseAmount = lAmount;
	                }
	            
	                lResidual = lResidual + lBaseAmount / Math.pow ((1 + lGuessRate), (y - 1) / iYearBase);
	            }
	        
	            if ((lResidual > 0) && (lSign == 0))
	            {
	                lRateChange = lRateChange / 2;
	                lSign = 1;
	            }
				
	            if ((lResidual < 0) && (lSign == 1))
	            {
	                lRateChange = lRateChange / 2;
	                lSign = 0;
	            }

	            if ((lSign == 0))
	            {
	                lGuessRate = lGuessRate - lRateChange;
	            }   
	            else
	            {
	            	lGuessRate = lGuessRate + lRateChange;
	            }
	            if ((Math.trunc(lResidual * 100000)) == 0 )	        
	            {
	            break;
	            }
	        }

	        lGuessRate = (Math.pow (1 + lGuessRate, 1 / iYearBase) - 1) * iYearBase;
	        
	        return lGuessRate;
	    }
	    else
	    {	
	        return "";
	    }
	    
}
