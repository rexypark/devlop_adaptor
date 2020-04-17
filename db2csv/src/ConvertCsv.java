

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class ConvertCsv {
	
	public static void main(String[] args) {
		List<String> testList = null;
		String subject = "testCSV";
		String path = "C:/indigopj/file/recv/test_csv";
				
	  for(int i = 0; i < 100; i++) {
		  testList.add(Integer.toString(i));
        }

	  System.out.println("testList : " + testList);
	  
 	 createCSV(testList,subject, path);
	}
	
	public static int createCSV(List<String> list, String title, String filepath) {

		int resultCount = 0;

		try {

			BufferedWriter fw =

					new BufferedWriter(new FileWriter(filepath + "/" + title + ".csv", true));

			for (String dom : list) {

				fw.write(dom + "," + "test");

				fw.newLine();

				resultCount++;

				if (resultCount % 100 == 0)

					System.out.println("resultCount :" + resultCount + "/" + list.size());

			}

			fw.flush();

			// °´Ã¼ ´Ý±â
			fw.close();

		} catch (Exception e) {

			// TODO: handle exception

			e.printStackTrace();

		}
		return resultCount;
	}
}


