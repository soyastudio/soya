package soya.framework.commandline.tasks.document.poi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.sl.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.commonmark.node.Image;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;

import java.awt.*;
import java.beans.BeanDescriptor;
import java.beans.FeatureDescriptor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SoyaCoderMarkdown extends AbstractVisitor {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String EVENT_START_PREFIX = "<!-- pptx:";

    public static final String EVENT_END_TOKEN = "#####";

    public static final File home = new File("C:/github/SoyaCoder/website");

    //public static final Color FONT_COLOR = new Color(78, 147, 89);
    //public static final Color FONT_COLOR = new Color(33, 69, 18);
    // public static final Color FONT_COLOR = new Color(34, 82, 14);

    public static final Color FONT_COLOR = new Color(13, 52, 120);

    public static final File[] IMAGES = new File[]{
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/wave_green.jpg")
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/blue.jpg")
            new File(home, "media/blue_elegant.jpg")
            //new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/blue_flourish.jpg")
            //new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/light_blue.jpg")
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/white_flower.jpg")
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/water_splash.jpg")
    };


    private List<Buffer> nodes = new ArrayList<>();

    private Buffer buffer;

    @Override
    public void visit(BlockQuote blockQuote) {
        super.visit(blockQuote);
    }

    @Override
    public void visit(BulletList bulletList) {
        super.visit(bulletList);
    }

    @Override
    public void visit(Code code) {
        super.visit(code);
    }

    @Override
    public void visit(Document document) {
        super.visit(document);
    }

    @Override
    public void visit(Emphasis emphasis) {
        super.visit(emphasis);
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        super.visit(fencedCodeBlock);
        nodes.add(new Buffer(fencedCodeBlock));
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        super.visit(hardLineBreak);
    }

    @Override
    public void visit(Heading heading) {
        super.visit(heading);
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {
        super.visit(thematicBreak);
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        super.visit(htmlInline);
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        super.visit(htmlBlock);

        String token = htmlBlock.getLiteral();
        if (token.startsWith("<!--") && token.endsWith("-->")) {
            token = token.substring(4, token.length() - 3).trim();

            if (token.equals(EVENT_END_TOKEN)) {
                nodes.add(buffer);
                buffer = null;
            } else {
                buffer = new Buffer();
                if (token.contains(":")) {
                    int index = token.indexOf(":");
                    buffer.type = token.substring(0, index);
                    buffer.name = token.substring(index + 1);
                } else {
                    buffer.name = token;
                }
            }


        }

    }

    @Override
    public void visit(Image image) {
        super.visit(image);
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        super.visit(indentedCodeBlock);
    }

    @Override
    public void visit(Link link) {
        super.visit(link);
    }

    @Override
    public void visit(ListItem listItem) {
        super.visit(listItem);
    }

    @Override
    public void visit(OrderedList orderedList) {
        super.visit(orderedList);
    }

    @Override
    public void visit(Paragraph paragraph) {
        super.visit(paragraph);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        super.visit(softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        super.visit(strongEmphasis);
    }

    @Override
    public void visit(Text text) {
        super.visit(text);

        if (buffer != null) {
            buffer.nodes.add(text);
        }

        String token = text.getLiteral().trim();
        if (token.startsWith("|") && token.endsWith("|")) {
            StringTokenizer tokenizer = new StringTokenizer(token, "|");
            while (tokenizer.hasMoreTokens()) {
                String t = tokenizer.nextToken().trim();
                //System.out.println("------------- " + t);
            }
        }
    }

    @Override
    public void visit(LinkReferenceDefinition linkReferenceDefinition) {
        super.visit(linkReferenceDefinition);
    }

    @Override
    public void visit(CustomBlock customBlock) {
        super.visit(customBlock);
    }

    @Override
    public void visit(CustomNode customNode) {
        super.visit(customNode);
    }

    static class Buffer {
        private String type;
        private String name;
        private Node node;
        private List<Node> nodes;

        Buffer() {
            nodes = new ArrayList<>();
        }

        Buffer(Node node) {
            this.node = node;
        }
    }

    private static String getTitle(FencedCodeBlock block) {
        String token = block.getInfo();
        if (token.contains("[")) {
            token = token.substring(token.lastIndexOf("[") + 1, token.lastIndexOf("]"));
        }

        return token;
    }

    private static String getBody(FencedCodeBlock block) {
        List<String> items = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(block.getLiteral(), "\n");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.startsWith("- ") || token.startsWith("-- ")) {
                items.add(token.trim());
            }

        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(items.get(i));
        }

        return builder.toString();
    }

    private static List<String> getBodyAsList(FencedCodeBlock block) {
        List<String> items = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(block.getLiteral(), "\n");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.startsWith("- ") || token.startsWith("-- ")) {
                items.add(token.trim());
            }

        }

        return items;
    }

    private static void setSlide(XSLFSlide slide, XSLFPictureData background) {
        CTBackgroundProperties backgroundProperties = slide.getXmlObject().getCSld().addNewBg().addNewBgPr();
        CTBlipFillProperties blipFillProperties = backgroundProperties.addNewBlipFill();
        CTRelativeRect ctRelativeRect = blipFillProperties.addNewStretch().addNewFillRect();
        String idx = slide.addRelation(null, XSLFRelation.IMAGES, background).getRelationship().getId();
        CTBlip blib = blipFillProperties.addNewBlip();
        blib.setEmbed(idx);
    }

    private static XSLFSlide createCover(String title, String author, XMLSlideShow ppt) throws IOException {

        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.PIC_TX);
        XSLFSlide cover = ppt.createSlide(layout);

        setSlide(cover, ppt.getPictureData().get(0));

        XSLFShape pic = cover.getPlaceholder(1);
        java.awt.geom.Rectangle2D anchor = pic.getAnchor();

        byte[] pictureData = IOUtils.toByteArray(
                new FileInputStream(new File(home, "media/apache_avro_logo.png")));
        XSLFPictureData pd = ppt.addPicture(pictureData, PictureData.PictureType.PNG);
        XSLFPictureShape picture = cover.createPicture(pd);
        cover.removeShape(pic);
        picture.setAnchor(anchor);

        XSLFTextShape name = cover.getPlaceholder(0);
        // Clearing text to remove the predefined one in the template
        name.clearText();

        XSLFTextParagraph tp = name.addNewTextParagraph();
        tp.setTextAlign(TextParagraph.TextAlign.CENTER);
        XSLFTextRun tr = tp.addNewTextRun();
        tr.setText(title);
        tr.setFontColor(FONT_COLOR);
        tr.setFontSize(56.);

        XSLFTextShape body = cover.getPlaceholder(2);

        //clear the existing text in the slide
        body.clearText();

        XSLFTextParagraph bp = body.addNewTextParagraph();
        bp.setTextAlign(TextParagraph.TextAlign.CENTER);

        XSLFTextRun bt = bp.addNewTextRun();
        bt.setText(author);
        bt.setFontColor(FONT_COLOR);
        bt.setFontSize(28.);

        return cover;
    }

    private static XSLFSlide createEnd(XMLSlideShow ppt) throws IOException {

        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.BLANK);
        XSLFSlide cover = ppt.createSlide(layout);
        setSlide(cover, ppt.getPictureData().get(0));

        byte[] pictureData = IOUtils.toByteArray(
                new FileInputStream(new File(home, "media/thank-you.png")));
        XSLFPictureData pd = ppt.addPicture(pictureData, PictureData.PictureType.PNG);
        XSLFPictureShape picture = cover.createPicture(pd);
        picture.setAnchor(new Rectangle(180, 100, 360, 240));

        return cover;
    }

    private static XSLFSlide createDefaultSlide(XMLSlideShow ppt, FencedCodeBlock block) {

        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);

        XSLFSlide slide = ppt.createSlide(layout);
        setSlide(slide, ppt.getPictureData().get(0));

        XSLFTextShape title = slide.getPlaceholder(0);
        // Clearing text to remove the predefined one in the template
        title.clearText();

        XSLFTextParagraph p = title.addNewTextParagraph();
        XSLFTextRun r1 = p.addNewTextRun();
        r1.setText(getTitle(block));
        r1.setFontColor(FONT_COLOR);
        r1.setFontSize(42.);

        //selection of body placeholder
        XSLFTextShape body = slide.getPlaceholder(1);

        body.setLeftInset(75.);
        body.setRightInset(50.);

        //clear the existing text in the slide
        body.clearText();

        List<String> items = getBodyAsList(block);
        for (String item : items) {
            if (item.startsWith("- ")) {
                //adding new paragraph
                XSLFTextParagraph paragraph = body.addNewTextParagraph();
                paragraph.setBulletAutoNumber(AutoNumberingScheme.arabicPeriod, 1);
                XSLFTextRun textRun = paragraph.addNewTextRun();
                textRun.setFontColor(FONT_COLOR);
                textRun.setFontSize(22.);
                textRun.setText(item.substring(2).trim());
            } else if (item.startsWith("-- ")) {
                XSLFTextParagraph paragraph = body.addNewTextParagraph();
                paragraph.setIndentLevel(1);
                XSLFTextRun textRun = paragraph.addNewTextRun();
                textRun.setFontColor(FONT_COLOR);
                textRun.setFontSize(20.);
                textRun.setText(item.substring(3).trim());

            }
        }

        return slide;
    }

    private static XSLFSlide createCode(XMLSlideShow ppt, FencedCodeBlock block) throws IOException {
        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.BLANK);
        XSLFSlide slide = ppt.createSlide(layout);
        setSlide(slide, ppt.getPictureData().get(0));

        XSLFTextBox textBox = slide.createTextBox();

        textBox.clearText();
        textBox.setTopInset(30.);
        textBox.setLeftInset(30.);
        //textBox.setFillColor(new Color(240, 253, 254));
        textBox.setFillColor(Color.BLACK);

        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();

        //String json = GSON.toJson(JsonParser.parseString(block.getLiteral()));
        //textRun.setFontColor(FONT_COLOR);
        textRun.setFontColor(Color.white);
        textRun.setFontSize(10.0);
        textRun.setText(block.getLiteral());

        textBox.setAnchor(new Rectangle(100, 75, 500, 400));

        return slide;
    }

    private static void createTable(XSLFSlide slide, String name, Table table) {
        XSLFTextShape title = slide.getPlaceholder(0);
        // Clearing text to remove the predefined one in the template
        title.clearText();

        XSLFTextParagraph textParagraph = title.addNewTextParagraph();
        XSLFTextRun r1 = textParagraph.addNewTextRun();
        r1.setText(name);
        r1.setFontColor(FONT_COLOR);
        r1.setFontSize(42.);

        XSLFTable tbl = slide.createTable();
        tbl.setAnchor(new Rectangle(120, 100, 750, 300));

        int numColumns = table.header().length;
        int numRows = table.rows();

        // header
        XSLFTableRow headerRow = tbl.addRow();
        headerRow.setHeight(18);
        for (int i = 0; i < numColumns; i++) {
            XSLFTableCell th = headerRow.addCell();
            XSLFTextParagraph p = th.addNewTextParagraph();
            p.setTextAlign(TextParagraph.TextAlign.CENTER);
            XSLFTextRun r = p.addNewTextRun();
            r.setText(table.header()[i]);
            r.setBold(true);
            r.setFontColor(Color.white);
            r.setFontSize(12.);
            th.setFillColor(new Color(79, 129, 189));
            th.setBorderWidth(TableCell.BorderEdge.bottom, 2.0);
            th.setBorderColor(TableCell.BorderEdge.bottom, Color.white);
            // all columns are equally sized
            tbl.setColumnWidth(i, 150);
        }

        // data
        for (int rownum = 0; rownum < numRows; rownum++) {
            String[] values = table.row(rownum);

            XSLFTableRow tr = tbl.addRow();
            tr.setHeight(16);

            int min = Math.min(numColumns, values.length);

            for (int i = 0; i < min; i++) {
                XSLFTableCell cell = tr.addCell();
                XSLFTextParagraph p = cell.addNewTextParagraph();
                XSLFTextRun r = p.addNewTextRun();
                r.setFontColor(FONT_COLOR);
                r.setFontSize(10.);

                r.setText(values[i]);
                if (rownum % 2 == 0) {
                    cell.setFillColor(new Color(208, 216, 232));
                } else {
                    //cell.setFillColor(new Color(233, 247, 244));
                    cell.setFillColor(new Color(240, 253, 254));
                }
            }
        }
    }

    private static void createSide(Buffer buffer, XMLSlideShow ppt) {

        if ("TABLE".equalsIgnoreCase(buffer.type)) {
            Table table = createTable(buffer);
            XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);

            XSLFSlide slide = ppt.createSlide(defaultMaster.getLayout(SlideLayout.TITLE_ONLY));
            setSlide(slide, ppt.getPictureData().get(0));
            createTable(slide, buffer.name, createTable(buffer));

        }

    }

    private static Table createTable(Buffer buffer) {

        Text text = (Text) buffer.nodes.get(0);
        String[] headers = toRow(text.getLiteral().trim());

        Table.Builder builder = Table.builder(headers);

        for (int i = 2; i < buffer.nodes.size(); i++) {
            Text line = (Text) buffer.nodes.get(i);
            builder.addRow(toRow(line.getLiteral()));
        }

        return builder.create();
    }

    private static String[] toRow(String line) {
        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(line, "|");
        while (tokenizer.hasMoreTokens()) {
            String h = tokenizer.nextToken().trim();
            list.add(h);
        }

        return list.toArray(new String[list.size()]);

    }

    public static void main(String[] args) throws Exception {

        BeanDescriptor beanDescriptor;
        DynaProperty property;
        FeatureDescriptor featureDescriptor;

        Parser parser = Parser.builder().build();
        //Node document = parser.parseReader(new FileReader(new File(home, "markdown/spring_boot_overview.md")));
        Node document = parser.parseReader(new FileReader(new File(home, "markdown/apache_avro_developer.md")));

        SoyaCoderMarkdown visitor = new SoyaCoderMarkdown();
        document.accept(visitor);

        // Create presentation
        XMLSlideShow ppt = new XMLSlideShow();
        //ppt.setPageSize(new Dimension(960, 540));
        XSLFPictureData[] pictures = new XSLFPictureData[]{
                ppt.addPicture(new FileInputStream(IMAGES[0]), PictureData.PictureType.JPEG)
        };

        // create cover
        createCover("Apache Avro", "Qun Wen", ppt);

        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);

        // Retriving the slide layout
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);

        for (Buffer buffer : visitor.nodes) {
            if (buffer.node != null) {
                Node node = buffer.node;
                if (node instanceof FencedCodeBlock) {

                    FencedCodeBlock block = (FencedCodeBlock) node;
                    String info = block.getInfo();
                    if (info == null || info.trim().length() == 0) {
                        createCode(ppt, block);

                    } else if (info.startsWith("pptx")) {
                        createDefaultSlide(ppt, block);
                    }

                }

            } else {
                createSide(buffer, ppt);
            }

        }

        createEnd(ppt);

        // Save presentation
        FileOutputStream out = new FileOutputStream(new File(home, "avro_for_developer.pptx"));
        ppt.write(out);
        out.close();

        // Closing presentation
        ppt.close();
    }
}

