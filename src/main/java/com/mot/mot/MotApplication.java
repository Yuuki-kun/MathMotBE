package com.mot.mot;


import com.mot.mot.authService.AuthenticationService;
import com.mot.mot.controller.authController.AuthenticationController;
import com.mot.mot.helper.DocumentReader;
import com.mot.mot.model.RegisterRequest;
import com.mot.mot.model.dto.UploadImageResponse;
import com.mot.mot.model.entity.EmbedImage;
import com.mot.mot.repository.EmbedImageRepository;
import com.mot.mot.service.IImageUpload;
import com.mot.mot.service.ImageService;
import com.mot.mot.service.ImgBBService;
import com.mot.mot.service.LocalStoreImageService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.*;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@RequiredArgsConstructor
public class MotApplication {

	private final AuthenticationService authenticationService;
	private final EmbedImageRepository embedImageRepository;
	private final ImageService imageService;

	private final DocumentReader documentReader;

	public static void main(String[] args) {
		SpringApplication.run(MotApplication.class, args);
	}

//	@PostConstruct
//	public void registerShutdownHook() {
//		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			// Logic xử lý trước khi ứng dụng tắt
//
//			List<EmbedImage> embedImages = embedImageRepository.findAll();
//			for (EmbedImage embedImage : embedImages) {
//				System.out.println("to delete img = "+ embedImage.toString());
//
//				imageService.deleteImage(embedImage.getDeleteHash());
//
//			}
//
//		}));
//	}
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


//		try (InputStream is = new FileInputStream("src/main/resources/static/images/word/Equation2.docx")){
//			XWPFDocument doc = new XWPFDocument(is);
//			for (XWPFPictureData pictureData : doc.getAllPictures()) {
//				byte[] bytes = pictureData.getData();
//				String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
//				System.out.println("data:image/png;base64," + base64);
//
//				try {
//					byte[] imageBytes = Base64.getDecoder().decode(base64);
//
//					// Lưu tệp ảnh
//					try (FileOutputStream fos = new FileOutputStream("src/main/resources/static/images/word/image_1733112640037.png")) {
//						fos.write(imageBytes);
//						System.out.println("Hình ảnh đã được giải mã và lưu thành công!");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					System.out.println("Đã xảy ra lỗi khi giải mã hình ảnh.");
//				}
//
//			}
//		}




//		documentReader.readDocument();

//		ImageService imageService = new ImageService();
//
//		File file = new File("src/main/resources/static/images/image_1733112640037.png");
//
//		byte[] fileContent = null;
//		try {
//			fileContent = Files.readAllBytes(file.toPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		String imageUrl = imageService.upload("test.jog", fileContent );
//		System.out.println("imageUrl = "+imageUrl);

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


}

