package com.indigo.esb.std.p02;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.ZipUtils;
import com.indigo.fileserver.client.IndigoFileTransferAPI;

public class SignalDBtoFilePoll extends OnSignalDBSupport {

	public String path = "../DB_FILE";
	public String serverInfo = "127.0.0.1:24212";
	public String spitChar = "|";

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		List<Map<String, Object>> pollDataList = null;
		Map<String, String> onSignalValueMap = onSignalResult.getInterfaceInfo().getAddDataMap();
		boolean remoteFlag = onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE).equals("REMOTE");
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		String fileName = onSignalResult.getProperty(ESB_TX_ID);
		if(onSignalValueMap.get("trg_file") != null){
		    fileName = onSignalValueMap.get("trg_file");
		    onSignalResult.getProperties().addHeaderInfo("trg_file", onSignalValueMap.get("trg_file"));
		}
		if(onSignalValueMap.get("trg_path") != null){
		    onSignalResult.getProperties().addHeaderInfo("trg_path", onSignalValueMap.get("trg_path"));
		}
		DBRowHandler rowHandler = new DBRowHandler(getPath(), getSpitChar(),fileName);
		/*log.info("sql : " + info.getInterfaceId());
		log.info("sql :" + getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_UPDATE));
		DBRowHandlerUpdate rowHandler = new DBRowHandlerUpdate(getPath(), getSpitChar(),fileName, 
				getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_UPDATE),sqlSession);*/
		if (onSignalValueMap.get(ESB_IF_DATA_TYPE) != null) {
			log.info("#### Remote Message Map : " + onSignalValueMap);
			sqlSession.select(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT), onSignalValueMap,
					rowHandler);
			rowHandler.getFileWriter().close();

		} else {
			sqlSession.select(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT), null, rowHandler);
			rowHandler.getFileWriter().close();

		}
		log.info("total count : " + rowHandler.getTotalRowCount());
		if(rowHandler.getTotalRowCount() == 0 ){
			rowHandler.file.delete();
			onSignalResult.setResultCount(0);
			if(remoteFlag){
				throw new Exception("select count 0");
			}
			return;
		}
		onSignalResult.setPollResultDataObj(pollDataList);
		if (dbFileSend(rowHandler.getDBFile(), getServerInfo(), onSignalResult.getProperty(ESB_TX_ID))) {
		    	log.debug("doFileSend ok..");
			onSignalResult.setResultCount(rowHandler.getTotalRowCount());
		} else {
	    	log.debug("doFileSend fail..");
			onSignalResult.setResultCount(0);
			throw new Exception("DB file send fail");
		}

	}

	public boolean dbFileSend(File file, String serverInfo, String trgFile) {
		try {
		    	File targetFile = new File(file.getParent(), trgFile + ".zip");
		    	log.debug("Zip file : "  + file.getAbsolutePath() + " trgfile : " + targetFile.getAbsolutePath());
			ZipUtils.compress(file, targetFile);
			
			log.info("=========# make zip file end =======");
			String[] svrInfo = serverInfo.split("\\,");
			for (int i = 0; i < svrInfo.length; i++) {
			    if(socketSend(targetFile.getParent(),targetFile.getName(), svrInfo[i])){
				log.info("=========# make excel file send end =======");
				targetFile.delete();
				file.delete();
				return true;
			    }
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// new File(sendDir, tx_id + ".zip").delete();
	}
	public boolean dbFileSend(File file, String serverInfo) {
		try {

			ZipUtils.compress(file, file.getParent(), new File(file.getAbsoluteFile() + ".zip"));
			String[] svrInfo = serverInfo.split("\\,");
			for (int i = 0; i < svrInfo.length; i++) {
			    if(socketSend(file.getParent(), file.getName() + ".zip", svrInfo[i])){
				    log.info("=========# make excel file send end =======");
				    new File(file.getAbsoluteFile() + ".zip").delete();
				    return true;
			    }
			}
			return false;
			/*log.info("=========# make zip file end =======");
			if(!socketSend(file.getParent(), file.getName() + ".zip", serverInfo)){
			    return false;
			}
			log.info("=========# make excel file send end =======");
			new File(file.getAbsoluteFile() + ".zip").delete();
			file.delete();
			return true;
			*/
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// new File(sendDir, tx_id + ".zip").delete();
	}
	
	public boolean socketSend(String filePath, String fileName, String serverInfo) {
		try {
			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);
			log.debug("file server :" + serverIp+ " port :" + serverPort);
			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp, serverPort);
			api.setChunkSize(8192);
			String file = filePath + File.separator + fileName;
			api.simpleFilePut("", file);
			log.info("File Socket Send Success !!!");
			return true;
		} catch (Exception e) {
			log.info("File Socket Send Fail !!!");
			return false;
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public void setSpitChar(String spitChar) {
		this.spitChar = spitChar;
	}

	public String getSpitChar() {
		return spitChar;
	}
	public static void main(String[] aa) {
		File file = new File("D:\\sw\\indigo\\TEST\\aaa");
		File trg = new File("D:\\sw\\indigo\\TEST\\aaa.zip");
		try {
			System.out.println(file.getParent());
			System.out.println(FilenameUtils.getPath("D:\\sw\\indigo\\TEST\\aaa"));
			ZipUtils.compress(file, trg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
