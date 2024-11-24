package com.mot.mot.helper;

import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.Question;
import com.mot.mot.repository.AnswerRepository;
import com.mot.mot.repository.QuestionRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

@Component
public class DocumentReader {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private static final String questionClassName = "question";
    private static final String answerClassName = "answer";

    public DocumentReader(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    static File stylesheet = new File("src/main/resources/static/images/word/OMML2MML.xsl");
    static TransformerFactory tFactory = TransformerFactory.newInstance();
    static StreamSource stylesource = new StreamSource(stylesheet);

    public void readDocument() throws Exception {
		XWPFDocument document = new XWPFDocument(new FileInputStream("src/main/resources/static/images/word/Equation.docx"));
        boolean isProcessingAnswer = false;

        StringBuilder currentQuestion = new StringBuilder();
        StringBuilder currentAnswer = new StringBuilder();
        List<String> currentAnswers = new ArrayList<>();

        //question and answers
        Map<Integer, List<String>> questions = new HashMap<>();

        //answer and images
        Map<Integer, List<String>> images = new HashMap<>();
        int questionid = -1;
        int answerid = -1;

        Question currentProcessingQuestion = null;
        Answer currentProcessingAnswer = null;

        for(IBodyElement iBodyElement : document.getBodyElements()){
            if (iBodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
                XWPFParagraph paragraph = (XWPFParagraph) iBodyElement;

                String paragraphText = paragraph.getText().trim();

                if(paragraphText.startsWith("Câu hỏi") || paragraphText.contains("Câu hỏi")){

                    //reset the current question
                    currentQuestion = new StringBuilder();
                    currentProcessingQuestion = null;

                    isProcessingAnswer = false;
                    questionid++;
//                    currentQuestion.append("id = "+questionid);
                    currentQuestion.append(getTextAndFormulas(paragraph));

                    var toSaveQuestion = Question.builder()
                            .title(currentQuestion.toString())
                            .type("MATH")
                            .point(1.0f)
                            .build();

                    currentProcessingQuestion = questionRepository.save(toSaveQuestion);

                    System.out.println("Câu hỏi "+currentQuestion);
                }else if(paragraphText.startsWith("Đáp án")) {
                    currentAnswers = new ArrayList<>();
                    isProcessingAnswer = true;
                 }else if(isProcessingAnswer){
                    if(currentProcessingQuestion != null){
                        //process images in paragraph
//                        List<String> imageList = images.computeIfAbsent(answerid, k -> new ArrayList<>());
                        System.out.println("pa="+paragraphText);
                        System.out.println("paragraphText = " +(!paragraphText.isEmpty() && Character.isUpperCase(paragraphText.charAt(0)) && paragraphText.charAt(1) == '.'));

                        //check if the paragraph starts with a letter (A, B, C, D, ...) by using regex
                        if(!paragraphText.isEmpty() && Character.isUpperCase(paragraphText.charAt(0)) && paragraphText.charAt(1) == '.'){
                            //reset the current answer
                            currentAnswer = new StringBuilder();
                            currentProcessingAnswer = null;
                            String answerLetter = paragraphText.substring(0, 1);
                            boolean isCorrect =
                                    paragraphText.endsWith(".X.") || paragraphText.endsWith(".x.") || paragraphText.endsWith("đ") || paragraphText.endsWith("Đ");
                            currentAnswer.append(getTextAndFormulas(paragraph));
                            answerid++;
                            var toSaveAnswer = com.mot.mot.model.entity.Answer.builder()
                                    .content(currentAnswer.toString())
                                    .correct(isCorrect)
                                    .letter(answerLetter)
                                    .question(currentProcessingQuestion)
                                    .build();
                            currentProcessingAnswer = answerRepository.save(toSaveAnswer);
                        }else {
                                //update
                            if(!processImagesInParagraph(paragraph, currentProcessingAnswer != null ? currentProcessingAnswer.getId().intValue() : -1)){
                                answerid++;

                                currentAnswer.append("<br/>"+getTextAndFormulas(paragraph));
//                            currentAnswers.add(currentAnswer);
//                            questions.put(questionid, currentAnswers);

                                if(currentProcessingAnswer != null) {
                                    var toSaveAnswer = com.mot.mot.model.entity.Answer.builder()
                                            .content(currentAnswer.toString())
                                            .correct(currentProcessingAnswer.getCorrect())
                                            .letter(currentProcessingAnswer.getLetter())
                                            .question(currentProcessingQuestion)
                                            .build();

                                    currentProcessingAnswer = answerRepository.save(toSaveAnswer);
                                }

                            }
                        }


                    }
                }else {
                    // a text without header is considered as part of the question

                    //update the current question
                    currentQuestion.append(getTextAndFormulas(paragraph));

                    if(currentProcessingQuestion != null){
                        var toSaveQuestion = Question.builder()
                                .id(currentProcessingQuestion.getId())
                                .title(currentQuestion.toString())
                                .type("MATH")
                                .point(1.0f)
                                .build();

                        currentProcessingQuestion = questionRepository.save(toSaveQuestion);
                    }

                }
            }
        }

        System.out.println("questions = "+currentQuestion);
        System.out.println("answers = "+questions);
        System.out.println("images = "+images);
    }

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
    private boolean processImagesInParagraph(XWPFParagraph paragraph, int anwserid) throws Exception {
        boolean hasImage = false;
        try {
            // Duyệt qua các ảnh trong đoạn văn
            for (XWPFPicture picture : paragraph.getRuns().stream()
                    .flatMap(run -> run.getEmbeddedPictures().stream())
                    .toList()) {

                // Lấy dữ liệu hình ảnh
                XWPFPictureData pictureData = picture.getPictureData();

                // Tạo tên file với định dạng (vd: image1.png)
                String imageExtension = pictureData.suggestFileExtension(); // Lấy đuôi file (png, jpg, ...)
                String imageName = "image_" + System.currentTimeMillis() + "." + imageExtension;

                // Lưu hình ảnh ra file
                String filePath = "src/main/resources/static/images/"+anwserid + imageName;

                // Ghi dữ liệu hình ảnh ra file
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    fileOutputStream.write(pictureData.getData());
                }
                // Thêm đường dẫn vào danh sách
                hasImage = true;

                System.out.println("Đã lưu hình ảnh: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý hình ảnh: " + e.getMessage());
        }

        return hasImage;
    }
}
