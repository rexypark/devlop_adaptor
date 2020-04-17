 CREATE IF_1FTC0001
   (
	S1_BENO                  VARCHAR(7 BYTE), 
	S1_FNCTNL_STATE_EXPLN    VARCHAR(7 BYTE), 
	S1_PYSCL_STATE_EXPLN     VARCHAR(7 BYTE), 
	S1_TGT_STATE_EXPLN       VARCHAR(7 BYTE), 
	S1_BDA_RFLTDT            VARCHAR(7 BYTE), 
	S1_RGST_DTTM             VARCHAR(7 BYTE), 
	S1_FNL_RNWL_DTTM         VARCHAR(7 BYTE), 
	S1_REGSTR_SRVNO          VARCHAR(7 BYTE), 
	S1_FNL_RNWPS_SRVNO       VARCHAR(7 BYTE), 
	
	
	ESB_UID VARCHAR(27 BYTE) PRIMARY KEY,  
	ESB_TX_ID VARCHAR2(40 BYTE),            
	ESB_STATE_CD CHAR(1 BYTE), 
	ESB_TIME CHAR(17 BYTE)
	);
		
			S1_BENO                     ,
			S1_FNCTNL_STATE_EXPLN       ,
			S1_PYSCL_STATE_EXPLN        ,
			S1_TGT_STATE_EXPLN          ,
			S1_BDA_RFLTDT               ,
			S1_RGST_DTTM                ,
			S1_FNL_RNWL_DTTM            ,
			S1_REGSTR_SRVNO             ,
			S1_FNL_RNWPS_SRVNO          ,
            ESB_UID 				    , 
            ESB_TX_ID                   ,
            ESB_STATE_CD