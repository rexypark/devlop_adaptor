package com.indigo.esb.std.file.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_FILES;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.config.FlatFileInterfaceInfo;
import com.indigo.esb.convertor.NamedRowMapper;
import com.indigo.esb.std.com.snd.EncryptionProcess;
import com.indigo.esb.util.DateUtil;
import com.tecacet.jflat.CSVReader;
import com.tecacet.jflat.excel.PoiExcelReader;

/**
 * @author clupine-mb JFlat를 사용한 플랫파일 데이터 추출 대용량 데이터 파일에는 적합하지 않음
 */
public class SimpleJFlatConvertorDataToJMS implements OnSignalStrategy {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	EncryptionProcess encryption = new EncryptionProcess();

	public void setEncryption(EncryptionProcess encryption) {
		this.encryption = encryption;
	}

	@Autowired
	JmsTemplate jmsTemplate;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		FlatFileInterfaceInfo interfaceInfo = (FlatFileInterfaceInfo) onSignalResult.getInterfaceInfo();
		logger.info("interface Info : " + interfaceInfo);
		String[] fileNames = onSignalResult.getProperty(ESB_FILES).split(",");

		for (String filename : fileNames) {
			logger.info("FileName : " + filename);
			File file = new File(interfaceInfo.getTmpDir(), filename);
			
			try {
				
				List<Map> dataList = null;
				
				if (interfaceInfo.getFileType().equals("EXCEL")) {
					logger.info("File Type EXCEL  : " + file.getCanonicalPath());
					PoiExcelReader<Map> reader = new PoiExcelReader<Map>(file.getCanonicalPath(), new NamedRowMapper(
							interfaceInfo.getNameSet()));
					if (interfaceInfo.isFirstRowSkip()) {
						reader.setSkipLines(1);
					}
					 dataList = reader.readAll();
				} else if (interfaceInfo.getFileType().equals("CSV")) {
					logger.info("File Type CSV ");
					CSVReader<Map> reader = new CSVReader<Map>(new FileReader(file), new NamedRowMapper(interfaceInfo.getNameSet()));
					if (interfaceInfo.isFirstRowSkip()) {
						reader.setSkipLines(1);
					}
					 dataList = reader.readAll();
				} 
				String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
				onSignalResult.addProperty(ESB_TX_ID, interfaceInfo.getTxidGenerator().create());
				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, String.valueOf(dataList.size()));
				onSignalResult.setPollResultDataObj(dataList);
				encryption.onStart(onSignalResult);
				this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(), onSignalResult.getProperties());
			} catch (Exception e) {
				logger.error(filename + " 파일 처리중 에러 발생 Error 디렉토리로 이동  ", e);
				FileUtils.moveFile(file, new File(interfaceInfo.getErrDir(), filename + "." + DateUtil.getTodayTimeMilli()));
				continue;
			}
			logger.info("송신 처리 완료 : " + file.getName());
			FileUtils.moveFile(file, new File(interfaceInfo.getScsDir(), filename + "." + DateUtil.getTodayTimeMilli()));
		}

	}
}
