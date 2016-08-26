package com.sprd.bugremind.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class GetConfigXML {
	/**
	 * @category 解析XML文件节点 @
	 * @author 
	 * @date 
	 */
	public static String[] getXmlValue(String file, String value, String attr) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse("BRConfig\\"+ file); // 获取到xml文件
			String s;
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//param"); // 获取节点名称
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			ArrayList<String> sp = new ArrayList<String>();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node nd = nodes.item(i);
				if (nd.getAttributes().getNamedItem("name").getNodeValue()
						.equals(value)) {
					s = nd.getAttributes().getNamedItem(attr).getNodeValue();
					// System.out.println("--getXmlValue-- :" + s);
					sp.add(s);
				}
			}
			String[] array = sp.toArray(new String[0]);
			// System.out.println(array.length);
			return array;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * 获取项目的相对路径下文件的绝对路径
     * 
     * @param parentDir
     *            目标文件的父目录,例如说,工程的目录下,有lib与bin和conf目录,那么程序运行于lib or
     *            bin,那么需要的配置文件却是conf里面,则需要找到该配置文件的绝对路径
     * @param fileName
     *            文件名
     * @return 一个绝对路径
     */
    public static String getPath(String parentDir, String fileName) {
        String path = null;
        String userdir = System.getProperty("user.dir");
        String userdirName = new File(userdir).getName();
        if (userdirName.equalsIgnoreCase("lib")
                || userdirName.equalsIgnoreCase("bin")) {
            File newf = new File(userdir);
            File newp = new File(newf.getParent());
            if (fileName.trim().equals("")) {
                path = newp.getPath() + File.separator + parentDir;
            } else {
                path = newp.getPath() + File.separator + parentDir
                        + File.separator + fileName;
            }
        } else {
            if (fileName.trim().equals("")) {
                path = userdir + File.separator + parentDir;
            } else {
                path = userdir + File.separator + parentDir + File.separator
                        + fileName;
            }
        }
 
        return path;
    }
	
	
}
	