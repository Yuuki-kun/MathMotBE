package com.mot.mot;


import com.mot.mot.authService.AuthenticationService;
import com.mot.mot.controller.authController.AuthenticationController;
import com.mot.mot.helper.DocumentReader;
import com.mot.mot.model.RegisterRequest;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class MotApplication {

	private final AuthenticationService authenticationService;

	private final DocumentReader documentReader;

	public static void main(String[] args) {
		SpringApplication.run(MotApplication.class, args);
	}

	//on application started
	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReadyEvent() throws Exception {
		authenticationService.registerForTest(
				RegisterRequest.builder().email("teacher@gmail.com").fullName("TEACHER").role("Teacher").password(
						"aZ230902@").build()
		);

		authenticationService.registerForTest(
				RegisterRequest.builder().email("student@gmail.com").fullName("STUDENT").role("Student").password(
						"aZ230902@").build()
		);

		documentReader.readDocument();

//		List<String> equations = new ArrayList<>();
//
//		XWPFDocument document = new XWPFDocument(new FileInputStream("src/main/resources/static/images/word/Equation" +
//				".docx"));
//		StringBuffer allHTML = new StringBuffer();
//
//		for (IBodyElement ibodyelement : document.getBodyElements()) {
//			StringBuilder texts = new StringBuilder();
//
//			//reset the text buffer
//			if (ibodyelement.getElementType().equals(BodyElementType.PARAGRAPH)) {
//				XWPFParagraph paragraph = (XWPFParagraph)ibodyelement;
//
//				if(paragraph.getText().startsWith("câu hỏi")) {
//					texts.append("<p>");
//					texts.append(getTextAndFormulas(paragraph));
//					texts.append("</p>");
//					System.out.println("Câu hỏi = "+texts);
//
//				}
//
//				allHTML.append("<p>");
//
//
//				allHTML.append(getTextAndFormulas(paragraph));
//
//				allHTML.append("</p>");
//			} else if (ibodyelement.getElementType().equals(BodyElementType.TABLE)) {
//				XWPFTable table = (XWPFTable)ibodyelement;
//				allHTML.append("<table border=1>");
//				for (XWPFTableRow row : table.getRows()) {
//					allHTML.append("<tr>");
//					for (XWPFTableCell cell : row.getTableCells()) {
//						allHTML.append("<td>");
//						for (XWPFParagraph paragraph : cell.getParagraphs()) {
//							allHTML.append("<p>");
//							allHTML.append(getTextAndFormulas(paragraph));
//							allHTML.append("</p>");
//						}
//						allHTML.append("</td>");
//					}
//					allHTML.append("</tr>");
//				}
//				allHTML.append("</table>");
//			}
//
//			System.out.println(texts.toString());
//		}
//		document.close();
//		//creating a sample HTML file
//		String encoding = "UTF-8";
//		FileOutputStream fos = new FileOutputStream("result.html");
//		OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);
//		writer.write("<!DOCTYPE html>\n");
//		writer.write("<html lang=\"en\">");
//		writer.write("<head>");
//		writer.write("<meta charset=\"utf-8\"/>");
//
//		//using MathJax for helping all browsers to interpret MathML
//		writer.write("<script type=\"text/javascript\"");
//		writer.write(" async src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=MML_CHTML\"");
//		writer.write(">");
//		writer.write("</script>");
//
//		writer.write("</head>");
//		writer.write("<body>");
//
//		writer.write(allHTML.toString());
//
//		writer.write("</body>");
//		writer.write("</html>");
//		writer.close();
//
//		Desktop.getDesktop().browse(new File("result.html").toURI());

	}

	static File stylesheet = new File("src/main/resources/static/images/word/OMML2MML.xsl");
	static TransformerFactory tFactory = TransformerFactory.newInstance();
	static StreamSource stylesource = new StreamSource(stylesheet);

	static String getMathML(CTOMath ctomath) throws Exception {
		Transformer transformer = tFactory.newTransformer(stylesource);

		Node node = ctomath.getDomNode();

		DOMSource source = new DOMSource(node);
		StringWriter stringwriter = new StringWriter();
		StreamResult result = new StreamResult(stringwriter);
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		transformer.transform(source, result);

		String mathML = stringwriter.toString();
		stringwriter.close();
		mathML = mathML.replaceAll("xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"", "");
		mathML = mathML.replaceAll("xmlns:mml", "xmlns");
		mathML = mathML.replaceAll("mml:", "");
		return mathML;
	}
	static String getTextAndFormulas(XWPFParagraph paragraph) throws Exception {

		StringBuffer textWithFormulas = new StringBuffer();

		//using a cursor to go through the paragraph from top to down
		XmlCursor xmlcursor = paragraph.getCTP().newCursor();

		while (xmlcursor.hasNextToken()) {
			XmlCursor.TokenType tokentype = xmlcursor.toNextToken();
			if (tokentype.isStart()) {
				if (xmlcursor.getName().getPrefix().equalsIgnoreCase("w") && xmlcursor.getName().getLocalPart().equalsIgnoreCase("r")) {
					//elements w:r are text runs within the paragraph
					//simply append the text data
					textWithFormulas.append(xmlcursor.getTextValue());
				} else if (xmlcursor.getName().getLocalPart().equalsIgnoreCase("oMath")) {
					//we have oMath
					//append the oMath as MathML
					textWithFormulas.append(getMathML((CTOMath)xmlcursor.getObject()));
				}
			} else if (tokentype.isEnd()) {
				//we have to check whether we are at the end of the paragraph
				xmlcursor.push();
				xmlcursor.toParent();
				if (xmlcursor.getName().getLocalPart().equalsIgnoreCase("p")) {
					break;
				}
				xmlcursor.pop();
			}
		}

		return textWithFormulas.toString();
	}

}

