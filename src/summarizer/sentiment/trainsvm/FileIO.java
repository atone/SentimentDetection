package summarizer.sentiment.trainsvm;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author wyq
 *
 */
public class FileIO {

	/**
	 * @param inputFilePath
	 * @return
	 */
	public static String readFile(String inputFilePath) {
		String txtContent = "";
		try {
			String encoding = "utf-8";
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					txtContent += lineTxt;
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件出错");
			e.printStackTrace();
		}
		return txtContent;
	}

	public static int getFileRowNum(String inputFilePath) {
		int rowNum = 0;
		try {
			String encoding = "utf-8";
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				while ((bufferedReader.readLine()) != null) {
					rowNum++;
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件出错");
			e.printStackTrace();
		}
		return rowNum;
	}

	/*
	 * public static ArrayList<WeiboObject> readXMLFile(String xmlFile){
	 * ArrayList<WeiboObject> list=new ArrayList<WeiboObject>(); SAXReader
	 * saxReader=new SAXReader(); try { Document document =saxReader.read(new
	 * File(xmlFile)); Element root=document.getRootElement(); List<Element>
	 * childList=root.elements("comment"); for(Element ele:childList){
	 * WeiboObject tmp=new WeiboObject(); tmp.setContent(ele.getText());
	 * tmp.setOriginURL(ele.attributeValue("url")); list.add(tmp); }
	 * System.out.println(); } catch (DocumentException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * return list; }
	 */
	public static void writeFile(String str, String filePath) {
		try {
			File fileSave = new File(filePath);
			String[] tmp = filePath.split("/");
			filePath = filePath.substring(0, filePath.length()
					- tmp[tmp.length - 1].length());
			if (!new File(filePath).exists())
				new File(filePath).mkdirs();
			PrintWriter printWriter = new PrintWriter(fileSave);
			printWriter.print(str);
			printWriter.close();
		} catch (Exception e) {
			System.out.println("写入文件出错");
			e.printStackTrace();
		}
	}

	public static void buildFolder(String folderPath) {
		System.out.println(folderPath);
		new File(folderPath).mkdirs();
	}

	public static void writeFile(ArrayList<?> list, String filePath) {
		StringBuffer buffer = new StringBuffer();
		for (Object tmp : list)
			buffer.append(tmp.toString() + "\n");
		writeFile(buffer.toString(), filePath);
	}
	/**
	 * @param list
	 * @param filePath
	 * 将list中的字符串以xml格式写入到filePath文件中
	 */
	public static void writeXmlFile(ArrayList<String> list, String filePath) {
		Document doc= DocumentHelper.createDocument();
		Element root =doc.addElement("root");
		root.addAttribute("count",list.size()+"");
		for(int i=0;i<list.size();i++){
			Element ele=root.addElement("review");
			ele.setText(list.get(i));
		}
		try {
			XMLWriter xmlWriter =new XMLWriter(new FileWriter(filePath));
			xmlWriter.write(doc);
			xmlWriter.flush();
			xmlWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeFeatureFile(ArrayList<Word> list, String filePath) {
		StringBuffer buffer = new StringBuffer();
		int i = 1;
		for (Word tmp : list)
			buffer.append(tmp.word + " " + i++ + "\n");
		writeFile(buffer.toString(), filePath);
	}

	public static void updateFile(String str, String filePath) {
		try {
			BufferedWriter bufferedWriter;
			File file = new File(filePath);
			if (file.exists()) {
				FileWriter printWriter = new FileWriter(filePath, true);
				bufferedWriter = new BufferedWriter(printWriter);
				bufferedWriter.append(str);
				bufferedWriter.flush();
				bufferedWriter.close();
			} else {
				writeFile(str, filePath);
			}
		} catch (Exception e) {
			System.out.println("Write File Error");
			e.printStackTrace();
		}
	}

	public static HashMap<String, Integer> readMapFile(String inputFilePath) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try {
			String encoding = "utf-8"; //
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null
						&& lineTxt.length() > 0) {
					String[] tmp = lineTxt.split(" ");
					map.put(tmp[0], Integer.parseInt(tmp[1]));
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件 " + inputFilePath + " 出错");
			e.printStackTrace();
		}
		return map;
	}

	public static HashMap<String, ArrayList<Double>> readVectorFile(
			String inputFilePath) {
		HashMap<String, ArrayList<Double>> map;
		try {
			String encoding = "utf-8"; //
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				// read map size
				String lineTxt = bufferedReader.readLine();
				map = new HashMap<String, ArrayList<Double>>(
						Integer.parseInt(lineTxt.split(" ")[0]));
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] tmp = lineTxt.split(" ", 2);
					map.put(tmp[0], FileIO.getVector(tmp[1]));
				}
				bufferedReader.close();
				return map;
			} else {
				System.out.println(inputFilePath + " File not exist...");
				System.exit(0);
			}
			return null;
		} catch (Exception e) {
			System.out.println("Read File Error");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	public static HashMap<String, ArrayList<Double>> readVectorBinFile(
			String inputFilePath) {
		HashMap<String, ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();
		try {
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				DataInputStream dis = new DataInputStream(new FileInputStream(
						inputFilePath));
				// String lineTxt = null;

				while (dis.available() > 0) {
					// System.out.println(dis.readUTF(dis));
					// String[] tmp = lineTxt.split(" ", 2);
					// System.out.println(tmp[0]);
					// map.put(tmp[0], BasicIO.getVector(tmp[1]));
				}
				dis.close();
			}
		} catch (Exception e) {
			System.out.println("Read File Error");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param file1
	 * @param file2
	 * @param outputfile
	 *            merge file1 and file2 to outputfile
	 */
	public static void mergeFile(String file1, String file2, String outputfile) {
		StringBuffer txtContent = new StringBuffer();
		String encoding = "utf-8";
		int itemNum = 0;
		int updateMax = 100000;
		try {
			FileIO.copyFile(file1, outputfile);
			InputStreamReader inputRead = new InputStreamReader(
					new FileInputStream(file2), encoding);
			BufferedReader bufferedReader = new BufferedReader(inputRead);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				txtContent.append(lineTxt + "\n");
				itemNum++;
				if (itemNum % updateMax == 0) {
					FileIO.updateFile(txtContent.toString(), outputfile);
					txtContent = new StringBuffer();
				}
			}
			FileIO.updateFile(txtContent.toString(), outputfile);
			bufferedReader.close();
			System.out.println(file1 + " and " + file2 + " has been merged.");
		} catch (Exception e) {
			System.out.println("Read file error...");
			e.printStackTrace();
		}
	}

	/**
	 * @param sourceFile
	 * @param terminalFile
	 */
	public static void copyFile(String sourceFile, String terminalFile) {
		try {
			FileInputStream fin = new FileInputStream(new File(sourceFile));
			FileOutputStream fout = new FileOutputStream(new File(terminalFile));
			FileChannel in = fin.getChannel();
			FileChannel out = fout.getChannel();
			out.transferFrom(in, 0, in.size());
			out.close();
			fout.close();
			in.close();
			fin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(sourceFile + " not found...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static ArrayList<Double> getVector(String str) {
		ArrayList<Double> vector = new ArrayList<Double>();
		String[] tmp = str.split(" ");
		for (String s : tmp) {
			vector.add(Double.parseDouble(s));
		}
		return vector;
	}

	public static HashMap<String, Float> readIdfMapFile(String inputFilePath) {
		HashMap<String, Float> map = new HashMap<String, Float>();
		try {
			String encoding = "utf-8";
			File inputFile = new File(inputFilePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] tmp = lineTxt.split("_");
					map.put(tmp[0] + "_", Float.parseFloat(tmp[1]));
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件出错");
			e.printStackTrace();
		}
		return map;
	}

	public static HashSet<String> readStopWordList(String filePath) {
		HashSet<String> hashSet = new HashSet<String>();
		try {
			String encoding = "utf-8";
			File inputFile = new File(filePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					hashSet.add(lineTxt);
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件出错");
			e.printStackTrace();
		}
		return hashSet;
	}

	public static ArrayList<String> readListFile(String filePath) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String encoding = "utf-8";
			File inputFile = new File(filePath);
			if (inputFile.isFile() && inputFile.exists()) {
				InputStreamReader inputRead = new InputStreamReader(
						new FileInputStream(inputFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(inputRead);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null
						&& lineTxt.length() > 0) {
					list.add(lineTxt);
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			System.out.println("Read File Error...");
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<String> readXmlFile(String xmlFilePath) {
		ArrayList<String> list = new ArrayList<String>();
		HashSet<String> set = new HashSet<String>();
		try {
			File inputFile = new File(xmlFilePath);
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(inputFile);
			Element root = doc.getRootElement();
			Iterator<Element> iter = root.elementIterator();
			while (iter.hasNext()) {
				Element tmp = (Element) iter.next();
				set.add(tmp.getData().toString());
			}
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
		} catch (Exception e) {
			System.out.println("Read File Error...");
			e.printStackTrace();
		}
		return list;
	}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		} else {
			System.out.println("File " + filePath + " not exist");
		}
	}
	
	
}