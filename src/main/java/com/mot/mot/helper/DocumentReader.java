package com.mot.mot.helper;

import com.mot.mot.model.dto.CreateExamResponse;
import com.mot.mot.model.dto.UploadImageResponse;
import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.EmbedImage;
import com.mot.mot.model.entity.Exam;
import com.mot.mot.model.entity.Question;
import com.mot.mot.repository.AnswerRepository;
import com.mot.mot.repository.EmbedImageRepository;
import com.mot.mot.repository.ExamRepository;
import com.mot.mot.repository.QuestionRepository;
import com.mot.mot.service.abstractInterface.IImageUpload;
import com.mot.mot.service.LocalStoreImageService;
import jakarta.transaction.Transactional;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DocumentReader {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ExamRepository examRepository;
    private final EmbedImageRepository embedImageRepository;

    private final IImageUpload imageService;

    private static final String questionClassName = "question";
    private static final String answerClassName = "answer";

    boolean isProcessingAnswer = false;
    boolean isProcessingQuestion = false;
    Question currentProcessingQuestion = null;
    Answer currentProcessingAnswer = null;
    public DocumentReader(QuestionRepository questionRepository, AnswerRepository answerRepository,
                          ExamRepository examRepository,
                          EmbedImageRepository embedImageRepository,
                          LocalStoreImageService localStoreImageService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.examRepository = examRepository;
        this.embedImageRepository = embedImageRepository;
        this.imageService = localStoreImageService;
    }

    static File stylesheet = new File("src/main/resources/static/images/word/OMML2MML.xsl");
    static TransformerFactory tFactory = TransformerFactory.newInstance();
    static StreamSource stylesource = new StreamSource(stylesheet);


    public CreateExamResponse readDocument(MultipartFile file) throws Exception {
        Exam exam = Exam.builder()
                .title("Exam")
                .createdDate(new Date())
                .build();

        examRepository.save(exam);

        List<Question> resultSetQuestionAndAnswer = new ArrayList<>();

         currentProcessingQuestion = null;
         currentProcessingAnswer = null;

         isProcessingAnswer = false;
         isProcessingQuestion = false;

        List<Question> questionsToSave = new ArrayList<>();
        List<Answer> answersToSave = new ArrayList<>();
        List<EmbedImage> imagesToSave = new ArrayList<>();

        StringBuilder currentProcessingQuestionContent = new StringBuilder();
        StringBuilder currentProcessingAnswerContent = new StringBuilder();

//        XWPFDocument document = new XWPFDocument(new FileInputStream("src/main/resources/static/images/word/Equation" +
//                ".doc"));

        boolean isFirstQuestion = true;
        boolean isFirstAnswer = true;
        int orderNumber = 1;
        try(InputStream inputStream = file.getInputStream()){
            XWPFDocument document = new XWPFDocument(inputStream);
            for (XWPFParagraph paragraph : document.getParagraphs()) {
               String paragarphText = paragraph.getText();
//                System.out.println("html content content content= "+htmlContent);

                if(paragarphText.startsWith("Câu") && paragarphText.charAt(3) == ' ' && (int) paragarphText.charAt(4) > 0){
                    isProcessingQuestion = true;
                    isProcessingAnswer = false;


                    Question question = Question.builder()
                            .type("Trắc nghiệm")
                            .point(0.0f)
                            .exam(exam)
                            .answers(new ArrayList<>())
                            .level(1)
                            .orderNumber(orderNumber++)
                            .build();


                    currentProcessingQuestion = question;

                    String htmlContent = getTextAndFormulas(paragraph, document, "src/main/resources/static/images",
                            imagesToSave);

                    //remove chars before :
                    htmlContent = htmlContent.substring(htmlContent.indexOf(":") + 1);
                    currentProcessingQuestionContent = new StringBuilder(htmlContent);
                    question.setTitle(htmlContent);

                    questionsToSave.add(question);
                } else if (!paragarphText.isEmpty() && paragarphText.length() > 1  && paragarphText.charAt(0)>='A' && paragarphText.charAt(1)=='.') {
                    isProcessingAnswer = true;
                    isProcessingQuestion = false;


                    Answer answer = Answer.builder()
//                            .content(htmlContent)
//                            .correct(htmlContent.endsWith("xxx") || htmlContent.endsWith("XXX"))
//                            .letter(htmlContent.substring(0, 1))
                            .question(currentProcessingQuestion)
                            .build();

                    currentProcessingAnswer = answer;
                    String htmlContent = getTextAndFormulas(paragraph, document, "src/main/resources/static/images",
                            imagesToSave);
                    //<span>A.
                    String letter = htmlContent.charAt(0) + "";
                    boolean isCorrect = htmlContent.endsWith("xxx");
                    //if correct, remove the xxx
                    if(isCorrect){
                        htmlContent = htmlContent.substring(htmlContent.indexOf(".") + 1, htmlContent.indexOf("xxx"));
                    }else {
                        htmlContent = htmlContent.substring(htmlContent.indexOf(".") + 1);

                    }
                    currentProcessingAnswerContent = new StringBuilder(htmlContent);
                    answer.setContent(htmlContent);
                    answer.setCorrect(isCorrect);
                    answer.setLetter(letter);

                    answersToSave.add(answer);

                    currentProcessingQuestion.getAnswers().add(answer);
                }else{
                    String htmlContent = getTextAndFormulas(paragraph, document, "src/main/resources/static/images",
                            imagesToSave);
                    if (isProcessingQuestion){
                        currentProcessingQuestionContent.append("<br/>").append("<div style=\"display: block; flex-basis: 100%\">").append(htmlContent).append("</div>");
                        currentProcessingQuestion.setTitle(currentProcessingQuestionContent.toString());
                    }else if (isProcessingAnswer){
                        currentProcessingAnswerContent.append("<br/>").append("<div style=\"display: block; " +
                                "flex-basis: 100%\">").append(htmlContent).append("</div>");
                        Answer lastAnswer = answersToSave.get(answersToSave.size() - 1);
                        lastAnswer.setContent(currentProcessingAnswerContent.toString());

                        currentProcessingQuestion.getAnswers().set(currentProcessingQuestion.getAnswers().size() - 1, lastAnswer);


                    }else{
                        System.out.println("htmlContent = " + htmlContent);
                    }
                }

            }

            saveQuestionsAndAnswers(questionsToSave, answersToSave);

            System.out.println("RETURNED 1");
            List<Question> result = questionsToSave;

            if(!questionsToSave.isEmpty()){
                if(questionsToSave.size() > 2){
                    //only upload two first questions

                    List<Question> firstTwoQuestions = questionsToSave.subList(0, 2);
                    int totalImagesInFirstTwoQ = 0;

                    for(Question question: firstTwoQuestions){
                        String regex = "src";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(question.getTitle());
                        while (matcher.find()) {
                            totalImagesInFirstTwoQ++;
                        }
                        for (Answer answer : question.getAnswers()) {
                            matcher = pattern.matcher(answer.getContent());
                            while (matcher.find()) {
                                totalImagesInFirstTwoQ++;
                            }
                        }

                    }

                    List<EmbedImage> embedImagesInFirstTwoQ = imagesToSave.subList(0, totalImagesInFirstTwoQ);


//                    List<EmbedImage> leavedImages = imagesToSave.subList(totalImagesInFirstTwoQ, imagesToSave.size());
                    for(EmbedImage embedImage : embedImagesInFirstTwoQ){
                        if(!embedImage.isUploaded()){
                            UploadImageResponse uploadImageResponse = imageService.upload(embedImage.getName(),
                                    embedImage.getFile());
                            if(uploadImageResponse != null){

                                embedImage.setType(uploadImageResponse.getType());
                                embedImage.setSize(uploadImageResponse.getSize());
                                embedImage.setDeleteHash(uploadImageResponse.getDeleteHash());
                                embedImage.setStorageWeb(uploadImageResponse.getStorageWeb());
                                embedImage.setUrl(uploadImageResponse.getUrl());
                                embedImage.setUploaded(true);




                                if(embedImage.getQuestion()!=null && embedImage.getQuestion().getId() > 0){
                                    embedImage.getQuestion().setTitle(embedImage.getQuestion().getTitle().replace(embedImage.getName(), uploadImageResponse.getUrl()));
                                    questionRepository.save(embedImage.getQuestion());


                                } else if (embedImage.getAnswer()!=null && embedImage.getAnswer().getId() > 0){
                                    embedImage.getAnswer().setContent(embedImage.getAnswer().getContent().replace(embedImage.getName(), uploadImageResponse.getUrl()));
                                    answerRepository.save(embedImage.getAnswer());

                                }
                                //update the src in the question
                            }
                        }
                    }
                    result = questionsToSave;


                    CompletableFuture<Void> uploadImagesBackgroundTask = CompletableFuture.runAsync(()->{

                        for(EmbedImage embedImage : imagesToSave){
                            if(!embedImage.isUploaded()){
                                System.out.println("upload background at " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
                                UploadImageResponse uploadImageResponse = imageService.upload(embedImage.getName(),
                                        embedImage.getFile());
                                if(uploadImageResponse != null){

                                    embedImage.setType(uploadImageResponse.getType());
                                    embedImage.setSize(uploadImageResponse.getSize());
                                    embedImage.setDeleteHash(uploadImageResponse.getDeleteHash());
                                    embedImage.setStorageWeb(uploadImageResponse.getStorageWeb());
                                    embedImage.setUrl(uploadImageResponse.getUrl());
                                    embedImage.setUploaded(true);

                                    embedImageRepository.save(embedImage);
                                    if(embedImage.getQuestion()!=null && embedImage.getQuestion().getId() > 0){
                                        embedImage.getQuestion().setTitle(embedImage.getQuestion().getTitle().replace(embedImage.getName(), uploadImageResponse.getUrl()));
                                        questionRepository.save(embedImage.getQuestion());

                                    } else if (embedImage.getAnswer()!=null && embedImage.getAnswer().getId() > 0){
                                        embedImage.getAnswer().setContent(embedImage.getAnswer().getContent().replace(embedImage.getName(), uploadImageResponse.getUrl()));
                                        answerRepository.save(embedImage.getAnswer());

                                    }
                                    //update the src in the question
                                }
                            }
                        }
                    });

//                    for (EmbedImage embedImageToSave : imagesToSave) {
////                        System.out.println("embedImageToSave = " + embedImageToSave.getId());
//                        if(!embedImageToSave.isUploaded()){
//                            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//
//                                try{
//
//                                    synchronized (embedImageToSave){
//
//
////                                        UploadImageResponse uploadImageResponse = uploadImageAsync(embedImageToSave.getName(),
////                                                embedImageToSave.getFile()).join();
//
//                                        UploadImageResponse uploadImageResponse = imageService.upload(embedImageToSave.getName(),
//                                                embedImageToSave.getFile());
//
//                                        if (uploadImageResponse == null) {
//                                            throw new RuntimeException("Error uploading image");
//                                        }
//
//                                        embedImageToSave.setType(uploadImageResponse.getType());
//                                        embedImageToSave.setSize(uploadImageResponse.getSize());
//                                        embedImageToSave.setDeleteHash(uploadImageResponse.getDeleteHash());
//                                        embedImageToSave.setStorageWeb(uploadImageResponse.getStorageWeb());
//                                        embedImageToSave.setUrl(uploadImageResponse.getUrl());
//                                        embedImageToSave.setUploaded(true);
//
//                                        Long entityId = embedImageToSave.getQuestion() != null
//                                                ? embedImageToSave.getQuestion().getId()
//                                                : embedImageToSave.getAnswer().getId();
//                                        Semaphore entitySemaphore = semaphoreMap.computeIfAbsent(entityId, id -> new Semaphore(1));
//                                        entitySemaphore.acquire(); // Chặn nếu có luồng khác đang xử lý
//                                        try {
//
//                                            synchronized (this){
//                                                if (embedImageToSave.getQuestion() != null) {
//
//                                                    Question question = embedImageToSave.getQuestion();
//                                                    question.setTitle(
//                                                            embedImageToSave.getQuestion().getTitle().replace(embedImageToSave.getName(), embedImageToSave.getUrl()));
//                                                    questionRepository.save(question);
//
//
//                                                } else if (embedImageToSave.getAnswer() != null) {
//                                                    Answer answer = embedImageToSave.getAnswer();
//                                                    answer.setContent(
//                                                            embedImageToSave.getAnswer().getContent().replace(embedImageToSave.getName(), embedImageToSave.getUrl()));
////                                                    System.out.println("Before saving Answer: " + embedImageToSave.getAnswer());
//                                                    answerRepository.save(answer);
////                                                    System.out.println("After saving Answer: " + embedImageToSave.getAnswer());
//
//                                                }
//                                            }
//
//                                        } finally {
//                                            entitySemaphore.release(); // Giải phóng Semaphore
//                                        }
//
//                                    }
//
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//
//
//                            }, executorService);
//                            futures.add(future);
//
//                        }
//
//
//                    }
                    //return result;
                    System.out.println("RETURNED 2");

                }else {

//                    for(Question question: questionsToSave){

//                        if(question.getTitle().contains("src=")){
//                            //upload image
//                            for(EmbedImage embedImage: imagesToSave){
//                                String regex = "src='(.*?)'";
//                                Pattern pattern = Pattern.compile(regex);
//                                Matcher matcher = pattern.matcher(question.getTitle());
//                                if (matcher.find()) {
//                                    String srcValue = matcher.group(1);
//
//                                    if(srcValue.equals(embedImage.getName())){
//                                        UploadImageResponse uploadImageResponse = imageService.upload(embedImage.getName(),
//                                                embedImage.getFile());
//                                        if(uploadImageResponse != null){
//                                            EmbedImage image = EmbedImage.builder()
//                                                    .name(embedImage.getName())
//                                                    .url(uploadImageResponse.getUrl())
//                                                    .type(uploadImageResponse.getType())
//                                                    .size(uploadImageResponse.getSize())
//                                                    .deleteHash(uploadImageResponse.getDeleteHash())
//                                                    .storageWeb(uploadImageResponse.getStorageWeb())
//                                                    .question(question)
//                                                    .uploaded(true)
//                                                    .build();
//                                            imagesToSave.add(image);
//
//                                            //update the src in the question
//                                            question.setTitle(question.getTitle().replace(srcValue, uploadImageResponse.getUrl()));
//
//                                        }
//                                        break;
//                                    }
//                                }else {
//                                    throw new RuntimeException("Error extracting src value");
//                                }
//                            }
//                        }


//                    }

//                    List<EmbedImage> embedImagesInFirstTwoQ = imagesToSave.subList(0, totalImagesInFirstTwoQ);

                    for(EmbedImage embedImage : imagesToSave){
                        if(!embedImage.isUploaded()){
                            UploadImageResponse uploadImageResponse = imageService.upload(embedImage.getName(),
                                    embedImage.getFile());
                            if(uploadImageResponse != null){

                                      embedImage.setType(uploadImageResponse.getType());
                                        embedImage.setSize(uploadImageResponse.getSize());
                                        embedImage.setDeleteHash(uploadImageResponse.getDeleteHash());
                                        embedImage.setStorageWeb(uploadImageResponse.getStorageWeb());
                                        embedImage.setUrl(uploadImageResponse.getUrl());
                                        embedImage.setUploaded(true);

                                embedImageRepository.save(embedImage);
                                if(embedImage.getQuestion()!=null && embedImage.getQuestion().getId() > 0){
                                    embedImage.getQuestion().setTitle(embedImage.getQuestion().getTitle().replace(embedImage.getName(), uploadImageResponse.getUrl()));
                                    questionRepository.save(embedImage.getQuestion());

                                } else if (embedImage.getAnswer()!=null && embedImage.getAnswer().getId() > 0){
                                    embedImage.getAnswer().setContent(embedImage.getAnswer().getContent().replace(embedImage.getName(), uploadImageResponse.getUrl()));
                                    answerRepository.save(embedImage.getAnswer());

                                }
                                //update the src in the question
                            }
                        }
                    }

                }
            }


            //log
//            questionsToSave.forEach(System.out::println);
//            answersToSave.forEach(System.out::println);
//            imagesToSave.forEach(System.out::println);


            System.out.println("RETURNED 3");

            if(result.size()>2) {
                return CreateExamResponse.builder().id(exam.getId()).title(exam.getTitle())
                        .description(exam.getDescription())
                        .totalQuestions(result.size()).questions(result.subList(0,2)).build();
            }

            return CreateExamResponse.builder().id(exam.getId()).title(exam.getTitle())
                    .description(exam.getDescription())
                    .totalQuestions(result.size()).questions(result).build();
        }
    }


    @Transactional
    public  void processEmbedImage(EmbedImage embedImageToSave) {



        if (embedImageToSave.getQuestion() != null) {
            synchronized (embedImageToSave.getQuestion()) {

                embedImageToSave.getQuestion().setTitle(
                        embedImageToSave.getQuestion().getTitle().replace(embedImageToSave.getName(), embedImageToSave.getUrl()));
                System.out.println("Before saving Question: " + embedImageToSave.getQuestion());
                questionRepository.save(embedImageToSave.getQuestion());
                System.out.println("After saving Question: " + embedImageToSave.getQuestion());

            }
        } else if (embedImageToSave.getAnswer() != null) {
            synchronized (embedImageToSave.getAnswer()) {
                embedImageToSave.getAnswer().setContent(
                        embedImageToSave.getAnswer().getContent().replace(embedImageToSave.getName(), embedImageToSave.getUrl()));
                System.out.println("Before saving Answer: " + embedImageToSave.getAnswer());
                answerRepository.save(embedImageToSave.getAnswer());
                System.out.println("After saving Answer: " + embedImageToSave.getAnswer());
            }
        }
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

        //
        XmlCursor endCursor = paragraph.getCTP().newCursor();
        endCursor.toEndToken();
        //

        while (xmlcursor.hasNextToken()) {
            if (xmlcursor.isAtSamePositionAs(endCursor)) {
                break; // Dừng nếu con trỏ đến cuối đoạn văn
            }
            XmlCursor.TokenType tokentype = xmlcursor.toNextToken();
            if (tokentype.isStart()) {
                if (xmlcursor.getName().getPrefix().equalsIgnoreCase("w") && xmlcursor.getName().getLocalPart()
                        .equalsIgnoreCase("r")) {
                    //elements w:r are text runs within the paragraph
                    //simply append the text data
                    textWithFormulas.append(xmlcursor.getTextValue());
                } else if (xmlcursor.getName().getLocalPart().equalsIgnoreCase("oMath")) {
                    //we have oMath
                    //append the oMath as MathML
                    textWithFormulas.append(getMathML((CTOMath) xmlcursor.getObject()));
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

        //
        xmlcursor.dispose();
        endCursor.dispose();

        //
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
                String filePath = "src/main/resources/static/images/" + anwserid + imageName;

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


    private static String processPicture(XWPFPicture picture, String imageSaveDir) throws IOException {
        // Lấy dữ liệu hình ảnh
        XWPFPictureData pictureData = picture.getPictureData();
        String imageExtension = pictureData.suggestFileExtension();
        String imageName = "image_" + System.currentTimeMillis() + "." + imageExtension;

        // Đường dẫn lưu hình ảnh
        String filePath = imageSaveDir + File.separator + imageName;

        // Ghi hình ảnh ra file
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(pictureData.getData());
        }

        // Tạo thẻ <img> HTML
        return String.format("<img src='%s' alt='Embedded Image' />", imageName);
    }

    // Sửa lại hàm getTextAndFormulas để nhận document
    public String getTextAndFormulas(XWPFParagraph paragraph, XWPFDocument document, String imageSaveDir,
                                     List<EmbedImage> imagesToSave) throws Exception {
        StringBuilder content = new StringBuilder();
        XmlCursor cursor = paragraph.getCTP().newCursor();

//      //
        XmlCursor endCursor = paragraph.getCTP().newCursor();
        endCursor.toEndToken();
        //
            while (cursor.hasNextToken()) {
                XmlCursor.TokenType tokenType = cursor.toNextToken();
                if (cursor.isAtSamePositionAs(endCursor)) {
                    break; // Dừng nếu con trỏ đến cuối đoạn văn
                }
                if (tokenType.isStart()) {
                    String localName = cursor.getName().getLocalPart();
                    String prefix = cursor.getName().getPrefix();

                    if (prefix.equals("w") && localName.equals("t")) {
                        // Xử lý văn bản
                        content.append(cursor.getTextValue());
                    } else if (prefix.equals("m") && localName.equals("oMath")) {
                        // Xử lý công thức toán học
                        CTOMath oMath = (CTOMath) cursor.getObject();
                        content.append("<span>").append(getMathML(oMath)).append("</span>");
                    } else if (prefix.equals("w") && localName.equals("drawing")) {
                        // Xử lý hình ảnh
                        XmlCursor drawingCursor = cursor.newCursor();
                        System.out.println("drawingCursor = " + drawingCursor);
                        EmbedImage embedImage = processPictureFromCursor(drawingCursor, imageSaveDir, document);


                        System.out.println("imgUrl = " + embedImage);
                        if (embedImage != null) {

                            if (embedImage.getName() != null) {
                                float width = embedImage.getWidth();
                                float height = embedImage.getHeight();
                                String imgTag = String.format("<img src='%s' alt='Embedded Image' style=\"width: %" +
                                                ".2fpx; " +
                                                "height: %.2fpx" +
                                                "\" />",
                                        embedImage.getName(), width, height);

//                                content.append("<div>").append(imgTag).append("</div>");
                                content.append(imgTag);
                                imagesToSave.add(embedImage);
                            }
                        }

                    }

                }
        }
        cursor.dispose();
        endCursor.dispose();
        return content.toString();

    }


    // Sửa hàm processPictureFromCursor để nhận document
    private  EmbedImage processPictureFromCursor(XmlCursor cursor, String imageSaveDir, XWPFDocument document) {
        System.out.println("cursor = " + cursor.xmlText());
        try {
            while (cursor.hasNextToken()) {
                XmlCursor.TokenType tokentype = cursor.toNextToken();
                if (tokentype.isStart() && "blip".equals(cursor.getName().getLocalPart())) {
                    String embedId = cursor.getAttributeText(new QName("http://schemas.openxmlformats" +
                            ".org/officeDocument/2006/relationships", "embed"));

                    if (embedId != null) {
                        System.out.println("Embed ID: " + embedId);

                        // Tìm dữ liệu hình ảnh theo embedId
                        XWPFPictureData
                        pictureDatafounded = findPictureDataById(embedId, document);


                        if (pictureDatafounded == null) {
                            System.out.println("Not found");
                        }
                        if (pictureDatafounded != null) {
                            // Tạo file hình ảnh
                            System.out.println("Suggest file extension: " + pictureDatafounded.suggestFileExtension());
                            String imageExtension = pictureDatafounded.suggestFileExtension();
                            String imageName = "image_" + System.currentTimeMillis() + "." +
                                    imageExtension;
//                            String filePath = imageSaveDir + File.separator + imageName;
                            // Ghi dữ liệu hình ảnh ra file
//                            try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                                fos.write(pictureDatafounded.getData());
//                            }

                            //resize

                            byte[] pictureBytes = null;
                            pictureBytes = pictureDatafounded.getData();
                            ByteArrayInputStream bais = new ByteArrayInputStream(pictureBytes);
                            BufferedImage originalImage = ImageIO.read(bais);

                            byte[] fileContent = pictureBytes;
                            EmbedImage embedImage = EmbedImage.builder()
                                    .name(imageName)
                                    .width(originalImage.getWidth()*0.6f)
                                    .height(originalImage.getHeight()*0.6f)
                                    .file(fileContent)
                                    .build();

                            if(isProcessingQuestion){
                                embedImage.setQuestion(currentProcessingQuestion);
                                embedImage.setAnswer(null);
                            }else if(isProcessingAnswer){
                                embedImage.setAnswer(currentProcessingAnswer);
                                embedImage.setQuestion(null);
                            }else {
                                System.out.println("embedImage = " + embedImage);
                                System.out.println("both current question and answer are null");
//                                throw new RuntimeException("both current question and answer are null");
                            }

                            // Trả về thẻ <img>
                            return embedImage;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private XWPFPictureData findPictureDataById(String embedId, XWPFDocument document) throws InvalidFormatException {
        List<XWPFPictureData> allPictures = document.getAllPictures();
        PackageRelationshipCollection relations = document.getPackagePart().getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/image");
        PackagePart imagePart = null;

        for (PackageRelationship rel : relations) { if (rel.getId().equals(embedId)) { imagePart = document.getPackagePart().getRelatedPart(rel); break; } }
        if (imagePart != null) {
            for (XWPFPictureData pictureData : allPictures) {
                if (pictureData.getPackagePart().getPartName().equals(imagePart.getPartName())) {
                    return pictureData;
                }
            }
        }

        return null;
    }


    public CompletableFuture<UploadImageResponse> uploadImageAsync(String imageName, byte[] fileContent) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return imageService.upload(imageName, fileContent);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading image", e);
            }
        });
    }


    @Transactional
    public void saveQuestionsAndAnswers(List<Question> questionsToSave, List<Answer> answersToSave) {
        questionRepository.saveAll(questionsToSave); answerRepository.saveAll(answersToSave);
    }
}
