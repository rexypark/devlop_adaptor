 CREATE ADDRESS_TYPE1_TARGET
   (
	ZIPCODE CHAR(7 BYTE), 
	SIDO VARCHAR2(8 BYTE), 
	GUGUN VARCHAR2(34 BYTE), 
	DONG VARCHAR2(104 BYTE), 
	BUNJI VARCHAR2(34 BYTE), 
	SEQ NUMBER(5,0), 
	ESB_UID VARCHAR2(27 BYTE) PRIMARY KEY, 
	ESB_TX_ID VARCHAR2(40 BYTE), 
	ESB_STATE_CD CHAR(1 BYTE), 
	ESB_TIME CHAR(17 BYTE)
	);
